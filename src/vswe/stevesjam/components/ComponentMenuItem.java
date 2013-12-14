package vswe.stevesjam.components;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.interfaces.GuiJam;
import vswe.stevesjam.network.DataBitHelper;
import vswe.stevesjam.network.DataReader;
import vswe.stevesjam.network.DataWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComponentMenuItem extends ComponentMenu {



    public ComponentMenuItem(FlowComponent parent) {
        super(parent);

        text = "";
        result = new ArrayList<>();
        settings = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            settings.add(new ItemSetting());
        }
        numberTextBoxes = new TextBoxNumberList();
        numberTextBoxes.addTextBox(amountTextBox = new TextBoxNumber(80 ,24, 3, true) {
            @Override
            public boolean isVisible() {
                return selectedSetting.isLimitedByAmount;
            }

            @Override
            public void setNumber(int number) {
                super.setNumber(number);
                selectedSetting.item.stackSize = number;
            }
        });
        numberTextBoxes.addTextBox(damageValueTextBox = new TextBoxNumber(70 ,52, 5, true) {
            @Override
            public boolean isVisible() {
                return !selectedSetting.isFuzzy;
            }

            @Override
            public void setNumber(int number) {
                super.setNumber(number);
                selectedSetting.item.setItemDamage(number);
            }
        });
        updateScrolling();
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
    private static final int AMOUNT_TEXT_X = 75;
    private static final int AMOUNT_TEXT_Y = 9;

    private static final int ARROW_SIZE_W = 10;
    private static final int ARROW_SIZE_H = 6;
    private static final int ARROW_SRC_X = 64;
    private static final int ARROW_SRC_Y = 165;
    private static final int ARROW_X = 105;
    private static final int ARROW_Y_UP = 32;
    private static final int ARROW_Y_DOWN = 42;


    private static final int ITEMS_PER_ROW = 5;
    private static final int VISIBLE_ROWS = 2;
    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SIZE_WITH_MARGIN = 20;
    private static final int ITEM_X = 5;
    private static final int ITEM_Y = 20;

    private static final int SETTING_SRC_X = 0;
    private static final int SETTING_SRC_Y = 189;

    private static final int EDIT_ITEM_X = 5;
    private static final int EDIT_ITEM_Y = 5;

    private static final int CHECK_BOX_SIZE = 8;
    private static final int CHECK_BOX_SRC_X = 42;
    private static final int CHECK_BOX_SRC_Y = 106;
    private static final int CHECK_BOX_TEXT_X = 12;
    private static final int CHECK_BOX_TEXT_Y = 2;

    private static final int DMG_VAL_TEXT_X = 15;
    private static final int DMG_VAL_TEXT_Y = 55;

    private static final int BACK_SRC_X = 46;
    private static final int BACK_SRC_Y = 52;
    private static final int BACK_SIZE_W = 9;
    private static final int BACK_SIZE_H = 9;
    private static final int BACK_X = 108;
    private static final int BACK_Y = 57;

    private static final int DELETE_SRC_X = 0;
    private static final int DELETE_SRC_Y = 130;
    private static final int DELETE_SIZE_W = 32;
    private static final int DELETE_SIZE_H = 11;
    private static final int DELETE_X = 85;
    private static final int DELETE_Y = 3;
    private static final int DELETE_TEXT_Y = 3;

    private int offsetItems;
    private int offsetSettings;
    private boolean canScroll;
    private int dir;
    private boolean clicked;
    private boolean selected;
    private String text;
    private int cursor;
    private int cursorPosition;
    private List<ItemStack> result;
    private List<ItemSetting> settings;
    private ItemSetting selectedSetting;
    private boolean editSetting;
    private TextBoxNumberList numberTextBoxes;
    private TextBoxNumber amountTextBox;
    private TextBoxNumber damageValueTextBox;

    private CheckBox[] checkBoxes = {new CheckBox("Specify amount?", 5, 25) {
        @Override
        public void setValue(boolean val) {
            selectedSetting.isLimitedByAmount = val;
        }

        @Override
        public boolean getValue() {
            return selectedSetting.isLimitedByAmount;
        }
    },
    new CheckBox("Is detection fuzzy?", 5, 40) {
        @Override
        public void setValue(boolean val) {
            selectedSetting.isFuzzy = val;
        }

        @Override
        public boolean getValue() {
            return selectedSetting.isFuzzy;
        }
    }};


    @Override
    public String getName() {
        return "Items";
    }

    @Override
    public void draw(GuiJam gui, int mX, int mY) {
        if (isEditing()) {
            gui.drawItemStack(selectedSetting.item, EDIT_ITEM_X, EDIT_ITEM_Y);

            for (CheckBox checkBox : checkBoxes) {
                int srcCheckBoxX = checkBox.getValue() ? 1 : 0;
                int srcCheckBoxY = GuiJam.inBounds(checkBox.x, checkBox.y, CHECK_BOX_SIZE, CHECK_BOX_SIZE, mX, mY) ? 1 : 0;

                gui.drawTexture(checkBox.x, checkBox.y, CHECK_BOX_SRC_X + srcCheckBoxX * CHECK_BOX_SIZE, CHECK_BOX_SRC_Y + srcCheckBoxY * CHECK_BOX_SIZE, CHECK_BOX_SIZE, CHECK_BOX_SIZE);
                gui.drawString(checkBox.name, checkBox.x + CHECK_BOX_TEXT_X, checkBox.y + CHECK_BOX_TEXT_Y, 0.7F, 0x404040);
            }

            if (!selectedSetting.isFuzzy) {
                gui.drawString("Damage value", DMG_VAL_TEXT_X, DMG_VAL_TEXT_Y, 0.7F, 0x404040);
            }
            numberTextBoxes.draw(gui, mX, mY);


            int srcDeleteY = inDeleteBounds(mX, mY) ? 1 : 0;
            gui.drawTexture(DELETE_X, DELETE_Y, DELETE_SRC_X, DELETE_SRC_Y + srcDeleteY * DELETE_SIZE_H, DELETE_SIZE_W, DELETE_SIZE_H);
            gui.drawCenteredString("Delete", DELETE_X, DELETE_Y + DELETE_TEXT_Y, 0.7F, DELETE_SIZE_W, 0xBB4040);
        }else if (isSearching()) {
            int srcBoxY = selected ? 1 : 0;

            gui.drawTexture(TEXT_BOX_X, TEXT_BOX_Y, TEXT_BOX_SRC_X, TEXT_BOX_SRC_Y + srcBoxY * TEXT_BOX_SIZE_H, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H);
            gui.drawString(text, TEXT_BOX_X + TEXT_BOX_TEXT_X, TEXT_BOX_Y + TEXT_BOX_TEXT_Y, 0xFFFFFF);

            if (selected) {
                gui.drawCursor(TEXT_BOX_X + cursorPosition + CURSOR_X, TEXT_BOX_Y + CURSOR_Y, CURSOR_Z, 0xFFFFFFFF);
            }

            if (isScrollingVisible()) {
                gui.drawString("Found " + result.size(), AMOUNT_TEXT_X, AMOUNT_TEXT_Y, 0.7F, 0x404040);

                List<Point> points = getItemCoordinates();
                for (Point point : points) {
                    gui.drawItemStack(result.get(point.id), point.x, point.y);
                }
            }
        }else{

            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                ItemSetting setting = settings.get(point.id);

                int srcSettingX = setting.item == null ? 1 : 0;
                int srcSettingY = GuiJam.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY) ? 1 : 0;

                gui.drawTexture(point.x, point.y, SETTING_SRC_X + srcSettingX * ITEM_SIZE, SETTING_SRC_Y + srcSettingY * ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
                if (setting.item != null) {
                    gui.drawItemStack(setting.item, point.x, point.y);
                }
            }

        }

        if (isScrollingVisible()) {
            drawArrow(gui, true, mX, mY);
            drawArrow(gui, false, mX, mY);

            if (clicked && canScroll) {
                setOffset(getOffset() + dir);
                int min = 0;
                int max = ((int)(Math.ceil(((float)getScrollingList().size() / ITEMS_PER_ROW)) - VISIBLE_ROWS)) * ITEM_SIZE_WITH_MARGIN - (ITEM_SIZE_WITH_MARGIN - ITEM_SIZE);
                if (getOffset() < min) {
                    setOffset(min);
                }else if(getOffset() > max) {
                    setOffset(max);
                }

            }
        }

        if (selectedSetting != null) {
            int srcBackX = inBackBounds(mX, mY) ? 1 : 0;

            gui.drawTexture(BACK_X, BACK_Y, BACK_SRC_X + srcBackX * BACK_SIZE_W, BACK_SRC_Y, BACK_SIZE_W, BACK_SIZE_H);
        }
    }

    private boolean inBackBounds(int mX, int mY) {
        return GuiJam.inBounds(BACK_X, BACK_Y, BACK_SIZE_W, BACK_SIZE_H, mX, mY);
    }

    private boolean inDeleteBounds(int mX, int mY) {
        return GuiJam.inBounds(DELETE_X, DELETE_Y, DELETE_SIZE_W, DELETE_SIZE_H, mX, mY);
    }

    private boolean isScrollingVisible() {
        return !isEditing() && getScrollingList().size() > 0;
    }

    private List getScrollingList() {
        return isSearching() ? result : settings;
    }

    private int getOffset() {
        return isSearching() ? offsetItems : offsetSettings;
    }

    private void setOffset(int val) {
        if (isSearching()) {
            offsetItems = val;
        }else{
            offsetSettings = val;
        }
    }

    private void updateScrolling() {
        canScroll = getScrollingList().size() > ITEMS_PER_ROW * VISIBLE_ROWS;
        if (!canScroll) {
            setOffset(0);
        }
    }

    private int getFirstRow() {
        return (TEXT_BOX_Y + TEXT_BOX_SIZE_H + getOffset() - ITEM_Y) / ITEM_SIZE_WITH_MARGIN;
    }

    private void drawArrow(GuiJam gui, boolean down, int mX, int mY) {
        int srcArrowX = canScroll ? clicked && down == (dir == 1) ? 2 : inArrowBounds(down, mX, mY) ? 1 : 0 : 3;
        int srcArrowY = down ? 1 : 0;

        gui.drawTexture(ARROW_X, down ? ARROW_Y_DOWN : ARROW_Y_UP, ARROW_SRC_X + srcArrowX * ARROW_SIZE_W, ARROW_SRC_Y + srcArrowY * ARROW_SIZE_H, ARROW_SIZE_W, ARROW_SIZE_H);
    }

    private boolean inArrowBounds(boolean down, int mX, int mY) {
        return GuiJam.inBounds(ARROW_X, down ? ARROW_Y_DOWN : ARROW_Y_UP, ARROW_SIZE_W, ARROW_SIZE_H, mX, mY);
    }


    @Override
    public void drawMouseOver(GuiJam gui, int mX, int mY) {
        if (isEditing()) {
            if (GuiJam.inBounds(EDIT_ITEM_X, EDIT_ITEM_Y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                gui.drawMouseOver(getToolTip(selectedSetting.item), mX, mY);
            }else if(inDeleteBounds(mX, mY)) {
                gui.drawMouseOver("Delete this item selection", mX, mY);
            }
        }

        if (isScrollingVisible()) {
            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                if (GuiJam.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                    if (isSearching()) {
                        gui.drawMouseOver(getToolTip(result.get(point.id)), mX, mY);
                    }else{
                        gui.drawMouseOver(settings.get(point.id).getMouseOver(), mX, mY);
                    }


                }
            }
        }

        if (selectedSetting != null && inBackBounds(mX, mY)) {
            gui.drawMouseOver(isEditing() ? "Go back" : "Cancel", mX, mY);
        }
    }


    private List<Point> getItemCoordinates() {
        List<Point> points = new ArrayList<>();

        int start = getFirstRow();
        for (int row = start; row < start + VISIBLE_ROWS + 1; row++) {
            for (int col = 0; col < ITEMS_PER_ROW; col++) {
                int id = row * ITEMS_PER_ROW + col;
                if (id >= 0 && id < getScrollingList().size())  {
                    int y = ITEM_Y + row * ITEM_SIZE_WITH_MARGIN - getOffset();
                    if (y > TEXT_BOX_Y + TEXT_BOX_SIZE_H && y + ITEM_SIZE < FlowComponent.getMenuOpenSize()) {
                        points.add(new Point(id, ITEM_X + ITEM_SIZE_WITH_MARGIN * col, y));
                    }
                }
            }
        }

        return points;
    }

    @Override
    public void onClick(int mX, int mY, int button) {
        if (isEditing()) {
            for (CheckBox checkBox : checkBoxes) {
                if (GuiJam.inBounds(checkBox.x, checkBox.y, CHECK_BOX_SIZE, CHECK_BOX_SIZE, mX, mY)) {
                    checkBox.setValue(!checkBox.getValue());
                    break;
                }
            }

            numberTextBoxes.onClick(mX, mY, button);

            if (inDeleteBounds(mX, mY)) {
                selectedSetting.clear();
                selectedSetting = null;
                updateScrolling();
            }
        }else if (isSearching()) {
            if (GuiJam.inBounds(TEXT_BOX_X, TEXT_BOX_Y, TEXT_BOX_SIZE_W, TEXT_BOX_SIZE_H, mX, mY)) {
                selected = !selected;
            }

            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                if (GuiJam.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                    selectedSetting.item = result.get(point.id).copy();
                    selectedSetting = null;
                    updateScrolling();
                    break;
                }
            }
        }else{
            List<Point> points = getItemCoordinates();
            for (Point point : points) {
                if (GuiJam.inBounds(point.x, point.y, ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                    selectedSetting = settings.get(point.id);
                    editSetting = button == 1;


                    if (editSetting && selectedSetting.item == null) {
                        selectedSetting = null;
                        editSetting = false;
                    }else{
                        if (editSetting) {
                            amountTextBox.setNumber(selectedSetting.item.stackSize);
                            damageValueTextBox.setNumber(selectedSetting.item.getItemDamage());
                        }
                        updateScrolling();
                    }

                    break;
                }
            }
        }

        if (isScrollingVisible() && canScroll) {
            if(inArrowBounds(true, mX, mY)) {
                clicked = true;
                dir = 1;
            }else if (inArrowBounds(false, mX, mY)){
                clicked = true;
                dir = -1;
            }
        }
        if (selectedSetting != null && inBackBounds(mX, mY)) {
            selectedSetting = null;
            updateScrolling();
        }
    }

    private boolean isEditing() {
        return selectedSetting != null && editSetting;
    }

    private boolean isSearching() {
        return selectedSetting != null && !editSetting;
    }

    @Override
    public void onDrag(int mX, int mY) {

    }

    @Override
    public void onRelease(int mX, int mY) {
        clicked = false;
    }

    @Override
    public boolean onKeyStroke(GuiJam gui, char c, int k) {
        if (selected && isSearching()) {
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
        }else if (isEditing()){
            return numberTextBoxes.onKeyStroke(gui, c, k);
        }else{
            return false;
        }
    }

    @Override
    public void writeData(DataWriter dw, TileEntityJam jam) {
        for (ItemSetting setting : settings) {
            dw.writeBoolean(setting.item != null);
            if (setting.item != null) {
                dw.writeData(setting.item.itemID, DataBitHelper.MENU_ITEM_ID);
                dw.writeBoolean(setting.isFuzzy);
                dw.writeData(setting.item.getItemDamage(), DataBitHelper.MENU_ITEM_META);
                dw.writeBoolean(setting.isLimitedByAmount);
                if (setting.isLimitedByAmount) {
                    dw.writeData(setting.item.stackSize, DataBitHelper.MENU_ITEM_AMOUNT);
                }
            }
        }
    }

    @Override
    public void readData(DataReader dr, TileEntityJam jam) {
        for (ItemSetting setting : settings) {
            if (!dr.readBoolean()) {
                setting.clear();
            }else{
                int id = dr.readData(DataBitHelper.MENU_ITEM_ID);
                setting.isFuzzy = dr.readBoolean();
                int meta = dr.readData(DataBitHelper.MENU_ITEM_META);
                setting.isLimitedByAmount = dr.readBoolean();
                int amount;
                if (setting.isLimitedByAmount) {
                    amount = dr.readData(DataBitHelper.MENU_ITEM_AMOUNT);
                }else{
                    amount = 1;
                }

                setting.item = new ItemStack(id, amount, meta);
            }
        }
    }

    @Override
    public void readDataOnServer(DataReader dr) {
        //To change body of implemented methods use File | Settings | File Templates.
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
        }else{
            result.clear();
            updateScrolling();
        }
    }

    private void updateSearch() {
        result.clear();
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

        updateScrolling();
    }


    private class ItemSetting {
        private ItemStack item;
        private boolean isFuzzy;
        private boolean isLimitedByAmount;

        public List<String> getMouseOver() {
            if (item != null && GuiScreen.isShiftKeyDown()) {
                return getToolTip(item);
            }

            List<String> ret = new ArrayList<>();

            if (item == null) {
                ret.add("[No item selected]");
            }else{
                ret.add(getDisplayName(item));
            }

            ret.add("");
            ret.add("Left click to change item");
            if (item != null) {
                ret.add("Right click to edit settings");
                ret.add("Hold shift to see the item's full description");
            }

            return ret;
        }

        public void clear() {
            item = null;
            isFuzzy = false;
            isLimitedByAmount = false;
        }
    }

    private class Point {
        int id, x, y;

        private Point(int id, int x, int y) {
            this.id = id;
            this.x = x;
            this.y = y;
        }
    }

    private abstract class CheckBox {
        int x, y;
        String name;

        private CheckBox( String name, int x, int y) {
            this.x = x;
            this.y = y;
            this.name = name;
        }

        public abstract void setValue(boolean val);
        public abstract boolean getValue();
    }

    public List<String> getToolTip(ItemStack itemStack) {
        try {
            return itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        }catch (Exception ex) {
            if (itemStack.getItemDamage() == 0) {
                return new ArrayList<>();
            }else{
                ItemStack newItem = itemStack.copy();
                newItem.setItemDamage(0);
                return getToolTip(newItem);
            }
        }
    }

    public String getDisplayName(ItemStack itemStack) {
        try {
            return itemStack.getDisplayName();
        }catch (Exception ex) {
            if (itemStack.getItemDamage() == 0) {
                return "";
            }else{
                ItemStack newItem = itemStack.copy();
                newItem.setItemDamage(0);
                return getDisplayName(newItem);
            }
        }
    }
}
