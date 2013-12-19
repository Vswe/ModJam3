package vswe.stevesfactory.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.blocks.WorldCoordinate;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.network.PacketHandler;

import java.util.ArrayList;
import java.util.List;


public class ContainerManager extends Container {

    private TileEntityManager manager;

    public ContainerManager(TileEntityManager manager, InventoryPlayer player) {
        this.manager = manager;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return entityplayer.getDistanceSq(manager.xCoord, manager.yCoord, manager.zCoord) <= 64;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (oldComponents != null) {
            if (oldIdIndexToRemove < manager.getRemovedIds().size()) {
                int idToRemove = manager.getRemovedIds().get(oldIdIndexToRemove);
                oldIdIndexToRemove++;
                manager.removeFlowComponent(idToRemove, oldComponents);
                PacketHandler.sendRemovalPacket(this, idToRemove);
            }


            for (int i = 0; i < manager.getFlowItems().size(); i++) {
                if (i >= oldComponents.size()) {
                    PacketHandler.sendNewFlowComponent(this, manager.getFlowItems().get(i));
                    oldComponents.add(manager.getFlowItems().get(i).copy());
                }else{
                    oldComponents.get(i).refreshData(this, manager.getFlowItems().get(i));
                }
            }

            boolean hasInventoriesChanged = oldInventories.size() != manager.getConnectedInventories().size();

            if (!hasInventoriesChanged) {
                for (int i = 0; i < oldInventories.size(); i++) {
                    TileEntity tileEntity = manager.getConnectedInventories().get(i);
                    if (oldInventories.get(i).equals(new WorldCoordinate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord))) {
                        hasInventoriesChanged = true;
                        break;
                    }
                }
            }



            if (hasInventoriesChanged) {
                oldInventories.clear();
                for (TileEntity tileEntity : manager.getConnectedInventories()) {
                    oldInventories.add(new WorldCoordinate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
                }
                PacketHandler.sendUpdateInventoryPacket(this);
            }
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);

        PacketHandler.sendAllData(this, player, manager);
        oldComponents = new ArrayList<FlowComponent>();
        for (FlowComponent component : manager.getFlowItems()) {
            oldComponents.add(component.copy());
        }
        manager.updateInventories();
        oldInventories = new ArrayList<WorldCoordinate>();
        for (TileEntity tileEntity : manager.getConnectedInventories()) {
            oldInventories.add(new WorldCoordinate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord));
        }
        oldIdIndexToRemove = manager.getRemovedIds().size();
    }

    public TileEntityManager getManager() {
        return manager;
    }

    private List<FlowComponent> oldComponents;
    private List<WorldCoordinate> oldInventories;
    private int oldIdIndexToRemove;

    public List<ICrafting> getCrafters() {
        return crafters;
    }
}
