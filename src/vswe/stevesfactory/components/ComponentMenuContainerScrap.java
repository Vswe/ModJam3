package vswe.stevesfactory.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.List;


public class ComponentMenuContainerScrap extends ComponentMenuContainer {
    public ComponentMenuContainerScrap(FlowComponent parent) {
        super(parent, ConnectionBlockType.INVENTORY);
    }

    @Override
    public String getName() {
        return "Excess Inventories";
    }

    private static final int MENU_WIDTH = 120;
    private static final int TEXT_MARGIN_X = 5;
    private static final int TEXT_Y = 30;

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        super.draw(gui, mX, mY);

        gui.drawSplitString("These inventories will be used if some of the crafted items don't fit. It will also be used for container items, such as buckets.", TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2,  0.7F, 0x404040);
    }

    @Override
    public void addErrors(List<String> errors) {
        if (selectedInventories.isEmpty()) {
            errors.add("No excess inventory selected");
        }
    }

    @Override
    protected void initRadioButtons() {
        //no radio buttons
    }
}
