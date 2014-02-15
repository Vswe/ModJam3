package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.GuiManager;

public abstract class ComponentMenuCamouflageAdvanced extends ComponentMenu {
    public ComponentMenuCamouflageAdvanced(FlowComponent parent) {
        super(parent);
    }

    private static final int ERROR_X = 115;
    private static final int ERROR_Y = 2;
    private static final int ERROR_SIZE_W = 2;
    private static final int ERROR_SIZE_H = 10;
    private static final int ERROR_SRC_X = 44;
    private static final int ERROR_SRC_Y = 212;

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        int srcY = CollisionHelper.inBounds(ERROR_X, ERROR_Y, ERROR_SIZE_W, ERROR_SIZE_H, mX , mY) ? 1 : 0;
        gui.drawTexture(ERROR_X, ERROR_Y, ERROR_SRC_X, ERROR_SRC_Y + srcY * ERROR_SIZE_H, ERROR_SIZE_W, ERROR_SIZE_H);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        if (CollisionHelper.inBounds(ERROR_X, ERROR_Y, ERROR_SIZE_W, ERROR_SIZE_H, mX , mY)) {
            gui.drawMouseOver(getWarningText(), mX, mY, 200);
        }
    }

    protected abstract String getWarningText();
}
