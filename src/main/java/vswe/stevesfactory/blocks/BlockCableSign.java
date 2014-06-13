package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableSign extends BlockContainer {
    public BlockCableSign() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(soundTypeMetal);
        setBlockName(StevesFactoryManager.UNLOCALIZED_START + ModBlocks.CABLE_SIGN_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignUpdater();
    }

    @SideOnly(Side.CLIENT)
    private IIcon frontIcon;


    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_idle");
        frontIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_sign");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return getIconFromSideAndMeta(side, 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return getIconFromSideAndMeta(side, world.getBlockMetadata(x, y, z));
    }
    @SideOnly(Side.CLIENT)
    private IIcon getIconFromSideAndMeta(int side, int meta) {
        return side == meta ? frontIcon : blockIcon;
    }


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);

        TileEntitySignUpdater sign = TileEntityCluster.getTileEntity(TileEntitySignUpdater.class, world, x, y, z);
        if (sign != null) {
            sign.setMetaData(meta);
        }
    }


}
