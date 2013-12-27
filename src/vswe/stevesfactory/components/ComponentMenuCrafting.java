package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.interfaces.GuiManager;

public class ComponentMenuCrafting extends ComponentMenuItem {
    private ItemStack resultItem;
    private CraftingDummy dummy;

    public ComponentMenuCrafting(FlowComponent parent) {
        super(parent, CraftingSetting.class);

        dummy = new CraftingDummy(this);
    }

    @Override
    public String getName() {
        return "Crafting";
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        super.draw(gui, mX, mY);
        if (!isEditing() && !isSearching() && resultItem != null) {
            drawResultObject(gui, resultItem, getResultX(), getResultY());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        super.drawMouseOver(gui, mX, mY);
        if (!isEditing() && !isSearching() && resultItem != null) {
            if (CollisionHelper.inBounds(getResultX(), getResultY(), ITEM_SIZE, ITEM_SIZE, mX, mY)) {
                drawResultObjectMouseOver(gui, resultItem, mX, mY);
            }
        }
    }

    private int getResultX() {
        return ITEM_X + ITEM_SIZE_WITH_MARGIN * 3;
    }

    private int getResultY() {
        return getScrollingStartY() + ITEM_SIZE_WITH_MARGIN;
    }

    @Override
    protected int getSettingCount() {
        return 9;
    }

    @Override
    protected int getItemsPerRow() {
        return 3;
    }

    @Override
    protected int getVisibleRows() {
        return 3;
    }

    @Override
    protected int getItemUpperLimit() {
        return 2;
    }

    @Override
    protected void initRadioButtons() {
        //no radio buttons
    }

    @Override
    protected void onSettingContentChange() {
        resultItem = dummy.getResult();
    }
}
