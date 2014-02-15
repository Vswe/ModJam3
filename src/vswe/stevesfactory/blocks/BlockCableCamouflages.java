package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.List;


public class BlockCableCamouflages extends BlockCamouflageBase {


    protected BlockCableCamouflages(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityCamouflage();
    }

    @SideOnly(Side.CLIENT)
    private Icon[] icons;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        icons = new Icon[TileEntityCamouflage.CamouflageType.values().length];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":" + TileEntityCamouflage.CamouflageType.values()[i].getIcon());
        }
    }
    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int meta) {
        return getDefaultIcon(side, meta, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected Icon getDefaultIcon(int side, int blockMeta, int camoMeta) {
        return icons[camoMeta % icons.length];
    }

    @Override
    public void getSubBlocks(int id, CreativeTabs tabs, List list) {
        for (int i = 0; i < TileEntityCamouflage.CamouflageType.values().length; i++) {
            list.add(new ItemStack(id, 1, i));
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
