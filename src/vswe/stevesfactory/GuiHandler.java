package vswe.stevesfactory;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.blocks.ITileEntityInterface;


public class GuiHandler implements IGuiHandler {
    public GuiHandler() {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te != null && te instanceof ITileEntityInterface) {
            return ((ITileEntityInterface)te).getContainer(te, player.inventory);
        }else{
            return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te != null && te instanceof ITileEntityInterface) {
            return ((ITileEntityInterface)te).getGui(te, player.inventory);
        }else{
            return null;
        }
    }

}
