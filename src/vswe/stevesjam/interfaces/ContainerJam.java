package vswe.stevesjam.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.components.FlowComponent;
import vswe.stevesjam.network.PacketHandler;

import java.util.ArrayList;
import java.util.List;


public class ContainerJam extends Container {

    private TileEntityJam jam;

    public ContainerJam(TileEntityJam jam, InventoryPlayer player) {
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
            for (int i = 0; i < jam.getFlowItems().size(); i++) {
                oldComponents.get(i).refreshData(this, jam.getFlowItems().get(i));
            }
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);

        PacketHandler.sendAllData(this, player, jam);
        oldComponents = new ArrayList<>();
        for (FlowComponent component : jam.getFlowItems()) {
            oldComponents.add(component.copy());
        }
    }

    public TileEntityJam getJam() {
        return jam;
    }

    private List<FlowComponent> oldComponents;
    public List<ICrafting> getCrafters() {
        return crafters;
    }
}
