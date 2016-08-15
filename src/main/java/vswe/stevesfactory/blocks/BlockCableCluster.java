package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.ArrayList;
import java.util.List;

public class BlockCableCluster extends BlockCamouflageBase {

    protected BlockCableCluster() {
        super(Material.IRON);
        setCreativeTab(ModBlocks.creativeTab);
        setSoundType(SoundType.METAL);
        setHardness(2F);
    }

    public static final IProperty FACING = PropertyDirection.create("facing");
    public static final IProperty ADVANCED = PropertyBool.create("advanced");

    @Override
    protected BlockStateContainer createBlockState() {

        IProperty [] listedProperties = new IProperty[]{ADVANCED, FACING};
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[]{BlockCableCamouflages.BLOCK_POS};
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ADVANCED, isAdvanced(meta)).withProperty(FACING, getSide(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return addAdvancedMeta(((EnumFacing) state.getValue(FACING)).getIndex(), ((Boolean) state.getValue(ADVANCED)) ? 8 : 0);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

        TileEntityCluster tileEntity = (TileEntityCluster) world.getTileEntity(pos);
        if (state instanceof IExtendedBlockState && tileEntity != null) {

            return ((IExtendedBlockState)state).withProperty(BlockCableCamouflages.BLOCK_POS, pos);
        }

        return state;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        /*ItemStack itemStack = getItemStack(world, pos, state);

        if (itemStack != null) {
            dropBlockAsItem(world, pos, itemStack, 0);
        }*/

        super.breakBlock(world, pos, state);

        if (isAdvanced(state.getBlock().getMetaFromState(state))) {
            ModBlocks.blockCable.updateInventories(world, pos, state);
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack itemStack = getItemStack(world, pos, world.getBlockState(pos));
        if (itemStack != null) {
            return itemStack;
        }

        return super.getPickBlock(state, target, world, pos, player) ;
    }

    private ItemStack getItemStack(IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);

        if (te != null && te instanceof  TileEntityCluster) {
            TileEntityCluster cluster = (TileEntityCluster)te;
            ItemStack itemStack = new ItemStack(ModBlocks.blockCableCluster, 1, damageDropped(state));
            NBTTagCompound compound = new NBTTagCompound();
            itemStack.setTagCompound(compound);
            NBTTagCompound cable = new NBTTagCompound();
            compound.setTag(ItemCluster.NBT_CABLE, cable);
            cable.setByteArray(ItemCluster.NBT_TYPES, cluster.getTypes());
            return itemStack;
        }

        return null;
    }


    @Override
    public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drop = new ArrayList<ItemStack>();
        drop.add(getItemStack(world, pos, state));
        return drop;
        //return new ArrayList<ItemStack>(); //TODO Drop items here, not sure how to though since the TE is gone. please help
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCluster();
    }

    private TileEntityCluster getTe(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof  TileEntityCluster) {
            return (TileEntityCluster)te;
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
        int meta = addAdvancedMeta(BlockPistonBase.getFacingFromEntity(pos, entity).getIndex(), itemStack.getItemDamage());
        world.setBlockState(pos, state.getBlock().getStateFromMeta(meta), 2);

        TileEntityCluster cluster = getTe(world, pos);

        if (cluster != null) {
            cluster.loadElements(itemStack);

            cluster.onBlockPlacedBy(world, pos, state, entity, itemStack);
        }
    }


//    @Override
//    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
//        TileEntityCluster cluster = getTe(world, pos);
//
//        if (cluster != null) {
//            cluster.onNeighborBlockChange(world, pos, state, block);
//        }
//
//        if (isAdvanced(state.getBlock().getMetaFromState(state))) {
//            ModBlocks.blockCable.updateInventories(world, pos, state);
//        }
//    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntityCluster cluster = getTe(world, pos);

        if (cluster != null) {
            return cluster.canConnectRedstone(state, world, pos, side);
        }

        return false;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        TileEntityCluster cluster = getTe(world, pos);

        if (cluster != null) {
            cluster.onBlockAdded(world, pos, state);
        }

        if (isAdvanced(state.getBlock().getMetaFromState(state))) {
            ModBlocks.blockCable.updateInventories(world, pos, state);
        }
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntityCluster cluster = getTe(world, pos);

        if (cluster != null) {
            return cluster.shouldCheckWeakPower(state, world, pos, side);
        }

        return false;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntityCluster cluster = getTe(world, pos);

        if (cluster != null) {
            return cluster.isProvidingWeakPower(state, world, pos, side);
        }

        return 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        TileEntityCluster cluster = getTe(blockAccess, pos);

        if (cluster != null) {
            return cluster.isProvidingStrongPower(state, blockAccess, pos, side);
        }

        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityCluster cluster = getTe(world, pos);

        if (cluster != null) {
            return cluster.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
        }

        return false;
    }



    @Override
    public void getSubBlocks(Item item, CreativeTabs tabs, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 8));
    }

    public boolean isAdvanced(int meta) {
        return (meta & 8) != 0;
    }

    public int getSideMeta(int meta) {
        return meta & 7;
    }

    public EnumFacing getSide(int meta) {
        return EnumFacing.getFront(getSideMeta(meta));
    }

    private int addAdvancedMeta(int meta, int advancedMeta) {
        return meta | (advancedMeta & 8);
    }

    private int getAdvancedMeta(int meta) {
        return addAdvancedMeta(0, meta);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getAdvancedMeta(state.getBlock().getMetaFromState(state));
    }

}
