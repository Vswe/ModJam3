package vswe.stevesjam.blocks;

import net.minecraft.tileentity.TileEntity;
import vswe.stevesjam.components.FlowItemBase;

import java.util.ArrayList;
import java.util.List;


public class TileEntityJam extends TileEntity {

    private List<FlowItemBase> items;

    public TileEntityJam() {
        items = new ArrayList<FlowItemBase>();

        items.add(new FlowItemBase(30, 30));
        items.add(new FlowItemBase(200, 30));
        items.add(new FlowItemBase(200, 80));
    }


    public List<FlowItemBase> getFlowItems() {
        return items;
    }
}
