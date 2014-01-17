package vswe.stevesfactory.interfaces;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import vswe.stevesfactory.blocks.TileEntityInterface;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.blocks.TileEntityRelay;

import java.util.List;


public abstract class ContainerBase extends Container {
    private TileEntityInterface te;
    private InventoryPlayer player;


    protected ContainerBase(TileEntityInterface te, InventoryPlayer player) {
        this.te = te;
        this.player = player;
    }

    public TileEntityInterface getTileEntity() {
        return te;
    }

    public List<ICrafting> getCrafters() {
        return crafters;
    }
}
