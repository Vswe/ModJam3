package vswe.stevesfactory.components;



import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

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
    public int getSizeInventory()
    {
        return 9;
    }

    @Override
    public ItemStack getStackInSlot(int id)
    {
        return id < 0 || id >= this.getSizeInventory() ? null : ((CraftingSetting)crafting.getSettings().get(id)).getItem();
    }


    @Override
    public ItemStack getStackInRowAndColumn(int par1, int par2)
    {
        if (par1 >= 0 && par1 < this.inventoryWidth){
            int k = par1 + par2 * this.inventoryWidth;
            return this.getStackInSlot(k);
        }else{
            return null;
        }
    }


    @Override
    public ItemStack getStackInSlotOnClosing(int par1) {
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

    public ItemStack getResult() {
        return CraftingManager.getInstance().findMatchingRecipe(this, crafting.getParent().getManager().worldObj);
    }

    public IRecipe getRecipe() {
        for (int i = 0; i < CraftingManager.getInstance().getRecipeList().size(); ++i)
        {
            IRecipe irecipe = (IRecipe) CraftingManager.getInstance().getRecipeList().get(i);

            if (irecipe.matches(this, crafting.getParent().getManager().worldObj))
            {
                return irecipe;
            }
        }

        return null;
    }

}
