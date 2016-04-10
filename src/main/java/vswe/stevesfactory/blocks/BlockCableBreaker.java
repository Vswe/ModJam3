package vswe.stevesfactory.blocks;


import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableBreaker extends BlockContainer {
    public BlockCableBreaker() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(soundTypeMetal);
        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + ModBlocks.CABLE_BREAKER_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBreaker();
    }

    public static final IProperty FRONT = PropertyDirection.create("front");
    public static final IProperty DIRECTION = PropertyDirection.create("direction");

    @Override
    public BlockState createBlockState() {
        return new BlockState(this, DIRECTION, FRONT);
    }

    public static EnumFacing getSide(int meta) {
        return EnumFacing.getFront(meta % EnumFacing.values().length);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FRONT, getSide(meta)).withProperty(DIRECTION, getSide(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing)state.getValue(FRONT)).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntityBreaker entityBreaker = (TileEntityBreaker) worldIn.getTileEntity(pos);
        if (entityBreaker != null && entityBreaker.getPlaceDirection() != null) {
            return state.withProperty(DIRECTION, entityBreaker.getPlaceDirection()).withProperty(FRONT, getSide(getMetaFromState(state)));
        }
        return state.withProperty(DIRECTION, getSide(getMetaFromState(state))).withProperty(FRONT, getSide(getMetaFromState(state)));
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack item) {
        if (!world.isRemote) {
            EnumFacing facing = BlockPistonBase.getFacingFromEntity(world, pos, entity);

            TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, pos);
            if (breaker != null) {
                breaker.setPlaceDirection(facing);
                breaker.setMetaData(facing.getIndex());
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {

        if (player.isSneaking()) {
            side = side.getOpposite();
        }

        TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, pos);
        if (breaker != null && !breaker.isBlocked()) {
            breaker.setPlaceDirection(side);
            return true;
        }

        return false;
    }
}
