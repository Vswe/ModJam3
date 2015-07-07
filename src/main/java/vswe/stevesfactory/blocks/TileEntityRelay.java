package vswe.stevesfactory.blocks;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesfactory.interfaces.ContainerRelay;
import vswe.stevesfactory.interfaces.GuiRelay;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataReader;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;
import vswe.stevesfactory.wrappers.InventoryWrapper;
import vswe.stevesfactory.wrappers.InventoryWrapperHorse;
import vswe.stevesfactory.wrappers.InventoryWrapperPlayer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class TileEntityRelay extends TileEntityClusterElement implements IInventory, ISidedInventory, IFluidHandler, ITileEntityInterface {

    private static final int MAX_CHAIN_LENGTH = 512;
    private int[] cachedAllSlots;
    private boolean blockingUsage;
    private int chainLength;
    private Entity[] cachedEntities = new Entity[2];
    private InventoryWrapper cachedInventoryWrapper;

    //used by the advanced version
    private List<UserPermission> permissions = new ArrayList<UserPermission>();
    private boolean doesListRequireOp = false;
    private UUID owner = null;
    private boolean creativeMode;
    public static int PERMISSION_MAX_LENGTH = 255;

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(EntityLivingBase entity) {
        if (entity != null && entity instanceof EntityPlayer) {
            owner = entity.getUniqueID();
        }
    }

    public void setListRequireOp(boolean val) {
        doesListRequireOp = val;
    }

    public boolean doesListRequireOp() {
        return doesListRequireOp;
    }

    public boolean isCreativeMode() {
        return creativeMode;
    }

    public void setCreativeMode(boolean creativeMode) {
        this.creativeMode = creativeMode;
    }

    public List<UserPermission> getPermissions() {
        return permissions;
    }

    private boolean isAdvanced() {
        return ModBlocks.blockCableRelay.isAdvanced(getBlockMetadata());
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                if (inventory instanceof ISidedInventory) {
                    return ((ISidedInventory)inventory).getSlotsForFace(side);
                }else{
                    int size = inventory.getSizeInventory();
                    if (cachedAllSlots == null || cachedAllSlots.length != size) {
                        cachedAllSlots = new int[size];
                        for (int i = 0; i < size; i++) {
                            cachedAllSlots[i] = i;
                        }
                    }
                    return cachedAllSlots;
                }
            }

            return new int[0];
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, EnumFacing side) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                if (inventory instanceof ISidedInventory) {
                    return ((ISidedInventory)inventory).canInsertItem(i, itemstack, side);
                }else{
                    return inventory.isItemValidForSlot(i, itemstack);
                }
            }

            return false;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, EnumFacing side) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                if (inventory instanceof ISidedInventory) {
                    return ((ISidedInventory)inventory).canExtractItem(i, itemstack, side);
                }else{
                    return inventory.isItemValidForSlot(i, itemstack);
                }
            }

            return false;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public int getSizeInventory() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.getSizeInventory();
            }

            return 0;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.getStackInSlot(i);
            }

            return null;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return  inventory.decrStackSize(i, j);
            }

            return null;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        //don't drop the things twice
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                inventory.setInventorySlotContents(i, itemstack);
            }
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public String getName() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.getName();
            }

            return "Unknown";
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public boolean hasCustomName() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.hasCustomName();
            }

            return false;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public IChatComponent getDisplayName() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.getDisplayName();
            }

            return null;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.getInventoryStackLimit();
            }

            return 0;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.isUseableByPlayer(entityplayer);
            }

            return false;
        }finally {
            unBlockUsage();
        }
    }


    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.isItemValidForSlot(i, itemstack);
            }

            return false;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public int getField(int id) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.getFieldCount();
            }

            return 0;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public void setField(int id, int value) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                inventory.setField(id, value);
            }
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public int getFieldCount() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.getFieldCount();
            }

            return 0;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public void clear() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                inventory.clear();
            }
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public void openInventory(EntityPlayer player) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                inventory.openInventory(player);
            }
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                inventory.closeInventory(player);
            }
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        try {
            IFluidHandler tank = getTank();

            if (tank != null) {
                return tank.fill(from, resource, doFill);
            }

            return 0;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        try {
            IFluidHandler tank = getTank();

            if (tank != null) {
                return tank.drain(from, resource, doDrain);
            }

            return null;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        try {
            IFluidHandler tank = getTank();

            if (tank != null) {
                return tank.drain(from, maxDrain, doDrain);
            }

            return null;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        try {
            IFluidHandler tank = getTank();

            if (tank != null) {
                return tank.canFill(from, fluid);
            }

            return false;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        try {
            IFluidHandler tank = getTank();

            if (tank != null) {
                return tank.canDrain(from, fluid);
            }

            return false;
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        try {
            IFluidHandler tank = getTank();

            if (tank != null) {
                return tank.getTankInfo(from);
            }

            return new FluidTankInfo[0];
        }finally {
            unBlockUsage();
        }
    }


    @Override
    public void markDirty() {
        //super.onInventoryChanged();
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                inventory.markDirty();
            }
        }finally {
            unBlockUsage();
        }
    }

    private void blockUsage() {
        blockingUsage = true;
    }

    private void unBlockUsage() {
        blockingUsage = false;
        chainLength = 0;
    }

    public boolean isBlockingUsage() {
        return blockingUsage || chainLength >= MAX_CHAIN_LENGTH;
    }

    private IFluidHandler getTank() {
        return getContainer(IFluidHandler.class, 1);
    }

    private IInventory getInventory() {
        return getContainer(IInventory.class, 0);
    }

    private <T> T getContainer(Class<T> type, int id) {
        if (isBlockingUsage()) {
            return null;
        }

        blockUsage();

        if (cachedEntities[id] != null) {
            if (cachedEntities[id].isDead) {
                cachedEntities[id] = null;
                if (id == 0) {
                    cachedInventoryWrapper = null;
                }
            }else{
                return getEntityContainer(id);
            }
        }

        EnumFacing direction = ((EnumFacing)getWorld().getBlockState(getPos()).getValue(BlockCableRelay.FACING));

        int x = getPos().getX() + direction.getFrontOffsetX();
        int y = getPos().getY() + direction.getFrontOffsetY();
        int z = getPos().getZ() + direction.getFrontOffsetZ();

        World world = getWorld();
        if (world != null) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

            if (te != null && type.isInstance(te)) {
                if (te instanceof TileEntityRelay) {
                    TileEntityRelay relay = (TileEntityRelay)te;
                    relay.chainLength = chainLength + 1;
                }
                return (T) te;
            }


            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
            if (entities != null) {
                double closest = -1;
                for (Entity entity : entities) {
                    double distance = entity.getDistanceSq(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);
                    if (isEntityValid(entity, type, id) && (closest == -1 || distance < closest)) {
                        closest = distance;
                        cachedEntities[id] = entity;
                    }
                }
                if (id == 0 && cachedEntities[id] != null) {
                    cachedInventoryWrapper = getInventoryWrapper(cachedEntities[id]);
                }

                return getEntityContainer(id);
            }
        }


        return null;
    }

    private InventoryWrapper getInventoryWrapper(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return new InventoryWrapperPlayer((EntityPlayer)entity);
        }else if(entity instanceof EntityHorse) {
            return new InventoryWrapperHorse((EntityHorse)entity);
        }else{
            return null;
        }
    }


    private boolean isEntityValid(Entity entity, Class type, int id) {
        return type.isInstance(entity) || (id == 0 && ((entity instanceof EntityPlayer && allowPlayerInteraction((EntityPlayer) entity)) || entity instanceof EntityHorse));
    }

    private <T> T getEntityContainer(int id) {
        if (id == 0 && cachedInventoryWrapper != null) {
            return (T)cachedInventoryWrapper;
        }

        return (T)cachedEntities[id];
    }

    @Override
    public void update() {
        cachedEntities[0] = null;
        cachedEntities[1] = null;
        cachedInventoryWrapper = null;
    }

    public boolean allowPlayerInteraction(EntityPlayer player) {
        return isAdvanced() && (creativeMode != isPlayerActive(player));
    }

    private boolean isPlayerActive(EntityPlayer player) {
        if (player != null) {
            for (UserPermission permission : permissions) {
                if (permission.getUserId().equals(player.getUniqueID())) {
                    return permission.isActive();
                }
            }
        }

        return false;
    }

    @Override
    public Container getContainer(TileEntity te, InventoryPlayer inv) {
        return new ContainerRelay((TileEntityRelay)te, inv);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(TileEntity te, InventoryPlayer inv) {
        return new GuiRelay((TileEntityRelay)te, inv);
    }

    @Override
    public void writeAllData(DataWriter dw) {
        dw.writeString(owner.toString(), DataBitHelper.UUID_LENGTH);
        dw.writeBoolean(creativeMode);
        dw.writeBoolean(doesListRequireOp);
        dw.writeData(permissions.size(), DataBitHelper.PERMISSION_ID);
        for (UserPermission permission : permissions) {
            dw.writeString(permission.getUserId().toString(), DataBitHelper.UUID_LENGTH);
            dw.writeString(permission.getUserName(), DataBitHelper.NAME_LENGTH);
            dw.writeBoolean(permission.isActive());
            dw.writeBoolean(permission.isOp());
        }

    }

    @Override
    public void readAllData(DataReader dr, EntityPlayer player) {
        owner = UUID.fromString(dr.readString(DataBitHelper.UUID_LENGTH));

        creativeMode = dr.readBoolean();
        doesListRequireOp = dr.readBoolean();
        int length = dr.readData(DataBitHelper.PERMISSION_ID);
        permissions.clear();

        for (int i = 0; i < length; i++) {
            UserPermission permission = new UserPermission(UUID.fromString(dr.readString(DataBitHelper.UUID_LENGTH)), dr.readString(DataBitHelper.NAME_LENGTH));
            permission.setActive(dr.readBoolean());
            permission.setOp(dr.readBoolean());
            permissions.add(permission);
        }
    }



    @Override
    public void readUpdatedData(DataReader dr, EntityPlayer player) {
        if (!worldObj.isRemote)  {
            boolean action = dr.readBoolean();
            if (action) {

                return;
            }
        }

        UUID userId = player.getUniqueID();

        boolean isOp = false;
        if (worldObj.isRemote || userId.equals(owner)) {
            isOp = true;
        }else{
            for (UserPermission permission : permissions) {
                if (userId.equals(permission.getUserId())) {
                    isOp = permission.isOp();
                    break;
                }
            }
        }

        boolean userData = dr.readBoolean();
        if (userData) {
            boolean added = dr.readBoolean();
            if (added) {
                String UUIDS = dr.readString(DataBitHelper.UUID_LENGTH);
                UserPermission permission = new UserPermission(UUID.fromString(UUIDS), dr.readString(DataBitHelper.NAME_LENGTH));

                for (UserPermission userPermission : permissions) {
                    if (userPermission.getUserId().equals(permission.getUserId())) {
                        return;
                    }
                }

                if (worldObj.isRemote) {
                    permission.setActive(dr.readBoolean());
                    permission.setOp(dr.readBoolean());
                }

                if (permissions.size() < TileEntityRelay.PERMISSION_MAX_LENGTH && (worldObj.isRemote || permission.getUserId().equals(userId))) {
                    permissions.add(permission);
                }
            }else{
                int id = dr.readData(DataBitHelper.PERMISSION_ID);

                if (id >= 0 && id < permissions.size()) {
                    boolean deleted = dr.readBoolean();
                    if (deleted) {
                        UserPermission permission = permissions.get(id);
                        if (isOp || permission.getUserId().equals(userId)) {
                            permissions.remove(id);
                        }
                    }else if(isOp){

                        UserPermission permission = permissions.get(id);
                        permission.setActive(dr.readBoolean());
                        permission.setOp(dr.readBoolean());

                    }
                }
            }


        }else if(isOp){
            creativeMode = dr.readBoolean();
            doesListRequireOp = dr.readBoolean();
        }
    }


    public void updateData(ContainerRelay container) {
        if (container.oldCreativeMode != isCreativeMode() || container.oldOpList != doesListRequireOp()) {
            container.oldOpList = doesListRequireOp();
            container.oldCreativeMode = isCreativeMode();

            DataWriter dw = PacketHandler.getWriterForUpdate(container);
            dw.writeBoolean(false); //no user data
            dw.writeBoolean(creativeMode);
            dw.writeBoolean(doesListRequireOp);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        //added
        if (permissions.size() > container.oldPermissions.size()) {
            int id = container.oldPermissions.size();
            UserPermission permission = permissions.get(id);

            DataWriter dw = PacketHandler.getWriterForUpdate(container);
            dw.writeBoolean(true); //user data
            dw.writeBoolean(true); //added
            dw.writeString(permission.getUserId().toString(), DataBitHelper.UUID_LENGTH);
            dw.writeString(permission.getUserName(), DataBitHelper.NAME_LENGTH);
            dw.writeBoolean(permission.isActive());
            dw.writeBoolean(permission.isOp());
            PacketHandler.sendDataToListeningClients(container, dw);

            container.oldPermissions.add(permission.copy());
        //removed
        }else if (permissions.size() < container.oldPermissions.size()){
            for (int i = 0; i < container.oldPermissions.size(); i++) {
                if (i >= permissions.size() || !permissions.get(i).getUserId().equals(container.oldPermissions.get(i).getUserId())) {
                    DataWriter dw = PacketHandler.getWriterForUpdate(container);
                    dw.writeBoolean(true); //user data
                    dw.writeBoolean(false); //existing
                    dw.writeData(i, DataBitHelper.PERMISSION_ID);
                    dw.writeBoolean(true); //deleted
                    PacketHandler.sendDataToListeningClients(container, dw);
                    container.oldPermissions.remove(i);
                    break;
                }
            }
        //updated
        }else{
            for (int i = 0; i < permissions.size(); i++) {
                UserPermission permission = permissions.get(i);
                UserPermission oldPermission = container.oldPermissions.get(i);

                if (permission.isOp() != oldPermission.isOp() || permission.isActive() != oldPermission.isActive()) {
                    DataWriter dw = PacketHandler.getWriterForUpdate(container);
                    dw.writeBoolean(true); //user data
                    dw.writeBoolean(false); //existing
                    dw.writeData(i, DataBitHelper.PERMISSION_ID);
                    dw.writeBoolean(false); //update
                    dw.writeBoolean(permission.isActive());
                    dw.writeBoolean(permission.isOp());
                    PacketHandler.sendDataToListeningClients(container, dw);

                    oldPermission.setActive(permission.isActive());
                    oldPermission.setOp(permission.isOp());
                }
            }
        }
    }

    private static final String NBT_OWNER = "Owner";
    private static final String NBT_CREATIVE = "Creative";
    private static final String NBT_LIST = "ShowList";
    private static final String NBT_PERMISSIONS = "Permissions";
    private static final String NBT_UUID = "UUID";
    private static final String NBT_NAME = "Name";
    private static final String NBT_ACTIVE = "Active";
    private static final String NBT_EDITOR = "Editor";

    @Override
    public void writeContentToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setByte(ModBlocks.NBT_PROTOCOL_VERSION, ModBlocks.NBT_CURRENT_PROTOCOL_VERSION);

        if (isAdvanced()) {
            nbtTagCompound.setString(NBT_OWNER, owner != null ? owner.toString(): UUID.randomUUID().toString());
            nbtTagCompound.setBoolean(NBT_CREATIVE, creativeMode);
            nbtTagCompound.setBoolean(NBT_LIST, doesListRequireOp);

            NBTTagList permissionTags = new NBTTagList();
            for (UserPermission permission : permissions) {
                NBTTagCompound permissionTag = new NBTTagCompound();
                permissionTag.setString(NBT_UUID, permission.getUserId().toString());
                permissionTag.setString(NBT_NAME, permission.getUserName());
                permissionTag.setBoolean(NBT_ACTIVE, permission.isActive());
                permissionTag.setBoolean(NBT_EDITOR, permission.isOp());
                permissionTags.appendTag(permissionTag);
            }
            nbtTagCompound.setTag(NBT_PERMISSIONS, permissionTags);
        }
    }

    @Override
    public void readContentFromNBT(NBTTagCompound nbtTagCompound) {
        int version = nbtTagCompound.getByte(ModBlocks.NBT_PROTOCOL_VERSION);

        if (nbtTagCompound.hasKey(NBT_OWNER)) {
            if (version > 12) {
                owner = UUID.fromString(nbtTagCompound.getString(NBT_OWNER));
            } else {
                owner = null;
            }
            creativeMode = nbtTagCompound.getBoolean(NBT_CREATIVE);
            doesListRequireOp = nbtTagCompound.getBoolean(NBT_LIST);
            permissions.clear();

            NBTTagList permissionTags = nbtTagCompound.getTagList(NBT_PERMISSIONS, 10);
            for (int i = 0; i < permissionTags.tagCount(); i++) {
                NBTTagCompound permissionTag = permissionTags.getCompoundTagAt(i);
                UserPermission permission = new UserPermission(UUID.fromString(permissionTag.getString(NBT_UUID)), permissionTag.getString(NBT_NAME));
                permission.setActive(permissionTag.getBoolean(NBT_ACTIVE));
                permission.setOp(permissionTag.getBoolean(NBT_EDITOR));
                permissions.add(permission);
            }
        }
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations() {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY, ClusterMethodRegistration.ON_BLOCK_ACTIVATED);
    }
}
