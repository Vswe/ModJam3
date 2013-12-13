package vswe.stevesjam.components;


import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import vswe.stevesjam.interfaces.GuiJam;

public class ComponentMenuTarget extends ComponentMenu {


    public ComponentMenuTarget(FlowComponent parent) {
        super(parent);

        selectingRangeId = -1;
    }

    private static final int DIRECTION_SIZE_W = 31;
    private static final int DIRECTION_SIZE_H = 12;
    private static final int DIRECTION_SRC_X = 0;
    private static final int DIRECTION_SRC_Y = 70;
    private static final int DIRECTION_X_LEFT = 2;
    private static final int DIRECTION_X_RIGHT = 88;
    private static final int DIRECTION_Y = 5;
    private static final int DIRECTION_MARGIN = 10;
    private static final int DIRECTION_TEXT_X = 2;
    private static final int DIRECTION_TEXT_Y = 3;

    private static final int BUTTON_SIZE_W = 42;
    private static final int BUTTON_SIZE_H = 12;
    private static final int BUTTON_SRC_X = 0;
    private static final int BUTTON_SRC_Y = 106;
    private static final int BUTTON_X = 39;
    private static final int BUTTON_TEXT_Y = 5;

    private static final int TEXT_BOX_SIZE_W = 21;
    private static final int TEXT_BOX_SIZE_H = 12;
    private static final int TEXT_BOX_SRC_X = 0;
    private static final int TEXT_BOX_SRC_Y = 130;
    private static final int TEXT_BOX_Y = 49;
    private static final int TEXT_Y = 3;

    private Button[] buttons = {new Button(5) {
        @Override
        protected String getLabel() {
            return isActive(selectingRangeId) ? "Deactivate" : "Activate";
        }

        @Override
        protected String getMouseOverText() {
            return isActive(selectingRangeId) ? "Click to prevent this side from being used" : "Click to use this side";
        }

        @Override
        protected void onClicked() {
            swapActiveness(selectingRangeId);
        }
    },
    new Button(27) {
        @Override
        protected String getLabel() {
            return useRange(selectingRangeId) ? "Use all slots" : "Use id range";
        }

        @Override
        protected String getMouseOverText() {
            return useRange(selectingRangeId) ? "Click to use all slots for this side instead" : "Click to use a slot id range for this specific side";
        }

        @Override
        protected void onClicked() {
            swapRangeUsage(selectingRangeId);
        }
    }};

    private TextBox[] textBoxes = {new TextBox(39), new TextBox(60)};

    @Override
    public String getName() {
        return "Target";
    }


    private static ForgeDirection[] directions = {ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};

    private int selectingRangeId;
    private byte activatedDirections;
    private byte useRangeForDirections;
    private TextBox selectedTextBox;


    @Override
    public void draw(GuiJam gui, int mX, int mY) {
        for (int i = 0; i < directions.length; i++) {
            ForgeDirection direction = directions[i];

            int x = getDirectionX(i);
            int y = getDirectionY(i);

            int srcDirectionX = isActive(i) ? 1 : 0;
            int srcDirectionY = selectingRangeId != -1 && selectingRangeId != i ? 2 : GuiJam.inBounds(x, y, DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY) ? 1 : 0;


            gui.drawTexture(x, y, DIRECTION_SRC_X + srcDirectionX * DIRECTION_SIZE_W, DIRECTION_SRC_Y + srcDirectionY * DIRECTION_SIZE_H, DIRECTION_SIZE_W, DIRECTION_SIZE_H);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            int color =  selectingRangeId != -1 && selectingRangeId != i ? 0x70404040 : 0x404040;
            gui.drawString(direction.toString().charAt(0) + direction.toString().substring(1).toLowerCase(), x + DIRECTION_TEXT_X, y + DIRECTION_TEXT_Y, color);
            GL11.glPopMatrix();
        }

        if (selectingRangeId != -1) {
            for (Button button : buttons) {
                int srcButtonY = GuiJam.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY) ? 1 : 0;

                gui.drawTexture(BUTTON_X, button.y, BUTTON_SRC_X, BUTTON_SRC_Y + srcButtonY * BUTTON_SIZE_H, BUTTON_SIZE_W, BUTTON_SIZE_H);
                gui.drawCenteredString(button.getLabel(), BUTTON_X, button.y + BUTTON_TEXT_Y, 0.5F, BUTTON_SIZE_W, 0x404040);
            }

            if (useRange(selectingRangeId)) {
                for (TextBox textBox : textBoxes) {
                    int srcTextBoxX = textBox.equals(selectedTextBox) ? 1 : 0;

                    gui.drawTexture(textBox.x, TEXT_BOX_Y, TEXT_BOX_SRC_X + srcTextBoxX * TEXT_BOX_SIZE_W, TEXT_BOX_SRC_Y, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H);
                    String str = String.valueOf(textBox.number);
                    gui.drawString(str, textBox.x + (TEXT_BOX_SIZE_W - gui.getStringWidth(str)) / 2, TEXT_BOX_Y + TEXT_Y, 0xFFFFFF);
                }
            }
        }
    }

    private boolean isActive(int i) {
        return (activatedDirections & (1 << i)) != 0;
    }

    private void swapActiveness(int i) {
        activatedDirections ^= 1 << i;
    }

    private int getDirectionX(int i) {
        return i % 2 == 0 ? DIRECTION_X_LEFT : DIRECTION_X_RIGHT;
    }

    private void swapRangeUsage(int i) {
        useRangeForDirections ^= 1 << i;
    }


    private boolean useRange(int i) {
        return (useRangeForDirections & (1 << i)) != 0;
    }


    private int getDirectionY(int i) {
        return DIRECTION_Y + (DIRECTION_SIZE_H + DIRECTION_MARGIN) * (i / 2);
    }

    @Override
    public void drawMouseOver(GuiJam gui, int mX, int mY) {
        if (selectingRangeId != -1) {
            for (Button button : buttons) {
                if (GuiJam.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY)) {
                    gui.drawMouseOver(button.getMouseOverText(), mX, mY);
                }
            }
        }
    }

    @Override
    public void onClick(int mX, int mY) {
        for (int i = 0; i < directions.length; i++) {
            if (GuiJam.inBounds(getDirectionX(i), getDirectionY(i), DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY)) {
                if (selectingRangeId == i) {
                    selectingRangeId = -1;
                }else if (selectingRangeId == -1) {
                    selectingRangeId = i;
                }

                break;
            }
        }

        for (Button button : buttons) {
            if (GuiJam.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY)) {
                button.onClicked();
                break;
            }
        }

        if (useRange(selectingRangeId)) {
            for (TextBox textBox : textBoxes) {
                if (GuiJam.inBounds(textBox.x, TEXT_BOX_Y, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H, mX, mY)) {
                    if (textBox.equals(selectedTextBox)) {
                        selectedTextBox = null;
                    }else{
                        selectedTextBox = textBox;
                    }

                    break;
                }
            }
        }
    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {

    }

    private abstract class Button {
        private int y;

        protected Button(int y) {
            this.y = y;
        }

        protected abstract String getLabel();
        protected abstract String getMouseOverText();
        protected abstract void onClicked();
    }

    private class TextBox {
        private int x;
        private int number;

        private TextBox(int x) {
            this.x = x;
            number = 0;
        }
    }

    @Override
    public boolean onKeyStroke(GuiJam gui, char c, int k) {
        if (selectedTextBox != null && useRange(selectingRangeId)) {

            if (Character.isDigit(c)) {
                int number = Integer.parseInt(String.valueOf(c));
                if (selectedTextBox.number < 10){
                    selectedTextBox.number *= 10;
                    selectedTextBox.number += number;
                }
                return true;
            }else if(k == 14){
                selectedTextBox.number /= 10;
                return true;
            }else if(k == 15){
                for (int i = 0; i < textBoxes.length; i++) {
                    TextBox textBox = textBoxes[i];

                    if (textBox.equals(selectedTextBox)) {
                        int nextId = (i + 1) % textBoxes.length;
                        selectedTextBox = textBoxes[nextId];
                        break;
                    }
                }
                return true;
            }

        }


        return false;
    }
}
