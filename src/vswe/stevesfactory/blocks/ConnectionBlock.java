package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.TileEntity;

import java.util.EnumSet;

public class ConnectionBlock {

    private TileEntity tileEntity;
    private EnumSet<ConnectionBlockType> types;

    public ConnectionBlock(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
        types = EnumSet.noneOf(ConnectionBlockType.class);
    }

    public void addType(ConnectionBlockType type) {
        types.add(type);
    }

    public boolean isOfType(ConnectionBlockType type) {
        return types.contains(type);
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }
}
