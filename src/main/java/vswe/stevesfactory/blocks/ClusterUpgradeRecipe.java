package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.ArrayList;
import java.util.List;


public class ClusterUpgradeRecipe extends ShapelessRecipes {

    private static final ItemStack RESULT;
    private static final List RECIPE;

    static {
        RESULT = new ItemStack(ModBlocks.blockCableCluster, 1, 8);
        RECIPE = new ArrayList();
        RECIPE.add(new ItemStack(ModBlocks.blockCableCluster, 1, 0));
        for (int i = 0; i < 8; i++) {
            RECIPE.add(new ItemStack(ModBlocks.blockCable));
        }
    }

    public ClusterUpgradeRecipe() {
        super(RESULT, RECIPE);
        RecipeSorter.register("sfm:clusterupgrade", ClusterUpgradeRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemStack = inv.getStackInSlot(i);

            if (itemStack != null && itemStack.getItem() != null && Block.getBlockFromItem(itemStack.getItem()) == ModBlocks.blockCableCluster) {
                ItemStack copy = itemStack.copy();
                copy.setItemDamage(8);
                return copy;
            }
        }

        return super.getCraftingResult(inv);
    }
}
