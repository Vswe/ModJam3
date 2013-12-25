package vswe.stevesfactory.components;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public class OutputItemCounter {
    private Setting setting;
    private boolean useWhiteList;
    private int currentInventoryStackSize;
    private int currentBufferStackSize;

    public OutputItemCounter(List<ItemBufferElement> itemBuffer, List<SlotInventoryHolder> inventories, IInventory inventory, Setting setting, boolean useWhiteList) {
        this.setting = setting;
        this.useWhiteList = useWhiteList;

        if (setting != null && ((ItemSetting)setting).getItem() != null && setting.isLimitedByAmount()) {
            if (useWhiteList) {
                if (inventories.get(0).isShared()) {
                    for (SlotInventoryHolder slotInventoryHolder : inventories) {
                        addInventory(slotInventoryHolder.getInventory());
                    }
                }else{
                    addInventory(inventory);
                }
            }else{
                for (ItemBufferElement itemBufferElement : itemBuffer) {
                    currentBufferStackSize += itemBufferElement.getBufferSize(setting);
                }
            }
        }
    }

    private void addInventory(IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);
            if (item != null && ((ItemSetting)setting).getItem().itemID == item.itemID && (((ItemSetting)setting).isFuzzy() || ((ItemSetting)setting).getItem().getItemDamage() == item.getItemDamage())) {
                currentInventoryStackSize += item.stackSize;
            }
        }
    }

    public boolean areSettingsSame(Setting setting) {
        return (this.setting == null && setting == null) || (this.setting != null && setting != null && this.setting.getId() == setting.getId());
    }

    public int retrieveItemCount(int desiredItemCount) {
        if (setting == null || !setting.isLimitedByAmount()) {
            return desiredItemCount;
        }else {
            int itemsAllowedToBeMoved;
            if (useWhiteList) {
                itemsAllowedToBeMoved = ((ItemSetting)setting).getItem().stackSize - currentInventoryStackSize;
            }else{
                itemsAllowedToBeMoved = currentBufferStackSize - ((ItemSetting)setting).getItem().stackSize;
            }


            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        }
    }

    public void modifyStackSize(int itemsToMove) {
        if (useWhiteList) {
            currentInventoryStackSize += itemsToMove;
        }else{
            currentBufferStackSize -=  itemsToMove;
        }
    }
}
