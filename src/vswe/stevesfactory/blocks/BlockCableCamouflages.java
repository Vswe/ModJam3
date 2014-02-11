package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;


public class BlockCableCamouflages extends BlockContainer {


    protected BlockCableCamouflages(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + Blocks.CABLE_CAMOUFLAGE_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityCamouflage();
    }



    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_camo");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityCamouflage te = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, world, x, y, z);

        if (te != null) {
            Icon icon = te.getIcon(side);

            if (icon != null) {
                return icon;
            }
        }

        return super.getBlockTexture(world, x, y, z, side);
    }
}
