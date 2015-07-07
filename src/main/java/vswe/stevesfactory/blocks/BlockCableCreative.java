package vswe.stevesfactory.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;


public class BlockCableCreative extends BlockContainer {
    public BlockCableCreative() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(soundTypeMetal);
        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + ModBlocks.CABLE_CREATIVE_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCreative();
    }

    @Override
    public int getRenderType() {
        return 3;
    }

}