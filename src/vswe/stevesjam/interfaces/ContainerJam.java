package vswe.stevesjam.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.network.PacketHandler;


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


    }

    @Override
    public void addCraftingToCrafters(ICrafting player) {
        super.addCraftingToCrafters(player);

        PacketHandler.sendAllData(this, player, jam);
    }

    public TileEntityJam getJam() {
        return jam;
    }
}
