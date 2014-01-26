package vswe.stevesfactory.interfaces;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IInterfaceRenderer {
    void draw(GuiManager gui, int mX, int mY);
    void drawMouseOver(GuiManager gui, int mX, int mY);
    void onClick(GuiManager gui, int mX, int mY);
    void onDrag(GuiManager gui, int mX, int mY);
    void onRelease(GuiManager gui, int mX, int mY);
    void onKeyTyped(GuiManager gui, char c, int k);
}
