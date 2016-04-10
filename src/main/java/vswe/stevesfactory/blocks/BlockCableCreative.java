package vswe.stevesfactory.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;


public class BlockCableCreative extends BlockContainer {
    public BlockCableCreative() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(SoundType.METAL);
        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + ModBlocks.CABLE_CREATIVE_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCreative();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}