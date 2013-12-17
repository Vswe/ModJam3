package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.ArrayList;
import java.util.List;

public abstract class RadioButtonList {

    private static final int RADIO_SIZE = 8;
    private static final int RADIO_SRC_X = 30;
    private static final int RADIO_SRC_Y = 52;
    private static final int RADIO_TEXT_X = 12;
    private static final int RADIO_TEXT_Y = 2;

    private List<RadioButton> radioButtonList;
    private int selectedOption;

    public RadioButtonList() {
        radioButtonList = new ArrayList<>();

    }
    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY) {
        for (int i = 0; i < radioButtonList.size(); i++) {
            RadioButton radioButton = radioButtonList.get(i);

            int srcRadioX = selectedOption == i ? 1 : 0;
            int srcRadioY = CollisionHelper.inBounds(radioButton.getX(), radioButton.getY(), RADIO_SIZE, RADIO_SIZE, mX, mY) ? 1 : 0;

            gui.drawTexture(radioButton.getX(), radioButton.getY(), RADIO_SRC_X + srcRadioX * RADIO_SIZE, RADIO_SRC_Y + srcRadioY * RADIO_SIZE, RADIO_SIZE, RADIO_SIZE);
            gui.drawString(radioButton.getText(), radioButton.getX() + RADIO_TEXT_X, radioButton.getY() + RADIO_TEXT_Y, 0.7F, 0x404040);
        }
    }

    public void onClick(int mX, int mY, int button) {
        for (int i = 0; i < radioButtonList.size(); i++) {
            RadioButton radioButton = radioButtonList.get(i);

            if (CollisionHelper.inBounds(radioButton.getX(), radioButton.getY(), RADIO_SIZE, RADIO_SIZE, mX, mY)) {
                updateSelectedOption(i);
                break;
            }
        }
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    public abstract void updateSelectedOption(int selectedOption);

    public void add(RadioButton radioButton) {
        radioButtonList.add(radioButton);
    }
}
