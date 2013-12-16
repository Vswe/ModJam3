package vswe.stevesjam.blocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesjam.components.*;

import java.util.*;


public class TileEntityJam extends TileEntity {

    private List<FlowComponent> items;
    private Connection currentlyConnecting;
    private boolean isPowered;

    public TileEntityJam() {
        items = new ArrayList<>();

    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);

        items.add(new FlowComponent(this, 30, 30, ComponentType.OUTPUT));
        items.add(new FlowComponent(this, 200, 30, ComponentType.INPUT));
        items.add(new FlowComponent(this, 200, 80, ComponentType.TRIGGER));
        items.add(new FlowComponent(this, 330, 30, ComponentType.INPUT));
        items.add(new FlowComponent(this, 400, 30, ComponentType.OUTPUT));
        items.add(new FlowComponent(this, 100, 30, ComponentType.INPUT));
        items.add(new FlowComponent(this, 100, 80, ComponentType.INPUT));
        items.add(new FlowComponent(this, 400, 80, ComponentType.TRIGGER));
    }

    public List<FlowComponent> getFlowItems() {
        return items;
    }

    List<TileEntity> inventories = new ArrayList<>();
    public List<TileEntity> getConnectedInventories() {
        return inventories;
    }

    public static final int MAX_CABLE_LENGTH = 64;

    public void updateInventories() {
        WorldCoordinate[] oldCoordinates = new WorldCoordinate[inventories.size()];
        for (int i = 0; i < oldCoordinates.length; i++) {
            TileEntity inventory = inventories.get(i);
            oldCoordinates[i] = new WorldCoordinate(inventory.xCoord, inventory.yCoord, inventory.zCoord);
        }

        List<WorldCoordinate> visited = new ArrayList<>();
        inventories.clear();
        Queue<WorldCoordinate> queue = new PriorityQueue<>();
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
                                    if (isPowered) {
                                        valid.add(ConnectionOption.REDSTONE_HIGH);
                                    }else{
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


    public void triggerRedstone(boolean powered) {
        if (powered && !this.isPowered) {
            activateTriggers(EnumSet.of(ConnectionOption.REDSTONE_PULSE_HIGH));
        }else if (!powered && this.isPowered) {
            activateTriggers(EnumSet.of(ConnectionOption.REDSTONE_PULSE_LOW));
        }


        this.isPowered = powered;
    }
}
