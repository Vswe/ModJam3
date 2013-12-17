package vswe.stevesfactory.components;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class ItemSetting {
    private int id;
    private ItemStack item;
    private boolean isFuzzy;
    private boolean isLimitedByAmount;

    public ItemSetting(int id) {
        this.id = id;
    }

    public List<String> getMouseOver() {
        if (item != null && GuiScreen.isShiftKeyDown()) {
            return ComponentMenuItem.getToolTip(item);
        }

        List<String> ret = new ArrayList<>();

        if (item == null) {
            ret.add("[No item selected]");
        }else{
            ret.add(ComponentMenuItem.getDisplayName(item));
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

    public int getId() {
        return id;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public boolean isFuzzy() {
        return isFuzzy;
    }

    public void setFuzzy(boolean fuzzy) {
        isFuzzy = fuzzy;
    }

    public boolean isLimitedByAmount() {
        return isLimitedByAmount;
    }

    public void setLimitedByAmount(boolean limitedByAmount) {
        isLimitedByAmount = limitedByAmount;
    }
}