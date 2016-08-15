package vswe.stevesfactory.interfaces;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import vswe.stevesfactory.blocks.ITileEntityInterface;

import java.util.List;

public abstract class ContainerBase extends Container {
    private ITileEntityInterface te;
    private InventoryPlayer player;


    protected ContainerBase(ITileEntityInterface te, InventoryPlayer player) {
        this.te = te;
        this.player = player;
    }

    public ITileEntityInterface getTileEntity() {
        return te;
    }

    public List<IContainerListener> getCrafters() {
        return listeners;
    }
}
