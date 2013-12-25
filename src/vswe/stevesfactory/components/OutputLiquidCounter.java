package vswe.stevesfactory.components;


import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.List;

public class OutputLiquidCounter {
    private Setting setting;
    private boolean useWhiteList;
    private int currentTankTransferSize;
    private int currentBufferTransferSize;

    public OutputLiquidCounter(List<LiquidBufferElement> liquidBuffer, List<SlotInventoryHolder> tanks, IFluidHandler tank, Setting setting, boolean useWhiteList) {
        this.setting = setting;
        this.useWhiteList = useWhiteList;

        //TODO fix
        /*if (setting != null && setting.getItem() != null && setting.isLimitedByAmount()) {
            if (useWhiteList) {
                if (tanks.get(0).isShared()) {
                    for (SlotInventoryHolder slotInventoryHolder : tanks) {
                        addInventory(slotInventoryHolder.getTank());
                    }
                }else{
                    addInventory(tank);
                }
            }else{
                for (LiquidBufferElement liquidBufferElement : liquidBuffer) {
                    currentBufferTransferSize += liquidBufferElement.getBufferSize(setting);
                }
            }
        } */
    }

    private void addInventory(IFluidHandler tank) {
        //TODO make a proper one
        FluidStack temp = tank.drain(ForgeDirection.UNKNOWN, CommandExecutor.MAX_FLUID_TRANSFER, false);
        if (temp != null) {
            currentTankTransferSize += temp.amount;
        }
        /*for (int i = 0; i < tank.get(); i++) {
            ItemStack item = inventory.getStackInSlot(i);
            if (item != null && setting.getItem().itemID == item.itemID && (setting.isFuzzy() || setting.getItem().getItemDamage() == item.getItemDamage())) {
                currentTankTransferSize += item.stackSize;
            }
        }*/
    }

    public boolean areSettingsSame(Setting setting) {
        return (this.setting == null && setting == null) || (this.setting != null && setting != null && this.setting.getId() == setting.getId());
    }

    //TODO fix
    public int retrieveItemCount(int desiredItemCount) {
        //if (setting == null || !setting.isLimitedByAmount()) {
            return desiredItemCount;
        /*}else {
            int itemsAllowedToBeMoved;
            if (useWhiteList) {
                itemsAllowedToBeMoved = setting.getItem().stackSize - currentTankTransferSize;
            }else{
                itemsAllowedToBeMoved = currentBufferTransferSize - setting.getItem().stackSize;
            }


            return Math.min(itemsAllowedToBeMoved, desiredItemCount);
        }*/
    }

    public void modifyStackSize(int itemsToMove) {
        if (useWhiteList) {
            currentTankTransferSize += itemsToMove;
        }else{
            currentBufferTransferSize -=  itemsToMove;
        }
    }
}
