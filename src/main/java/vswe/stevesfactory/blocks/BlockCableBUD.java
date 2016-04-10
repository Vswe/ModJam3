package vswe.stevesfactory.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

public class BlockCableBUD extends BlockContainer {
    public BlockCableBUD() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(SoundType.METAL);
        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + ModBlocks.CABLE_BUD_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBUD();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        TileEntityBUD bud = TileEntityCluster.getTileEntity(TileEntityBUD.class, world, pos);
        if (bud != null) {
            bud.onTrigger();
        }
    }


}
