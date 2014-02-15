package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.components.ComponentMenuCamouflageInside;
import vswe.stevesfactory.network.*;

import java.util.EnumSet;


public class TileEntityCamouflage extends TileEntityClusterElement implements IPacketBlock {

    public enum CamouflageType {
        NORMAL("BlockCableCamouflage", false, false),
        INSIDE("BlockCableInsideCamouflage", true, false),
        SHAPE("BlockCableShapeCamouflage", true, true);

        private String unlocalized;
        private boolean useDouble;
        private boolean useShape;

        private CamouflageType(String unlocalized, boolean useDouble, boolean useShape) {
            this.unlocalized = unlocalized;
            this.useDouble = useDouble;
            this.useShape = useShape;
        }

        public String getUnlocalized() {
            return unlocalized;
        }

        public boolean useDoubleRendering() {
            return useDouble;
        }

        public boolean useSpecialShape() {
            return useShape;
        }
    }

    public CamouflageType getCamouflageType() {
        return CamouflageType.values()[Blocks.blockCableCamouflage.getId(getBlockMetadata())];
    }

    public void setBlockBounds(BlockCamouflageBase blockCamouflageBase) {
        blockCamouflageBase.setBlockBounds(0, 0, 0, 1, 0.5F, 1);
    }

    private int[] ids = new int[ForgeDirection.VALID_DIRECTIONS.length * 2];
    private int[] metas = new int[ForgeDirection.VALID_DIRECTIONS.length * 2];

    public void setItem(ItemStack item, int side, ComponentMenuCamouflageInside.InsideSetType type) {
        switch (type) {
            case ONLY_OUTSIDE:
                setItem(item, side);
                break;
            case ONLY_INSIDE:
                setItemForInside(item, side + ForgeDirection.VALID_DIRECTIONS.length);
                break;
            case SAME:
                setItem(item, side);
                setItemForInside(item, side + ForgeDirection.VALID_DIRECTIONS.length);
                break;
            case OPPOSITE:
                setItem(item, side);
                int sidePair = side / 3;
                int sidePairInternalId = side % 3;
                int insideSide = sidePair * 3 + (sidePairInternalId == 0 ? 1 : 0);
                setItemForInside(item, insideSide + ForgeDirection.VALID_DIRECTIONS.length);
                break;
            default:
        }
    }

    private void setItemForInside(ItemStack item, int side) {
        if (getCamouflageType().useDoubleRendering()) {
            setItem(item, side);
        }
    }

    private void setItem(ItemStack item, int side) {
        int oldId = ids[side];
        int oldMeta = metas[side];

        if (item == null){
            ids[side] = 0;
            metas[side] = 0;
        }else if(item.getItem() != null && item.getItem() instanceof ItemBlock) {
            Block block = Block.blocksList[((ItemBlock)item.getItem()).getBlockID()];
            if (block != null) {
                ids[side] = block.blockID;
                metas[side] = item.getItem().getMetadata(item.getItemDamage());
            }else{
                ids[side] = 0;
                metas[side] = 0;
            }
        }

        if (ids[side] != oldId || metas[side] != oldMeta) {
            isServerDirty = true;
        }
    }

    public int getId(int side) {
        return ids[side];
    }

    public int getMeta(int side) {
        return metas[side];
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations() {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY);
    }

    @Override
    public void writeData(DataWriter dw, EntityPlayer player, boolean onServer, int id) {
        if (onServer) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == 0) {
                    dw.writeBoolean(false);
                }else{
                    dw.writeBoolean(true);
                    dw.writeData(ids[i], DataBitHelper.BLOCK_ID);
                    dw.writeData(metas[i], DataBitHelper.BLOCK_META);
                }
            }
        }else{
            //nothing to write, empty packet
        }
    }

    @Override
    public void readData(DataReader dr, EntityPlayer player, boolean onServer, int id) {
        if (onServer) {
            //respond by sending the data to the client that required it
            PacketHandler.sendBlockPacket(this, player, 0);
        }else{
            for (int i = 0; i < ids.length; i++) {
                if (!dr.readBoolean()) {
                    ids[i] = 0;
                    metas[i] = 0;
                }else{
                    ids[i] = dr.readData(DataBitHelper.BLOCK_ID);
                    metas[i] = dr.readData(DataBitHelper.BLOCK_META);
                }
            }
            worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public int infoBitLength(boolean onServer) {
        return 1;
    }

    private static final int UPDATE_BUFFER_DISTANCE = 5;
    private boolean hasClientUpdatedData;
    private boolean isServerDirty;

    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            keepClientDataUpdated();
        }else{
            if (isServerDirty) {
                isServerDirty = false;
                PacketHandler.sendBlockPacket(this, null, 0);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void keepClientDataUpdated() {
        double distance = Minecraft.getMinecraft().thePlayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);

        if (distance > Math.pow(PacketHandler.BLOCK_UPDATE_RANGE, 2)) {
            hasClientUpdatedData = false;
        }else if(!hasClientUpdatedData && distance < Math.pow(PacketHandler.BLOCK_UPDATE_RANGE - UPDATE_BUFFER_DISTANCE, 2)) {
            hasClientUpdatedData = true;
            PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 0);
        }
    }

    private static final String NBT_SIDES = "Sides";
    private static final String NBT_ID = "Id";
    private static final String NBT_META = "Meta";

    @Override
    protected void writeContentToNBT(NBTTagCompound tagCompound) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < ids.length; i++) {
            NBTTagCompound element = new NBTTagCompound();

            element.setShort(NBT_ID, (short) ids[i]);
            element.setByte(NBT_META, (byte)metas[i]);

            list.appendTag(element);
        }

        tagCompound.setTag(NBT_SIDES, list);
    }

    @Override
    protected void readContentFromNBT(NBTTagCompound tagCompound) {
        NBTTagList list = tagCompound.getTagList(NBT_SIDES);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound element = (NBTTagCompound)list.tagAt(i);

            ids[i] = element.getShort(NBT_ID);
            metas[i] = element.getByte(NBT_META);
        }
    }

    @SideOnly(Side.CLIENT)
    private Icon getIcon(int side, boolean inside) {
        if (inside) {
            side += ForgeDirection.VALID_DIRECTIONS.length;
        }

        Block block = Block.blocksList[ids[side]];
        if (block != null) {
            try {
                Icon icon = block.getIcon(side, metas[side]);
                if (icon != null) {
                    return icon;
                }
            }catch (Exception ignored) {}
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public Icon getIconWithDefault(IBlockAccess world, int x, int y, int z, BlockCamouflageBase block, int side, boolean inside) {
        Icon icon = getIcon(side, inside);
        if (icon == null) {
            icon = block.getDefaultIcon(side, world.getBlockMetadata(x, y, z)); //here we actually want to fetch the meta data of the block, rather then getting the tile entity version
        }

        return icon;
    }
}
