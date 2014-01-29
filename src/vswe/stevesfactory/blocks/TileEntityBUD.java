package vswe.stevesfactory.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;
import java.util.List;


public class TileEntityBUD extends TileEntity implements ISystemListener, ITriggerNode{
    private List<TileEntityManager> managerList = new ArrayList<TileEntityManager>();
    private int[] oldData = new int[ForgeDirection.VALID_DIRECTIONS.length];
    private int[] data = new int[ForgeDirection.VALID_DIRECTIONS.length];

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
        updateData();

        for (TileEntityManager tileEntityManager : managerList) {
            tileEntityManager.triggerBUD(this);
        }

        makeOld();
    }



    @Override
    public int[] getData() {
        return data;
    }

    @Override
    public int[] getOldData() {
        return oldData;
    }

    public void updateData() {
        data = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
            int x = direction.offsetX + this.xCoord;
            int y = direction.offsetY + this.yCoord;
            int z = direction.offsetZ + this.zCoord;

            data[i] = (worldObj.getBlockId(x, y, z) << 4) | (worldObj.getBlockMetadata(x, y, z) & 15);
        }
    }

    public void makeOld() {
        oldData = data;
    }

    private static final String NBT_SIDES = "Sides";
    private static final String NBT_DATA = "Data";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        int version = nbtTagCompound.getByte(Blocks.NBT_PROTOCOL_VERSION);


        NBTTagList sidesTag = nbtTagCompound.getTagList(NBT_SIDES);
        for (int i = 0; i < sidesTag.tagCount(); i++) {

            NBTTagCompound sideTag = (NBTTagCompound)sidesTag.tagAt(i);

            oldData[i] = data[i] = sideTag.getShort(NBT_DATA);
        }
    }



    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setByte(Blocks.NBT_PROTOCOL_VERSION, Blocks.NBT_CURRENT_PROTOCOL_VERSION);

        NBTTagList sidesTag = new NBTTagList();
        for (int i = 0; i < data.length; i++) {
            NBTTagCompound sideTag = new NBTTagCompound();

            sideTag.setShort(NBT_DATA, (short) data[i]);

            sidesTag.appendTag(sideTag);
        }


        nbtTagCompound.setTag(NBT_SIDES, sidesTag);
    }
}
