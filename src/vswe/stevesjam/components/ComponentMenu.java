package vswe.stevesjam.components;


import vswe.stevesjam.interfaces.GuiJam;

public abstract class ComponentMenu {


    private FlowComponent parent;

    public ComponentMenu(FlowComponent parent) {
        this.parent = parent;
    }

    public abstract String getName();
    public abstract void draw(GuiJam gui, int mX, int mY);
    public abstract void drawMouseOver(GuiJam gui, int mX, int mY);

    public abstract void onClick(int mX, int mY);
    public abstract void onDrag(int mX, int mY);
    public abstract void onRelease(int mX, int mY);


    public boolean onKeyStroke(char c, int k) {
        return false;
    }
}
