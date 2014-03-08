package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.ArrayList;
import java.util.List;

public class ItemSetting extends Setting {
    private FuzzyMode fuzzyMode;
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
            ret.add(Localization.NO_ITEM_SELECTED.toString());
        }else{
            ret.add(ComponentMenuItem.getDisplayName(item));
        }

        ret.add("");
        ret.add(Localization.CHANGE_ITEM.toString());
        if (item != null) {
            ret.add(Localization.EDIT_SETTING.toString());
            ret.add(Localization.FULL_DESCRIPTION.toString());
        }

        return ret;
    }

    @Override
    public void clear() {
        super.clear();

        fuzzyMode = FuzzyMode.PRECISE;
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

    public FuzzyMode getFuzzyMode() {
        return fuzzyMode;
    }

    public void setFuzzyMode(FuzzyMode fuzzy) {
        this.fuzzyMode = fuzzy;
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public void writeData(DataWriter dw) {
        dw.writeData(item.itemID, DataBitHelper.MENU_ITEM_ID);
        dw.writeData(fuzzyMode.ordinal(), DataBitHelper.FUZZY_MODE);
        dw.writeData(item.getItemDamage(), DataBitHelper.MENU_ITEM_META);
        dw.writeNBT(item.getTagCompound());
    }

    @Override
    public void readData(DataReader dr) {
        int id = dr.readData(DataBitHelper.MENU_ITEM_ID);
        fuzzyMode = FuzzyMode.values()[dr.readData(DataBitHelper.FUZZY_MODE)];
        int meta = dr.readData(DataBitHelper.MENU_ITEM_META);
        item = new ItemStack(id, 1, meta);
        item.setTagCompound(dr.readNBT());
    }

    @Override
    public void copyFrom(Setting setting) {
        item = ((ItemSetting)setting).getItem().copy();
        fuzzyMode = ((ItemSetting)setting).fuzzyMode;
    }

    @Override
    public int getDefaultAmount() {
        return 1;
    }

    private static final String NBT_SETTING_ITEM_ID = "ItemId";
    private static final String NBT_SETTING_ITEM_DMG = "ItemDamage";
    private static final String NBT_SETTING_FUZZY_OLD = "Fuzzy";
    private static final String NBT_SETTING_FUZZY = "FuzzyMode";
    private static final String NBT_SETTING_ITEM_COUNT = "ItemCount";
    private static final String NBT_TAG = "tag"; //must be "tag" to match the vanilla value, see ItemStack.readFromNBT
    @Override
    public void load(NBTTagCompound settingTag) {
        item = new ItemStack(settingTag.getShort(NBT_SETTING_ITEM_ID), settingTag.getShort(NBT_SETTING_ITEM_COUNT), settingTag.getShort(NBT_SETTING_ITEM_DMG));

        //used to be a boolean
        if (settingTag.hasKey(NBT_SETTING_FUZZY_OLD)) {
            fuzzyMode = settingTag.getBoolean(NBT_SETTING_FUZZY_OLD) ? FuzzyMode.FUZZY : FuzzyMode.PRECISE;
        }else{
            fuzzyMode = FuzzyMode.values()[settingTag.getByte(NBT_SETTING_FUZZY)];
        }

        if (settingTag.hasKey(NBT_TAG)) {
            item.setTagCompound(settingTag.getCompoundTag(NBT_TAG));
        }else{
            item.setTagCompound(null);
        }
    }

    @Override
    public void save(NBTTagCompound settingTag) {
        settingTag.setShort(NBT_SETTING_ITEM_ID, (short)item.itemID);
        settingTag.setShort(NBT_SETTING_ITEM_COUNT, (short)item.stackSize);
        settingTag.setShort(NBT_SETTING_ITEM_DMG, (short)item.getItemDamage());
        settingTag.setByte(NBT_SETTING_FUZZY, (byte)fuzzyMode.ordinal());
        if (item.getTagCompound() != null) {
            settingTag.setCompoundTag(NBT_TAG, item.getTagCompound());
        }
    }

    @Override
    public boolean isContentEqual(Setting otherSetting) {
        return item.itemID == ((ItemSetting)otherSetting).item.itemID && ItemStack.areItemStackTagsEqual(item, ((ItemSetting)otherSetting).item);
    }

    @Override
    public void setContent(Object obj) {
        item = ((ItemStack)obj).copy();
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public boolean isEqualForCommandExecutor(ItemStack other) {
        if (!isValid() || other == null) {
            return false;
        }else {
            switch (fuzzyMode) {
                case ORE_DICTIONARY:
                    int id = OreDictionary.getOreID(this.getItem());
                    if (id != -1) {
                        return  id == OreDictionary.getOreID(other);
                    }
                    //note that this falls through into the precise one, this is on purpose
                case PRECISE:
                    return this.getItem().itemID == other.itemID && this.getItem().getItemDamage() == other.getItemDamage() && ItemStack.areItemStackTagsEqual(getItem(), other);
                case NBT_FUZZY:
                    return this.getItem().itemID == other.itemID && this.getItem().getItemDamage() == other.getItemDamage();
                case FUZZY:
                    return this.getItem().itemID == other.itemID;
                case MOD_GROUPING:
                    return ModItemHelper.areItemsFromSameMod(this.getItem().getItem(), other.getItem());
                case ALL:
                    return true;
                default:
                    return false;
            }
        }
    }
}
