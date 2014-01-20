package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.components.*;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.*;

import java.util.*;


public class TileEntityManager extends TileEntityInterface {

    public static final int BUTTON_SIZE_W = 14;
    public static final int BUTTON_SIZE_H = 14;
    public static final int BUTTON_SRC_X = 242;
    public static final int BUTTON_SRC_Y = 0;
    public static final int BUTTON_INNER_SIZE_W = 12;
    public static final int BUTTON_INNER_SIZE_H = 12;
    public static final int BUTTON_INNER_SRC_X = 230;
    public static final int BUTTON_INNER_SRC_Y = 0;
    private List<FlowComponent> items;
    private Connection currentlyConnecting;
    public List<Button> buttons;
    public boolean justSentServerComponentRemovalPacket;
    private List<FlowComponent> zLevelRenderingList;
    private Variable[] variables;

    public TileEntityManager() {
        items = new ArrayList<FlowComponent>();
        zLevelRenderingList = new ArrayList<FlowComponent>();
        buttons = new ArrayList<Button>();
        removedIds = new ArrayList<Integer>();
        variables = new Variable[VariableColor.values().length];
        for (int i = 0; i < variables.length; i++) {
            variables[i] = new Variable(i);
        }

        for (int i = 0; i < ComponentType.values().length; i++) {
            buttons.add(new ButtonCreate(ComponentType.values()[i]));
        }

        buttons.add(new Button("Delete [Drop command here]") {
            @Override
            protected void onClick(DataReader dr) {
                int idToRemove = dr.readData(DataBitHelper.FLOW_CONTROL_COUNT);
                removeFlowComponent(idToRemove);
            }

            @Override
            public void onClick(DataWriter dw) {
                justSentServerComponentRemovalPacket = true;
                for (FlowComponent item : items) {
                    if (item.isBeingMoved()) {
                        dw.writeData(item.getId(), DataBitHelper.FLOW_CONTROL_COUNT);
                        return;
                    }
                }
            }

            @Override
            public boolean activateOnRelease() {
                return true;
            }
        });
    }

    private List<Integer> removedIds;

    public void removeFlowComponent(int idToRemove, List<FlowComponent> items) {
        for (int i =  items.size() - 1; i >= 0; i--) {
            if (i == idToRemove) {
                items.remove(i);
            }else{
                FlowComponent component = items.get(i);
                component.updateConnectionIdsAtRemoval(idToRemove);
                if (i > idToRemove) {
                    component.decreaseId();
                }
            }
        }


    }

    public void removeFlowComponent(int idToRemove) {
        removeFlowComponent(idToRemove, items);
        if (!worldObj.isRemote) {
            removedIds.add(idToRemove);
        }else{
            for (int i = 0; i < zLevelRenderingList.size(); i++) {
                if (zLevelRenderingList.get(i).getId() == idToRemove) {
                    zLevelRenderingList.remove(i);
                    break;
                }
            }
        }
        updateVariables();
    }


    public List<FlowComponent> getFlowItems() {
        return items;
    }

    public List<FlowComponent> getZLevelRenderingList() {
        return zLevelRenderingList;
    }

    List<ConnectionBlock> inventories = new ArrayList<ConnectionBlock>();
    public List<ConnectionBlock> getConnectedInventories() {
        return inventories;
    }

    public static final int MAX_CABLE_LENGTH = 64;
    public static final int MAX_COMPONENT_AMOUNT = 127;
    public static final int MAX_CONNECTED_INVENTORIES = 1023;

    private boolean firstInventoryUpdate = true;
    private boolean firstCommandExecution = true;

    public void updateInventories() {
        WorldCoordinate[] oldCoordinates = new WorldCoordinate[inventories.size()];
        for (int i = 0; i < oldCoordinates.length; i++) {
            TileEntity inventory = inventories.get(i).getTileEntity();
            oldCoordinates[i] = new WorldCoordinate(inventory.xCoord, inventory.yCoord, inventory.zCoord);
            oldCoordinates[i].setTileEntity(inventory);
        }

        List<WorldCoordinate> visited = new ArrayList<WorldCoordinate>();
        inventories.clear();
        Queue<WorldCoordinate> queue = new PriorityQueue<WorldCoordinate>();
        WorldCoordinate start = new WorldCoordinate(xCoord, yCoord, zCoord, 0);
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            WorldCoordinate element = queue.poll();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
                            WorldCoordinate target = new WorldCoordinate(element.getX() + x, element.getY() + y, element.getZ() + z, element.getDepth() + 1);

                            if (!visited.contains(target) && inventories.size() < MAX_CONNECTED_INVENTORIES) {
                                visited.add(target);
                                ConnectionBlock connection = new ConnectionBlock(worldObj.getBlockTileEntity(target.getX(), target.getY(), target.getZ()));
                                boolean isValidConnection = false;

                                for (ConnectionBlockType connectionBlockType : ConnectionBlockType.values()) {
                                    if (connectionBlockType.isInstance(connection.getTileEntity())) {
                                        isValidConnection = true;
                                        connection.addType(connectionBlockType);
                                    }
                                }

                                if (isValidConnection) {
                                    connection.setId(variables.length + inventories.size());
                                    inventories.add(connection);
                                    if (connection.getTileEntity() instanceof ISystemListener) {
                                        ((ISystemListener)connection.getTileEntity()).added(this);
                                    }
                                }else if (element.getDepth() < MAX_CABLE_LENGTH){
                                    if (worldObj.getBlockId(target.getX(), target.getY(), target.getZ()) == Blocks.blockCable.blockID) {
                                        queue.add(target);
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }

        if (!firstInventoryUpdate) {
            for (WorldCoordinate oldCoordinate : oldCoordinates) {
                if (oldCoordinate.getTileEntity() instanceof ISystemListener) {
                    boolean found = false;
                    for (ConnectionBlock inventory : inventories) {
                        if (oldCoordinate.getX() == inventory.getTileEntity().xCoord && oldCoordinate.getY() == inventory.getTileEntity().yCoord && oldCoordinate.getZ() == inventory.getTileEntity().zCoord) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        ((ISystemListener)oldCoordinate.getTileEntity()).removed(this);
                    }
                }
            }

            if (!worldObj.isRemote) {
                updateInventorySelection(oldCoordinates);
            }
        }


        firstInventoryUpdate = false;
    }

    private void updateInventorySelection(WorldCoordinate[] oldCoordinates) {
        for (FlowComponent item : items) {
            for (ComponentMenu menu : item.getMenus()) {
                if (menu instanceof ComponentMenuContainer) {
                    ComponentMenuContainer menuInventory = (ComponentMenuContainer)menu;


                    List<Integer> oldSelection = menuInventory.getSelectedInventories();
                    List<Integer> newSelection = new ArrayList<Integer>();

                    for (int i = 0; i < oldSelection.size(); i++) {
                        int selection = oldSelection.get(i);
                        if (selection >= 0 && selection < oldCoordinates.length) {
                            WorldCoordinate coordinate = oldCoordinates[selection];

                            for (int j = 0; j < inventories.size(); j++) {
                                TileEntity inventory = inventories.get(j).getTileEntity();
                                if (coordinate.getX() == inventory.xCoord && coordinate.getY() == inventory.yCoord && coordinate.getZ() == inventory.zCoord) {
                                    if (!newSelection.contains(j)) {
                                        newSelection.add(j);
                                    }

                                    break;
                                }
                            }

                        }
                    }

                    menuInventory.setSelectedInventories(newSelection);
                }
            }
        }
    }


    public Connection getCurrentlyConnecting() {
        return currentlyConnecting;
    }

    public void setCurrentlyConnecting(Connection currentlyConnecting) {
        this.currentlyConnecting = currentlyConnecting;
    }

    private int timer = 0;
    @Override
    public void updateEntity() {
        justSentServerComponentRemovalPacket = false;
        if (!worldObj.isRemote) {

            if (timer >= 20) {
                timer = 0;

                for (FlowComponent item : items) {
                    if (item.getType() == ComponentType.TRIGGER) {
                        for (ComponentMenu menu : item.getMenus()) {
                            if (menu instanceof ComponentMenuInterval) {
                                int interval = ((ComponentMenuInterval)menu).getInterval();
                                item.setCurrentInterval(item.getCurrentInterval() + 1);
                                if (item.getCurrentInterval() >= interval) {
                                    item.setCurrentInterval(0);

                                    EnumSet<ConnectionOption> valid = EnumSet.of(ConnectionOption.INTERVAL);
                                    if (isTriggerPowered(item, true)) {
                                        valid.add(ConnectionOption.REDSTONE_HIGH);
                                    }
                                    if (isTriggerPowered(item, false)) {
                                        valid.add(ConnectionOption.REDSTONE_LOW);
                                    }
                                    activateTrigger(item, valid);
                                }
                            }
                        }
                    }
                }


            }else{
                timer++;
            }
        }
    }



    private void activateTriggers(EnumSet<ConnectionOption> validTriggerOutputs) {
        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.TRIGGER) {
                activateTrigger(item, validTriggerOutputs);
            }
        }
    }

    private void activateTrigger(FlowComponent component, EnumSet<ConnectionOption> validTriggerOutputs) {
        if (firstCommandExecution) {
            updateInventories();
            firstCommandExecution = false;
        }

        for (ConnectionBlock inventory : inventories) {
            if (inventory.getTileEntity().isInvalid()) {
                updateInventories();
                break;
            }
        }

        new CommandExecutor(this).executeTriggerCommand(component, validTriggerOutputs);
    }

    private boolean isTriggerPowered(ComponentMenuRedstoneSidesTrigger menuSides, ComponentMenuRedstoneStrength menuStrength, int[] currentPower, boolean high) {
        for (int i = 0; i < currentPower.length; i++) {
            if (menuSides.isSideRequired(i)) {
                if (isRedstonePowered(menuStrength, currentPower[i]) == high) {
                    if (!menuSides.requireAll()) {
                        return true;
                    }
                }else if (menuSides.requireAll()){
                    return false;
                }
            }
        }

        return menuSides.requireAll();
    }


    private boolean hasRedStoneFlipped(FlowComponent component, int[] newPower, int[] oldPower, boolean high) {
        ComponentMenuRedstoneSides menuRedstone = (ComponentMenuRedstoneSides)component.getMenus().get(2);
        ComponentMenuRedstoneStrength menuStrength = (ComponentMenuRedstoneStrength)component.getMenus().get(3);
        for (int i = 0; i < oldPower.length; i++) {
            if (menuRedstone.isSideRequired(i)) {
                if ((high && !isRedstonePowered(menuStrength, oldPower[i]) && isRedstonePowered(menuStrength, newPower[i])) || (!high && isRedstonePowered(menuStrength, oldPower[i]) && !isRedstonePowered(menuStrength, newPower[i]))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isRedstonePowered(ComponentMenuRedstoneStrength menuStrength, int power) {
        boolean inRange = menuStrength.getLow() <= power && power <= menuStrength.getHigh();

        return inRange != menuStrength.isInverted();
    }

    private boolean isPulseReceived(FlowComponent component, int[] newPower, int[] oldPower, boolean high) {
        return hasRedStoneFlipped(component, newPower, oldPower, high) && isTriggerPowered((ComponentMenuRedstoneSidesTrigger)component.getMenus().get(2), (ComponentMenuRedstoneStrength)component.getMenus().get(3), newPower, high);
    }




    public void triggerRedstone(TileEntityInput inputTrigger) {
        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.TRIGGER && item.getConnectionSet() == ConnectionSet.REDSTONE) {
                List<SlotInventoryHolder> receivers = CommandExecutor.getContainers(this, item.getMenus().get(0), ConnectionBlockType.RECEIVER);

                if (receivers != null) {
                    ComponentMenuContainer componentMenuContainer = (ComponentMenuContainer)item.getMenus().get(0);
                    int[] newPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
                    int[] oldPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
                    if (componentMenuContainer.getOption() == 0) {
                        for (SlotInventoryHolder receiver : receivers) {
                           TileEntityInput input = receiver.getReceiver();

                            for (int i = 0; i < newPower.length; i++) {
                                newPower[i] = Math.min(15, newPower[i] + input.getPowered()[i]);
                                oldPower[i] = Math.min(15, oldPower[i] + input.getOldPowered()[i]);
                            }
                        }
                        if (isPulseReceived(item, newPower, oldPower, true)) {
                            activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_HIGH));
                        }
                        if (isPulseReceived(item, newPower, oldPower, false)) {
                            activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_LOW));
                        }
                    }else {
                        TileEntityInput trigger = componentMenuContainer.getOption() == 1 ? inputTrigger : null;
                        if (isPulseReceived(item, receivers, trigger, true)) {
                            activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_HIGH));
                        }

                        if (isPulseReceived(item, receivers, trigger, false)) {
                            activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_LOW));
                        }
                    }

                }
            }
        }
    }

    private boolean isPulseReceived(FlowComponent component,List<SlotInventoryHolder> receivers, TileEntityInput trigger, boolean high) {
        boolean requiresAll = trigger != null;
        for (SlotInventoryHolder receiver : receivers) {
            TileEntityInput input = receiver.getReceiver();


            boolean flag;
            if (input.equals(trigger) || !requiresAll) {
                flag = isPulseReceived(component, input.getPowered(), input.getOldPowered(), high);
            }else{
                flag = isTriggerPowered((ComponentMenuRedstoneSidesTrigger)component.getMenus().get(2), (ComponentMenuRedstoneStrength)component.getMenus().get(3), input.getPowered(), high);
            }

            if (flag) {
                if (!requiresAll) {
                    return true;
                }
            }else if(requiresAll) {
                return false;
            }
        }

        return requiresAll;
    }

    private boolean isTriggerPowered(FlowComponent item, boolean high) {
        List<SlotInventoryHolder> receivers = CommandExecutor.getContainers(this, item.getMenus().get(0), ConnectionBlockType.RECEIVER);

        return receivers != null && isTriggerPowered(receivers, (ComponentMenuContainer)item.getMenus().get(0), (ComponentMenuRedstoneSidesTrigger) item.getMenus().get(2), (ComponentMenuRedstoneStrength) item.getMenus().get(3), high);
    }

    public boolean isTriggerPowered(List<SlotInventoryHolder> receivers, ComponentMenuContainer menuContainer, ComponentMenuRedstoneSidesTrigger menuSides, ComponentMenuRedstoneStrength menuStrength, boolean high) {
        if (menuContainer.getOption() == 0) {
            int[] currentPower =  new int[ForgeDirection.VALID_DIRECTIONS.length];
            for (SlotInventoryHolder receiver : receivers) {
                IRedstoneNode node = receiver.getNode();
                for (int i = 0; i < currentPower.length; i++) {
                    currentPower[i] = Math.min(15, currentPower[i] + node.getPower()[i]);
                }
            }

            return isTriggerPowered(menuSides, menuStrength, currentPower, high);
        }else{
            boolean requiresAll = menuContainer.getOption() == 1;
            for (SlotInventoryHolder receiver : receivers) {
                if (isTriggerPowered(menuSides, menuStrength, receiver.getNode().getPower(), high)) {
                    if (!requiresAll) {
                        return true;
                    }
                }else{
                    if (requiresAll) {
                        return false;
                    }
                }
            }
            return requiresAll;
        }
    }


    public void readGenericData(DataReader dr) {
        if (worldObj.isRemote) {
            if (dr.readBoolean()){
                updateInventories();
            }else{
                removeFlowComponent(dr.readData(DataBitHelper.FLOW_CONTROL_COUNT));
            }
        }else{
            int buttonId = dr.readData(DataBitHelper.GUI_BUTTON_ID);
            if (buttonId >= 0 && buttonId < buttons.size()) {
                buttons.get(buttonId).onClick(dr);
            }
        }
    }



    private TileEntityManager self = this;

    public List<Integer> getRemovedIds() {
        return removedIds;
    }

    @Override
    public Container getContainer(TileEntity te, InventoryPlayer inv) {
        return new ContainerManager((TileEntityManager)te, inv);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(TileEntity te, InventoryPlayer inv) {
        return new GuiManager((TileEntityManager)te, inv);
    }

    @Override
    public void readAllData(DataReader dr, EntityPlayer player) {
        updateInventories();
        int flowControlCount = dr.readData(DataBitHelper.FLOW_CONTROL_COUNT);
        getFlowItems().clear();
        getZLevelRenderingList().clear();
        for (int i = 0; i < flowControlCount; i++) {
            readAllComponentData(dr);
        }
    }

    private void readAllComponentData(DataReader dr) {
        int x = dr.readData(DataBitHelper.FLOW_CONTROL_X);
        int y = dr.readData(DataBitHelper.FLOW_CONTROL_Y);
        int id = dr.readData(DataBitHelper.FLOW_CONTROL_TYPE_ID);

        FlowComponent flowComponent = new FlowComponent(this, x, y, ComponentType.getTypeFromId(id));
        flowComponent.setComponentName(dr.readString(DataBitHelper.NAME_LENGTH));

        for (ComponentMenu menu : flowComponent.getMenus()) {
            menu.readData(dr);
        }

        flowComponent.clearConnections();
        for (int i = 0; i < flowComponent.getConnectionSet().getConnections().length; i++) {
            boolean hasConnection = dr.readBoolean();

            if (hasConnection) {
                Connection connection = new Connection(dr.readData(DataBitHelper.FLOW_CONTROL_COUNT), dr.readData(DataBitHelper.CONNECTION_ID));
                flowComponent.setConnection(i, connection);


                int length = dr.readData(DataBitHelper.NODE_ID);
                for (int j = 0; j < length; j++) {
                    connection.getNodes().add(new Point(dr.readData(DataBitHelper.FLOW_CONTROL_X), dr.readData(DataBitHelper.FLOW_CONTROL_Y)));
                }
            }
        }


        getFlowItems().add(flowComponent);
        getZLevelRenderingList().add(0, flowComponent);

        updateVariables();
    }

    @Override
    public void readUpdatedData(DataReader dr, EntityPlayer player) {
        boolean isNew = worldObj.isRemote && dr.readBoolean();
        if (isNew) {
            readAllComponentData(dr);
        }else{
            boolean isSpecificComponent = dr.readBoolean();
            if (isSpecificComponent) {

                IComponentNetworkReader nr = getNetworkReaderForComponentPacket(dr, this);

                if (nr != null) {
                    nr.readNetworkComponent(dr);
                }
            }else{
                readGenericData(dr);
            }
        }
    }

    @Override
    public void writeAllData(DataWriter dw) {
        dw.writeData(getFlowItems().size(), DataBitHelper.FLOW_CONTROL_COUNT);
        for (FlowComponent flowComponent : getFlowItems()) {
            PacketHandler.writeAllComponentData(dw, flowComponent);
        }
    }

    private IComponentNetworkReader getNetworkReaderForComponentPacket(DataReader dr, TileEntityManager jam) {
        int componentId = dr.readData(DataBitHelper.FLOW_CONTROL_COUNT);
        if (componentId >= 0 && componentId < jam.getFlowItems().size()) {
            FlowComponent component = jam.getFlowItems().get(componentId);

            if (dr.readBoolean()) {
                int menuId = dr.readData(DataBitHelper.FLOW_CONTROL_MENU_COUNT);
                if (menuId >= 0 && menuId < component.getMenus().size()) {
                    return component.getMenus().get(menuId);
                }
            }else{
                return component;
            }
        }

        return null;
    }

    public Variable[] getVariables() {
        return variables;
    }

    public void updateVariables() {
        for (Variable variable : variables) {
            variable.setDeclaration(false);
        }

        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.VARIABLE && item.getConnectionSet() == ConnectionSet.EMPTY) {
                int selectedVariable = ((ComponentMenuVariable)item.getMenus().get(0)).getSelectedVariable();
                variables[selectedVariable].setDeclaration(true);
            }
        }
    }

    public abstract class Button {
        private int x;
        private int y;
        private String mouseOver;

        protected Button(String mouseOver) {
            this.x = 5;
            this.y = 5 + buttons.size() * 18;
            this.mouseOver = mouseOver;
        }

        protected abstract void onClick(DataReader dr);
        public abstract void onClick(DataWriter dw);

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public String getMouseOver() {
            return mouseOver;
        }

        public boolean activateOnRelease() {
            return false;
        }
    }

    private class ButtonCreate extends Button {

        private ComponentType type;

        protected ButtonCreate(ComponentType type) {
            super("Create " + type.getLongName());

            this.type = type;
        }

        @Override
        protected void onClick(DataReader dr) {
            if (getFlowItems().size() < MAX_COMPONENT_AMOUNT) {
                getFlowItems().add(new FlowComponent(self, 50, 50, type));
            }
        }

        @Override
        public void onClick(DataWriter dw) {

        }

        @Override
        public String getMouseOver() {
            if (getFlowItems().size() == MAX_COMPONENT_AMOUNT) {
                return "Maximum component amount reached";
            }else{
                return super.getMouseOver();
            }
        }
    }


    private static final String NBT_TIMER = "Timer";
    private static final String NBT_COMPONENTS = "Components";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        readContentFromNBT(nbtTagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        writeContentToNBT(nbtTagCompound);
    }

    public void readContentFromNBT(NBTTagCompound nbtTagCompound) {
        int version =  nbtTagCompound.getByte(Blocks.NBT_PROTOCOL_VERSION);


        timer = nbtTagCompound.getByte(NBT_TIMER);

        NBTTagList components = nbtTagCompound.getTagList(NBT_COMPONENTS);
        for (int i = 0; i < components.tagCount(); i++) {
            NBTTagCompound component = (NBTTagCompound)components.tagAt(i);

            items.add(FlowComponent.readFromNBT(this, component, version));
        }
    }

    public void writeContentToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setByte(Blocks.NBT_PROTOCOL_VERSION, Blocks.NBT_CURRENT_PROTOCOL_VERSION);

        nbtTagCompound.setByte(NBT_TIMER, (byte)timer);

        NBTTagList components = new NBTTagList();
        for (FlowComponent item : items) {
            NBTTagCompound component = new NBTTagCompound();
            item.writeToNBT(component);
            components.appendTag(component);
        }
        nbtTagCompound.setTag(NBT_COMPONENTS, components);

    }

}
