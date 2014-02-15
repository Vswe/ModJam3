package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.interfaces.GuiManager;

public abstract class ComponentMenuCamouflageAdvanced extends ComponentMenu {
    public ComponentMenuCamouflageAdvanced(FlowComponent parent) {
        super(parent);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        //TODO add warning
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        //TODO add warning
    }

    protected abstract String getWarningText();
}
