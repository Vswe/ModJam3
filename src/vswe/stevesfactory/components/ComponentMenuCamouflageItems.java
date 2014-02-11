package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.List;

public class ComponentMenuCamouflageItems extends ComponentMenuItem {
    public ComponentMenuCamouflageItems(FlowComponent parent) {
        super(parent);
    }

    @Override
    protected int getSettingCount() {
        return 1;
    }

    @Override
    public String getName() {
        return Localization.CAMOUFLAGE_ITEM_MENU.toString();
    }

    @Override
    protected boolean doAllowEdit() {
        return false;
    }

    @Override
    protected void initRadioButtons() {
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Localization.CLEAR_CAMOUFLAGE));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Localization.SET_CAMOUFLAGE));
    }

    private static final int TEXT_MARGIN_X = 5;
    private static final int TEXT_Y = 40;
    private static final int MENU_WIDTH = 120;

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        super.draw(gui, mX, mY);

        if (!isEditing() && !isSearching()) {
            gui.drawSplitString(Localization.CAMOUFLAGE_INFO.toString(), TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2, 0.7F, 0x404040);
        }
    }

    @Override
    public void addErrors(List<String> errors) {
        if (!isFirstRadioButtonSelected() && !getSettings().get(0).isValid()) {
            errors.add(Localization.NO_CAMOUFLAGE_SETTING.toString());
        }
    }

    @Override
    protected boolean isListVisible() {
        return isSearching() || !isFirstRadioButtonSelected();
    }
}
