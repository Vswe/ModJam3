package vswe.stevesfactory.components;

import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.blocks.TileEntityBUD;

import java.util.EnumSet;
import java.util.List;


public class TriggerHelperBUD extends TriggerHelper {
    public TriggerHelperBUD() {
        super(false, 1, 3, ConnectionBlockType.BUD);
    }

    @Override
    protected boolean isBlockPowered(FlowComponent component, int power) {
        return false;
    }

    @Override
    public void onTrigger(FlowComponent item, EnumSet<ConnectionOption> valid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void triggerBUD(FlowComponent item, TileEntityBUD tileEntityBUD) {
        List<SlotInventoryHolder> buds = CommandExecutor.getContainers(item.getManager(), item.getMenus().get(containerId), ConnectionBlockType.BUD);

        if (buds != null) {
            for (SlotInventoryHolder bud : buds) {
                if (bud.getBUD().equals(tileEntityBUD)) {
                    activateTrigger(item, EnumSet.of(ConnectionOption.BUD));
                    break;
                }
            }
        }

    }
}
