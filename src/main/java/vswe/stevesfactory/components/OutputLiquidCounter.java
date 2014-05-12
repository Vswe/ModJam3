package vswe.stevesfactory.components;


import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class OutputLiquidCounter {
    private Setting setting;
    private boolean useWhiteList;
    private int currentTankTransferSize;
    private int currentBufferTransferSize;

    public OutputLiquidCounter(List<LiquidBufferElement> liquidBuffer, List<SlotInventoryHolder> tanks, SlotInventoryHolder tank, Setting setting, boolean useWhiteList) {
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
                for (LiquidBufferElement liquidBufferElement : liquidBuffer) {
                    currentBufferTransferSize += liquidBufferElement.getBufferSize(setting);
                }
            }
        }
    }

    private void addTank(Setting setting, SlotInventoryHolder tankHolder) {
        int max = 0;

        for (SlotSideTarget slotSideTarget : tankHolder.getValidSlots().values()) {
            for (int side : slotSideTarget.getSides()) {
                FluidStack temp = tankHolder.getTank().drain(ForgeDirection.VALID_DIRECTIONS[side], CommandExecutor.MAX_FLUID_TRANSFER, false);

                if (temp != null && temp.fluidID == ((LiquidSetting)setting).getLiquidId()) {
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
