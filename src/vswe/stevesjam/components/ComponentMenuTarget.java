package vswe.stevesjam.components;


import vswe.stevesjam.interfaces.GuiJam;

public class ComponentMenuTarget extends ComponentMenu {
    public ComponentMenuTarget(FlowComponent parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return "Target";
    }

    @Override
    public void draw(GuiJam gui, int renderX, int renderY, int mX, int mY) {

    }

    @Override
    public void drawText(GuiJam gui, int renderX, int renderY) {
        gui.drawString("Cake", renderX + 5, renderY + 5, 0xFFFF00);
    }
}
