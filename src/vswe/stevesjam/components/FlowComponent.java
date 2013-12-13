package vswe.stevesjam.components;


import org.lwjgl.opengl.GL11;
import vswe.stevesjam.interfaces.GuiJam;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class FlowComponent {
    private static final int COMPONENT_SRC_X = 0;
    private static final int COMPONENT_SRC_Y = 0;
    private static final int COMPONENT_SIZE_W = 64;
    private static final int COMPONENT_SIZE_H = 20;
    private static final int COMPONENT_SIZE_LARGE_W = 124;
    private static final int COMPONENT_SIZE_LARGE_H = 152;
    private static final int COMPONENT_SRC_LARGE_X = 64;
    private static final int DRAGGABLE_SIZE = 6;

    private static final int ARROW_X = -10;
    private static final int ARROW_Y = 5;
    private static final int ARROW_SIZE_W = 9;
    private static final int ARROW_SIZE_H = 10;
    private static final int ARROW_SRC_X = 0;
    private static final int ARROW_SRC_Y = 20;

    private static final int MENU_ITEM_SIZE_W = 120;
    private static final int MENU_ITEM_SIZE_H = 13;
    private static final int MENU_ITEM_SRC_X = 0;
    private static final int MENU_ITEM_SRC_Y = 152;
    private static final int MENU_X = 2;
    private static final int MENU_Y = 20;
    private static final int MENU_SIZE_H = 130;
    private static final int MENU_ITEM_CAPACITY = 5;

    private static final int MENU_ARROW_X = 109;
    private static final int MENU_ARROW_Y = 2;
    private static final int MENU_ARROW_SIZE_W = 9;
    private static final int MENU_ARROW_SIZE_H = 9;
    private static final int MENU_ARROW_SRC_X = 0;
    private static final int MENU_ARROW_SRC_Y = 40;

    private static final int MENU_ITEM_TEXT_X = 5;
    private static final int MENU_ITEM_TEXT_Y = 3;

    private static final int CONNECTION_SIZE_W = 7;
    private static final int CONNECTION_SIZE_H = 6;
    private static final int CONNECTION_SRC_X = 0;
    private static final int CONNECTION_SRC_Y = 58;


    public FlowComponent(int x, int y, ComponentType type, ConnectionSet connectionSet) {
        this.x = x;
        this.y = y;
        this.connectionSet = connectionSet;

        menus = new ArrayList<>();
        for (Class<? extends ComponentMenu> componentMenuClass : type.getClasses()) {
            try {
                Constructor<? extends ComponentMenu> constructor = componentMenuClass.getConstructor(FlowComponent.class);
                Object obj = constructor.newInstance(this);


                menus.add((ComponentMenu)obj);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        openMenuId = -1;

    }

    private int x;
    private int y;
    private int mouseDragX;
    private int mouseDragY;
    private boolean isDragging;
    private boolean isLarge;
    private List<ComponentMenu> menus;
    private int openMenuId;
    private ConnectionSet connectionSet;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ConnectionSet getConnectionSet() {
        return connectionSet;
    }

    public void setConnectionSet(ConnectionSet connectionSet) {
        this.connectionSet = connectionSet;
    }

    public void draw(GuiJam gui, int mX, int mY) {
        gui.drawTexture(x, y, isLarge ? COMPONENT_SRC_LARGE_X : COMPONENT_SRC_X, COMPONENT_SRC_Y, getComponentWidth(), getComponentHeight());

        int internalX = mX - x;
        int internalY = mY - y;

        int srcArrowX = isLarge ? 1 : 0;
        int srcArrowY = inArrowBounds(internalX, internalY) ? 1 : 0;
        gui.drawTexture(x + getComponentWidth() + ARROW_X, y + ARROW_Y, ARROW_SRC_X + ARROW_SIZE_W * srcArrowX, ARROW_SRC_Y + ARROW_SIZE_H * srcArrowY, ARROW_SIZE_W, ARROW_SIZE_H);


        if (isLarge) {
            for (int i = 0; i < menus.size(); i++) {
                ComponentMenu menu = menus.get(i);

                int itemX = getMenuAreaX();
                int itemY = y + getMenuItemY(i);
                gui.drawTexture(itemX, itemY, MENU_ITEM_SRC_X, MENU_ITEM_SRC_Y, MENU_ITEM_SIZE_W, MENU_ITEM_SIZE_H);

                int srcItemArrowX = inMenuArrowBounds(i, internalX, internalY) ? 1 : 0;
                int srcItemArrowY = i == openMenuId ? 1 : 0;
                gui.drawTexture(itemX + MENU_ARROW_X, itemY + MENU_ARROW_Y, MENU_ARROW_SRC_X + MENU_ARROW_SIZE_W * srcItemArrowX, MENU_ARROW_SRC_Y + MENU_ARROW_SIZE_H * srcItemArrowY, MENU_ARROW_SIZE_W, MENU_ARROW_SIZE_H);

                gui.drawString(menu.getName(), x + MENU_X + MENU_ITEM_TEXT_X, y + getMenuItemY(i) + MENU_ITEM_TEXT_Y, 0x404040);

                if (i == openMenuId) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(itemX, getMenuAreaY(i), 0);
                    menu.draw(gui, mX - itemX, mY - getMenuAreaY(i));
                    GL11.glPopMatrix();
                }
            }
        }

        int outputCount = 0;
        int inputCount = 0;
        for (ConnectionOption connection : connectionSet.getConnections()) {
            int[] location = getConnectionLocation(connection, inputCount, outputCount);
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }

            int srcConnectionX = (GuiJam.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) ? 1 : 0;

            gui.drawTexture(location[0], location[1], CONNECTION_SRC_X +  srcConnectionX * CONNECTION_SIZE_W, location[2], CONNECTION_SIZE_W, CONNECTION_SIZE_H);
        }
    }



    public void drawMouseOver(GuiJam gui, int mX, int mY) {
        if (isLarge) {
            for (int i = 0; i < menus.size(); i++) {
                ComponentMenu menu = menus.get(i);

                if (i == openMenuId) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(getMenuAreaX(), getMenuAreaY(i), 0);
                    menu.drawMouseOver(gui, mX - getMenuAreaX(), mY - getMenuAreaY(i));
                    GL11.glPopMatrix();
                }
            }
        }

        int outputCount = 0;
        int inputCount = 0;
        for (ConnectionOption connection : connectionSet.getConnections()) {
            int[] location = getConnectionLocation(connection, inputCount, outputCount);
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }

            if (GuiJam.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) {
                gui.drawMouseOver(connection.toString(), mX, mY);
            }
        }
    }

    public void onClick(int mX, int mY) {
        if (GuiJam.inBounds(x, y, getComponentWidth(), getComponentHeight(), mX, mY)) {
           int internalX = mX - x;
           int internalY = mY - y;

            if (internalX <= DRAGGABLE_SIZE && internalY <= DRAGGABLE_SIZE) {
                mouseDragX = mX;
                mouseDragY = mY;
                isDragging = true;
            }else if(inArrowBounds(internalX, internalY)) {
                isLarge = !isLarge;
            }else if (isLarge){

                for (int i = 0; i < menus.size(); i++) {
                    ComponentMenu menu = menus.get(i);

                    if (inMenuArrowBounds(i, internalX, internalY)) {
                        if (openMenuId == i) {
                            openMenuId = -1;
                        }else{
                            openMenuId = i;
                        }

                        return;
                    }

                    menu.onClick(mX - getMenuAreaX(), mY - getMenuAreaY(i));
                }

            }
        }else{
            int outputCount = 0;
            int inputCount = 0;
            for (ConnectionOption connection : connectionSet.getConnections()) {
                int[] location = getConnectionLocation(connection, inputCount, outputCount);
                if (connection.isInput()) {
                    inputCount++;
                }else{
                    outputCount++;
                }

                if (GuiJam.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) {
                    System.out.println("Connection");
                }
            }
        }
    }


    public void onDrag(int mX, int mY) {
        followMouse(mX, mY);

        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);
            menu.onDrag(mX - getMenuAreaX(), mY - getMenuAreaY(i));
        }
    }

    public void onRelease(int mX, int mY) {
        followMouse(mX, mY);
        isDragging = false;

        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);
            menu.onRelease(mX - getMenuAreaX(), mY - getMenuAreaY(i));
        }
    }

    private void followMouse(int mX, int mY) {
        if (isDragging) {
            x += mX - mouseDragX;
            y += mY - mouseDragY;

            mouseDragX = mX;
            mouseDragY = mY;
        }
    }


    private boolean inArrowBounds(int internalX, int internalY) {
        return GuiJam.inBounds(getComponentWidth() + ARROW_X, ARROW_Y, ARROW_SIZE_W, ARROW_SIZE_H, internalX, internalY);
    }


    private boolean inMenuArrowBounds(int i, int internalX, int internalY) {
        return GuiJam.inBounds(MENU_X + MENU_ARROW_X, getMenuItemY(i) + MENU_ARROW_Y, MENU_ARROW_SIZE_W, MENU_ARROW_SIZE_H, internalX, internalY);
    }

    private int getMenuItemY(int i) {
        int ret = MENU_Y + i * (MENU_ITEM_SIZE_H - 1);
        if (openMenuId != -1 && openMenuId < i) {
            ret += MENU_SIZE_H - (MENU_ITEM_CAPACITY * (MENU_ITEM_SIZE_H - 1) + 1);
        }

        return ret;
    }




    private int getComponentWidth() {
        return isLarge ? COMPONENT_SIZE_LARGE_W : COMPONENT_SIZE_W;
    }

    private int getComponentHeight() {
        return isLarge ? COMPONENT_SIZE_LARGE_H : COMPONENT_SIZE_H;
    }

    private int[] getConnectionLocation(ConnectionOption connection, int inputCount, int outputCount) {
        int targetX;
        int targetY;

        int srcConnectionY;
        int currentCount;
        int totalCount;

        if (connection.isInput()) {
            currentCount = inputCount;
            totalCount = connectionSet.getInputCount();
            srcConnectionY = 1;
            targetY = y - CONNECTION_SIZE_H;
        }else{
            currentCount = outputCount;
            totalCount = connectionSet.getOutputCount();
            srcConnectionY = 0;
            targetY = y + getComponentHeight();
        }

        targetX = x + (int)(getComponentWidth() * ((currentCount + 0.5)  / totalCount));
        targetX -= CONNECTION_SIZE_W / 2;

        return new int[] {targetX, targetY, CONNECTION_SRC_Y + srcConnectionY * CONNECTION_SIZE_H};
    }


    private int getMenuAreaX() {
        return x + MENU_X;
    }

    private int getMenuAreaY(int i) {
        return  y + getMenuItemY(i) + MENU_ITEM_SIZE_H;
    }

    public boolean onKeyStroke(GuiJam gui, char c, int k) {
        if (isLarge && openMenuId != -1) {
            return menus.get(openMenuId).onKeyStroke(gui, c, k);
        }

        return false;
    }
}
