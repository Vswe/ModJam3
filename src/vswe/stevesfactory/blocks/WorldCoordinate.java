package vswe.stevesfactory.blocks;


import net.minecraft.tileentity.TileEntity;

public class WorldCoordinate implements Comparable<WorldCoordinate> {
    private int x, y, z, depth;
    private TileEntity tileEntity;

    public WorldCoordinate(int x, int y, int z) {
        this(x, y, z, 0);
    }

    public WorldCoordinate(int x, int y, int z, int depth) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorldCoordinate that = (WorldCoordinate) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public int compareTo(WorldCoordinate o) {
        return ((Integer)depth).compareTo(o.depth);
    }

    public void setTileEntity(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }
}
