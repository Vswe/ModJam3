package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;
import java.util.List;


public class TileEntityBUD extends TileEntity implements ISystemListener, ITriggerNode{
    private List<TileEntityManager> managerList = new ArrayList<TileEntityManager>();

    @Override
    public void added(TileEntityManager owner) {
        if (!managerList.contains(owner)) {
            managerList.add(owner);
        }
    }

    @Override
    public void removed(TileEntityManager owner) {
        managerList.remove(owner);
    }

    public void onTrigger() {
        for (TileEntityManager tileEntityManager : managerList) {
            tileEntityManager.triggerBUD(this);
        }
    }

    @Override
    public int[] getData() {
        return new int[ForgeDirection.VALID_DIRECTIONS.length];
    }

    @Override
    public int[] getOldData() {
        return new int[ForgeDirection.VALID_DIRECTIONS.length];
    }
}
