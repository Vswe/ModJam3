package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.GuiManager;

import java.util.ArrayList;
import java.util.List;

public  abstract class ScrollController<T> {

    private int offset;
    private boolean canScroll;
    private int dir;
    private boolean clicked;
    private boolean selected;
    private TextBoxLogic textBox;
    private List<T> result;
    private boolean hasSearchBox;

    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SIZE_WITH_MARGIN = 20;

    private static final int ARROW_SIZE_W = 10;
    private static final int ARROW_SIZE_H = 6;
    private static final int ARROW_SRC_X = 64;
    private static final int ARROW_SRC_Y = 165;
    private static final int ARROW_X = 105;
    private static final int ARROW_Y_UP = 32;
    private static final int ARROW_Y_DOWN = 42;

    private static final int TEXT_BOX_SIZE_W = 64;
    private static final int TEXT_BOX_SIZE_H = 12;
    private static final int TEXT_BOX_SRC_X = 0;
    private static final int TEXT_BOX_SRC_Y = 165;
    private static final int TEXT_BOX_X = 5;
    private static final int TEXT_BOX_Y = 5;
    private static final int TEXT_BOX_TEXT_X = 3;
    private static final int TEXT_BOX_TEXT_Y = 3;
    private static final int CURSOR_X = 2;
    private static final int CURSOR_Y = 0;
    private static final int CURSOR_Z = 5;
    private static final int AMOUNT_TEXT_X = 75;
    private static final int AMOUNT_TEXT_Y = 9;


    private int itemsPerRow = 5;
    private int visibleRows = 2;
    private int startX = 5;
    private int scrollingUpperLimit = TEXT_BOX_Y + TEXT_BOX_SIZE_H;
    private boolean disabledScroll;

    public ScrollController(boolean hasSearchBox) {
        this(hasSearchBox ? "" : null);
    }

    public ScrollController(String defaultText) {
        this.hasSearchBox = defaultText != null;
        if (hasSearchBox) {
            textBox = new TextBoxLogic(Integer.MAX_VALUE, TEXT_BOX_SIZE_W - TEXT_BOX_TEXT_X * 2) {
                @Override
                protected void textChanged() {
                    if (getText().length() > 0) {
                        updateSearch();
                    }else{
                        result.clear();
                        updateScrolling();
                    }
                }
            };

            textBox.setTextAndCursor(defaultText);
        }

        updateSearch();
    }


    protected abstract List<T> updateSearch(String search, boolean all);
    @SideOnly(Side.CLIENT)
    protected abstract void onClick(T t, int mX, int mY, int button);
    @SideOnly(Side.CLIENT)
    protected abstract void draw(GuiManager gui, T t, int x, int y, boolean hover);
    @SideOnly(Side.CLIENT)
    protected abstract void drawMouseOver(GuiManager gui, T t, int mX, int mY);


    public void setX(int val) {
        startX = val;
    }

    public int getScrollingStartX() {
        return startX;
    }

    public int getScrollingStartY() {
        return scrollingUpperLimit + 3;
    }


    private int getFirstRow() {
        return (scrollingUpperLimit + offset - getScrollingStartY()) / ITEM_SIZE_WITH_MARGIN;
    }

    private List<Point> getItemCoordinates() {
        List<Point> points = new ArrayList<Point>();

        int start = getFirstRow();
        for (int row = start; row < start + visibleRows + 1; row++) {
            for (int col = 0; col < itemsPerRow; col++) {
                int id = row * itemsPerRow + col;
                if (id >= 0 && id < result.size())  {
                    int x = getScrollingStartX() + ITEM_SIZE_WITH_MARGIN * col;
                    int y = getScrollingStartY() + row * ITEM_SIZE_WITH_MARGIN - offset;
                    if (y > scrollingUpperLimit && y + ITEM_SIZE < FlowComponent.getMenuOpenSize()) {
                        points.add(new Point(id, x, y));
                    }
                }
            }
        }

        return points;
    }

    public void onClick(int mX, int mY, int button) {
        if (CollisionHelper.inBounds(TEXT_BOX_X, TEXT_BOX_Y, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H, mX, mY)) {
            if (button == 0 || !selected) {
                selected = !selected;
            }else if (hasSearchBox){
                textBox.setTextAndCursor("");
            }
        }

        List<Point> points = getItemCoordinates();
        for (Point point : points) {
            if (CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                onClick(result.get(point.id), mX, mY, button);
                break;
            }
        }

        if(inArrowBounds(true, mX, mY)) {
            clicked = true;
            dir = 1;
        }else if (inArrowBounds(false, mX, mY)){
            clicked = true;
            dir = -1;
        }
    }




    public void onRelease(int mX, int mY) {
        clicked = false;
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        if (selected && hasSearchBox) {
            textBox.onKeyStroke(gui, c, k);

            return true;
        }else{
            return false;
        }
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY) {
        int srcBoxY = selected ? 1 : 0;

        if (hasSearchBox) {
            gui.drawTexture(TEXT_BOX_X, TEXT_BOX_Y, TEXT_BOX_SRC_X, TEXT_BOX_SRC_Y + srcBoxY * TEXT_BOX_SIZE_H, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H);
            gui.drawString(textBox.getText(), TEXT_BOX_X + TEXT_BOX_TEXT_X, TEXT_BOX_Y + TEXT_BOX_TEXT_Y, 0xFFFFFF);

            if (selected) {
                gui.drawCursor(TEXT_BOX_X + textBox.getCursorPosition(gui) + CURSOR_X, TEXT_BOX_Y + CURSOR_Y, CURSOR_Z, 0xFFFFFFFF);
            }

            if (textBox.getText().length() > 0 || result.size() > 0) {
                gui.drawString(Localization.ITEMS_FOUND.toString() + " " + result.size(), AMOUNT_TEXT_X, AMOUNT_TEXT_Y, 0.7F, 0x404040);
            }
        }

        if (result.size() > 0) {
            drawArrow(gui, true, mX, mY);
            drawArrow(gui, false, mX, mY);

            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                draw(gui, result.get(point.id), point.x, point.y, CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY));
            }
        }
    }

    private static final int SCROLL_SPEED = 100;

    private float left;
    public void update(float partial) {
        if (clicked && canScroll) {
            partial += left;
            int change = (int)(partial * SCROLL_SPEED);
            left = partial - (change / (float)SCROLL_SPEED);


            moveOffset(change * dir);
        }
    }

    private void moveOffset(int change) {
        offset += change;
        int min = 0;
        int max = ((int)(Math.ceil(((float)result.size() / itemsPerRow)) - visibleRows)) * ITEM_SIZE_WITH_MARGIN - (ITEM_SIZE_WITH_MARGIN - ITEM_SIZE);
        if (offset < min) {
            offset = min;
        }else if(offset > max) {
            offset = max;
        }
    }

    @SideOnly(Side.CLIENT)
    private void drawArrow(GuiManager gui, boolean down, int mX, int mY) {
        if (canScroll) {
            int srcArrowX = canScroll ? clicked && down == (dir == 1) ? 2 : inArrowBounds(down, mX, mY) ? 1 : 0 : 3;
            int srcArrowY = down ? 1 : 0;

            gui.drawTexture(ARROW_X, down ? ARROW_Y_DOWN : ARROW_Y_UP, ARROW_SRC_X + srcArrowX * ARROW_SIZE_W, ARROW_SRC_Y + srcArrowY * ARROW_SIZE_H, ARROW_SIZE_W, ARROW_SIZE_H);
        }
    }

    private boolean inArrowBounds(boolean down, int mX, int mY) {
        return CollisionHelper.inBounds(ARROW_X, down ? ARROW_Y_DOWN : ARROW_Y_UP, ARROW_SIZE_W, ARROW_SIZE_H, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        List<Point> points = getItemCoordinates();
        for (Point point : points) {
            if (CollisionHelper.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                drawMouseOver(gui, result.get(point.id), mX, mY);
            }
        }
    }



    public void updateScrolling() {
        canScroll = result.size() > itemsPerRow * visibleRows;
        if (!canScroll) {
            offset = 0;
        }
    }

    public void setItemsPerRow(int n) {
        itemsPerRow = n;
    }

    public void setVisibleRows(int n) {
        visibleRows = n;
    }

    public void setItemUpperLimit(int n) {
        scrollingUpperLimit = n;
    }

    public void updateSearch() {
        if (hasSearchBox) {
            result = updateSearch(textBox.getText().toLowerCase(), textBox.getText().toLowerCase().equals(".all"));
        }else{
            result = updateSearch("", false);
        }
        updateScrolling();
    }

    public List<T> getResult() {
        return result;
    }

    public void setText(String s) {
        textBox.setText(s);
        updateSearch();
    }

    public String getText() {
        return textBox.getText();
    }

    public void setTextAndCursor(String s) {
        textBox.setTextAndCursor(s);
    }

    public void doScroll(int scroll) {
        if (!disabledScroll) {
            moveOffset(scroll / -20);
        }
    }

    public void setDisabledScroll(boolean disabledScroll) {
        this.disabledScroll = disabledScroll;
    }


    private class Point {
        int id, x, y;

        private Point(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }
}
