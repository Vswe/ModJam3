package vswe.stevesjam.components;


import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.interfaces.GuiJam;
import vswe.stevesjam.network.DataBitHelper;
import vswe.stevesjam.network.DataReader;
import vswe.stevesjam.network.DataWriter;

public class ComponentMenuTarget extends ComponentMenu {


    public ComponentMenuTarget(FlowComponent parent) {
        super(parent);

        selectingRangeId = -1;
        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(startTextBox = new TextBoxNumber(39 ,49, 2, false) {
            @Override
            public void setNumber(int number) {
                super.setNumber(number);

                if (selectingRangeId != -1) {
                    startRange[selectingRangeId] = number;
                }
            }
        });
        textBoxes.addTextBox(endTextBox = new TextBoxNumber(60 ,49, 2, false) {
            @Override
            public void setNumber(int number) {
                super.setNumber(number);

                if (selectingRangeId != -1) {
                    endRange[selectingRangeId] = number;
                }
            }
        });
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
            if (useRange(selectingRangeId)) {
                refreshTextBoxes();
            }
        }
    }};

    private TextBoxNumberList textBoxes;
    private TextBoxNumber startTextBox;
    private TextBoxNumber endTextBox;

    @Override
    public String getName() {
        return "Target";
    }


    private static ForgeDirection[] directions = {ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};

    private int selectingRangeId;
    private boolean[] activatedDirections = new boolean[directions.length];
    private boolean[] useRangeForDirections = new boolean[directions.length];
    private int[] startRange = new int[directions.length];
    private int[] endRange = new int[directions.length];



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
                textBoxes.draw(gui, mX, mY);
            }
        }
    }

    private boolean isActive(int i) {
        return activatedDirections[i];
    }

    private void swapActiveness(int i) {
        activatedDirections[i] = !activatedDirections[i];
    }

    private int getDirectionX(int i) {
        return i % 2 == 0 ? DIRECTION_X_LEFT : DIRECTION_X_RIGHT;
    }

    private void swapRangeUsage(int i) {
        useRangeForDirections[i] = !useRangeForDirections[i];
    }


    private boolean useRange(int i) {
        return useRangeForDirections[i];
    }

    private void refreshTextBoxes() {
        startTextBox.setNumber(startRange[selectingRangeId]);
        endTextBox.setNumber(endRange[selectingRangeId]);
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
    public void onClick(int mX, int mY, int button) {
        for (int i = 0; i < directions.length; i++) {
            if (GuiJam.inBounds(getDirectionX(i), getDirectionY(i), DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY)) {
                if (selectingRangeId == i) {
                    selectingRangeId = -1;
                }else if (selectingRangeId == -1) {
                    selectingRangeId = i;
                    refreshTextBoxes();
                }

                break;
            }
        }

        if (selectingRangeId != -1) {
            for (Button optionButton : buttons) {
                if (GuiJam.inBounds(BUTTON_X, optionButton.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY)) {
                    optionButton.onClicked();
                    break;
                }
            }

            if (useRange(selectingRangeId)) {
                textBoxes.onClick(mX, mY, button);
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


    @Override
    public boolean onKeyStroke(GuiJam gui, char c, int k) {
        if (selectingRangeId != -1 && useRange(selectingRangeId)) {
            return textBoxes.onKeyStroke(gui, c, k);
        }


        return false;
    }

    @Override
    public void writeData(DataWriter dw, TileEntityJam jam) {
        for (int i = 0; i < directions.length; i++) {
            dw.writeBoolean(isActive(i));
            dw.writeBoolean(useRange(i));
            if (useRange(i)) {
                dw.writeData(startRange[i], DataBitHelper.MENU_TARGET_RANGE);
                dw.writeData(endRange[i], DataBitHelper.MENU_TARGET_RANGE);
            }

        }
    }

    @Override
    public void readData(DataReader dr, TileEntityJam jam) {
        for (int i = 0; i < directions.length; i++) {

            activatedDirections[i] = dr.readBoolean();
            useRangeForDirections[i] = dr.readBoolean();
            if (useRange(i)) {
                startRange[i] = dr.readData(DataBitHelper.MENU_TARGET_RANGE);
                endRange[i] = dr.readData(DataBitHelper.MENU_TARGET_RANGE);
            }else{
                startRange[i] = 0;
                endRange[i] = 0;
            }


        }
    }

    @Override
    public void readDataOnServer(DataReader dr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
