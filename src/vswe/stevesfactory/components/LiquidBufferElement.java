package vswe.stevesfactory.components;


import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class LiquidBufferElement {
    private ItemSetting setting;
    private FlowComponent component;
    private boolean useWhiteList;
    private int currentTransferSize;
    private int totalTransferSize;
    private SlotInventoryHolder inventoryHolder;

    private List<StackTankHolder> holders;


    public LiquidBufferElement(FlowComponent owner, ItemSetting setting, SlotInventoryHolder inventoryHolder, boolean useWhiteList, StackTankHolder target) {
        this.component = owner;
        this.setting = setting;
        this.inventoryHolder = inventoryHolder;
        this.useWhiteList = useWhiteList;
        holders = new ArrayList<StackTankHolder>();
        addTarget(target);
    }

    public boolean addTarget(FlowComponent owner, ItemSetting setting,  SlotInventoryHolder inventoryHolder, StackTankHolder target) {
        if (component.getId() == owner.getId() && (this.setting == null || this.setting.getId() == setting.getId()) && (this.inventoryHolder.isShared() || this.inventoryHolder.equals(inventoryHolder))) {
            addTarget(target);
            return true;
        }else{
            return false;
        }
    }

    private void addTarget(StackTankHolder target) {
        holders.add(target);

        FluidStack temp = target.getFluidStack();
        if (temp != null) {
            totalTransferSize += temp.amount;
            currentTransferSize = totalTransferSize;
        }
    }

    public ItemSetting getSetting() {
        return setting;
    }

    public List<StackTankHolder> getHolders() {
        return holders;
    }

    public int retrieveItemCount(int desiredItemCount) {
        if (setting == null || !setting.isLimitedByAmount()) {
            return desiredItemCount;
        }else{
            int itemsAllowedToBeMoved;
            if (useWhiteList) {
                int movedItems = totalTransferSize - currentTransferSize;
                itemsAllowedToBeMoved = setting.getItem().stackSize - movedItems;
            }else{
                itemsAllowedToBeMoved = currentTransferSize - setting.getItem().stackSize;
            }


            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        }
    }

    public void decreaseStackSize(int itemsToMove) {
        currentTransferSize -= itemsToMove;
    }

    public ItemStack getItemStack() {
       if (setting != null && setting.getItem() != null) {
           return setting.getItem();
       }else{
           //TODO fix for liquid
           //return holders.get(0).getItemStack();
           return null;
       }
    }

    public int getBufferSize(ItemSetting outputSetting) {
        int bufferSize = 0;
        //TODO fix for liquid
        /**for (StackTankHolder holder : getHolders()) {
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
                maxSize = totalTransferSize - setting.getItem().stackSize;
            }
            bufferSize = Math.min(bufferSize, maxSize);
        }**/
        return bufferSize;
    }
}
