package vswe.stevesjam.blocks;


import cpw.mods.fml.common.network.FMLNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import vswe.stevesjam.StevesJam;

public class BlockJam extends BlockContainer {
    public BlockJam(int id) {
        super(id, Material.iron);

        setUnlocalizedName(Blocks.JAM_NAME_TAG);
        setStepSound(Block.soundMetalFootstep);
        setCreativeTab(CreativeTabs.tabRedstone);
    }


    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityJam();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, StevesJam.instance, 0, world, x, y , z);
        }

        return true;
    }

    @Override
    public Icon getIcon(int par1, int par2) {
        return super.getIcon(par1, par2);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesJam.RESOURCE_LOCATION + ":jam");
    }
}
