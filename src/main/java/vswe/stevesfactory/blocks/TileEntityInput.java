package vswe.stevesfactory.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class TileEntityInput extends TileEntityClusterElement implements IRedstoneNode, ISystemListener, ITriggerNode {
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

        for (int i = managerList.size() - 1; i >= 0; i--) {
            managerList.get(i).triggerRedstone(this);
        }


        oldPowered = isPowered;
    }

    @Override
    public int[] getPower() {
        return isPowered;
    }

    private static final String NBT_SIDES = "Sides";
    private static final String NBT_POWER = "Power";


    @Override
    public void readContentFromNBT(NBTTagCompound nbtTagCompound) {
        int version = nbtTagCompound.getByte(ModBlocks.NBT_PROTOCOL_VERSION);


        NBTTagList sidesTag = nbtTagCompound.getTagList(NBT_SIDES, 10);
        for (int i = 0; i < sidesTag.tagCount(); i++) {

            NBTTagCompound sideTag = sidesTag.getCompoundTagAt(i);

            oldPowered[i] = isPowered[i] = sideTag.getByte(NBT_POWER);
        }
    }



    @Override
    public void writeContentToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setByte(ModBlocks.NBT_PROTOCOL_VERSION, ModBlocks.NBT_CURRENT_PROTOCOL_VERSION);

        NBTTagList sidesTag = new NBTTagList();
        for (int i = 0; i < isPowered.length; i++) {
            NBTTagCompound sideTag = new NBTTagCompound();

            sideTag.setByte(NBT_POWER, (byte)isPowered[i]);

            sidesTag.appendTag(sideTag);
        }


        nbtTagCompound.setTag(NBT_SIDES, sidesTag);
    }

    @Override
    public int[] getData() {
        return isPowered;
    }

    @Override
    public int[] getOldData() {
        return oldPowered;
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations() {
        return EnumSet.of(ClusterMethodRegistration.CAN_CONNECT_REDSTONE, ClusterMethodRegistration.ON_NEIGHBOR_BLOCK_CHANGED, ClusterMethodRegistration.ON_BLOCK_ADDED);
    }
}
