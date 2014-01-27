package vswe.stevesfactory.blocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

public enum ConnectionBlockType {
    INVENTORY(IInventory.class, false),
    TANK(IFluidHandler.class, false),
    EMITTER(TileEntityOutput.class, false),
    RECEIVER(TileEntityInput.class, false),
    NODE(IRedstoneNode.class, true),
    BUD(TileEntityBUD.class, false);

    private Class clazz;
    private boolean group;

    private ConnectionBlockType(Class clazz, boolean group) {
        this.clazz = clazz;
        this.group = group;
    }

    public boolean isInstance(TileEntity tileEntity) {
        return clazz.isInstance(tileEntity);
    }

    public <T> T getObject(TileEntity tileEntity) {
        return (T)tileEntity;
    }

    public boolean isGroup() {
        return group;
    }


    @Override
    public String toString() {
        return super.toString().charAt(0) + super.toString().substring(1).toLowerCase();
    }
}
