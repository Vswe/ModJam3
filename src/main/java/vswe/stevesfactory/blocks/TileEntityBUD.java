package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


public class TileEntityBUD extends TileEntityClusterElement implements ISystemListener, ITriggerNode{
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

        for (int i = managerList.size() - 1; i >= 0; i--) {
            managerList.get(i).triggerBUD(this);
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
        if (worldObj != null) {
            data = new int[data.length];
            for (int i = 0; i < data.length; i++) {
                ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[i];
                int x = direction.offsetX + this.xCoord;
                int y = direction.offsetY + this.yCoord;
                int z = direction.offsetZ + this.zCoord;

                data[i] = (Block.getIdFromBlock(worldObj.getBlock(x, y, z)) << 4) | (worldObj.getBlockMetadata(x, y, z) & 15);
            }
        }
    }

    public void makeOld() {
        oldData = data;
    }

    private static final String NBT_SIDES = "Sides";
    private static final String NBT_DATA = "Data";

    @Override
    public void readContentFromNBT(NBTTagCompound nbtTagCompound) {
        int version = nbtTagCompound.getByte(ModBlocks.NBT_PROTOCOL_VERSION);


        NBTTagList sidesTag = nbtTagCompound.getTagList(NBT_SIDES, 10);
        for (int i = 0; i < sidesTag.tagCount(); i++) {

            NBTTagCompound sideTag = sidesTag.getCompoundTagAt(i);

            oldData[i] = data[i] = sideTag.getShort(NBT_DATA);
        }
    }



    @Override
    public void writeContentToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setByte(ModBlocks.NBT_PROTOCOL_VERSION, ModBlocks.NBT_CURRENT_PROTOCOL_VERSION);

        NBTTagList sidesTag = new NBTTagList();
        for (int i = 0; i < data.length; i++) {
            NBTTagCompound sideTag = new NBTTagCompound();

            sideTag.setShort(NBT_DATA, (short) data[i]);

            sidesTag.appendTag(sideTag);
        }


        nbtTagCompound.setTag(NBT_SIDES, sidesTag);
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations() {
        return EnumSet.of(ClusterMethodRegistration.ON_NEIGHBOR_BLOCK_CHANGED);
    }
}
