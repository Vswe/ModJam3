package vswe.stevesfactory.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.network.PacketHandler;

import java.util.ArrayList;
import java.util.List;


public class ContainerManager extends Container {

    private TileEntityManager jam;

    public ContainerManager(TileEntityManager jam, InventoryPlayer player) {
        this.jam = jam;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return entityplayer.getDistanceSq(jam.xCoord, jam.yCoord, jam.zCoord) <= 64;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if (oldComponents != null) {
            if (oldIdIndexToRemove < jam.getRemovedIds().size()) {
                int idToRemove = jam.getRemovedIds().get(oldIdIndexToRemove);
                oldIdIndexToRemove++;
                jam.removeFlowComponent(idToRemove, oldComponents);
                PacketHandler.sendRemovalPacket(this, idToRemove);
            }


            for (int i = 0; i < jam.getFlowItems().size(); i++) {
                if (i >= oldComponents.size()) {
                    PacketHandler.sendNewFlowComponent(this, jam.getFlowItems().get(i));
                    oldComponents.add(jam.getFlowItems().get(i).copy());
                }else{
                    oldComponents.get(i).refreshData(this, jam.getFlowItems().get(i));
                }
            }

            if (oldInventoryLength != jam.getConnectedInventories().size()) {
                oldInventoryLength = jam.getConnectedInventories().size();
                PacketHandler.sendUpdateInventoryPacket(this);
            }
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);

        PacketHandler.sendAllData(this, player, jam);
        oldComponents = new ArrayList<FlowComponent>();
        for (FlowComponent component : jam.getFlowItems()) {
            oldComponents.add(component.copy());
        }
        jam.updateInventories();
        oldInventoryLength = jam.getConnectedInventories().size();
        oldIdIndexToRemove = jam.getRemovedIds().size();
    }

    public TileEntityManager getManager() {
        return jam;
    }

    private List<FlowComponent> oldComponents;
    private int oldInventoryLength;
    private int oldIdIndexToRemove;

    public List<ICrafting> getCrafters() {
        return crafters;
    }
}
