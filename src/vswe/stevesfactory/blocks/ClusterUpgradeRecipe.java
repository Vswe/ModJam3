package vswe.stevesfactory.blocks;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

import java.util.ArrayList;
import java.util.List;


public class ClusterUpgradeRecipe extends ShapelessRecipes {

    private static final ItemStack RESULT;
    private static final List RECIPE;

    static {
        RESULT = new ItemStack(Blocks.blockCableCluster, 1, 8);
        RECIPE = new ArrayList();
        RECIPE.add(new ItemStack(Blocks.blockCableCluster, 1, 0));
        for (int i = 0; i < 8; i++) {
            RECIPE.add(new ItemStack(Blocks.blockCable));
        }
    }

    public ClusterUpgradeRecipe() {
        super(RESULT, RECIPE);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() != null && itemStack.getItem().itemID == Blocks.blockCableCluster.blockID) {
                ItemStack copy = itemStack.copy();
                copy.setItemDamage(8);
                return copy;
            }
        }

        return super.getCraftingResult(inv);
    }
}
