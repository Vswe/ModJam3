package vswe.stevesjam.components;


import java.util.ArrayList;
import java.util.List;

public class ItemBufferElement {
    private ItemSetting setting;
    private FlowComponent component;
    private boolean useWhiteList;
    private int currentStackSize;
    private int totalStackSize;

    private List<SlotStackInventoryHolder> holders;

    public ItemBufferElement(FlowComponent owner, ItemSetting setting, boolean useWhiteList, SlotStackInventoryHolder target) {
        this.component = owner;
        this.setting = setting;
        this.useWhiteList = useWhiteList;
        holders = new ArrayList<>();
        addTarget(target);
    }

    public boolean addTarget(FlowComponent owner, ItemSetting setting, SlotStackInventoryHolder target) {
        if (component.getId() == owner.getId() && (this.setting == null || this.setting.getId() == setting.getId())) {
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

            int itemsToMove = Math.min(itemsAllowedToBeMoved, desiredItemCount);

            currentStackSize -= itemsToMove;
            return  itemsToMove;
        }
    }
}
