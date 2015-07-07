package vswe.stevesfactory.blocks;


import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public abstract class BlockCamouflageBase extends BlockContainer {

    protected BlockCamouflageBase(Material material) {
        super(material);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        if (!setBlockCollisionBoundsBasedOnState(world, pos)) {
            setBlockBounds(0, 0, 0, 0, 0, 0);
        }

        return super.getSelectedBoundingBox(world, pos);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        if (!setBlockCollisionBoundsBasedOnState(world, pos)) {
            return null;
        }

        return super.getCollisionBoundingBox(world, pos, state);
    }

    private boolean setBlockCollisionBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        setBlockBoundsBasedOnState(world, pos);

        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, pos);
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
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, pos);

        return camouflage == null || camouflage.isNormalBlock();
    }


    @Override
    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
        if (!setBlockCollisionBoundsBasedOnState(world, pos)) {
            setBlockBounds(0, 0, 0, 0, 0, 0);
        }

        return super.collisionRayTrace(world, pos, start, end);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer){
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, worldObj, new BlockPos(target.getBlockPos().getX(), target.getBlockPos().getY(), target.getBlockPos().getZ()));
        if (camouflage != null) {
            if (camouflage.addBlockEffect(this, target.sideHit, effectRenderer)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, pos);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape()) {
            camouflage.setBlockBounds(this);
        }else{
            setBlockBoundsForItemRender();
        }
    }

    @Override
    public float getBlockHardness(World world, BlockPos pos) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, pos);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape() && !camouflage.isUseCollision()) {
            return 600000;
        }
        return super.getBlockHardness(world, pos);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

}
