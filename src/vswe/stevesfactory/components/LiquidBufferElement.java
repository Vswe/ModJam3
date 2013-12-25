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


    public LiquidBufferElement(FlowComponent owner, Setting setting, SlotInventoryHolder inventoryHolder, boolean useWhiteList, StackTankHolder target) {
        this.component = owner;
        this.setting = setting;
        this.inventoryHolder = inventoryHolder;
        this.useWhiteList = useWhiteList;
        holders = new ArrayList<StackTankHolder>();
        addTarget(target);
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
            totalTransferSize += temp.amount;
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
            }else{
                itemsAllowedToBeMoved = currentTransferSize - setting.getAmount();
            }


            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        }
    }

    public void decreaseStackSize(int itemsToMove) {
        currentTransferSize -= itemsToMove;
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
}
