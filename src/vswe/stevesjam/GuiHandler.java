package vswe.stevesjam;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.interfaces.ContainerJam;
import vswe.stevesjam.interfaces.GuiJam;


public class GuiHandler implements IGuiHandler {
    public GuiHandler() {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntityJam te = getTileEntity(world, x, y, z);

        if (te != null) {
            return new ContainerJam(te, player.inventory);
        }else{
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntityJam te = getTileEntity(world, x, y, z);

        if (te != null) {
            return new GuiJam(te, player.inventory);
        }else{
            return null;
        }
    }

    private TileEntityJam getTileEntity(World world, int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityJam) {
            return (TileEntityJam)te;
        }else{
            return null;
        }

    }
}
