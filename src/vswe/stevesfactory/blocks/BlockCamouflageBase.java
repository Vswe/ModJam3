package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


public abstract class BlockCamouflageBase extends BlockContainer {

    protected BlockCamouflageBase(int id, Material material) {
        super(id, material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);

        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape()) {
            if (!camouflage.isUseCollision()) {
                return null;
            }else if(camouflage.isFullCollision()) {
                setBlockBoundsForItemRender();
            }
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }


    @Override
    public boolean isBlockNormalCube(World world, int x, int y, int z) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);

        return camouflage == null || camouflage.isNormalBlock();
    }


    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape()) {
            camouflage.setBlockBounds(this);
        }else{
            setBlockBoundsForItemRender();
        }
    }



    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public int getRenderType() {
        return Blocks.CAMOUFLAGE_RENDER_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public final Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityCamouflage te = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);

        if (te != null) {
            Icon icon = te.getIconWithDefault(world, x, y, z, this, side, false);

            if (icon != null) {
                return icon;
            }
        }

        return getDefaultIcon(side, world.getBlockMetadata(x, y, z), 0);
    }


    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    protected abstract Icon getDefaultIcon(int side, int blockMeta, int camoMeta);
}
