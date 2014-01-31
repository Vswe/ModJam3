package vswe.stevesfactory.components;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotStackInventoryHolder implements IItemBufferSubElement {
    private ItemStack itemStack;
    private IInventory inventory;
    private int slot;
    private int sizeLeft;

    public SlotStackInventoryHolder(ItemStack itemStack, IInventory inventory, int slot) {
        this.itemStack = itemStack;
        this.inventory = inventory;
        this.slot = slot;
        this.sizeLeft = itemStack.stackSize;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }


    public IInventory getInventory() {
        return inventory;
    }


    public int getSlot() {
        return slot;
    }

    @Override
    public void remove() {
        if (itemStack.stackSize == 0) {
            getInventory().setInventorySlotContents(getSlot(), null);
        }
    }

    @Override
    public void onUpdate() {
        getInventory().onInventoryChanged();
    }

    public int getSizeLeft() {
        return Math.min(itemStack.stackSize, sizeLeft);
    }

    public void reduceAmount(int val) {
        itemStack.stackSize -= val;
        sizeLeft -= val;
    }

    public SlotStackInventoryHolder getSplitElement(int elementAmount, int id, boolean fair) {
        SlotStackInventoryHolder element = new SlotStackInventoryHolder(this.itemStack, this.inventory, this.slot);
        int oldAmount = getSizeLeft();
        int amount = oldAmount / elementAmount;
        if (!fair) {
            int amountLeft = oldAmount % elementAmount;
            if (id < amountLeft) {
                amount++;
            }
        }

        element.sizeLeft = amount;
        return element;
    }
}
