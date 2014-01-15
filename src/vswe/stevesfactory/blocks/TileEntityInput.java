package vswe.stevesfactory.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;
import java.util.List;


public class TileEntityInput extends TileEntity implements IRedstoneNode, ISystemListener {
    private List<TileEntityManager> managerList = new ArrayList<TileEntityManager>();
    private int[] oldPowered = new int[ForgeDirection.VALID_DIRECTIONS.length];
    private int[] isPowered = new int[ForgeDirection.VALID_DIRECTIONS.length];

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

    public void triggerRedstone() {
        isPowered = new int[isPowered.length];
        for (int i = 0; i < isPowered.length; i++) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
            isPowered[i] = worldObj.getIndirectPowerLevelTo(direction.offsetX + this.xCoord, direction.offsetY + this.yCoord, direction.offsetZ + this.zCoord, direction.ordinal());
        }

        for (TileEntityManager tileEntityManager : managerList) {
            tileEntityManager.triggerRedstone(this);
        }

        oldPowered = isPowered;
    }

    public int[] getOldPowered() {
        return oldPowered;
    }

    public int[] getPowered() {
        return isPowered;
    }

    @Override
    public int[] getPower() {
        return isPowered;
    }

    private static final String NBT_SIDES = "Sides";
    private static final String NBT_POWER = "Power";


    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);

        int version = nbtTagCompound.getByte(Blocks.NBT_PROTOCOL_VERSION);


        NBTTagList sidesTag = nbtTagCompound.getTagList(NBT_SIDES);
        for (int i = 0; i < sidesTag.tagCount(); i++) {

            NBTTagCompound sideTag = (NBTTagCompound)sidesTag.tagAt(i);

            oldPowered[i] = isPowered[i] = sideTag.getByte(NBT_POWER);
        }
    }



    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);

        nbtTagCompound.setByte(Blocks.NBT_PROTOCOL_VERSION, Blocks.NBT_CURRENT_PROTOCOL_VERSION);

        NBTTagList sidesTag = new NBTTagList();
        for (int i = 0; i < isPowered.length; i++) {
            NBTTagCompound sideTag = new NBTTagCompound();

            sideTag.setByte(NBT_POWER, (byte)isPowered[i]);

            sidesTag.appendTag(sideTag);
        }


        nbtTagCompound.setTag(NBT_SIDES, sidesTag);
    }
}
