package vswe.stevesfactory.interfaces;


import java.util.List;

public interface IAdvancedTooltip {

    int getMinWidth(GuiBase gui);
    int getExtraHeight(GuiBase gui);
    void drawContent(GuiBase gui, int x, int y, int mX, int mY);
    List<String> getPrefix(GuiBase gui);
    List<String> getSuffix(GuiBase gui);
}
