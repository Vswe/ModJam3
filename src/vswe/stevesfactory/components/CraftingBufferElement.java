package vswe.stevesfactory.components;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import vswe.stevesfactory.blocks.Blocks;

import java.util.*;

//TODO implement this!
public class CraftingBufferElement implements IItemBufferElement, IItemBufferSubElement {

    private static final ItemStack DUMMY_ITEM = new ItemStack(1, 0, 0);

    private CommandExecutor executor;
    private ComponentMenuCrafting craftingMenu;
    private IRecipe recipe;
    private ItemStack result;
    private boolean isCrafting;
    private boolean justRemoved;

    public CraftingBufferElement(CommandExecutor executor, ComponentMenuCrafting craftingMenu) {
        this.executor = executor;
        this.craftingMenu = craftingMenu;
        recipe = craftingMenu.getDummy().getRecipe();
        result = recipe == null ? null : recipe.getCraftingResult(craftingMenu.getDummy());
    }


    @Override
    public void prepareSubElements() {
        isCrafting = true;
        justRemoved = false;
    }

    @Override
    public IItemBufferSubElement getSubElement() {
        if (isCrafting && result != null) {
            isCrafting = false;
            return this;
        }else{
            return null;
        }
    }

    @Override
    public void removeSubElement() {

    }

    @Override
    public int retrieveItemCount(int moveCount) {
        return moveCount; //no limit
    }

    @Override
    public void decreaseStackSize(int moveCount) {
        //no limit
    }

    @Override
    public void remove() {
        //nothing to do
    }

    @Override
    public void onUpdate() {
        for (IInventory inventory : inventories) {
            inventory.onInventoryChanged();
        }
        inventories.clear();
    }


    @Override
    public int getSizeLeft() {
        if (!justRemoved) {
            return findItems(false) ? result.stackSize : 0;
        }else{
            justRemoved = false;
            return 0;
        }
    }

    @Override
    public void reduceAmount(int amount) {
        justRemoved = true;
        findItems(true);
        isCrafting = true;
    }

    @Override
    public ItemStack getItemStack() {
        return result;
    }

    private List<IInventory> inventories = new ArrayList<IInventory>();

    private boolean findItems(boolean remove) {
        Map<Integer, ItemStack> foundItems = new HashMap<Integer, ItemStack>();
        for (ItemBufferElement itemBufferElement : executor.itemBuffer) {
            int count = itemBufferElement.retrieveItemCount(9);
            for (Iterator<SlotStackInventoryHolder> iterator = itemBufferElement.getSubElements().iterator(); iterator.hasNext(); ) {
                IItemBufferSubElement itemBufferSubElement = iterator.next();
                ItemStack itemstack = itemBufferSubElement.getItemStack();
                int subCount = Math.min(count, itemBufferSubElement.getSizeLeft());
                for (int i = 0; i < 9; i++) {
                    CraftingSetting setting = (CraftingSetting) craftingMenu.getSettings().get(i);
                    if (foundItems.get(i) == null) {
                        if (!setting.isValid()) {
                            foundItems.put(i, DUMMY_ITEM);
                        } else if (subCount > 0 && setting.isEqualForCommandExecutor(itemstack)) {
                            foundItems.put(i, itemstack.copy());
                            if (craftingMenu.getDummy().isItemValidForRecipe(recipe, result, foundItems)) {
                                subCount--;
                                count--;
                                if (remove) {
                                    itemBufferElement.decreaseStackSize(1);
                                    itemBufferSubElement.reduceAmount(1);
                                    if (itemBufferSubElement.getSizeLeft() == 0) {
                                        itemBufferSubElement.remove();
                                        iterator.remove();
                                    }
                                    inventories.add(((SlotStackInventoryHolder)itemBufferSubElement).getInventory());
                                }
                            }else{
                                foundItems.remove(i);
                            }
                        }
                    }
                }
            }
        }

        return foundItems.size() == 9;
    }


}
