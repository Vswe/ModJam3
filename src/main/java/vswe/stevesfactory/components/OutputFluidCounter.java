package vswe.stevesfactory.components;


import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class OutputFluidCounter {
    private Setting setting;
    private boolean useWhiteList;
    private int currentTankTransferSize;
    private int currentBufferTransferSize;

    public OutputFluidCounter(List<FluidBufferElement> fluidBuffer, List<SlotInventoryHolder> tanks, SlotInventoryHolder tank, Setting setting, boolean useWhiteList) {
        this.setting = setting;
        this.useWhiteList = useWhiteList;

        if (setting != null && setting.isValid() && setting.isLimitedByAmount()) {
            if (useWhiteList) {
                if (tanks.get(0).isShared()) {
                    for (SlotInventoryHolder slotInventoryHolder : tanks) {
                        addTank(setting, slotInventoryHolder);
                    }
                }else{
                    addTank(setting, tank);
                }
            }else{
                for (FluidBufferElement fluidBufferElement : fluidBuffer) {
                    currentBufferTransferSize += fluidBufferElement.getBufferSize(setting);
                }
            }
        }
    }

    private void addTank(Setting setting, SlotInventoryHolder tankHolder) {
        int max = 0;

        for (SlotSideTarget slotSideTarget : tankHolder.getValidSlots().values()) {
            for (int side : slotSideTarget.getSides()) {
                FluidStack temp = tankHolder.getTank().drain(EnumFacing.getFront(side), CommandExecutor.MAX_FLUID_TRANSFER, false);

                if (temp != null && temp.getFluid().getName().equals(((FluidSetting)setting).getFluidName())) {
                    max = Math.max(max, temp.amount);
                }
            }
        }

        currentTankTransferSize += max;
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
                itemsAllowedToBeMoved = setting.getAmount() - currentTankTransferSize;
            }else{
                itemsAllowedToBeMoved = currentBufferTransferSize - setting.getAmount();
            }


            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        }
    }

    public void modifyStackSize(int itemsToMove) {
        if (useWhiteList) {
            currentTankTransferSize += itemsToMove;
        }else{
            currentBufferTransferSize -=  itemsToMove;
        }
    }
}
