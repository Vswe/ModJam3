package vswe.stevesfactory.components;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;

import java.util.Iterator;

public class ComponentMenuItem extends ComponentMenuStuff {
    public ComponentMenuItem(FlowComponent parent) {
        super(parent, ItemSetting.class);


        numberTextBoxes.addTextBox(damageValueTextBox = new TextBoxNumber(70 ,52, 5, true) {
            @Override
            public boolean isVisible() {
                return !getSelectedSetting().isFuzzy();
            }

            @Override
            public void onNumberChanged() {
                getSelectedSetting().getItem().setItemDamage(getNumber());
                writeServerData(DataTypeHeader.META);
            }
        });

        checkBoxes.addCheckBox(new CheckBox("Is detection fuzzy?", 5, 40) {
            @Override
            public void setValue(boolean val) {
                getSelectedSetting().setFuzzy(val);
            }

            @Override
            public boolean getValue() {
                return getSelectedSetting().isFuzzy();
            }

            @Override
            public void onUpdate() {
                writeServerData(DataTypeHeader.USE_FUZZY);
            }
        });
    }

    private static final int DMG_VAL_TEXT_X = 15;
    private static final int DMG_VAL_TEXT_Y = 55;

    private TextBoxNumber damageValueTextBox;

    protected ItemSetting getSelectedSetting() {
        return (ItemSetting)selectedSetting;
    }

    @Override
    public String getName() {
        return "Items";
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void drawInfoMenuContent(GuiManager gui, int mX, int mY) {
        if (!getSelectedSetting().isFuzzy()) {
            gui.drawString("Damage value", DMG_VAL_TEXT_X, DMG_VAL_TEXT_Y, 0.7F, 0x404040);
        }
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

    @Override
    protected void updateTextBoxes() {
        super.updateTextBoxes();
        damageValueTextBox.setNumber(getSelectedSetting().getItem().getItemDamage());
    }

    @Override
    public void refreshData(ContainerManager container, ComponentMenu newData) {
        super.refreshData(container, newData);
        for (int i = 0; i < settings.size(); i++) {
            ItemSetting setting = (ItemSetting)settings.get(i);
            ItemSetting newSetting = (ItemSetting)((ComponentMenuStuff)newData).settings.get(i);
            if (newSetting.isFuzzy() != setting.isFuzzy()) {
                setting.setFuzzy(newSetting.isFuzzy());
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
    protected void readSpecificHeaderData(DataReader dr, DataTypeHeader header, Setting setting) {
        ItemSetting itemSetting = (ItemSetting)setting;

        switch (header) {
            case SET_ITEM:
                int id = dr.readData(DataBitHelper.MENU_ITEM_ID);
                int dmg =  dr.readData(DataBitHelper.MENU_ITEM_META);

                itemSetting.setItem(new ItemStack(id, 1, dmg));

                if (isEditing()) {
                    updateTextBoxes();
                }

                break;
            case USE_FUZZY:
                itemSetting.setFuzzy(dr.readBoolean());
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
                dw.writeData(itemSetting.getItem().itemID, DataBitHelper.MENU_ITEM_ID);
                dw.writeData(itemSetting.getItem().getItemDamage(), DataBitHelper.MENU_ITEM_META);
                break;
            case USE_FUZZY:
                dw.writeBoolean(itemSetting.isFuzzy());
                break;
            case META:
                dw.writeData(itemSetting.getItem().getItemDamage(), DataBitHelper.MENU_ITEM_META);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void updateSearch(boolean showAll) {
        result.clear();
        Item[] items = Item.itemsList;
        int itemLength = items.length;

        for (int i = 0; i < itemLength; ++i) {
            Item item = items[i];

            if (item != null && item.getCreativeTab() != null) {
                item.getSubItems(item.itemID, null, result);
            }
        }

        if (!showAll) {
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
        }

        updateScrolling();
    }
}
