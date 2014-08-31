package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.GuiManager;

public class ComponentMenuCrafting extends ComponentMenuItem {
    private CraftingSetting resultItem;
    private CraftingDummy dummy;

    public ComponentMenuCrafting(FlowComponent parent) {
        super(parent, CraftingSetting.class);

        resultItem = new CraftingSetting(9) {
            @Override
            public boolean canChangeMetaData() {
                return false;
            }

            @Override
            public void delete() {
                for (Setting setting : settings) {
                    setting.clear();
                    writeServerData(DataTypeHeader.CLEAR, setting);
                }
            }
        };
        settings.add(resultItem);
        dummy = new CraftingDummy(this);


        scrollControllerSelected.setItemsPerRow(3);
        scrollControllerSelected.setVisibleRows(3);
        scrollControllerSelected.setItemUpperLimit(2);
        scrollControllerSelected.setDisabledScroll(true);
    }

    @Override
    public String getName() {
        return Localization.CRAFTING_MENU.toString();
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        super.draw(gui, mX, mY);
        if (!isEditing() && !isSearching() && resultItem.getItem() != null) {
            drawResultObject(gui, resultItem.getItem(), getResultX(), getResultY());
            gui.drawItemAmount(resultItem.getItem(), getResultX(), getResultY());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        super.drawMouseOver(gui, mX, mY);
        if (!isEditing() && !isSearching() && resultItem.getItem() != null) {
            if (CollisionHelper.inBounds(getResultX(), getResultY(), ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                gui.drawMouseOver(getResultObjectMouseOver(resultItem.getItem()), mX, mY);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button) {
        super.onClick(mX, mY, button);
        if (!isEditing() && !isSearching() && resultItem.getItem() != null) {
            if (button == 1 && CollisionHelper.inBounds(getResultX(), getResultY(), ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                scrollControllerSelected.onClick(resultItem, mX, mY, 1);
            }
        }
    }



    private int getResultX() {
        return ITEM_X + ITEM_SIZE_WITH_MARGIN * 3;
    }

    private int getResultY() {
        return scrollControllerSelected.getScrollingStartY() + ITEM_SIZE_WITH_MARGIN;
    }

    @Override
    protected int getSettingCount() {
        return 9;
    }


    @Override
    protected void initRadioButtons() {
        //no radio buttons
    }

    @Override
    protected void onSettingContentChange() {
        resultItem.setItem(dummy.getResult());
    }


    public CraftingDummy getDummy() {
        return dummy;
    }

    public CraftingSetting getResultItem() {
        return resultItem;
    }
}
