package vswe.stevesfactory.components;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotStackInventoryHolder {
    private ItemStack itemStack;
    private IInventory inventory;
    private int slot;

    public SlotStackInventoryHolder(ItemStack itemStack, IInventory inventory, int slot) {
        this.itemStack = itemStack;
        this.inventory = inventory;
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public IInventory getInventory() {
        return inventory;
    }

    public void setInventory(IInventory inventory) {
        this.inventory = inventory;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
