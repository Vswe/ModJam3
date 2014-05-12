package vswe.stevesfactory.blocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;
import vswe.stevesfactory.Localization;

public enum ConnectionBlockType {
    INVENTORY(Localization.TYPE_INVENTORY, IInventory.class, false),
    TANK(Localization.TYPE_TANK, IFluidHandler.class, false),
    EMITTER(Localization.TYPE_EMITTER, TileEntityOutput.class, false),
    RECEIVER(Localization.TYPE_RECEIVER, TileEntityInput.class, false),
    NODE(Localization.TYPE_NODE, IRedstoneNode.class, true),
    BUD(Localization.TYPE_BUD, TileEntityBUD.class, false),
    CAMOUFLAGE(Localization.TYPE_CAMOUFLAGE, TileEntityCamouflage.class, false),
    SIGN(Localization.TYPE_SIGN, TileEntitySignUpdater.class, false);

    private Localization name;
    private Class clazz;
    private boolean group;

    private ConnectionBlockType(Localization name, Class clazz, boolean group) {
        this.name = name;
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

    public Localization getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}
