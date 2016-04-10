package vswe.stevesfactory.blocks;


import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public abstract class BlockCamouflageBase extends BlockContainer {

    protected BlockCamouflageBase(Material material) {
        super(material);
    }
    public static final AxisAlignedBB NO_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return getBlockBoundsBasedOnState(state, source, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        if (!setBlockCollisionBoundsBasedOnState(state, world, pos)) {
            return NO_BLOCK_AABB;
        }

        return super.getSelectedBoundingBox(state, world, pos);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        if (!setBlockCollisionBoundsBasedOnState(state, world, pos)) {
            return NO_BLOCK_AABB;
        }

        return super.getCollisionBoundingBox(state, world, pos);
    }

    private boolean setBlockCollisionBoundsBasedOnState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, pos);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape()) {
            if (!camouflage.isUseCollision()) {
                return false;
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
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        if (!setBlockCollisionBoundsBasedOnState(state, world, pos)) {
            return rayTrace(pos, start, end, NO_BLOCK_AABB);
        }

        return super.collisionRayTrace(state, world, pos, start, end);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, EffectRenderer effectRenderer){
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, target.getBlockPos());
        if (camouflage != null) {
            if (camouflage.addBlockEffect(this, state, world, target.sideHit, effectRenderer)) {
                return true;
            }
        }
        return false;
    }

    public AxisAlignedBB getBlockBoundsBasedOnState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, pos);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape()) {
            return camouflage.getBlockBounds();
        }else{
            return getBlockBoundsForItemRender();
        }
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, pos);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape() && !camouflage.isUseCollision()) {
            return 600000;
        }
        return super.getBlockHardness(state, world, pos);
    }

    public AxisAlignedBB getBlockBoundsForItemRender() {
        return FULL_BLOCK_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.TRANSLUCENT;
    }

}
