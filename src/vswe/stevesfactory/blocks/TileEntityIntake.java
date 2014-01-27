package vswe.stevesfactory.blocks;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;


public class TileEntityIntake extends TileEntity implements IInventory {

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
        if (id < 0 || items.get(id).isDead) {
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
        if (id < 0 || items.get(id).isDead) {
            if (itemstack != null) {
                EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, itemstack);
                //todo define position better and speed
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
            items.get(id).setDead();
        }
    }

    private static final int DISTANCE = 3;

    private void  updateInventory() {
        if (items == null) {
            items = new ArrayList<EntityItem>();

            double centerX = xCoord + 0.5;
            double centerY = yCoord + 0.5;
            double centerZ = zCoord + 0.5;

            items = worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(centerX - DISTANCE, centerY - DISTANCE, centerZ - DISTANCE, centerX + DISTANCE, centerY + DISTANCE, centerZ + DISTANCE));
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public String getInvName() {
        return Blocks.CABLE_INTAKE_LOCALIZED_NAME;
    }

    @Override
    public boolean isInvNameLocalized() {
        return true;
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
    public void openChest() {

    }

    @Override
    public void closeChest() {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public void updateEntity() {
        items = null;
    }
}
