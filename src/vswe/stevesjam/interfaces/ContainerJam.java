package vswe.stevesjam.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import vswe.stevesjam.blocks.TileEntityJam;


public class ContainerJam extends Container {

    private TileEntityJam jam;

    public ContainerJam(TileEntityJam jam, InventoryPlayer player) {
        this.jam = jam;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return entityplayer.getDistanceSq(jam.xCoord, jam.yCoord, jam.zCoord) <= 64;
    }

}
