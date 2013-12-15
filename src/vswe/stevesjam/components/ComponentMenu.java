package vswe.stevesjam.components;


import net.minecraft.inventory.Container;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.interfaces.ContainerJam;
import vswe.stevesjam.interfaces.GuiJam;
import vswe.stevesjam.network.DataReader;
import vswe.stevesjam.network.DataWriter;
import vswe.stevesjam.network.IComponentNetworkReader;
import vswe.stevesjam.network.PacketHandler;

public abstract class ComponentMenu implements IComponentNetworkReader {


    private FlowComponent parent;
    private int id;

    public ComponentMenu(FlowComponent parent) {
        this.parent = parent;
        id = parent.getMenus().size();
    }

    public abstract String getName();
    public abstract void draw(GuiJam gui, int mX, int mY);
    public abstract void drawMouseOver(GuiJam gui, int mX, int mY);

    public abstract void onClick(int mX, int mY, int button);
    public abstract void onDrag(int mX, int mY);
    public abstract void onRelease(int mX, int mY);


    public boolean onKeyStroke(GuiJam gui, char c, int k) {
        return false;
    }

    public FlowComponent getParent() {
        return parent;
    }

    public abstract void writeData(DataWriter dw, TileEntityJam jam);
    public abstract void readData(DataReader dr, TileEntityJam jam);

    protected DataWriter getWriterForServerComponentPacket() {
        return PacketHandler.getWriterForServerComponentPacket(getParent(), this);
    }

    protected DataWriter getWriterForClientComponentPacket(ContainerJam container) {
        return PacketHandler.getWriterForClientComponentPacket(container, getParent(), this);
    }

    public abstract void copyFrom(ComponentMenu menu);
    public abstract void refreshData(ContainerJam container, ComponentMenu newData);

    public int getId() {
        return id;
    }
}
