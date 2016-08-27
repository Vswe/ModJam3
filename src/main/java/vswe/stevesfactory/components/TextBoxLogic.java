package vswe.stevesfactory.components;

import net.minecraft.util.ChatAllowedCharacters;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesfactory.interfaces.GuiManager;


public class TextBoxLogic {
    private String text;
    private int cursor;
    private int cursorPosition;
    private int charLimit;
    private int width;
    private boolean updatedCursor;
    private float mult;

    public TextBoxLogic(int charLimit, int width) {
        this.charLimit = charLimit;
        this.width = width;
        mult = 1F;
    }

    @SideOnly(Side.CLIENT)
    private void addText(GuiManager gui, String str) {
        String newText = text.substring(0, cursor) + str + text.substring(cursor);

        if (newText.length() <= charLimit && gui.getStringWidth(newText) * mult <= width) {
            text = newText;
            moveCursor(gui, str.length());
            textChanged();
        }
    }

    @SideOnly(Side.CLIENT)
    private void deleteText(GuiManager gui, int direction) {
        if (cursor + direction >= 0 && cursor + direction <= text.length()) {
            if (direction > 0) {
                text = text.substring(0, cursor) + text.substring(cursor + 1);
            }else{
                text = text.substring(0, cursor - 1) + text.substring(cursor);
                moveCursor(gui, direction);
            }
            textChanged();
        }
    }

    @SideOnly(Side.CLIENT)
    private void moveCursor(GuiManager gui, int steps) {
        cursor += steps;

        updateCursor();
    }


    protected void textChanged() {}

    public String getText() {
        return text;
    }

    public int getCursorPosition(GuiManager gui) {
        if (updatedCursor) {
            cursorPosition = (int)(gui.getStringWidth(text.substring(0, cursor)) * mult);
            updatedCursor = false;
        }

        return cursorPosition;
    }

    public void setText(String text) {
        this.text = text;
    }

    @SideOnly(Side.CLIENT)
    public void onKeyStroke(GuiManager gui, char c, int k) {
        if (k == 203) {
            moveCursor(gui, -1);
        }else if(k == 205) {
            moveCursor(gui, 1);
        }else if (k == 14) {
            deleteText(gui, -1);
        }else if (k == 211) {
            deleteText(gui, 1);
        }else if (ChatAllowedCharacters.isAllowedCharacter(c)) {
            addText(gui, Character.toString(c));
        }
    }

    public void updateCursor() {
        if (cursor < 0) {
            cursor = 0;
        }else if (cursor > text.length()) {
            cursor = text.length();
        }

        updatedCursor = true;
    }


    public void resetCursor() {
        cursor = text.length();
        updatedCursor = true;
    }

    public void setTextAndCursor(String s) {
        setText(s);
        resetCursor();
    }

    public void setMult(float mult) {
        this.mult = mult;
    }
}
