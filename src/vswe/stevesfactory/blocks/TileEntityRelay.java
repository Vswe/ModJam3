package vswe.stevesfactory.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import vswe.stevesfactory.wrappers.InventoryWrapper;
import vswe.stevesfactory.wrappers.InventoryWrapperHorse;
import vswe.stevesfactory.wrappers.InventoryWrapperPlayer;

import java.util.List;


public class TileEntityRelay extends TileEntity implements IInventory, ISidedInventory, IFluidHandler {

    private static final int MAX_CHAIN_LENGTH = 512;
    private int[] cachedAllSlots;
    private boolean blockingUsage;
    private int chainLength;
    private Entity[] cachedEntities = new Entity[2];
    private InventoryWrapper cachedInventoryWrapper;


    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                if (inventory instanceof ISidedInventory) {
                    return ((ISidedInventory)inventory).getAccessibleSlotsFromSide(var1);
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
    public boolean canInsertItem(int i, ItemStack itemstack, int j) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                if (inventory instanceof ISidedInventory) {
                    return ((ISidedInventory)inventory).canInsertItem(i, itemstack, j);
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
    public boolean canExtractItem(int i, ItemStack itemstack, int j) {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                if (inventory instanceof ISidedInventory) {
                    return ((ISidedInventory)inventory).canExtractItem(i, itemstack, j);
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
    public ItemStack getStackInSlotOnClosing(int i) {
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
    public String getInvName() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.getInvName();
            }

            return "Unknown";
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public boolean isInvNameLocalized() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                return inventory.isInvNameLocalized();
            }

            return false;
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
    public void openChest() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                inventory.openChest();
            }
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public void closeChest() {
        try {
            IInventory inventory = getInventory();

            if (inventory != null) {
                inventory.closeChest();
            }
        }finally {
            unBlockUsage();
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
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
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
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
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
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
    public boolean canFill(ForgeDirection from, Fluid fluid) {
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
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
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
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
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

        ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

        int x = xCoord + direction.offsetX;
        int y = yCoord + direction.offsetY;
        int z = zCoord + direction.offsetZ;

        World world = getWorldObj();
        if (world != null) {
            TileEntity te = world.getBlockTileEntity(x, y, z);

            if (te != null && type.isInstance(te)) {
                if (te instanceof TileEntityRelay) {
                    TileEntityRelay relay = (TileEntityRelay)te;
                    relay.chainLength = chainLength + 1;
                }
                return (T) te;
            }


            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
            if (entities != null) {
                double closest = -1;
                for (Entity entity : entities) {
                    double distance = entity.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
                    if (isEntityValid(entity, type) && (closest == -1 || distance < closest)) {
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


    private boolean isEntityValid(Entity entity, Class type) {
        return type.isInstance(entity) || (type == IInventory.class && (entity instanceof EntityPlayer || entity instanceof EntityHorse));
    }

    private <T> T getEntityContainer(int id) {
        if (id == 0 && cachedInventoryWrapper != null) {
            return (T)cachedInventoryWrapper;
        }

        return (T)cachedEntities[id];
    }

    @Override
    public void updateEntity() {
        cachedEntities[0] = null;
        cachedEntities[1] = null;
        cachedInventoryWrapper = null;
    }


}
