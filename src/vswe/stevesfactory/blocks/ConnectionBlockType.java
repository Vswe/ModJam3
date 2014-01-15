package vswe.stevesfactory.blocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

public enum ConnectionBlockType {
    INVENTORY(IInventory.class),
    TANK(IFluidHandler.class),
    EMITTER(TileEntityOutput.class),
    RECEIVER(TileEntityInput.class),
    NODE(IRedstoneNode.class);

    private Class clazz;

    private ConnectionBlockType(Class clazz) {
        this.clazz = clazz;
    }

    public boolean isInstance(TileEntity tileEntity) {
        return clazz.isInstance(tileEntity);
    }

    public <T> T getObject(TileEntity tileEntity) {
        return (T)tileEntity;
    }
}
