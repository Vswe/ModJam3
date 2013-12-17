package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.IComponentNetworkReader;
import vswe.stevesfactory.network.PacketHandler;

public abstract class ComponentMenu implements IComponentNetworkReader {


    private FlowComponent parent;
    private int id;

    public ComponentMenu(FlowComponent parent) {
        this.parent = parent;
        id = parent.getMenus().size();
    }

    public abstract String getName();
    @SideOnly(Side.CLIENT)
    public abstract void draw(GuiManager gui, int mX, int mY);
    @SideOnly(Side.CLIENT)
    public abstract void drawMouseOver(GuiManager gui, int mX, int mY);

    public abstract void onClick(int mX, int mY, int button);
    public abstract void onDrag(int mX, int mY);
    public abstract void onRelease(int mX, int mY);

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        return false;
    }

    public FlowComponent getParent() {
        return parent;
    }

    public abstract void writeData(DataWriter dw);
    public abstract void readData(DataReader dr);

    protected DataWriter getWriterForServerComponentPacket() {
        return PacketHandler.getWriterForServerComponentPacket(getParent(), this);
    }

    protected DataWriter getWriterForClientComponentPacket(ContainerManager container) {
        return PacketHandler.getWriterForClientComponentPacket(container, getParent(), this);
    }

    public abstract void copyFrom(ComponentMenu menu);
    public abstract void refreshData(ContainerManager container, ComponentMenu newData);

    public int getId() {
        return id;
    }

    public abstract void readFromNBT(NBTTagCompound nbtTagCompound);
    public abstract void writeToNBT(NBTTagCompound nbtTagCompound);

}
