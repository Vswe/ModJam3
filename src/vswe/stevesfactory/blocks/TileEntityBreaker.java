package vswe.stevesfactory.blocks;


import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileEntityBreaker extends TileEntity implements IInventory {

    private List<ItemStack> inventory;
    private List<ItemStack> inventoryCopy;

    private List<ItemStack> getInventory() {
        if (inventory == null) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

            int x = xCoord + direction.offsetX;
            int y = yCoord + direction.offsetY;
            int z = zCoord + direction.offsetZ;

            if (!worldObj.isAirBlock(x, y, z)) {
                Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
                if (block != null) {
                    inventory = block.getBlockDropped(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z), 0);
                }
            }
            if (inventory == null) {
                inventory = new ArrayList<ItemStack>();
            }
            inventoryCopy = new ArrayList<ItemStack>();

            for (ItemStack itemStack : inventory) {
                inventoryCopy.add(itemStack.copy());
            }
        }


        return inventory;
    }

    private static  final  double SPEED_MULTIPLIER = 0.05F;
    private static final Random rand = new Random();

    @Override
    public void updateEntity() {
        if (inventory != null && inventoryCopy == null) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

            double x = xCoord + direction.offsetX;
            double y = yCoord + direction.offsetY;
            double z = zCoord + direction.offsetZ;

            for (ItemStack itemStack : inventory) {
                if (itemStack != null) {
                    double spawnX = x + rand.nextDouble() * 0.8 + 0.1;
                    double spawnY = y + rand.nextDouble() * 0.8 + 0.1;
                    double spawnZ = z + rand.nextDouble() * 0.8 + 0.1;

                    EntityItem entityitem = new EntityItem(worldObj, spawnX, spawnY, spawnZ, itemStack);

                    entityitem.motionX = rand.nextGaussian() * SPEED_MULTIPLIER;
                    entityitem.motionY = rand.nextGaussian() * SPEED_MULTIPLIER + 0.2F;
                    entityitem.motionZ = rand.nextGaussian() * SPEED_MULTIPLIER;

                    worldObj.spawnEntityInWorld(entityitem);
                }
            }
        }
        inventory = null;
        inventoryCopy = null;
    }

    @Override
    public int getSizeInventory() {
        return getInventory().size();
    }

    @Override
    public ItemStack getStackInSlot(int id) {
        return getInventory().get(id);
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
        getInventory().set(id, itemstack);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public String getInvName() {
        return Blocks.CABLE_BREAKER_LOCALIZED_NAME;
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
        return false;
    }

    @Override
    public void onInventoryChanged() {
        super.onInventoryChanged();

        if (inventory != null && inventoryCopy != null) {
            boolean match = true;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.get(i);
                ItemStack itemStackCopy = inventoryCopy.get(i);

                if (!ItemStack.areItemStacksEqual(itemStack, itemStackCopy)) {
                    match = false;
                    break;
                }
            }

           if (!match) {
               inventoryCopy = null;
               ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

               int x = xCoord + direction.offsetX;
               int y = yCoord + direction.offsetY;
               int z = zCoord + direction.offsetZ;

               if (!worldObj.isAirBlock(x, y, z)) {
                   Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
                   if (block != null) {
                       int meta = worldObj.getBlockMetadata(x, y, z);
                       block.breakBlock(worldObj, x, y, z, block.blockID, meta);
                       worldObj.playAuxSFX(2001, x, y, z, block.blockID + (meta << 12));
                       worldObj.setBlockToAir(x, y, z);
                   }
               }

           }
        }
    }
}
