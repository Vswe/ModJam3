package vswe.stevesfactory.components;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.blocks.TileEntityManager;

import java.util.*;

//TODO implement this!
public class CraftingBufferElement implements IItemBufferElement, IItemBufferSubElement {

    private static final ItemStack DUMMY_ITEM = new ItemStack(1, 0, 0);

    private CommandExecutor executor;
    private ComponentMenuCrafting craftingMenu;
    private ComponentMenuContainerScrap scrapMenu;
    private IRecipe recipe;
    private ItemStack result;
    private boolean isCrafting;
    private boolean justRemoved;
    private int overflowBuffer;
    private List<ItemStack> containerItems;

    public CraftingBufferElement(CommandExecutor executor, ComponentMenuCrafting craftingMenu, ComponentMenuContainerScrap scrapMenu) {
        this.executor = executor;
        this.craftingMenu = craftingMenu;
        this.scrapMenu = scrapMenu;
        recipe = craftingMenu.getDummy().getRecipe();
        result = recipe == null ? null : recipe.getCraftingResult(craftingMenu.getDummy());
        containerItems = new ArrayList<ItemStack>();
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
        //nothing to do
    }

    @Override
    public void releaseSubElements() {
        if (result != null) {
            if (overflowBuffer > 0) {
                ItemStack overflow = result.copy();
                overflow.stackSize = overflowBuffer;
                disposeOfExtraItem(overflow);
                overflowBuffer = 0;
            }
            for (ItemStack containerItem : containerItems) {
                disposeOfExtraItem(containerItem);
            }
            containerItems.clear();
        }
    }

    private static  final  double SPEED_MULTIPLIER = 0.05F;
    private static final Random rand = new Random();
    private void disposeOfExtraItem(ItemStack itemStack) {
        TileEntityManager manager = craftingMenu.getParent().getManager();
        List<SlotInventoryHolder> inventories = CommandExecutor.getContainers(manager, scrapMenu, ConnectionBlockType.INVENTORY);

        for (SlotInventoryHolder inventoryHolder : inventories) {
            IInventory inventory = inventoryHolder.getInventory();

            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                if (inventory.isItemValidForSlot(i, itemStack)) {
                    ItemStack itemInSlot = inventory.getStackInSlot(i);
                    if (itemInSlot == null || (itemInSlot.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(itemStack, itemInSlot) && itemStack.isStackable())){
                        int itemCountInSlot = itemInSlot == null ? 0 : itemInSlot.stackSize;

                        int moveCount = Math.min(itemStack.stackSize, Math.min(inventory.getInventoryStackLimit(), itemStack.getMaxStackSize()) - itemCountInSlot);

                        if (moveCount > 0) {
                            if (itemInSlot == null) {
                                itemInSlot = itemStack.copy();
                                itemInSlot.stackSize = 0;
                                inventory.setInventorySlotContents(i, itemInSlot);
                            }

                            itemInSlot.stackSize += moveCount;
                            itemStack.stackSize -= moveCount;
                            inventory.onInventoryChanged();
                            if (itemStack.stackSize == 0) {
                                return;
                            }
                        }
                    }
                }
            }

        }



        double spawnX = manager.xCoord + rand.nextDouble() * 0.8 + 0.1;
        double spawnY = manager.yCoord + rand.nextDouble() * 0.3 + 1.1;
        double spawnZ = manager.zCoord + rand.nextDouble() * 0.8 + 0.1;

        EntityItem entityitem = new EntityItem(manager.worldObj, spawnX, spawnY, spawnZ, itemStack);

        entityitem.motionX = rand.nextGaussian() * SPEED_MULTIPLIER;
        entityitem.motionY = rand.nextGaussian() * SPEED_MULTIPLIER + 0.2F;
        entityitem.motionZ = rand.nextGaussian() * SPEED_MULTIPLIER;

        manager.worldObj.spawnEntityInWorld(entityitem);
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
            return overflowBuffer > 0 ? overflowBuffer : findItems(false) ? result.stackSize : 0;
        }else{
            justRemoved = false;
            return 0;
        }
    }

    @Override
    public void reduceAmount(int amount) {
        justRemoved = true;
        if (overflowBuffer > 0) {
            overflowBuffer = overflowBuffer - amount;
        }else{
            findItems(true);
            overflowBuffer = result.stackSize - amount;
        }
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
                                    if (itemstack.getItem().hasContainerItem()) {
                                        containerItems.add(itemstack.getItem().getContainerItemStack(itemstack));
                                    }
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
