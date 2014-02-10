package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;


public interface ITileEntityInterface {

    public abstract Container getContainer(TileEntity te, InventoryPlayer inv);
    @SideOnly(Side.CLIENT)
    public abstract GuiScreen getGui(TileEntity te, InventoryPlayer inv);
    public abstract void readAllData(DataReader dr, EntityPlayer player);
    public abstract void readUpdatedData(DataReader dr, EntityPlayer player);
    public abstract void writeAllData(DataWriter dw);
}
