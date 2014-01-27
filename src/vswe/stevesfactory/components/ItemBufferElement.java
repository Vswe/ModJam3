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

    private int sharedBy;
    private boolean fairShare;
    private int shareId;

    public ItemBufferElement(FlowComponent owner, Setting setting, SlotInventoryHolder inventoryHolder, boolean useWhiteList, SlotStackInventoryHolder target) {
        this(owner, setting, inventoryHolder, useWhiteList);
        addTarget(target);
        sharedBy = 1;
    }


    private ItemBufferElement(FlowComponent owner, Setting setting, SlotInventoryHolder inventoryHolder, boolean useWhiteList) {
        this.component = owner;
        this.setting = (ItemSetting)setting;
        this.inventoryHolder = inventoryHolder;
        this.useWhiteList = useWhiteList;
        holders = new ArrayList<SlotStackInventoryHolder>();

    }

    public boolean addTarget(FlowComponent owner, Setting setting,  SlotInventoryHolder inventoryHolder, SlotStackInventoryHolder target) {
        if (component.getId() == owner.getId() && (this.setting == null || (setting != null && this.setting.getId() == setting.getId())) && (this.inventoryHolder.isShared() || this.inventoryHolder.equals(inventoryHolder))) {
            addTarget(target);
            return true;
        }else{
            return false;
        }
    }

    private void addTarget(SlotStackInventoryHolder target) {
        holders.add(target);

        totalStackSize += target.getSizeLeft();
        currentStackSize = totalStackSize;
    }

    public Setting getSetting() {
        return setting;
    }

    public List<SlotStackInventoryHolder> getHolders() {
        return holders;
    }

    public int retrieveItemCount(int desiredItemCount) {
        if (setting == null || !setting.isLimitedByAmount()) {
            return desiredItemCount;
        }else{
            int itemsAllowedToBeMoved;
            if (useWhiteList) {
                int movedItems = totalStackSize - currentStackSize;
                itemsAllowedToBeMoved = setting.getItem().stackSize - movedItems;

                int amountLeft = itemsAllowedToBeMoved % sharedBy;
                itemsAllowedToBeMoved /= sharedBy;

                if (!fairShare) {
                    if (shareId < amountLeft) {
                        itemsAllowedToBeMoved++;
                    }
                }
            }else{
                itemsAllowedToBeMoved = currentStackSize - setting.getItem().stackSize;
            }


            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        }
    }

    public void decreaseStackSize(int itemsToMove) {
        currentStackSize -= itemsToMove * (useWhiteList ? sharedBy : 1);
    }

    public ItemStack getItemStack() {
       if (setting != null && setting.getItem() != null) {
           return setting.getItem();
       }else{
           return holders.get(0).getItemStack();
       }
    }

    public int getBufferSize(Setting outputSetting) {
        int bufferSize = 0;
        for (SlotStackInventoryHolder holder : getHolders()) {
            ItemStack item = holder.getItemStack();
            if (((ItemSetting)setting).isEqualForCommandExecutor(item)) {
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

    public ItemBufferElement getSplitElement(int elementAmount, int id, boolean fair) {

        ItemBufferElement element = new ItemBufferElement(this.component, this.setting, this.inventoryHolder, this.useWhiteList);
        element.holders = new ArrayList<SlotStackInventoryHolder>();
        for (SlotStackInventoryHolder holder : holders) {
            element.addTarget(holder.getSplitElement(elementAmount, id, fair));
        }
        if (useWhiteList) {
            element.sharedBy = sharedBy * elementAmount;
            element.fairShare = fair;
            element.shareId = elementAmount * shareId + id;
            element.currentStackSize -= totalStackSize - currentStackSize;
            if (element.currentStackSize < 0) {
                element.currentStackSize = 0;
            }
        }else{
            element.currentStackSize = Math.min(currentStackSize, element.totalStackSize);
        }

        return element;
    }
}
