package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.ArrayList;
import java.util.List;

public class ItemSetting extends Setting {
    private boolean isFuzzy;
    private ItemStack item;

    public ItemSetting(int id) {
        super(id);
    }

    @Override
    public List<String> getMouseOver() {
        if (item != null && GuiScreen.isShiftKeyDown()) {
            return ComponentMenuItem.getToolTip(item);
        }

        List<String> ret = new ArrayList<String>();

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

    @Override
    public void clear() {
        super.clear();

        isFuzzy = false;
        item = null;
    }

    @Override
    public int getAmount() {
        return item == null ? 0 : item.stackSize;
    }

    @Override
    public void setAmount(int val) {
        if (item != null) {
            item.stackSize = val;
        }
    }

    @Override
    public boolean isValid() {
        return item != null;
    }

    public boolean isFuzzy() {
        return isFuzzy;
    }

    public void setFuzzy(boolean fuzzy) {
        isFuzzy = fuzzy;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public void writeData(DataWriter dw) {
        dw.writeData(item.itemID, DataBitHelper.MENU_ITEM_ID);
        dw.writeBoolean(isFuzzy);
        dw.writeData(item.getItemDamage(), DataBitHelper.MENU_ITEM_META);
    }

    @Override
    public void readData(DataReader dr) {
        int id = dr.readData(DataBitHelper.MENU_ITEM_ID);
        isFuzzy = dr.readBoolean();
        int meta = dr.readData(DataBitHelper.MENU_ITEM_META);
        item = new ItemStack(id, 1, meta);
    }

    @Override
    public void copyFrom(Setting setting) {
        item = ((ItemSetting)setting).getItem().copy();
        isFuzzy = ((ItemSetting)setting).isFuzzy;
    }

    @Override
    public void setDefaultAmount() {
        setAmount(1);
    }

    private static final String NBT_SETTING_ITEM_ID = "ItemId";
    private static final String NBT_SETTING_ITEM_DMG = "ItemDamage";
    private static final String NBT_SETTING_FUZZY = "Fuzzy";
    private static final String NBT_SETTING_ITEM_COUNT = "ItemCount";

    @Override
    public void load(NBTTagCompound settingTag) {
        item = new ItemStack(settingTag.getShort(NBT_SETTING_ITEM_ID), settingTag.getShort(NBT_SETTING_ITEM_COUNT), settingTag.getShort(NBT_SETTING_ITEM_DMG));
        isFuzzy = settingTag.getBoolean(NBT_SETTING_FUZZY);
    }

    @Override
    public void save(NBTTagCompound settingTag) {
        settingTag.setShort(NBT_SETTING_ITEM_ID, (short)item.itemID);
        settingTag.setShort(NBT_SETTING_ITEM_COUNT, (short)item.stackSize);
        settingTag.setShort(NBT_SETTING_ITEM_DMG, (short)item.getItemDamage());
        settingTag.setBoolean(NBT_SETTING_FUZZY, isFuzzy);
    }

    @Override
    public boolean isContentEqual(Setting otherSetting) {
        return item.itemID == ((ItemSetting)otherSetting).item.itemID;
    }

    @Override
    public void setContent(Object obj) {
        item = ((ItemStack)obj).copy();
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
