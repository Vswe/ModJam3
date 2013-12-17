package vswe.stevesfactory.components;


import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

public class OutputItemCounter {
    private ItemSetting setting;
    private boolean useWhiteList;
    private int currentInventoryStackSize;
    private int currentBufferStackSize;

    public OutputItemCounter(List<ItemBufferElement> itemBuffer, IInventory inventory, ItemSetting setting, boolean useWhiteList) {
        this.setting = setting;
        this.useWhiteList = useWhiteList;

        if (setting != null && setting.getItem() != null && setting.isLimitedByAmount()) {
            if (useWhiteList) {
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    ItemStack item = inventory.getStackInSlot(i);
                    if (item != null && setting.getItem().itemID == item.itemID && (setting.isFuzzy() || setting.getItem().getItemDamage() == item.getItemDamage())) {
                        currentInventoryStackSize += item.stackSize;
                    }
                }
            }else{
                for (ItemBufferElement itemBufferElement : itemBuffer) {
                    currentBufferStackSize += itemBufferElement.getBufferSize(setting);
                }
            }
        }
    }

    public boolean areSettingsSame(ItemSetting setting) {
        return (this.setting == null && setting == null) || (this.setting != null && setting != null && this.setting.getId() == setting.getId());
    }

    public int retrieveItemCount(int desiredItemCount) {
        if (setting == null || !setting.isLimitedByAmount()) {
            return desiredItemCount;
        }else {
            int itemsAllowedToBeMoved;
            if (useWhiteList) {
                itemsAllowedToBeMoved = setting.getItem().stackSize - currentInventoryStackSize;
            }else{
                itemsAllowedToBeMoved = currentBufferStackSize - setting.getItem().stackSize;
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
