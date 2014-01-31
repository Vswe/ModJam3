package vswe.stevesfactory.blocks;


import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.FakePlayerFactory;
import net.minecraftforge.common.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TileEntityBreaker extends TileEntity implements IInventory {

    private List<ItemStack> inventory;
    private List<ItemStack> inventoryCache;
    private boolean broken;


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
            inventoryCache = new ArrayList<ItemStack>();
            for (ItemStack itemStack : inventory) {
                inventoryCache.add(itemStack.copy());
            }
        }


        return inventory;
    }

    private void placeItem(ItemStack itemstack) {
        ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

        int x = xCoord;
        int y = yCoord;
        int z = zCoord;
        int side = direction.ordinal();
        float hitX = 0.5F + direction.offsetX * 0.5F;
        float hitY = 0.5F + direction.offsetY * 0.5F;
        float hitZ = 0.5F + direction.offsetZ * 0.5F;

        EntityPlayerMP player = FakePlayerFactory.get(worldObj, "[SFM_PLAYER]");
        int rotationSide = ROTATION_SIDE_MAPPING[side];
        player.rotationYaw = rotationSide * 90;

        if (itemstack.getItem() != null && itemstack.stackSize > 0) {
            player.theItemInWorldManager.activateBlockOrUseItem(player, worldObj, itemstack, x, y, z, side, hitX, hitY, hitZ);
        }
    }

    private static  final  double SPEED_MULTIPLIER = 0.05F;
    private static final Random rand = new Random();

    @Override
    public void updateEntity() {
        if (inventory != null) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

            double x = xCoord + direction.offsetX;
            double y = yCoord + direction.offsetY;
            double z = zCoord + direction.offsetZ;

            for (ItemStack itemStack : getInventoryForDrop()) {
                placeItem(itemStack);
                if (itemStack != null && itemStack.stackSize > 0) {



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
        inventoryCache = null;
        broken = false;
    }

    private List<ItemStack> getInventoryForDrop() {
        List<ItemStack> ret = new ArrayList<ItemStack>();
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                ItemStack newStack = itemStack.copy();


                if (!broken) {
                    for (int i = 0; i < inventoryCache.size(); i++) {
                        ItemStack copyStack = inventoryCache.get(i);

                        if (copyStack != null && newStack.isItemEqual(copyStack) && ItemStack.areItemStackTagsEqual(newStack, copyStack)) {
                            int max = Math.min(copyStack.stackSize, newStack.stackSize);

                            copyStack.stackSize -= max;
                            if (copyStack.stackSize == 0) {
                                inventoryCache.set(0, null);
                            }

                            newStack.stackSize -= max;
                            if (newStack.stackSize == 0) {
                                newStack = null;
                                break;
                            }
                        }
                    }
                }


                if (newStack != null) {
                    ret.add(newStack);
                }
            }
        }
        return ret;
    }

    @Override
    public int getSizeInventory() {
        return getInventory().size() + 1;
    }

    @Override
    public ItemStack getStackInSlot(int id) {
        if (id < getInventory().size()) {
            return getInventory().get(id);
        }else{
            return null;
        }
    }

    @Override
    public ItemStack decrStackSize(int id, int count) {

        ItemStack item = getStackInSlot(id);
        if (item != null) {
            if (item.stackSize <= count) {
                getInventory().set(id, null);
                return item;
            }

            ItemStack ret = item.splitStack(count);

            if (item.stackSize == 0) {
                getInventory().set(id, null);
            }

            return ret;
        }else{
            return null;
        }

    }

    private static final int[] ROTATION_SIDE_MAPPING = {0, 0, 2, 0, 1, 3};

    @Override
    public void setInventorySlotContents(int id, ItemStack itemstack) {
        if (id <  getInventory().size()) {
            getInventory().set(id, itemstack);
        }else{
            getInventory().add(itemstack);
            inventoryCache.add(null);
        }

        /*if (id < getInventory().size() && itemstack == null) {
            getInventory().set(id, null);
        }else if(itemstack != null) {

            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

            int x = xCoord;
            int y = yCoord;
            int z = zCoord;
            int side = direction.ordinal();
            float hitX = 0.5F + direction.offsetX * 0.5F;
            float hitY = 0.5F + direction.offsetY * 0.5F;
            float hitZ = 0.5F + direction.offsetZ * 0.5F;

            EntityPlayerMP player = FakePlayerFactory.get(worldObj, "[SFM_PLAYER]");
            int rotationSide = ROTATION_SIDE_MAPPING[side];
            player.rotationYaw = rotationSide * 90;

            if (itemstack.getItem() != null && itemstack.stackSize > 0) {
                player.theItemInWorldManager.activateBlockOrUseItem(player, worldObj, itemstack, x, y, z, side, hitX, hitY, hitZ);

                if (id <  getInventory().size()) {
                    if (itemstack.stackSize == 0) {
                        itemstack = null;
                    }
                    getInventory().set(id, itemstack);
                }else if(itemstack.stackSize > 0){
                    getInventory().add(itemstack);
                    if (inventoryCache != null) {
                        inventoryCache.add(null);
                    }
                }


            }
        } */
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
        return true;
    }

    @Override
    public void onInventoryChanged() {
        super.onInventoryChanged();

        if (inventory != null && !broken) {
            boolean match = true;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.get(i);
                ItemStack itemStackCopy = inventoryCache.get(i);

                if (itemStackCopy != null && (itemStack == null || itemStack.itemID != itemStackCopy.itemID || itemStack.getItemDamage() != itemStackCopy.getItemDamage() || !ItemStack.areItemStackTagsEqual(itemStack, itemStackCopy) || itemStack.stackSize < itemStackCopy.stackSize)) {
                    match = false;
                    break;
                }
            }

           if (!match) {
               broken = true;
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
