package vswe.stevesjam.components;


import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.interfaces.GuiJam;
import vswe.stevesjam.network.DataReader;
import vswe.stevesjam.network.DataWriter;
import vswe.stevesjam.network.PacketHandler;

public abstract class ComponentMenu {


    private FlowComponent parent;

    public ComponentMenu(FlowComponent parent) {
        this.parent = parent;
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
    public abstract void readDataOnServer(DataReader dr);

    protected DataWriter getWriterForServerComponentPacket() {
        return PacketHandler.getWriterForServerComponentPacket(getParent(), this);
    }
}
