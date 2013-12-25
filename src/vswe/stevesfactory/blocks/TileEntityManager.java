package vswe.stevesfactory.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.components.*;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.*;


public class TileEntityManager extends TileEntity {

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
    private boolean[] isPowered;
    public List<Button> buttons;
    public boolean justSentServerComponentRemovalPacket;
    private List<FlowComponent> zLevelRenderingList;

    public TileEntityManager() {
        items = new ArrayList<FlowComponent>();
        zLevelRenderingList = new ArrayList<FlowComponent>();
        buttons = new ArrayList<Button>();
        removedIds = new ArrayList<Integer>();
        isPowered = new boolean[ForgeDirection.VALID_DIRECTIONS.length];

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

    public void updateInventories() {
        WorldCoordinate[] oldCoordinates = new WorldCoordinate[inventories.size()];
        for (int i = 0; i < oldCoordinates.length; i++) {
            TileEntity inventory = inventories.get(i).getTileEntity();
            oldCoordinates[i] = new WorldCoordinate(inventory.xCoord, inventory.yCoord, inventory.zCoord);
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
                                    inventories.add(connection);
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

        if (!worldObj.isRemote && !firstInventoryUpdate) {
            updateInventorySelection(oldCoordinates);
        }

        firstInventoryUpdate = false;
    }

    //TODO make sure this works properly with multiple types of "inventories"
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
        for (ConnectionBlock inventory : inventories) {
            if (inventory.getTileEntity().isInvalid()) {
                updateInventories();
                break;
            }
        }

        new CommandExecutor(this).executeTriggerCommand(component, validTriggerOutputs);
    }
    private boolean isTriggerPowered(FlowComponent component, boolean high) {
        return isTriggerPowered(component, isPowered, high);
    }
    private boolean isTriggerPowered(FlowComponent component, boolean[] currentPower, boolean high) {
        ComponentMenuRedstone menuRedstone = (ComponentMenuRedstone)component.getMenus().get(1);
        for (int i = 0; i < currentPower.length; i++) {
            if (menuRedstone.isSideRequired(i)) {
                if (currentPower[i] == high) {
                    if (!menuRedstone.requireAll()) {
                        return true;
                    }
                }else if (menuRedstone.requireAll()){
                    return false;
                }
            }
        }

        return menuRedstone.requireAll();
    }


    private boolean hasRedStoneFlipped(FlowComponent component, boolean[] newPower, boolean high) {
        ComponentMenuRedstone menuRedstone = (ComponentMenuRedstone)component.getMenus().get(1);
        for (int i = 0; i < isPowered.length; i++) {
            if (menuRedstone.isSideRequired(i)) {
                if ((high && !isPowered[i] && newPower[i]) || (!high && isPowered[i] && !newPower[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isPulseReceived(FlowComponent component, boolean[] newPower, boolean high) {
        return hasRedStoneFlipped(component, newPower, high) && isTriggerPowered(component, newPower, high);
    }

    public void triggerRedstone() {
        boolean[] powered = new boolean[isPowered.length];
        for (int i = 0; i < powered.length; i++) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
            powered[i] = worldObj.getIndirectPowerLevelTo(direction.offsetX + this.xCoord, direction.offsetY + this.yCoord, direction.offsetZ + this.zCoord, direction.getOpposite().ordinal()) > 0;
        }

        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.TRIGGER) {
                if (isPulseReceived(item, powered, true)) {
                    activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_HIGH));
                }
                if (isPulseReceived(item, powered, false)) {
                    activateTrigger(item, EnumSet.of(ConnectionOption.REDSTONE_PULSE_LOW));
                }
            }
        }

        for (int i = 0; i < isPowered.length; i++) {
            this.isPowered[i] = powered[i];
        }
        this.isPowered = powered;
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
            super("Create " + type.toString());

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

    private static final byte NBT_CURRENT_PROTOCOL_VERSION = 3;
    private static final String NBT_PROTOCOL_VERSION = "ProtocolVersion";
    private static final String NBT_POWERED = "IsPowered";
    private static final String NBT_TIMER = "Timer";
    private static final String NBT_COMPONENTS = "Components";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        int version =  nbtTagCompound.getByte(NBT_PROTOCOL_VERSION);

        //there used to be just one redstone detection, not specific sides
        if (version < 1) {
            boolean powered = nbtTagCompound.getBoolean(NBT_POWERED);
            for (int i = 0; i < isPowered.length; i++) {
                isPowered[i] = powered;
            }
        }else{
            byte powered = nbtTagCompound.getByte(NBT_POWERED);
            for (int i = 0; i < isPowered.length; i++) {
                isPowered[i] = (powered & (1 << i)) != 0;
            }
        }
        timer = nbtTagCompound.getByte(NBT_TIMER);

        NBTTagList components = nbtTagCompound.getTagList(NBT_COMPONENTS);
        for (int i = 0; i < components.tagCount(); i++) {
            NBTTagCompound component = (NBTTagCompound)components.tagAt(i);

            items.add(FlowComponent.readFromNBT(this, component, version));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setByte(NBT_PROTOCOL_VERSION, NBT_CURRENT_PROTOCOL_VERSION);

        byte powered = 0;
        for (int i = 0; i < isPowered.length; i++) {
            if (isPowered[i]) {
                powered |= 1 << i;
            }
        }
        nbtTagCompound.setByte(NBT_POWERED, powered);

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
