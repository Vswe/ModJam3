package vswe.stevesjam.components;


import vswe.stevesjam.interfaces.GuiJam;

public abstract class ComponentMenu {


    private FlowComponent parent;

    public ComponentMenu(FlowComponent parent) {
        this.parent = parent;
    }

    public abstract String getName();
    public abstract void draw(GuiJam gui, int renderX, int renderY, int mX, int mY);
    public abstract void drawText(GuiJam gui, int renderX, int renderY);
}
