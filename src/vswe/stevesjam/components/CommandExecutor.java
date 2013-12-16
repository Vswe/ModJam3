package vswe.stevesjam.components;


import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesjam.blocks.TileEntityJam;


import java.util.*;

public class CommandExecutor {

    private TileEntityJam jar;
    private List<ItemBufferElement> itemBuffer;

    public CommandExecutor(TileEntityJam jar) {
        this.jar = jar;
        itemBuffer = new ArrayList<>();
    }

    public void executeTriggerCommand(FlowComponent command, EnumSet<ConnectionOption> validTriggerOutputs) {
        for (int i = 0; i < command.getConnectionSet().getConnections().length; i++) {
            Connection connection = command.getConnection(i);
            ConnectionOption option = command.getConnectionSet().getConnections()[i];
            if (connection != null && !option.isInput() && validTriggerOutputs.contains(option)) {
                executeCommand(jar.getFlowItems().get(connection.getComponentId()));
            }
        }
    }


    private void executeCommand(FlowComponent command) {
        switch (command.getType()) {
            case INPUT:
                IInventory inputInventory = getInventory(command.getMenus().get(0));
                if (inputInventory != null) {
                    Map<Integer, SlotSideTarget> validSlots = getValidSlots(command.getMenus().get(1), inputInventory);
                    getItems(command.getMenus().get(2), inputInventory, validSlots);
                }
                break;
            case OUTPUT:
                IInventory outputInventory = getInventory(command.getMenus().get(0));
                if (outputInventory != null) {
                    Map<Integer, SlotSideTarget> validSlots = getValidSlots(command.getMenus().get(1), outputInventory);
                    insertItems(command.getMenus().get(2), outputInventory, validSlots);
                }
                break;
        }

        for (int i = 0; i < command.getConnectionSet().getConnections().length; i++) {
            Connection connection = command.getConnection(i);
            if (connection != null && !command.getConnectionSet().getConnections()[i].isInput()) {
                executeCommand(jar.getFlowItems().get(connection.getComponentId()));
            }
        }

    }

    private IInventory getInventory(ComponentMenu componentMenu) {
        ComponentMenuInventory menuInventory = (ComponentMenuInventory)componentMenu;

        List<TileEntity> inventories = jar.getConnectedInventories();
        int selected = menuInventory.getSelectedInventory();


        if (selected >= 0 && selected < inventories.size()) {
            TileEntity tileEntity = inventories.get(selected);

            if (tileEntity.isInvalid()) {
                return null;
            }

            return  (IInventory)tileEntity;
        }else{
            return null;
        }
    }

    private Map<Integer, SlotSideTarget> getValidSlots(ComponentMenu componentMenu, IInventory inventory) {
        ComponentMenuTarget menuTarget = (ComponentMenuTarget)componentMenu;

        Map<Integer, SlotSideTarget> validSlots = new HashMap<>();
        for (int side = 0; side < ComponentMenuTarget.directions.length; side++) {
            if (menuTarget.isActive(side)) {
                int[] inventoryValidSlots;
                if (inventory instanceof ISidedInventory) {
                    inventoryValidSlots =  ((ISidedInventory) inventory).getAccessibleSlotsFromSide(side);
                }else{
                    inventoryValidSlots = new int[inventory.getSizeInventory()];
                    for (int i = 0; i < inventoryValidSlots.length; i++) {
                        inventoryValidSlots[i] = i;
                    }
                }
                int start;
                int end;
                if (menuTarget.useRange(side)) {
                    start = menuTarget.getStart(side);
                    end = menuTarget.getEnd(side);
                }else{
                    start = 0;
                    end = inventory.getSizeInventory();
                }

                if (start > end) {
                    continue;
                }


                for (int inventoryValidSlot : inventoryValidSlots) {
                    if (inventoryValidSlot >= start && inventoryValidSlot <= end) {
                        SlotSideTarget target = validSlots.get(inventoryValidSlot);
                        if (target == null) {
                            validSlots.put(inventoryValidSlot, new SlotSideTarget(inventoryValidSlot, side));
                        }else{
                            target.addSide(side);
                        }
                    }
                }
            }
        }

        return validSlots;
    }

    private boolean isSlotValid(IInventory inventory, ItemStack item, SlotSideTarget slot, boolean isInput) {
        if (item == null) {
            return false;
        }else if (inventory instanceof ISidedInventory) {
            boolean hasValidSide = false;
            for (int side : slot.getSides()) {
                if (isInput && ((ISidedInventory)inventory).canExtractItem(slot.getSlot(), item, side)) {
                    hasValidSide = true;
                    break;
                }else if (!isInput && ((ISidedInventory)inventory).canInsertItem(slot.getSlot(), item, side)) {
                    hasValidSide = true;
                    break;
                }
            }

            if (!hasValidSide)  {
                return false;
            }
        }

        return isInput || inventory.isItemValidForSlot(slot.getSlot(), item);
    }

    private void getItems(ComponentMenu componentMenu, IInventory inventory, Map<Integer, SlotSideTarget> validSlots) {
        ComponentMenuItem menuItem = (ComponentMenuItem)componentMenu;
        for (SlotSideTarget slot : validSlots.values()) {
            ItemStack itemStack = inventory.getStackInSlot(slot.getSlot());

            if (!isSlotValid(inventory, itemStack, slot, true)) {
                continue;
            }

            ItemSetting setting = isItemValid(componentMenu, itemStack);
            if ((menuItem.useWhiteList() == (setting != null)) || (setting != null && setting.isLimitedByAmount())) {
                FlowComponent owner = componentMenu.getParent();
                SlotStackInventoryHolder target = new SlotStackInventoryHolder(itemStack, inventory, slot.getSlot());

                boolean added = false;
                for (ItemBufferElement itemBufferElement : itemBuffer) {
                    if (itemBufferElement.addTarget(owner, setting, target)) {
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    ItemBufferElement itemBufferElement = new ItemBufferElement(owner, setting, menuItem.useWhiteList(), target);
                    itemBuffer.add(itemBufferElement);
                }

            }
        }
    }


    private ItemSetting isItemValid(ComponentMenu componentMenu, ItemStack itemStack)  {
        ComponentMenuItem menuItem = (ComponentMenuItem)componentMenu;

        int itemId = itemStack.itemID;
        for (ItemSetting setting : menuItem.getSettings()) {
            if (setting.getItem() != null) {
                if (setting.getItem().itemID == itemId && (setting.isFuzzy() || setting.getItem().getItemDamage() == itemStack.getItemDamage())) {
                    return setting;
                }
            }
        }

        return null;
    }

    private void insertItems(ComponentMenu componentMenu, IInventory inventory, Map<Integer, SlotSideTarget> validSlots) {
        ComponentMenuItem menuItem = (ComponentMenuItem)componentMenu;

        List<OutputItemCounter> outputCounters = new ArrayList<>();

        Iterator<ItemBufferElement> bufferIterator = itemBuffer.iterator();
        while(bufferIterator.hasNext()) {
            ItemBufferElement itemBufferElement = bufferIterator.next();


            Iterator<SlotStackInventoryHolder> itemIterator = itemBufferElement.getHolders().iterator();
            while (itemIterator.hasNext()) {
                SlotStackInventoryHolder holder = itemIterator.next();
                ItemStack itemStack = holder.getItemStack();

                ItemSetting setting = isItemValid(componentMenu, itemStack);

                if ((menuItem.useWhiteList() == (setting == null)) &&  (setting == null || !setting.isLimitedByAmount())) {
                    System.out.println("Invalid: " + itemStack.toString());
                    continue;
                }
                System.out.println("Valid: " + itemStack.toString());

                OutputItemCounter outputItemCounter = null;
                for (OutputItemCounter e : outputCounters) {
                    if (e.areSettingsSame(setting)) {
                        outputItemCounter = e;
                        break;
                    }
                }

                if (outputItemCounter == null) {
                    outputItemCounter = new OutputItemCounter(itemBuffer, inventory, setting, menuItem.useWhiteList());
                    outputCounters.add(outputItemCounter);
                }

                for (SlotSideTarget slot : validSlots.values()) {
                    if (!isSlotValid(inventory, itemStack, slot, false)) {
                        continue;
                    }

                    ItemStack itemInSlot = inventory.getStackInSlot(slot.getSlot());

                    if (itemInSlot == null) {
                        ItemStack temp = itemStack.copy();
                        int moveCount = Math.min(itemStack.stackSize, inventory.getInventoryStackLimit());
                        moveCount = itemBufferElement.retrieveItemCount(moveCount);
                        moveCount = outputItemCounter.retrieveItemCount(moveCount);
                        if (moveCount > 0) {
                            itemBufferElement.decreaseStackSize(moveCount);
                            outputItemCounter.modifyStackSize(moveCount);
                            temp.stackSize = moveCount;
                            itemStack.stackSize -= moveCount;
                            inventory.setInventorySlotContents(slot.getSlot(), temp);
                            if (itemStack.stackSize == 0) {
                                removeItemFromBuffer(holder);
                                itemIterator.remove();
                                break;
                            }
                        }
                    }else if (itemInSlot.isItemEqual(itemStack) && ItemStack.areItemStackTagsEqual(itemStack, itemInSlot) && itemStack.isStackable()){
                        int moveCount = Math.min(itemStack.stackSize, Math.min(inventory.getInventoryStackLimit(), itemInSlot.getMaxStackSize()) - itemInSlot.stackSize);
                        moveCount = itemBufferElement.retrieveItemCount(moveCount);
                        moveCount = outputItemCounter.retrieveItemCount(moveCount);
                        if (moveCount > 0) {
                            itemBufferElement.decreaseStackSize(moveCount);
                            outputItemCounter.modifyStackSize(moveCount);
                            itemInSlot.stackSize += moveCount;
                            itemStack.stackSize -= moveCount;
                            if (itemStack.stackSize == 0) {
                                removeItemFromBuffer(holder);
                                itemIterator.remove();
                                break;
                            }
                        }
                    }
                }
            }

        }

    }

    private void removeItemFromBuffer(SlotStackInventoryHolder holder) {
        holder.getInventory().setInventorySlotContents(holder.getSlot(), null);
    }

}
