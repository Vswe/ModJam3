package vswe.stevesfactory.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryNamespaced;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComponentMenuItem extends ComponentMenuStuff {


    protected  ComponentMenuItem(FlowComponent parent, Class<? extends Setting> settingClass) {
        super(parent, settingClass);

        if (settings.get(0).isAmountSpecific()) {
            numberTextBoxes.addTextBox(amountTextBox = new TextBoxNumber(80 ,24, 3, true) {
                @Override
                public boolean isVisible() {
                    return selectedSetting.isLimitedByAmount();
                }

                @Override
                public void onNumberChanged() {
                    selectedSetting.setAmount(getNumber());
                    writeServerData(DataTypeHeader.AMOUNT);
                }
            });
        }

        numberTextBoxes.addTextBox(damageValueTextBox = new TextBoxNumber(70, 52, 5, true) {
            @Override
            public boolean isVisible() {
                return getSelectedSetting().canChangeMetaData() && getSelectedSetting().getFuzzyMode().requiresMetaData();
            }

            @Override
            public void onNumberChanged() {
                getSelectedSetting().getItem().setItemDamage(getNumber());
                writeServerData(DataTypeHeader.META);
            }
        });

        /*checkBoxes.addCheckBox(new CheckBox("Is detection fuzzy?", 5, 40) {
            @Override
            public void setValue(boolean val) {
                getSelectedSetting().setFuzzyMode(val ? FuzzyMode.FUZZY : FuzzyMode.PRECISE);
            }

            @Override
            public boolean getValue() {
                return getSelectedSetting().getFuzzyMode() == FuzzyMode.FUZZY;
            }

            @Override
            public void onUpdate() {
                writeServerData(DataTypeHeader.USE_FUZZY);
            }
        });*/
    }

    public ComponentMenuItem(FlowComponent parent) {
        this(parent, ItemSetting.class);
    }

    private static final int DMG_VAL_TEXT_X = 15;
    private static final int DMG_VAL_TEXT_Y = 55;

    private static final int ARROW_SRC_X = 18;
    private static final int ARROW_SRC_Y = 20;
    private static final int ARROW_WIDTH = 6;
    private static final int ARROW_HEIGHT = 10;
    private static final int ARROW_X_LEFT = 5;
    private static final int ARROW_X_RIGHT = 109;
    private static final int ARROW_Y = 37;
    private static final int ARROW_TEXT_Y = 40;


    private TextBoxNumber damageValueTextBox;
    private TextBoxNumber amountTextBox;

    protected ItemSetting getSelectedSetting() {
        return (ItemSetting)selectedSetting;
    }

    @Override
    public String getName() {
        return Localization.ITEM_MENU.toString();
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawInfoMenuContent(GuiManager gui, int mX, int mY) {
        if (damageValueTextBox.isVisible()) {
            gui.drawString(Localization.DAMAGE_VALUE.toString(), DMG_VAL_TEXT_X, DMG_VAL_TEXT_Y, 0.7F, 0x404040);
        }

        for (int i = 0; i < 2; i++) {
            int x = i == 0 ? ARROW_X_LEFT : ARROW_X_RIGHT;
            int y = ARROW_Y;

            int srcXArrow = i;
            int srcYArrow = CollisionHelper.inBounds(x, y, ARROW_WIDTH, ARROW_HEIGHT, mX, mY) ? 1 : 0;

            gui.drawTexture(x, y, ARROW_SRC_X + srcXArrow * ARROW_WIDTH, ARROW_SRC_Y + srcYArrow * ARROW_HEIGHT, ARROW_WIDTH, ARROW_HEIGHT);
        }
        gui.drawCenteredString(getSelectedSetting().getFuzzyMode().toString(), ARROW_X_LEFT, ARROW_TEXT_Y, 0.7F, ARROW_X_RIGHT - ARROW_X_LEFT + ARROW_WIDTH, 0x404040);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawResultObject(GuiManager gui, Object obj, int x, int y) {
        gui.drawItemStack((ItemStack)obj, x, y);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawSettingObject(GuiManager gui, Setting setting, int x, int y) {
        drawResultObject(gui,((ItemSetting)setting).getItem(), x, y);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected List<String> getResultObjectMouseOver(Object o) {
        return getToolTip((ItemStack)o);
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected List<String> getSettingObjectMouseOver(Setting setting) {
        return getResultObjectMouseOver(((ItemSetting)setting).getItem());
    }

    @Override
    protected void updateTextBoxes() {
        if (amountTextBox != null) {
            amountTextBox.setNumber(selectedSetting.getAmount());
        }
        damageValueTextBox.setNumber(getSelectedSetting().getItem().getItemDamage());
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        super.refreshData(container, newData);
        for (int i = 0; i < settings.size(); i++) {
            ItemSetting setting = (ItemSetting)settings.get(i);
            ItemSetting newSetting = (ItemSetting)((ComponentMenuStuff)newData).settings.get(i);
            if (newSetting.getFuzzyMode() != setting.getFuzzyMode()) {
                setting.setFuzzyMode(newSetting.getFuzzyMode());
                writeClientData(container, DataTypeHeader.USE_FUZZY, setting);
            }

            if (newSetting.isValid() && setting.isValid()) {
                if (newSetting.getItem().getItemDamage() != setting.getItem().getItemDamage()) {
                    setting.getItem().setItemDamage(newSetting.getItem().getItemDamage());
                    writeClientData(container, DataTypeHeader.META, setting);
                }

            }
        }
    }

    @Override
    protected DataBitHelper getAmountBitLength() {
        return DataBitHelper.MENU_ITEM_AMOUNT;
    }

    @Override
    protected void readSpecificHeaderData(DataReader dr, DataTypeHeader header, Setting setting) {
        ItemSetting itemSetting = (ItemSetting)setting;

        switch (header) {
            case SET_ITEM:
                int id = dr.readData(DataBitHelper.MENU_ITEM_ID);
                int dmg =  dr.readData(DataBitHelper.MENU_ITEM_META);

                itemSetting.setItem(new ItemStack(Item.getItemById(id), 1, dmg));
                itemSetting.getItem().setTagCompound(dr.readNBT());

                if (isEditing()) {
                    updateTextBoxes();
                }

                break;
            case USE_FUZZY:
                itemSetting.setFuzzyMode(FuzzyMode.values()[dr.readData(DataBitHelper.FUZZY_MODE)]);
                break;
            case META:
                if (setting.isValid()) {
                    itemSetting.getItem().setItemDamage(dr.readData(DataBitHelper.MENU_ITEM_META));
                    if (isEditing()) {
                        damageValueTextBox.setNumber(itemSetting.getItem().getItemDamage());
                    }
                }
                break;

        }
    }

    @Override
    protected void writeSpecificHeaderData(DataWriter dw, DataTypeHeader header, Setting setting) {
        ItemSetting itemSetting = (ItemSetting)setting;
        switch (header) {
            case SET_ITEM:
                dw.writeData(Item.getIdFromItem(itemSetting.getItem().getItem()), DataBitHelper.MENU_ITEM_ID);
                dw.writeData(itemSetting.getItem().getItemDamage(), DataBitHelper.MENU_ITEM_META);
                dw.writeNBT(itemSetting.getItem().getTagCompound());
                break;
            case USE_FUZZY:
                dw.writeData(itemSetting.getFuzzyMode().ordinal(), DataBitHelper.FUZZY_MODE);
                break;
            case META:
                dw.writeData(itemSetting.getItem().getItemDamage(), DataBitHelper.MENU_ITEM_META);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected List updateSearch(String search, boolean showAll) {
        List ret = new ArrayList();

        if (search.equals(".inv")) {
            IInventory inventory = Minecraft.getMinecraft().thePlayer.inventory;
            int itemLength = inventory.getSizeInventory();
            for (int i = 0; i < itemLength; i++) {
                ItemStack item = inventory.getStackInSlot(i);
                if (item != null) {
                    item = item.copy();
                    item.stackSize = 1;
                    boolean exists = false;
                    for (Object other : ret) {
                        if (ItemStack.areItemStacksEqual(item, (ItemStack)other)) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        ret.add(item);
                    }
                }
            }
        }else{
            Iterator itemTypeIterator = Item.itemRegistry.iterator();
            while (itemTypeIterator.hasNext()){
                Item item = (Item)itemTypeIterator.next();

                if (item != null && item.getCreativeTab() != null) {
                    item.getSubItems(item, null, ret);
                }
            }

            if (!showAll) {
                Iterator<ItemStack> itemIterator = ret.iterator();

                while (itemIterator.hasNext()) {

                    ItemStack itemStack = itemIterator.next();
                    List<String> description;

                    //if it encounters some weird items
                    try {
                        description = itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
                    }catch (Throwable ex) {
                        itemIterator.remove();
                        continue;
                    }

                    Iterator<String> descriptionIterator = description.iterator();

                    boolean foundSequence = false;

                    while (descriptionIterator.hasNext()) {
                        String line = descriptionIterator.next().toLowerCase();
                        if (line.contains(search)) {
                            foundSequence = true;
                            break;
                        }
                    }

                    if (!foundSequence) {
                        itemIterator.remove();
                    }
                }
            }
        }

        return ret;
    }

    @SideOnly(Side.CLIENT)
    public static List<String> getToolTip(ItemStack itemStack) {
        try {
            return itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        }catch (Exception ex) {
            if (itemStack.getItemDamage() == 0) {
                return new ArrayList<String>();
            }else{
                ItemStack newItem = itemStack.copy();
                newItem.setItemDamage(0);
                return getToolTip(newItem);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static String getDisplayName(ItemStack itemStack) {
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

    @Override
    public void onClick(int mX, int mY, int button) {
        super.onClick(mX, mY, button);

        if (isEditing()) {
            for (int i = -1; i <= 1; i+=2) {
                int x = i == 1 ? ARROW_X_RIGHT : ARROW_X_LEFT;
                int y = ARROW_Y;


                if (CollisionHelper.inBounds(x, y, ARROW_WIDTH, ARROW_HEIGHT, mX, mY)) {
                    int id = getSelectedSetting().getFuzzyMode().ordinal();
                    id += i;
                    if (id < 0) {
                        id = FuzzyMode.values().length - 1;
                    }else if(id == FuzzyMode.values().length) {
                        id = 0;
                    }
                    getSelectedSetting().setFuzzyMode(FuzzyMode.values()[id]);
                    writeServerData(DataTypeHeader.USE_FUZZY);
                    break;
                }
            }

            /*if (CollisionHelper.inBounds(EDIT_ITEM_X, EDIT_ITEM_Y, ITEM_SIZE, ITEM_SIZE, mX, mY) && getSelectedSetting().getItem().hasTagCompound()) {
                getParent().getManager().specialRenderer = new NBTRenderer(getSelectedSetting().getItem().getTagCompound());
            }*/
        }
    }
}
