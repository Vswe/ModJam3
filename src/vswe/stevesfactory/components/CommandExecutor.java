package vswe.stevesfactory.components;


import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.blocks.TileEntityManager;


import java.util.*;

public class CommandExecutor {

    private TileEntityManager manager;
    private List<ItemBufferElement> itemBuffer;
    private List<Integer> usedCommands;


    public CommandExecutor(TileEntityManager manager) {
        this.manager = manager;
        itemBuffer = new ArrayList<ItemBufferElement>();
        usedCommands = new ArrayList<Integer>();
    }

    public void executeTriggerCommand(FlowComponent command, EnumSet<ConnectionOption> validTriggerOutputs) {
        for (int i = 0; i < command.getConnectionSet().getConnections().length; i++) {
            Connection connection = command.getConnection(i);
            ConnectionOption option = command.getConnectionSet().getConnections()[i];
            if (connection != null && !option.isInput() && validTriggerOutputs.contains(option)) {
                executeCommand(manager.getFlowItems().get(connection.getComponentId()));
            }
        }
    }


    private void executeCommand(FlowComponent command) {
        //a loop has occurred
        if (usedCommands.contains(command.getId())) {
            return;
        }

        usedCommands.add(command.getId());
        switch (command.getType()) {
            case INPUT:
                List<SlotInventoryHolder> inputInventory = getInventories(command.getMenus().get(0));
                if (inputInventory != null) {
                    getValidSlots(command.getMenus().get(1), inputInventory);
                    getItems(command.getMenus().get(2), inputInventory);
                }
                break;
            case OUTPUT:
                List<SlotInventoryHolder> outputInventory = getInventories(command.getMenus().get(0));
                if (outputInventory != null) {
                    getValidSlots(command.getMenus().get(1), outputInventory);
                    insertItems(command.getMenus().get(2), outputInventory);
                }
                break;
            case CONDITION:
                List<SlotInventoryHolder> conditionInventory = getInventories(command.getMenus().get(0));
                if (conditionInventory != null) {
                    getValidSlots(command.getMenus().get(1), conditionInventory);
                    if (searchForItems(command.getMenus().get(2), conditionInventory)) {
                        executeTriggerCommand(command, EnumSet.of(ConnectionOption.CONDITION_TRUE));
                    }else{
                        executeTriggerCommand(command, EnumSet.of(ConnectionOption.CONDITION_FALSE));
                    }
                }
                return;
        }


        executeTriggerCommand(command, EnumSet.allOf(ConnectionOption.class));
        usedCommands.remove((Integer)command.getId());
    }

    private List<SlotInventoryHolder> getInventories(ComponentMenu componentMenu) {
        ComponentMenuInventory menuInventory = (ComponentMenuInventory)componentMenu;

        if (menuInventory.getSelectedInventories().size() == 0) {
            return null;
        }

        List<SlotInventoryHolder> ret = new ArrayList<SlotInventoryHolder>();

        List<TileEntity> inventories = manager.getConnectedInventories();
        for (int i = 0; i < menuInventory.getSelectedInventories().size(); i++) {
            int selected = menuInventory.getSelectedInventories().get(i);

            if (selected >= 0 && selected < inventories.size()) {
                TileEntity tileEntity = inventories.get(selected);

                if (tileEntity != null && tileEntity instanceof IInventory && !tileEntity.isInvalid()) {
                    ret.add(new SlotInventoryHolder(tileEntity, menuInventory.getOption()));
                }

            }
        }

        if (ret.isEmpty()) {
            return null;
        }else{
            return ret;
        }
    }

    private void getValidSlots(ComponentMenu componentMenu, List<SlotInventoryHolder> inventories) {
        ComponentMenuTarget menuTarget = (ComponentMenuTarget)componentMenu;

        for (int i = 0; i < inventories.size(); i++) {
            IInventory inventory = inventories.get(i).getInventory();
            Map<Integer, SlotSideTarget> validSlots = inventories.get(i).getValidSlots();

            for (int side = 0; side < ComponentMenuTarget.directions.length; side++) {
                if (menuTarget.isActive(side)) {
                    int[] inventoryValidSlots;
                    if (inventory instanceof ISidedInventory) {
                        inventoryValidSlots =  ((ISidedInventory) inventory).getAccessibleSlotsFromSide(side);
                    }else{
                        inventoryValidSlots = new int[inventory.getSizeInventory()];
                        for (int j = 0; j < inventoryValidSlots.length; j++) {
                            inventoryValidSlots[j] = j;
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

        }

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

    private void getItems(ComponentMenu componentMenu, List<SlotInventoryHolder> inventories) {
        for (SlotInventoryHolder inventory : inventories) {

            ComponentMenuItem menuItem = (ComponentMenuItem)componentMenu;
            for (SlotSideTarget slot : inventory.getValidSlots().values()) {
                ItemStack itemStack = inventory.getInventory().getStackInSlot(slot.getSlot());

                if (!isSlotValid(inventory.getInventory(), itemStack, slot, true)) {
                    continue;
                }

                ItemSetting setting = isItemValid(componentMenu, itemStack);
                if ((menuItem.useWhiteList() == (setting != null)) || (setting != null && setting.isLimitedByAmount())) {
                    FlowComponent owner = componentMenu.getParent();
                    SlotStackInventoryHolder target = new SlotStackInventoryHolder(itemStack, inventory.getInventory(), slot.getSlot());

                    boolean added = false;
                    for (ItemBufferElement itemBufferElement : itemBuffer) {
                        if (itemBufferElement.addTarget(owner, setting, inventory, target)) {
                            added = true;
                            break;
                        }
                    }

                    if (!added) {
                        ItemBufferElement itemBufferElement = new ItemBufferElement(owner, setting, inventory, menuItem.useWhiteList(), target);
                        itemBuffer.add(itemBufferElement);
                    }

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

    private void insertItems(ComponentMenu componentMenu, List<SlotInventoryHolder> inventories) {
        ComponentMenuItem menuItem = (ComponentMenuItem)componentMenu;

        List<OutputItemCounter> outputCounters = new ArrayList<OutputItemCounter>();
        for (SlotInventoryHolder inventoryHolder : inventories) {
            if (!inventoryHolder.isShared()) {
                outputCounters.clear();
            }

            IInventory inventory = inventoryHolder.getInventory();
            Iterator<ItemBufferElement> bufferIterator = itemBuffer.iterator();
            while(bufferIterator.hasNext()) {
                ItemBufferElement itemBufferElement = bufferIterator.next();


                Iterator<SlotStackInventoryHolder> itemIterator = itemBufferElement.getHolders().iterator();
                while (itemIterator.hasNext()) {
                    SlotStackInventoryHolder holder = itemIterator.next();
                    ItemStack itemStack = holder.getItemStack();

                    ItemSetting setting = isItemValid(componentMenu, itemStack);

                    if ((menuItem.useWhiteList() == (setting == null)) &&  (setting == null || !setting.isLimitedByAmount())) {
                        continue;
                    }

                    OutputItemCounter outputItemCounter = null;
                    for (OutputItemCounter e : outputCounters) {
                        if (e.areSettingsSame(setting)) {
                            outputItemCounter = e;
                            break;
                        }
                    }

                    if (outputItemCounter == null) {
                        outputItemCounter = new OutputItemCounter(itemBuffer, inventories, inventory, setting, menuItem.useWhiteList());
                        outputCounters.add(outputItemCounter);
                    }

                    for (SlotSideTarget slot : inventoryHolder.getValidSlots().values()) {
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

    }

    private void removeItemFromBuffer(SlotStackInventoryHolder holder) {
        holder.getInventory().setInventorySlotContents(holder.getSlot(), null);
    }

    private boolean searchForItems(ComponentMenu componentMenu, List<SlotInventoryHolder> inventories) {
        if (inventories.get(0).isShared()) {
            Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap = new HashMap<Integer, ConditionSettingChecker>();
            for (int i = 0; i < inventories.size(); i++) {
                calculateConditionData(componentMenu, inventories.get(i), conditionSettingCheckerMap);
            }
            return checkConditionResult(componentMenu, conditionSettingCheckerMap);
        }else{
            boolean useAnd = inventories.get(0).getSharedOption() == 1;
            for (int i = 0; i < inventories.size(); i++) {
                Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap = new HashMap<Integer, ConditionSettingChecker>();
                calculateConditionData(componentMenu, inventories.get(i), conditionSettingCheckerMap);

                if (checkConditionResult(componentMenu, conditionSettingCheckerMap)) {
                    if (!useAnd) {
                        return true;
                    }
                }else if (useAnd) {
                    return false;
                }
            }
            return useAnd;
        }
    }

    private void calculateConditionData(ComponentMenu componentMenu, SlotInventoryHolder inventoryHolder, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap) {
        for (SlotSideTarget slot : inventoryHolder.getValidSlots().values()) {
            ItemStack itemStack = inventoryHolder.getInventory().getStackInSlot(slot.getSlot());

            if (!isSlotValid(inventoryHolder.getInventory(), itemStack, slot, true)) {
                continue;
            }

            ItemSetting setting = isItemValid(componentMenu, itemStack);
            if (setting != null) {
                ConditionSettingChecker conditionSettingChecker = conditionSettingCheckerMap.get(setting.getId());
                if (conditionSettingChecker == null) {
                    conditionSettingCheckerMap.put(setting.getId(), conditionSettingChecker = new ConditionSettingChecker(setting));
                }
                conditionSettingChecker.addCount(itemStack.stackSize);
            }
        }
    }

    private boolean checkConditionResult(ComponentMenu componentMenu, Map<Integer, ConditionSettingChecker> conditionSettingCheckerMap) {
        ComponentMenuItemCondition menuItem = (ComponentMenuItemCondition)componentMenu;

        for (ItemSetting setting : menuItem.getSettings()) {
            if (setting.getItem() != null) {
                ConditionSettingChecker conditionSettingChecker = conditionSettingCheckerMap.get(setting.getId());

                if (conditionSettingChecker != null && conditionSettingChecker.isTrue()) {
                    if (!menuItem.requiresAll()) {
                        return true;
                    }
                }else if (menuItem.requiresAll()) {
                    return false;
                }
            }
        }

        return menuItem.requiresAll();
    }

}
