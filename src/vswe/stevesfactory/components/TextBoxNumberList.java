package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.ArrayList;
import java.util.List;

public class TextBoxNumberList {
    private static final int TEXT_BOX_SIZE_W = 21;
    private static final int TEXT_BOX_SIZE_W_WIDE = 33;
    private static final int TEXT_BOX_SIZE_H = 12;
    private static final int TEXT_BOX_SRC_X = 0;
    private static final int TEXT_BOX_SRC_Y = 221;
    private static final int TEXT_Y = 3;

    private List<TextBoxNumber> textBoxes;
    private TextBoxNumber selectedTextBox;

    public TextBoxNumberList() {
        textBoxes = new ArrayList<TextBoxNumber>();
    }
    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY) {
        for (TextBoxNumber textBox : textBoxes) {
            if (textBox.isVisible()) {
                int srcTextBoxX = textBox.equals(selectedTextBox) ? 1 : 0;
                int srcTextBoxY = textBox.isWide() ? 1 : 0;

                gui.drawTexture(textBox.getX(), textBox.getY(), TEXT_BOX_SRC_X + srcTextBoxX * getWidth(textBox.isWide()), TEXT_BOX_SRC_Y + srcTextBoxY * TEXT_BOX_SIZE_H, getWidth(textBox.isWide()), TEXT_BOX_SIZE_H);
                String str = String.valueOf(textBox.getNumber());
                gui.drawString(str, textBox.getX() + (getWidth(textBox.isWide()) - gui.getStringWidth(str)) / 2, textBox.getY() + TEXT_Y, 0xFFFFFF);
            }
        }
    }

    public void onClick(int mX, int mY, int button) {
        for (TextBoxNumber textBox : textBoxes) {
            if (textBox.isVisible() && CollisionHelper.inBounds(textBox.getX(), textBox.getY(), getWidth(textBox.isWide()), TEXT_BOX_SIZE_H, mX, mY)) {
                if (textBox.equals(selectedTextBox)) {
                    selectedTextBox = null;
                }else{
                    selectedTextBox = textBox;
                }

                break;
            }
        }
    }
    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        if (selectedTextBox != null && selectedTextBox.isVisible()) {

            if (Character.isDigit(c)) {
                int number = Integer.parseInt(String.valueOf(c));
                if (selectedTextBox.getNumber() < Math.pow(10, selectedTextBox.getLength() - 1)){
                   selectedTextBox.setNumber(selectedTextBox.getNumber() * 10 + number);
                   selectedTextBox.onNumberChanged();
                }
                return true;
            }else if(k == 14){
                selectedTextBox.setNumber(selectedTextBox.getNumber() / 10);
                selectedTextBox.onNumberChanged();
                return true;
            }else if(k == 15){
                for (int i = 0; i < textBoxes.size(); i++) {
                    TextBoxNumber textBox = textBoxes.get(i);

                    if (textBox.equals(selectedTextBox)) {
                        int nextId = (i + 1) % textBoxes.size();
                        selectedTextBox = textBoxes.get(nextId);
                        break;
                    }
                }
                return true;
            }

        }

        return false;
    }

    public void addTextBox(TextBoxNumber textBox) {
        textBoxes.add(textBox);
    }

    private int getWidth(boolean isWide) {
        return isWide ? TEXT_BOX_SIZE_W_WIDE : TEXT_BOX_SIZE_W;
    }

}
