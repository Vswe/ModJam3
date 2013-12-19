package vswe.stevesfactory.components;


import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemBufferElement {
    private ItemSetting setting;
    private FlowComponent component;
    private boolean useWhiteList;
    private int currentStackSize;
    private int totalStackSize;
    private SlotInventoryHolder inventoryHolder;

    private List<SlotStackInventoryHolder> holders;


    public ItemBufferElement(FlowComponent owner, ItemSetting setting, SlotInventoryHolder inventoryHolder, boolean useWhiteList, SlotStackInventoryHolder target) {
        this.component = owner;
        this.setting = setting;
        this.inventoryHolder = inventoryHolder;
        this.useWhiteList = useWhiteList;
        holders = new ArrayList<SlotStackInventoryHolder>();
        addTarget(target);
    }

    public boolean addTarget(FlowComponent owner, ItemSetting setting,  SlotInventoryHolder inventoryHolder, SlotStackInventoryHolder target) {
        if (component.getId() == owner.getId() && (this.setting == null || this.setting.getId() == setting.getId()) && (this.inventoryHolder.isShared() || this.inventoryHolder.equals(inventoryHolder))) {
            addTarget(target);
            return true;
        }else{
            return false;
        }
    }

    private void addTarget(SlotStackInventoryHolder target) {
        holders.add(target);

        totalStackSize += target.getItemStack().stackSize;
        currentStackSize = totalStackSize;
    }

    public ItemSetting getSetting() {
        return setting;
    }

    public List<SlotStackInventoryHolder> getHolders() {
        return holders;
    }

    public int retrieveItemCount(int desiredItemCount) {
        if (setting == null || !setting.isLimitedByAmount()) {
            return desiredItemCount;
        }else {
            int itemsAllowedToBeMoved;
            if (useWhiteList) {
                int movedItems = totalStackSize - currentStackSize;
                itemsAllowedToBeMoved = setting.getItem().stackSize - movedItems;
            }else{
                itemsAllowedToBeMoved = currentStackSize - setting.getItem().stackSize;
            }


            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        }
    }

    public void decreaseStackSize(int itemsToMove) {
        currentStackSize -= itemsToMove;
    }

    public ItemStack getItemStack() {
       if (setting != null && setting.getItem() != null) {
           return setting.getItem();
       }else{
           return holders.get(0).getItemStack();
       }
    }

    public int getBufferSize(ItemSetting outputSetting) {
        int bufferSize = 0;
        for (SlotStackInventoryHolder holder : getHolders()) {
            ItemStack item = holder.getItemStack();
            if (item != null && outputSetting.getItem().itemID == item.itemID && (outputSetting.isFuzzy() || outputSetting.getItem().getItemDamage() == item.getItemDamage())) {
                bufferSize += item.stackSize;
            }
        }
        if (setting != null && setting.isLimitedByAmount()){
            int maxSize;
            if (useWhiteList) {
                maxSize = setting.getItem().stackSize;
            }else{
                maxSize = totalStackSize - setting.getItem().stackSize;
            }
            bufferSize = Math.min(bufferSize, maxSize);
        }
        return bufferSize;
    }
}
