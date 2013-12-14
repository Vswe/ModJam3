package vswe.stevesjam.components;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import vswe.stevesjam.interfaces.GuiJam;

import java.util.List;

public class ComponentMenuInventory extends ComponentMenu {


    private static final int ARROW_SIZE_W = 6;
    private static final int ARROW_SIZE_H = 10;
    private static final int ARROW_SRC_X = 18;
    private static final int ARROW_SRC_Y = 20;
    private static final int ARROW_X_LEFT = 3;
    private static final int ARROW_X_RIGHT = 111;
    private static final int ARROW_Y = 7;

    private static final int MAX_INVENTORIES = 5;
    private static final int INVENTORY_SIZE = 16;
    private static final int INVENTORY_SIZE_W_WITH_MARGIN = 20;
    private static final int INVENTORY_X = 12;
    private static final int INVENTORY_Y = 5;
    private static final int INVENTORY_SRC_X = 30;
    private static final int INVENTORY_SRC_Y = 20;




    public ComponentMenuInventory(FlowComponent parent) {
        super(parent);
    }

    @Override
    public String getName() {
        return "Inventory";
    }

    @Override
    public void draw(GuiJam gui, int mX, int mY) {
        List<TileEntity> inventories = gui.getJam().getConnectedInventories();

        canScroll = inventories.size() > MAX_INVENTORIES;

        if (!canScroll) {
            offset = 0;
        }

        for (int i = 0; i < inventories.size(); i++) {
            TileEntity te = inventories.get(i);
            int x = getInventoryPosition(i);

            if (x > ARROW_X_LEFT + ARROW_SIZE_W && x + INVENTORY_SIZE < ARROW_X_RIGHT) {


                int srcInventoryX = i == 2 ? 1 : 0;
                int srcInventoryY = GuiJam.inBounds(x, INVENTORY_Y, INVENTORY_SIZE, INVENTORY_SIZE, mX, mY) ? 1 : 0;

                gui.drawTexture(x, INVENTORY_Y, INVENTORY_SRC_X + srcInventoryX * INVENTORY_SIZE, INVENTORY_SRC_Y + srcInventoryY * INVENTORY_SIZE, INVENTORY_SIZE, INVENTORY_SIZE);
                gui.drawItemStack(new ItemStack(te.getBlockType(), 1, te.getBlockMetadata()), x, INVENTORY_Y);
            }
        }

        drawArrow(gui, false, mX, mY);
        drawArrow(gui, true, mX, mY);

        if (clicked) {
            offset += dir;
            int min = (inventories.size() - MAX_INVENTORIES) * -INVENTORY_SIZE_W_WITH_MARGIN;
            int max = 0;

            if (offset < min) {
                offset = min;
            }else if(offset > max) {
                offset = max;
            }
        }
    }

    @Override
    public void drawMouseOver(GuiJam gui, int mX, int mY) {
        List<TileEntity> inventories = gui.getJam().getConnectedInventories();

        for (int i = 0; i < inventories.size(); i++) {
            TileEntity te = inventories.get(i);
            int x = getInventoryPosition(i);

            if (x > ARROW_X_LEFT + ARROW_SIZE_W && x + INVENTORY_SIZE < ARROW_X_RIGHT) {
                if (GuiJam.inBounds(x, INVENTORY_Y, INVENTORY_SIZE, INVENTORY_SIZE, mX, mY)) {
                    String name = ((IInventory)te).getInvName();

                    if (!((IInventory)te).isInvNameLocalized()) {
                        name = StatCollector.translateToLocal(name);
                    }

                    String str = name;
                    str += "\nX: " + te.xCoord + " Y: " + te.yCoord + " Z: " + te.zCoord;
                    str += "\n" + gui.getJam().getDistanceFrom(te.xCoord + 0.5, te.yCoord + 0.5, te.zCoord + 0.5) + " block(s) way";


                    gui.drawMouseOver(str, mX, mY);
                }
            }
        }
    }

    private boolean clicked;
    private int dir;
    private int offset;
    private boolean canScroll;

    private void drawArrow(GuiJam gui, boolean right, int mX, int mY) {
        int srcArrowX = right ? 1 : 0;
        int srcArrowY = canScroll ? clicked && right == (dir == -1) ? 2 : inArrowBounds(right, mX, mY) ? 1 : 0 : 3;

        gui.drawTexture(right ? ARROW_X_RIGHT : ARROW_X_LEFT, ARROW_Y, ARROW_SRC_X + srcArrowX * ARROW_SIZE_W, ARROW_SRC_Y + srcArrowY * ARROW_SIZE_H, ARROW_SIZE_W, ARROW_SIZE_H);
    }

    private boolean inArrowBounds(boolean right, int mX, int mY) {
        return GuiJam.inBounds(right ? ARROW_X_RIGHT : ARROW_X_LEFT, ARROW_Y, ARROW_SIZE_W, ARROW_SIZE_H, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        if (canScroll) {
            if (inArrowBounds(true, mX, mY)) {
                clicked = true;
                dir = -1;
            }else if (inArrowBounds(false, mX, mY)) {
                clicked = true;
                dir = 1;
            }
        }
    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {
        clicked = false;
    }

    private int getInventoryPosition(int i) {
        return INVENTORY_X + i * INVENTORY_SIZE_W_WITH_MARGIN + offset;
    }
}
