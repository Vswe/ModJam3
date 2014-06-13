package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.List;


public class BlockCableCamouflages extends BlockCamouflageBase {


    protected BlockCableCamouflages() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(soundTypeMetal);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCamouflage();
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        icons = new IIcon[TileEntityCamouflage.CamouflageType.values().length];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":" + TileEntityCamouflage.CamouflageType.values()[i].getIcon());
        }
    }
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        return getDefaultIcon(side, meta, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected IIcon getDefaultIcon(int side, int blockMeta, int camoMeta) {
        return icons[camoMeta % icons.length];
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
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
        TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);
        if (camouflage != null) {
            camouflage.setMetaData(item.getItemDamage());
        }
    }
}
