package vswe.stevesfactory.components;

import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.blocks.ConnectionBlockType;
import vswe.stevesfactory.blocks.TileEntityBUD;
import vswe.stevesfactory.blocks.TileEntityInput;

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
        List<SlotInventoryHolder> receivers = CommandExecutor.getContainers(item.getManager(), item.getMenus().get(containerId), blockType);

        if (receivers != null) {
            ComponentMenuContainer componentMenuContainer = (ComponentMenuContainer)item.getMenus().get(containerId);

            TileEntityBUD trigger = componentMenuContainer.getOption() == 0 ? tileEntityBUD : null;
            if (isPulseReceived(item, receivers, trigger, true)) {
                activateTrigger(item, EnumSet.of(ConnectionOption.BUD_PULSE));
            }

        }
    }
}
