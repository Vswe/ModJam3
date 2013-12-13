package vswe.stevesjam.components;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;
import vswe.stevesjam.interfaces.GuiJam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComponentMenuItem extends ComponentMenu {


    public ComponentMenuItem(FlowComponent parent) {
        super(parent);

        text = "";;
    }


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
    private static final int CURSOR_Z = 200;

    private boolean selected;
    private String text;
    private int cursor;
    private int cursorPosition;

    @Override
    public String getName() {
        return "Items";
    }

    @Override
    public void draw(GuiJam gui, int mX, int mY) {
        int srcBoxY = selected ? 1 : 0;

        gui.drawTexture(TEXT_BOX_X, TEXT_BOX_Y, TEXT_BOX_SRC_X, TEXT_BOX_SRC_Y + srcBoxY * TEXT_BOX_SIZE_H, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H);
        gui.drawString(text, TEXT_BOX_X + TEXT_BOX_TEXT_X, TEXT_BOX_Y + TEXT_BOX_TEXT_Y, 0xFFFFFF);

        if (selected) {
            gui.drawCursor(TEXT_BOX_X + cursorPosition + CURSOR_X, TEXT_BOX_Y + CURSOR_Y, CURSOR_Z, 0xFFFFFFFF);
        }
    }

    @Override
    public void drawMouseOver(GuiJam gui, int mX, int mY) {

    }

    @Override
    public void onClick(int mX, int mY) {
        if (GuiJam.inBounds(TEXT_BOX_X, TEXT_BOX_Y, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H, mX, mY)) {
            selected = !selected;
        }
    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {

    }

    @Override
    public boolean onKeyStroke(GuiJam gui, char c, int k) {
        if (selected) {
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

            return true;
        }else{
            return false;
        }
    }

    private void addText(GuiJam gui, String str) {
        text = text.substring(0, cursor) + str + text.substring(cursor);

        moveCursor(gui, str.length());
        textChanged();
    }

    private void deleteText(GuiJam gui, int direction) {
        if (cursor + direction >= 0 && cursor + direction <= text.length()) {
            if (direction > 0) {
                text = text.substring(0, cursor) + text.substring(cursor + 1);
            }else{
                text = text.substring(0, cursor - 1) + text.substring(cursor);
            }
            moveCursor(gui, direction);
            textChanged();
        }
    }

    private void moveCursor(GuiJam gui, int steps) {
        cursor += steps;

        if (cursor < 0) {
            cursor = 0;
        }else if (cursor > text.length()) {
            cursor = text.length();
        }

        cursorPosition = gui.getStringWidth(text.substring(0, cursor));
    }

    private void textChanged() {
        if (text.length() > 0) {
            updateSearch();
        }
    }

    private void updateSearch() {
        List<ItemStack> result = new ArrayList<>();
        Item[] items = Item.itemsList;
        int itemLength = items.length;

        for (int i = 0; i < itemLength; ++i) {
            Item item = items[i];

            if (item != null && item.getCreativeTab() != null) {
                item.getSubItems(item.itemID, null, result);
            }
        }

        Iterator<ItemStack> itemIterator = result.iterator();
        String searchString = text.toLowerCase();

        while (itemIterator.hasNext()) {

            ItemStack itemStack = itemIterator.next();
            Iterator<String> descriptionIterator = itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips).iterator();

            boolean foundSequence = false;

            while (descriptionIterator.hasNext()) {
                String line = descriptionIterator.next().toLowerCase();
                if (line.contains(searchString)) {
                    foundSequence = true;
                    break;
                }
            }

            if (!foundSequence) {
                itemIterator.remove();
            }
        }

        System.out.println(result.size());
    }
}
