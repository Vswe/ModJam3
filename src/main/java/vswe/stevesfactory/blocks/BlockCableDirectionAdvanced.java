package vswe.stevesfactory.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;


public abstract class BlockCableDirectionAdvanced extends BlockContainer {
    public BlockCableDirectionAdvanced() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(soundTypeMetal);
        setHardness(1.2F);
    }

    public static final IProperty FACING = PropertyDirection.create("facing");
    public static final IProperty ADVANCED = PropertyBool.create("advanced");

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, ADVANCED, FACING);
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
    public int getRenderType() {
        return 3;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack item) {
        int meta = addAdvancedMeta(BlockPistonBase.getFacingFromEntity(world, pos, entity).getIndex(), item.getItemDamage());

        TileEntityClusterElement element = TileEntityCluster.getTileEntity(getTeClass(), world, pos);
        if (element != null) {
            element.setMetaData(meta);
        }
    }

    protected abstract Class<? extends TileEntityClusterElement> getTeClass();

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
