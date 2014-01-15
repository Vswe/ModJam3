package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableOutput extends BlockContainer {
    public BlockCableOutput(int id) {
        super(id, Material.iron);
        setCreativeTab(CreativeTabs.tabRedstone);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(Blocks.CABLE_OUTPUT_NAME_TAG);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityOutput();
    }

    @SideOnly(Side.CLIENT)
    private Icon inactiveIcon;
    @SideOnly(Side.CLIENT)
    private Icon weakIcon;
    @SideOnly(Side.CLIENT)
    private Icon strongIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        strongIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_output_strong");
        weakIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_output_weak");
        inactiveIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_idle");
    }


    @Override
    public Icon getIcon(int side, int meta) {
        return weakIcon;
    }

    @Override
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityOutput te = getTileEntity(world, x, y, z);
        if (te != null && te.getStrengthFromSide(side) > 0) {
            return te.hasStrongSignalAtSide(side) ? strongIcon : weakIcon;
        }
        return inactiveIcon;    }

    @Override
    public boolean shouldCheckWeakPower(World world, int x, int y, int z, int side) {
        return true;
    }


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
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof TileEntityOutput) {
            return (TileEntityOutput)te;
        }else{
            return null;
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }
}
