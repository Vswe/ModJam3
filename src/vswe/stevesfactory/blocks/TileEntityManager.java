package vswe.stevesfactory.blocks;

import net.minecraft.inventory.IInventory;
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

    List<TileEntity> inventories = new ArrayList<TileEntity>();
    public List<TileEntity> getConnectedInventories() {
        return inventories;
    }

    public static final int MAX_CABLE_LENGTH = 64;
    public static final int MAX_COMPONENT_AMOUNT = 127;

    public void updateInventories() {
        WorldCoordinate[] oldCoordinates = new WorldCoordinate[inventories.size()];
        for (int i = 0; i < oldCoordinates.length; i++) {
            TileEntity inventory = inventories.get(i);
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

                            if (!visited.contains(target)) {
                                visited.add(target);
                                TileEntity tileEntity = worldObj.getBlockTileEntity(target.getX(), target.getY(), target.getZ());
                                if (tileEntity != null && tileEntity instanceof IInventory) {
                                    inventories.add(tileEntity);
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

        if (!worldObj.isRemote) {
            updateInventorySelection(oldCoordinates);
        }
    }

    private void updateInventorySelection(WorldCoordinate[] oldCoordinates) {
        for (FlowComponent item : items) {
            for (ComponentMenu menu : item.getMenus()) {
                if (menu instanceof ComponentMenuInventory) {
                    ComponentMenuInventory menuInventory = (ComponentMenuInventory)menu;

                    if (menuInventory.getSelectedInventory() >= 0 && menuInventory.getSelectedInventory() < oldCoordinates.length) {
                        WorldCoordinate coordinate = oldCoordinates[menuInventory.getSelectedInventory()];

                        boolean foundInventory = false;
                        for (int i = 0; i < inventories.size(); i++) {
                            TileEntity inventory = inventories.get(i);
                            if (coordinate.getX() == inventory.xCoord && coordinate.getY() == inventory.yCoord && coordinate.getZ() == inventory.zCoord) {
                                foundInventory = true;
                                menuInventory.setSelectedInventory(i);
                                break;
                            }
                        }

                        if (!foundInventory) {
                            menuInventory.setSelectedInventory(-1);
                        }

                    }
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
        for (TileEntity inventory : inventories) {
            if (inventory.isInvalid()) {
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
            super("Create " + type.toString().charAt(0) + type.toString().toLowerCase().substring(1));

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

    private static final byte NBT_CURRENT_PROTOCOL_VERSION = 1;
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
