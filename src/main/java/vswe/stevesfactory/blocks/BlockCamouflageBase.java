package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;


public abstract class BlockCamouflageBase extends BlockContainer {

    protected BlockCamouflageBase(Material material) {
        super(material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        if (!setBlockCollisionBoundsBasedOnState(world, x, y, z)) {
            setBlockBounds(0, 0, 0, 0, 0, 0);
        }

        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (!setBlockCollisionBoundsBasedOnState(world, x, y, z)) {
            return null;
        }


        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    private boolean setBlockCollisionBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);

        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape()) {
            if (!camouflage.isUseCollision()) {
                return false;
            }else if(camouflage.isFullCollision()) {
                setBlockBoundsForItemRender();
            }
        }

        return true;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);

        return camouflage == null || camouflage.isNormalBlock();
    }


    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        if (!setBlockCollisionBoundsBasedOnState(world, x, y, z)) {
            setBlockBounds(0, 0, 0, 0, 0, 0);
        }

        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer){
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, worldObj, target.blockX, target.blockY, target.blockZ);
        if (camouflage != null) {
            if (camouflage.addBlockEffect(this, target.sideHit, effectRenderer)) {
                return true;
            }
        }
        return false;
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
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape() && !camouflage.isUseCollision()) {
            return 600000;
        }
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public int getRenderType() {
        return ModBlocks.CAMOUFLAGE_RENDER_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public final IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityCamouflage te = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);

        if (te != null) {
            IIcon icon = te.getIconWithDefault(world, x, y, z, this, side, false);

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
    protected abstract IIcon getDefaultIcon(int side, int blockMeta, int camoMeta);
}
