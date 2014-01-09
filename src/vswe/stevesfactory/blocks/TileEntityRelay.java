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

import java.util.List;


public class TileEntityRelay extends TileEntity implements IInventory, ISidedInventory, IFluidHandler {

    private static final int MAX_CHAIN_LENGTH = 512;
    private int[] cachedAllSlots;
    private boolean blockingUsage;
    private int chainLength;
    private Entity cachedEntity;

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
        return getContainer(IFluidHandler.class);
    }

    private IInventory getInventory() {
        return getContainer(IInventory.class);
    }

    private <T> T getContainer(Class<T> type) {
        if (isBlockingUsage()) {
            return null;
        }

        blockUsage();

        if (cachedEntity != null) {
            if (cachedEntity.isDead) {
                cachedEntity = null;
            }else{
                return (T)cachedEntity;
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


            List<Entity> entities = world.getEntitiesWithinAABB(type, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
            if (entities != null && entities.size() > 0) {
                double closest = -1;
                for (Entity entity : entities) {
                    double distance = entity.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
                    if ((closest == -1 || distance < closest)) {
                        closest = distance;
                        cachedEntity = entity;
                    }
                }


                return (T)cachedEntity;
            }
        }


        return null;
    }



    @Override
    public void updateEntity() {
        cachedEntity = null;
    }


}
