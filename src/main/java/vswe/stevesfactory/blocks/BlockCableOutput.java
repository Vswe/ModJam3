package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableOutput extends BlockContainer {
    public BlockCableOutput() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(soundTypeMetal);
        setBlockName(StevesFactoryManager.UNLOCALIZED_START + ModBlocks.CABLE_OUTPUT_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int var2) {
        return new TileEntityOutput();
    }

    @SideOnly(Side.CLIENT)
    private IIcon inactiveIcon;
    @SideOnly(Side.CLIENT)
    private IIcon weakIcon;
    @SideOnly(Side.CLIENT)
    private IIcon strongIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        strongIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_output_strong");
        weakIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_output_weak");
        inactiveIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_idle");
    }


    @Override
    public IIcon getIcon(int side, int meta) {
        return weakIcon;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityOutput te = getTileEntity(world, x, y, z);
        if (te != null && te.getStrengthFromSide(side) > 0) {
            return te.hasStrongSignalAtSide(side) ? strongIcon : weakIcon;
        }
        return inactiveIcon;    }


    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityOutput te = getTileEntity(world, x, y, z);
        if (te != null) {
            return te.getStrengthFromOppositeSide(side);
        }
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityOutput te = getTileEntity(world, x, y, z);
        if (te != null && te.hasStrongSignalAtOppositeSide(side)) {
            return te.getStrengthFromOppositeSide(side);
        }

        return 0;
    }

    private TileEntityOutput getTileEntity(IBlockAccess world, int x, int y, int z) {
        return TileEntityCluster.getTileEntity(TileEntityOutput.class, world, x, y, z);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public boolean canProvidePower(){
        return true;
    }
}
