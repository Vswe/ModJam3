package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.interfaces.GuiManager;

public class ComponentMenuCrafting extends ComponentMenuItem {
    private ItemStack resultItem;
    private CraftingDummy dummy;

    public ComponentMenuCrafting(FlowComponent parent) {
        super(parent, CraftingSetting.class);

        dummy = new CraftingDummy(this);

        scrollControllerSelected.setItemsPerRow(3);
        scrollControllerSelected.setVisibleRows(3);
        scrollControllerSelected.setItemUpperLimit(2);
    }

    @Override
    public String getName() {
        return Localization.CRAFTING_MENU.toString();
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        super.draw(gui, mX, mY);
        if (!isEditing() && !isSearching() && resultItem != null) {
            drawResultObject(gui, resultItem, getResultX(), getResultY());
            gui.drawItemAmount(resultItem, getResultX(), getResultY());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        super.drawMouseOver(gui, mX, mY);
        if (!isEditing() && !isSearching() && resultItem != null) {
            if (CollisionHelper.inBounds(getResultX(), getResultY(), ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                gui.drawMouseOver(getResultObjectMouseOver(resultItem), mX, mY);
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
        resultItem = dummy.getResult();
    }


    public CraftingDummy getDummy() {
        return dummy;
    }
}
