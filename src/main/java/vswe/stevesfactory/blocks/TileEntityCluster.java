package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import vswe.stevesfactory.network.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntityCluster extends TileEntity implements ITileEntityInterface, IPacketBlock {

    private boolean requestedInfo;
    private List<TileEntityClusterElement> elements;
    private List<ClusterRegistry> registryList;
    private Map<ClusterMethodRegistration, List<Pair>> methodRegistration;
    private ITileEntityInterface interfaceObject;  //only the relay is currently having a interface
    private TileEntityCamouflage camouflageObject;

    public TileEntityCluster() {
        elements = new ArrayList<TileEntityClusterElement>();
        registryList = new ArrayList<ClusterRegistry>();
        methodRegistration = new HashMap<ClusterMethodRegistration, List<Pair>>();
        for (ClusterMethodRegistration clusterMethodRegistration : ClusterMethodRegistration.values()) {
            methodRegistration.put(clusterMethodRegistration, new ArrayList<Pair>());
        }
    }

    public void loadElements(ItemStack itemStack) {
        NBTTagCompound compound = itemStack.getTagCompound();

        if (compound != null && compound.hasKey(ItemCluster.NBT_CABLE)) {
            NBTTagCompound cable = compound.getCompoundTag(ItemCluster.NBT_CABLE);
            byte[] types = cable.getByteArray(ItemCluster.NBT_TYPES);
            loadElements(types);
        }
    }

    private void loadElements(byte[] types) {
        registryList.clear();
        elements.clear();

        for (byte type : types) {
            ClusterRegistry block = ClusterRegistry.getRegistryList().get(type);
            registryList.add(block);
            TileEntityClusterElement element = (TileEntityClusterElement)block.getBlock().createNewTileEntity(getWorldObj(), 0);
            elements.add(element);
            if (element instanceof ITileEntityInterface) {
                interfaceObject = (ITileEntityInterface)element;
            }else if(element instanceof TileEntityCamouflage) {
                camouflageObject = (TileEntityCamouflage)element;
            }
            for (ClusterMethodRegistration clusterMethodRegistration : element.getRegistrations()) {
                methodRegistration.get(clusterMethodRegistration).add(new Pair(block, element));
            }
            element.xCoord = xCoord;
            element.yCoord = yCoord;
            element.zCoord = zCoord;
            element.setWorldObj(worldObj);
            element.setPartOfCluster(true);
        }
    }

    private class Pair {
        private ClusterRegistry registry;
        private TileEntityClusterElement te;

        private Pair(ClusterRegistry registry, TileEntityClusterElement te) {
            this.registry = registry;
            this.te = te;
        }
    }

    public List<TileEntityClusterElement> getElements() {
        return elements;
    }


    @Override
    public void updateEntity() {
        for (TileEntityClusterElement element : elements) {
            setWorldObject(element);
            element.updateEntity();
        }

        if (!requestedInfo && worldObj.isRemote) {
            requestedInfo = true;
            requestData();
        }
    }

    public void setWorldObject(TileEntityClusterElement te) {
        if (!te.hasWorldObj()) {
            te.setWorldObj(this.worldObj);
        }
    }

    @SideOnly(Side.CLIENT)
    private void requestData() {
        PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 1);
    }

    private List<Pair> getRegistrations(ClusterMethodRegistration method) {
        return methodRegistration.get(method);
    }

    public void onBlockPlacedBy(EntityLivingBase entity, ItemStack itemStack) {
        for (Pair blockContainer :  getRegistrations(ClusterMethodRegistration.ON_BLOCK_PLACED_BY)) {
            setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onBlockPlacedBy(worldObj, xCoord, yCoord, zCoord, entity, blockContainer.registry.getItemStack());
        }
    }

    public void onNeighborBlockChange(Block block) {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.ON_NEIGHBOR_BLOCK_CHANGED)) {
            setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, block);
        }
    }

    public boolean canConnectRedstone(int side) {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.CAN_CONNECT_REDSTONE)) {
            setWorldObject(blockContainer.te);
            if (blockContainer.registry.getBlock().canConnectRedstone(worldObj, xCoord, yCoord, zCoord, side)) {
                return true;
            }
        }

        return false;
    }

    public void onBlockAdded() {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.ON_BLOCK_ADDED)) {
            setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onBlockAdded(worldObj, xCoord, yCoord, zCoord);
        }
    }

    public boolean shouldCheckWeakPower(int side) {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.SHOULD_CHECK_WEAK_POWER)) {
            setWorldObject(blockContainer.te);
            if (blockContainer.registry.getBlock().shouldCheckWeakPower(worldObj, xCoord, yCoord, zCoord, side)) {
                return true;
            }
        }

        return false;
    }


    public int isProvidingWeakPower(int side) {
        int max = 0;

        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.IS_PROVIDING_WEAK_POWER)) {
            setWorldObject(blockContainer.te);
            max = Math.max(max, blockContainer.registry.getBlock().isProvidingWeakPower(worldObj, xCoord, yCoord, zCoord, side));
        }

        return max;
    }

    public int isProvidingStrongPower(int side) {
        int max = 0;

        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.IS_PROVIDING_STRONG_POWER)) {
            setWorldObject(blockContainer.te);
            max = Math.max(max, blockContainer.registry.getBlock().isProvidingStrongPower(worldObj, xCoord, yCoord, zCoord, side));
        }

        return max;
    }

    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.ON_BLOCK_ACTIVATED)) {
            setWorldObject(blockContainer.te);
            if (blockContainer.registry.getBlock().onBlockActivated(worldObj, xCoord, yCoord, zCoord, player, side, hitX, hitY, hitZ)) {
                return true;
            }
        }

        return false;
    }


    public static <T> T getTileEntity(Class<? extends TileEntityClusterElement> clazz, IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

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


    @Override
    public Container getContainer(TileEntity te, InventoryPlayer inv) {
        return interfaceObject == null ? null : interfaceObject.getContainer((TileEntity)interfaceObject, inv);
    }

    @Override
    public GuiScreen getGui(TileEntity te, InventoryPlayer inv) {
        return interfaceObject == null ? null : interfaceObject.getGui((TileEntity) interfaceObject, inv);
    }

    @Override
    public void readAllData(DataReader dr, EntityPlayer player) {
        if (interfaceObject != null) {
            interfaceObject.readAllData(dr, player);
        }
    }

    @Override
    public void readUpdatedData(DataReader dr, EntityPlayer player) {
        if (interfaceObject != null) {
            interfaceObject.readUpdatedData(dr, player);
        }
    }

    @Override
    public void writeAllData(DataWriter dw) {
        if (interfaceObject != null) {
            interfaceObject.writeAllData(dw);
        }
    }

    private static final String NBT_SUB_BLOCKS = "SubBlocks";
    private static final String NBT_SUB_BLOCK_ID = "SubId";
    private static final String NBT_SUB_BLOCK_META = "SubMeta";

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        NBTTagList subList = new NBTTagList();
        for (int i = 0; i < elements.size(); i++) {
            TileEntityClusterElement element = elements.get(i);
            ClusterRegistry registryElement = registryList.get(i);
            NBTTagCompound sub = new NBTTagCompound();
            sub.setByte(NBT_SUB_BLOCK_ID, (byte)registryElement.getId());
            sub.setByte(NBT_SUB_BLOCK_META, (byte)element.getBlockMetadata());
            element.writeContentToNBT(sub);

            subList.appendTag(sub);
        }


        tagCompound.setTag(NBT_SUB_BLOCKS, subList);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        NBTTagList subList = tagCompound.getTagList(NBT_SUB_BLOCKS, 10);
        List<Byte> bytes = new ArrayList<Byte>();
        for (int i = 0; i < subList.tagCount(); i++) {
            NBTTagCompound sub = subList.getCompoundTagAt(i);
            bytes.add(sub.getByte(NBT_SUB_BLOCK_ID));
        }
        byte[] byteArr = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            byteArr[i] = bytes.get(i);
        }
        loadElements(byteArr);
        for (int i = 0; i < subList.tagCount(); i++) {
            NBTTagCompound sub = subList.getCompoundTagAt(i);
            TileEntityClusterElement element = elements.get(i);
            element.setMetaData(sub.getByte(NBT_SUB_BLOCK_META));
            element.readContentFromNBT(sub);
        }
    }

    @Override
    public void writeData(DataWriter dw, EntityPlayer player, boolean onServer, int id) {
        if (id == 0) {
            if (camouflageObject != null) {
                camouflageObject.writeData(dw, player, onServer, id);
            }
        }else{

            if (onServer) {
                dw.writeData(elements.size(), DataBitHelper.CLUSTER_SUB_ID);
                for (int i = 0; i < elements.size(); i++) {
                    dw.writeData((byte)registryList.get(i).getId(), DataBitHelper.CLUSTER_SUB_ID);
                }
                for (int i = 0; i < elements.size(); i++) {
                    dw.writeData((byte)elements.get(i).getBlockMetadata(), DataBitHelper.BLOCK_META);
                }
            }else{
                //nothing to write, empty packet
            }
        }
    }

    @Override
    public void readData(DataReader dr, EntityPlayer player, boolean onServer, int id) {
        if (id == 0) {
            if (camouflageObject != null) {
                camouflageObject.readData(dr, player, onServer, id);
            }
        }else{

            if (onServer) {
                //respond by sending the data to the client that required it
                PacketHandler.sendBlockPacket(this, player, 1);
            }else{
                int length = dr.readData(DataBitHelper.CLUSTER_SUB_ID);
                byte[] types = new byte[length];
                for (int i = 0; i < length; i++) {
                    types[i] = (byte)dr.readData(DataBitHelper.CLUSTER_SUB_ID);
                }
                loadElements(types);
                for (int i = 0; i < length; i++) {
                    elements.get(i).setMetaData(dr.readData(DataBitHelper.BLOCK_META));
                }
            }
        }
    }

    @Override
    public int infoBitLength(boolean onServer) {
        return 1;
    }

    public byte[] getTypes() {
        byte[] bytes = new byte[registryList.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)registryList.get(i).getId();
        }

        return bytes;
    }
}
