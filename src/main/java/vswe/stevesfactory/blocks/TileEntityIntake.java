package vswe.stevesfactory.blocks;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;


public class TileEntityIntake extends TileEntityClusterElement implements IInventory {

    private List<EntityItem> items;

    @Override
    public int getSizeInventory() {
        updateInventory();
        return items.size() + 1; //always leave an empty slot
    }

    @Override
    public ItemStack getStackInSlot(int id) {
        updateInventory();
        id--;
        if (id < 0 || !canPickUp(items.get(id))) {
            return null;
        }else{
            return items.get(id).getEntityItem();
        }
    }

    @Override
    public ItemStack decrStackSize(int id, int count) {
        ItemStack item = getStackInSlot(id);
        if (item != null) {
            if (item.stackSize <= count) {
                setInventorySlotContents(id, null);
                return item;
            }

            ItemStack ret = item.splitStack(count);

            if (item.stackSize == 0) {
                setInventorySlotContents(id, null);
            }

            return ret;
        }else{
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int id, ItemStack itemstack) {
        updateInventory();
        id--;
        if (id < 0 || !canPickUp(items.get(id))) {
            if (itemstack != null) {
                EnumFacing direction = EnumFacing.getFront(ModBlocks.blockCableIntake.getSideMeta(getBlockMetadata()) % EnumFacing.values().length);

                double posX = getPos().getX() + 0.5 + direction.getFrontOffsetX() * 0.75;
                double posY = getPos().getY() + 0.5 + direction.getFrontOffsetY() * 0.75;
                double posZ = getPos().getZ() + 0.5 + direction.getFrontOffsetZ() * 0.75;

                if (direction.getFrontOffsetY() == 0) {
                    posY -= 0.1;
                }

                EntityItem item = new EntityItem(worldObj, posX, posY, posZ, itemstack);

                item.motionX = direction.getFrontOffsetX() * 0.2;
                item.motionY = direction.getFrontOffsetY() * 0.2;
                item.motionZ = direction.getFrontOffsetZ() * 0.2;


                item.setPickupDelay(40);
                worldObj.spawnEntityInWorld(item);


                if (id < 0) {
                    items.add(item);
                }else{
                    items.set(id, item);
                }
            }
        }else if (itemstack != null){
            items.get(id).setEntityItemStack(itemstack);
        }else{
            //seems to be an issue with setting it to null
            items.get(id).setEntityItemStack(items.get(id).getEntityItem().copy());
            items.get(id).getEntityItem().stackSize = 0;
            items.get(id).setDead();
        }
    }

    @Override
    public String getName() {
        return ModBlocks.blockCableIntake.getLocalizedName();
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(ModBlocks.blockCableIntake.getLocalizedName());
    }

    private static final int DISTANCE = 3;

    private void  updateInventory() {
        if (items == null) {
            items = new ArrayList<EntityItem>();

            int lowX = getPos().getX() - DISTANCE;
            int lowY = getPos().getY() - DISTANCE;
            int lowZ = getPos().getZ() - DISTANCE;

            int highX = getPos().getX() + 1 + DISTANCE;
            int highY = getPos().getY() + 1 + DISTANCE;
            int highZ = getPos().getZ() + 1 + DISTANCE;

            items = worldObj.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(lowX, lowY, lowZ, highX, highY, highZ));

            //remove items we can't use right away, this check is done when we interact with items too, to make sure it hasn't changed
            for (Iterator<EntityItem> iterator = items.iterator(); iterator.hasNext(); ) {
                EntityItem next = iterator.next();
                if (!canPickUp(next)) {
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public void update() {
        items = null;
    }

    private boolean canPickUp(EntityItem item) {
        return !item.isDead && (!item.cannotPickup() || ModBlocks.blockCableIntake.isAdvanced(getBlockMetadata()));
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations() {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY);
    }
}
