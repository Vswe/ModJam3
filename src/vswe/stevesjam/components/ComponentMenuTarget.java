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

    @Override
    public String getName() {
        return "Target";
    }


    private static ForgeDirection[] directions = {ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};

    private int selectingRangeId;

    @Override
    public void draw(GuiJam gui, int mX, int mY) {
        for (int i = 0; i < directions.length; i++) {
            ForgeDirection direction = directions[i];

            int x = getDirectionX(i);
            int y = getDirectionY(i);

            int srcDirectionX = 0;
            int srcDirectionY = selectingRangeId != -1 && selectingRangeId != i ? 2 : GuiJam.inBounds(x, y, DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY) ? 1 : 0;


            gui.drawTexture(x, y, DIRECTION_SRC_X + srcDirectionX * DIRECTION_SIZE_W, DIRECTION_SRC_Y + srcDirectionY * DIRECTION_SIZE_H, DIRECTION_SIZE_W, DIRECTION_SIZE_H);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            int color =  selectingRangeId != -1 && selectingRangeId != i ? 0x70404040 : 0x404040;
            gui.drawString(direction.toString().charAt(0) + direction.toString().substring(1).toLowerCase(), x + DIRECTION_TEXT_X, y + DIRECTION_TEXT_Y, color);
            GL11.glPopMatrix();
        }
    }

    private int getDirectionX(int i) {
        return i % 2 == 0 ? DIRECTION_X_LEFT : DIRECTION_X_RIGHT;
    }

    private int getDirectionY(int i) {
        return DIRECTION_Y + (DIRECTION_SIZE_H + DIRECTION_MARGIN) * (i / 2);
    }

    @Override
    public void drawMouseOver(GuiJam gui, int mX, int mY) {

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
    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {

    }



}
