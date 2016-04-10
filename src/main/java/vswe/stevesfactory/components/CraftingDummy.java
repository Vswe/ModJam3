package vswe.stevesfactory.components;


import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import java.util.Map;

public class CraftingDummy extends InventoryCrafting
{

    private int inventoryWidth;

    private ComponentMenuCrafting crafting;

    public CraftingDummy(ComponentMenuCrafting crafting) {
        super(null, 3, 3);
        inventoryWidth = 3;

        this.crafting = crafting;
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public ItemStack getStackInSlot(int id) {
        if (overrideMap != null && overrideMap.get(id) != null && overrideMap.get(id).stackSize > 0) {
            return overrideMap.get(id);
        }else{
            return id < 0 || id >= this.getSizeInventory() ? null : ((CraftingSetting)crafting.getSettings().get(id)).getItem();
        }
    }


    @Override
    public ItemStack getStackInRowAndColumn(int par1, int par2) {
        if (par1 >= 0 && par1 < this.inventoryWidth){
            int k = par1 + par2 * this.inventoryWidth;
            return this.getStackInSlot(k);
        }else{
            return null;
        }
    }


    @Override
    public ItemStack removeStackFromSlot(int par1) {
        return null;
    }

    @Override
    public ItemStack decrStackSize(int par1, int par2)
    {
        return null;
    }


    @Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        return;
    }

    public ItemStack getResult(Map<Integer, ItemStack> overrideMap) {
        this.overrideMap = overrideMap;
        try {
            return getResult();
        }finally {
            this.overrideMap = null;
        }
    }

    public ItemStack getResult() {
        IRecipe recipe = getRecipe();
        return recipe == null ? null : recipe.getCraftingResult(this);
    }

    public IRecipe getRecipe() {
        for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); ++i) {
            IRecipe recipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(i);

            if (recipe.matches(this, crafting.getParent().getManager().getWorld())) {
                return recipe;
            }
        }

        return null;
    }

    private Map<Integer, ItemStack> overrideMap;
    public boolean isItemValidForRecipe(IRecipe recipe, ItemSetting result, Map<Integer, ItemStack> overrideMap, boolean advanced) {
        this.overrideMap = overrideMap;
        if ((advanced && getRecipe() == null) || (!advanced && !recipe.matches(this, crafting.getParent().getManager().getWorld()))) {
            return false;
        }
        ItemStack itemStack = recipe.getCraftingResult(this);
        this.overrideMap = null;
        return result.isEqualForCommandExecutor(itemStack);
    }
}
