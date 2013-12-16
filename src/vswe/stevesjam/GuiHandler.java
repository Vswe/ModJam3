package vswe.stevesjam;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesjam.blocks.TileEntityManager;
import vswe.stevesjam.interfaces.ContainerManager;
import vswe.stevesjam.interfaces.GuiManager;


public class GuiHandler implements IGuiHandler {
    public GuiHandler() {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntityManager te = getTileEntity(world, x, y, z);

        if (te != null) {
            return new ContainerManager(te, player.inventory);
        }else{
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntityManager te = getTileEntity(world, x, y, z);

        if (te != null) {
            return new GuiManager(te, player.inventory);
        }else{
            return null;
        }
    }

    private TileEntityManager getTileEntity(World world, int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityManager) {
            return (TileEntityManager)te;
        }else{
            return null;
        }

    }
}
