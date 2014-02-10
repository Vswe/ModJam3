package vswe.stevesfactory.blocks;


import net.minecraft.block.BlockContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityCluster extends TileEntity {


    private List<TileEntityClusterElement> elements;
    private Map<ClusterMethodRegistration, List<ClusterRegistry>> methodRegistration;
    private List<ClusterRegistry> blocks;

    public TileEntityCluster() {
        elements = new ArrayList<TileEntityClusterElement>();
        methodRegistration = new HashMap<ClusterMethodRegistration, List<ClusterRegistry>>();
        for (ClusterMethodRegistration clusterMethodRegistration : ClusterMethodRegistration.values()) {
            methodRegistration.put(clusterMethodRegistration, new ArrayList<ClusterRegistry>());
        }
    }

    public void loadElements(ItemStack itemStack) {
        List<ClusterRegistry> blocks = new ArrayList<ClusterRegistry>();

        NBTTagCompound compound = itemStack.getTagCompound();

        if (compound != null && compound.hasKey(ItemCluster.NBT_CABLE)) {
            NBTTagCompound cable = compound.getCompoundTag(ItemCluster.NBT_CABLE);
            byte[] types = cable.getByteArray(ItemCluster.NBT_TYPES);
            for (byte type : types) {
                blocks.add(ClusterRegistry.getRegistryList().get(type));
            }
        }

        loadElements(blocks);
    }

    private void loadElements(List<ClusterRegistry> blocks) {
        for (ClusterRegistry block : blocks) {
            TileEntityClusterElement element = (TileEntityClusterElement)block.getBlock().createNewTileEntity(getWorldObj());
            elements.add(element);
            for (ClusterMethodRegistration clusterMethodRegistration : element.getRegistrations()) {
                methodRegistration.get(clusterMethodRegistration).add(block);
            }
            element.xCoord = xCoord;
            element.yCoord = yCoord;
            element.zCoord = zCoord;
            element.worldObj = worldObj;
            element.setPartOfCluster(true);
        }
    }

    public List<TileEntityClusterElement> getElements() {
        return elements;
    }

    private List<ClusterRegistry> getRegistrations(ClusterMethodRegistration method) {
        return methodRegistration.get(method);
    }

    public void onBlockPlacedBy(EntityLivingBase entity, ItemStack itemStack) {
        for (ClusterRegistry blockContainer :  getRegistrations(ClusterMethodRegistration.ON_BLOCK_PLACED_BY)) {
            blockContainer.getBlock().onBlockPlacedBy(worldObj, xCoord, yCoord, zCoord, entity, blockContainer.getItemStack(false));
        }
    }

    public void onNeighborBlockChange(int id) {
        for (ClusterRegistry blockContainer : getRegistrations(ClusterMethodRegistration.ON_NEIGHBOR_BLOCK_CHANGED)) {
            blockContainer.getBlock().onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, id);
        }
    }

    public boolean canConnectRedstone(int side) {
        for (ClusterRegistry blockContainer : getRegistrations(ClusterMethodRegistration.CAN_CONNECT_REDSTONE)) {
            if (blockContainer.getBlock().canConnectRedstone(worldObj, xCoord, yCoord, zCoord, side)) {
                return true;
            }
        }

        return false;
    }

    public void onBlockAdded() {
        for (ClusterRegistry blockContainer : getRegistrations(ClusterMethodRegistration.ON_BLOCK_ADDED)) {
            blockContainer.getBlock().onBlockAdded(worldObj, xCoord, yCoord, zCoord);
        }
    }

    public boolean shouldCheckWeakPower(int side) {
        for (ClusterRegistry blockContainer : getRegistrations(ClusterMethodRegistration.SHOULD_CHECK_WEAK_POWER)) {
            if (blockContainer.getBlock().shouldCheckWeakPower(worldObj, xCoord, yCoord, zCoord, side)) {
                return true;
            }
        }

        return false;
    }


    public int isProvidingWeakPower(int side) {
        int max = 0;

        for (ClusterRegistry blockContainer : getRegistrations(ClusterMethodRegistration.IS_PROVIDING_WEAK_POWER)) {
            max = Math.max(max, blockContainer.getBlock().isProvidingWeakPower(worldObj, xCoord, yCoord, zCoord, side));
        }

        return max;
    }

    public int isProvidingStrongPower(int side) {
        int max = 0;

        for (ClusterRegistry blockContainer : getRegistrations(ClusterMethodRegistration.IS_PROVIDING_STRONG_POWER)) {
            max = Math.max(max, blockContainer.getBlock().isProvidingStrongPower(worldObj, xCoord, yCoord, zCoord, side));
        }

        return max;
    }

    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        for (ClusterRegistry blockContainer : getRegistrations(ClusterMethodRegistration.ON_BLOCK_ACTIVATED)) {
            if (blockContainer.getBlock().onBlockActivated(worldObj, xCoord, yCoord, zCoord, player, side, hitX, hitY, hitZ)) {
                return true;
            }
        }

        return false;
    }


    public static <T> T getTileEntity(Class<? extends TileEntityClusterElement> clazz, IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if (te != null) {
            if (clazz.isInstance(te)) {
                return (T)te;
            }else if(te instanceof TileEntityCluster) {
                for (TileEntityClusterElement element : ((TileEntityCluster) te).getElements()) {
                    if (clazz.isInstance(element)) {
                        return (T)element;
                    }
                }
            }
        }

        return null;
    }


}
