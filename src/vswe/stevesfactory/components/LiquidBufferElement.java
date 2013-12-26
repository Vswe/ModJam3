package vswe.stevesfactory.components;


import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class LiquidBufferElement {
    private Setting setting;
    private FlowComponent component;
    private boolean useWhiteList;
    private int currentTransferSize;
    private int totalTransferSize;
    private SlotInventoryHolder inventoryHolder;

    private List<StackTankHolder> holders;

    private int sharedBy;
    private boolean fairShare;
    private int shareId;

    public LiquidBufferElement(FlowComponent owner, Setting setting, SlotInventoryHolder inventoryHolder, boolean useWhiteList, StackTankHolder target) {
        this(owner, setting, inventoryHolder, useWhiteList);
        addTarget(target);
        sharedBy = 1;
    }

    public LiquidBufferElement(FlowComponent owner, Setting setting, SlotInventoryHolder inventoryHolder, boolean useWhiteList) {
        this.component = owner;
        this.setting = setting;
        this.inventoryHolder = inventoryHolder;
        this.useWhiteList = useWhiteList;
        holders = new ArrayList<StackTankHolder>();
    }

    public boolean addTarget(FlowComponent owner, Setting setting,  SlotInventoryHolder inventoryHolder, StackTankHolder target) {

        if (component.getId() == owner.getId() && (this.setting == null || (setting != null && this.setting.getId() == setting.getId())) && (this.inventoryHolder.isShared() || this.inventoryHolder.equals(inventoryHolder))) {
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
            totalTransferSize += target.getSizeLeft();
            currentTransferSize = totalTransferSize;
        }
    }

    public Setting getSetting() {
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
                itemsAllowedToBeMoved = setting.getAmount() - movedItems;

                int amountLeft = itemsAllowedToBeMoved % sharedBy;
                itemsAllowedToBeMoved /= sharedBy;

                if (!fairShare) {
                    if (shareId < amountLeft) {
                        itemsAllowedToBeMoved++;
                    }
                }
            }else{
                itemsAllowedToBeMoved = currentTransferSize - setting.getAmount();
            }


            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        }
    }

    public void decreaseStackSize(int itemsToMove) {
        currentTransferSize -= itemsToMove * (useWhiteList ? sharedBy : 1);
    }

    public int getBufferSize(Setting outputSetting) {
        int bufferSize = 0;

        for (StackTankHolder holder : getHolders()) {
            FluidStack fluidStack = holder.getFluidStack();
            if (fluidStack != null && fluidStack.fluidID == ((LiquidSetting)outputSetting).getLiquidId()) {
                bufferSize += fluidStack.amount;
            }
        }
        if (setting != null && setting.isLimitedByAmount()){
            int maxSize;
            if (useWhiteList) {
                maxSize = setting.getAmount();
            }else{
                maxSize = totalTransferSize - setting.getAmount();
            }
            bufferSize = Math.min(bufferSize, maxSize);
        }
        return bufferSize;
    }

    public LiquidBufferElement getSplitElement(int elementAmount, int id, boolean fair) {

        LiquidBufferElement element = new LiquidBufferElement(this.component, this.setting, this.inventoryHolder, this.useWhiteList);
        element.holders = new ArrayList<StackTankHolder>();
        for (StackTankHolder holder : holders) {
            element.addTarget(holder.getSplitElement(elementAmount, id, fair));
        }
        if (useWhiteList) {
            element.sharedBy = sharedBy * elementAmount;
            element.fairShare = fair;
            element.shareId = elementAmount * shareId + id;
            element.currentTransferSize -= totalTransferSize - currentTransferSize;
            if (element.currentTransferSize < 0) {
                element.currentTransferSize = 0;
            }
        }else{
            element.currentTransferSize = Math.min(currentTransferSize, element.totalTransferSize);
        }

        return element;
    }
}
