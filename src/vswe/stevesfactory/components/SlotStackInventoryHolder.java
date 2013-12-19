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


    public IInventory getInventory() {
        return inventory;
    }


    public int getSlot() {
        return slot;
    }

}
