package vswe.stevesfactory.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.List;


public class BlockCableCamouflages extends BlockCamouflageBase {


    protected BlockCableCamouflages() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(SoundType.METAL);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCamouflage();
    }

    public static final UnlistedBlockPosProperty BLOCK_POS = new UnlistedBlockPosProperty("block_pos");
    public static final IProperty CAMO_TYPE = PropertyCamouflageType.create("camo_type");

    @Override
    protected BlockStateContainer createBlockState() {

        IProperty [] listedProperties = new IProperty[]{CAMO_TYPE};
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[]{BLOCK_POS};
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CAMO_TYPE, TileEntityCamouflage.CamouflageType.getCamouflageType(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {

        if (state.getValue(CAMO_TYPE) != null) {
            return ((TileEntityCamouflage.CamouflageType)state.getValue(CAMO_TYPE)).ordinal();
        }

        return 0;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {

        TileEntityCamouflage tileEntity = (TileEntityCamouflage) world.getTileEntity(pos);
        if (state instanceof IExtendedBlockState && tileEntity != null) {

            return ((IExtendedBlockState)state).withProperty(BLOCK_POS, pos);
        }

        return state;
    }

    @Override
    public void getSubBlocks(Item block, CreativeTabs tabs, List list) {
        for (int i = 0; i < TileEntityCamouflage.CamouflageType.values().length; i++) {
            list.add(new ItemStack(block, 1, i));
        }
    }

    public int getId(int meta) {
        return meta % TileEntityCamouflage.CamouflageType.values().length;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getBlock().getMetaFromState(state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack item) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, pos);
        if (camouflage != null) {
            camouflage.setMetaData(item.getItemDamage());
        }
    }
}
