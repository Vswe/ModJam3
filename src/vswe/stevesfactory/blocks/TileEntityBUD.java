package vswe.stevesfactory.blocks;

import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;


public class TileEntityBUD extends TileEntity implements ISystemListener{
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
}
