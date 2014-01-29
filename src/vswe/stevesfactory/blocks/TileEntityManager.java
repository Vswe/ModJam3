package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.components.*;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.*;

import java.util.*;


public class TileEntityManager extends TileEntityInterface {
    public static final TriggerHelperRedstone redstoneTrigger = new TriggerHelperRedstone(3, 4);
    public static final TriggerHelperRedstone redstoneCondition = new TriggerHelperRedstone(1, 2);
    public static final TriggerHelperBUD budTrigger = new TriggerHelperBUD();

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
                    menuInventory.setSelectedInventories(getNewSelection(oldCoordinates, oldSelection, true));
                }
            }
        }

        for (Variable variable : variables) {
            variable.setContainers(getNewSelection(oldCoordinates, variable.getContainers(), false));
        }
    }

    private List<Integer> getNewSelection(WorldCoordinate[] oldCoordinates, List<Integer> oldSelection, boolean hasVariables)  {

        List<Integer> newSelection = new ArrayList<Integer>();

        for (int i = 0; i < oldSelection.size(); i++) {
            int selection = oldSelection.get(i);
            if (hasVariables && selection >= 0 && selection < 16) {
                newSelection.add(selection);
            }else{
                if (hasVariables) {
                    selection -=  variables.length;
                }

                if (selection >= 0 && selection < oldCoordinates.length) {
                    WorldCoordinate coordinate = oldCoordinates[selection];

                    for (int j = 0; j < inventories.size(); j++) {
                        TileEntity inventory = inventories.get(j).getTileEntity();
                        if (coordinate.getX() == inventory.xCoord && coordinate.getY() == inventory.yCoord && coordinate.getZ() == inventory.zCoord) {
                            int id = j + (hasVariables ? variables.length : 0);
                            if (!newSelection.contains(id)) {
                                newSelection.add(id);
                            }

                            break;
                        }
                    }

                }
            }
        }

        return newSelection;
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
                        ComponentMenuInterval componentMenuInterval = (ComponentMenuInterval)item.getMenus().get(TriggerHelper.TRIGGER_INTERVAL_ID);
                        int interval = componentMenuInterval.getInterval();
                        if (interval == 0) {
                            continue;
                        }
                        item.setCurrentInterval(item.getCurrentInterval() + 1);
                        if (item.getCurrentInterval() >= interval) {
                            item.setCurrentInterval(0);

                            EnumSet<ConnectionOption> valid = EnumSet.of(ConnectionOption.INTERVAL);
                            if (item.getConnectionSet() == ConnectionSet.REDSTONE) {
                                redstoneTrigger.onTrigger(item, valid);
                            }else if(item.getConnectionSet() == ConnectionSet.BUD) {
                                budTrigger.onTrigger(item, valid);
                            }
                            activateTrigger(item, valid);
                        }
                    }
                }


            }else{
                timer++;
            }
        }
    }

    public void activateTrigger(FlowComponent component, EnumSet<ConnectionOption> validTriggerOutputs) {
        if (firstCommandExecution) {
            updateInventories();
            updateVariables();
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






    public void triggerRedstone(TileEntityInput inputTrigger) {
        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.TRIGGER && item.getConnectionSet() == ConnectionSet.REDSTONE) {
                redstoneTrigger.onRedstoneTrigger(item, inputTrigger);
            }
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
            variable.setDeclaration(null);
        }

        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.VARIABLE && item.getConnectionSet() == ConnectionSet.EMPTY) {
                int selectedVariable = ((ComponentMenuVariable)item.getMenus().get(0)).getSelectedVariable();
                variables[selectedVariable].setDeclaration(item);
            }
        }
    }

    public void triggerBUD(TileEntityBUD tileEntityBUD) {
        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.TRIGGER && item.getConnectionSet() == ConnectionSet.BUD) {
                budTrigger.triggerBUD(item, tileEntityBUD);
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
    private static final String NBT_VARIABLES = "Variables";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        readContentFromNBT(nbtTagCompound, false);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        writeContentToNBT(nbtTagCompound, false);
    }

    public void readContentFromNBT(NBTTagCompound nbtTagCompound, boolean pickup) {
        int version =  nbtTagCompound.getByte(Blocks.NBT_PROTOCOL_VERSION);


        timer = nbtTagCompound.getByte(NBT_TIMER);

        NBTTagList components = nbtTagCompound.getTagList(NBT_COMPONENTS);
        for (int i = 0; i < components.tagCount(); i++) {
            NBTTagCompound component = (NBTTagCompound)components.tagAt(i);

            items.add(FlowComponent.readFromNBT(this, component, version, pickup));
        }

        NBTTagList variablesTag = nbtTagCompound.getTagList(NBT_VARIABLES);
        for (int i = 0; i < variablesTag.tagCount(); i++) {
            NBTTagCompound variableTag = (NBTTagCompound)variablesTag.tagAt(i);
            variables[i].readFromNBT(variableTag);
        }

    }

    public void writeContentToNBT(NBTTagCompound nbtTagCompound, boolean pickup) {
        nbtTagCompound.setByte(Blocks.NBT_PROTOCOL_VERSION, Blocks.NBT_CURRENT_PROTOCOL_VERSION);

        nbtTagCompound.setByte(NBT_TIMER, (byte)timer);

        NBTTagList components = new NBTTagList();
        for (FlowComponent item : items) {
            NBTTagCompound component = new NBTTagCompound();
            item.writeToNBT(component, pickup);
            components.appendTag(component);
        }
        nbtTagCompound.setTag(NBT_COMPONENTS, components);


        NBTTagList variablesTag = new NBTTagList();
        for (Variable variable : variables) {
            NBTTagCompound variableTag = new NBTTagCompound();
            variable.writeToNBT(variableTag);
            variablesTag.appendTag(variableTag);
        }
        nbtTagCompound.setTag(NBT_VARIABLES, variablesTag);
    }

}
