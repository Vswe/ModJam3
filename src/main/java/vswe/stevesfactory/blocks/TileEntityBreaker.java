package vswe.stevesfactory.blocks;


import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesfactory.network.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class TileEntityBreaker extends TileEntityClusterElement implements IInventory, IPacketBlock {

    private static final String FAKE_PLAYER_NAME = "[SFM_PLAYER]";
    private static final UUID FAKE_PLAYER_ID = null;
    private List<ItemStack> inventory;
    private List<ItemStack> inventoryCache;
    private boolean broken;
    private EnumFacing placeDirection;
    private boolean blocked;


    private List<ItemStack> getInventory() {
        if (inventory == null) {
            EnumFacing direction = EnumFacing.getFront(getBlockMetadata() % EnumFacing.values().length);

            int x = getPos().getX() + direction.getFrontOffsetX();
            int y = getPos().getY() + direction.getFrontOffsetY();
            int z = getPos().getZ() + direction.getFrontOffsetZ();
            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = worldObj.getBlockState(pos);
            if (canBreakBlock(state, state.getBlock(), pos)) {
                inventory = state.getBlock().getDrops(worldObj, pos, state, 0);
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

    private List<ItemStack> placeItem(ItemStack itemstack) {
        List<ItemStack> items = new ArrayList<ItemStack>();

        if (itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0) {
            EnumFacing side = EnumFacing.getFront(getBlockMetadata() % EnumFacing.values().length);
            EnumFacing direction = placeDirection.getOpposite();

            float hitX = 0.5F + direction.getFrontOffsetX() * 0.5F;
            float hitY = 0.5F + direction.getFrontOffsetY() * 0.5F;
            float hitZ = 0.5F + direction.getFrontOffsetZ() * 0.5F;

            EntityPlayerMP player = FakePlayerFactory.get((WorldServer) worldObj, new GameProfile(FAKE_PLAYER_ID, FAKE_PLAYER_NAME));
            int rotationSide = ROTATION_SIDE_MAPPING[direction.ordinal()];

            player.prevRotationPitch = player.rotationYaw = rotationSide * 90;
            player.prevRotationYaw = player.rotationPitch = direction == EnumFacing.UP ? 90 : direction == EnumFacing.DOWN ? -90 : 0;
            player.prevPosX = player.posX = getPos().getX() + side.getFrontOffsetX() + 0.5 + direction.getFrontOffsetX() * 0.4;
            player.prevPosY = player.posY = getPos().getY() + side.getFrontOffsetY() + 0.5 + direction.getFrontOffsetY() * 0.4;
            player.prevPosZ = player.posZ = getPos().getZ() + side.getFrontOffsetZ() + 0.5 + direction.getFrontOffsetZ() * 0.4;
            player.eyeHeight = 0;
            player.interactionManager.setBlockReachDistance(1);

            blocked = true;
            try {
                player.inventory.clear();
                player.inventory.currentItem = 0;
                player.inventory.setInventorySlotContents(0, itemstack);
                ActionResult<ItemStack> result = itemstack.useItemRightClick(worldObj, player, EnumHand.MAIN_HAND);
                if (result.getType().equals(EnumActionResult.PASS) && ItemStack.areItemStacksEqual(result.getResult(), itemstack)) {
                    int x = getPos().getX() + side.getFrontOffsetX() - direction.getFrontOffsetX();
                    int y = getPos().getY() + side.getFrontOffsetY() - direction.getFrontOffsetY();
                    int z = getPos().getZ() + side.getFrontOffsetZ() - direction.getFrontOffsetZ();

                    player.interactionManager.processRightClickBlock(player, worldObj, itemstack, EnumHand.MAIN_HAND, new BlockPos(x, y, z), direction, hitX, hitY, hitZ);

                }else{
                    player.inventory.setInventorySlotContents(0, result.getResult());
                }
            }catch (Exception ignored) {

            }finally {
                for (ItemStack itemStack : player.inventory.mainInventory) {
                    if (itemStack != null && itemStack.stackSize > 0) {
                        items.add(itemStack);
                    }
                }
                blocked = false;
            }

        }

        return items;
    }

    @Override
    public void update() {
        if (missingPlaceDirection) {
            setPlaceDirection(EnumFacing.getFront(getBlockMetadata()));
            missingPlaceDirection = false;
        }
        if (worldObj.isRemote) {
            keepClientDataUpdated();
        }

        if (inventory != null) {
            EnumFacing direction = EnumFacing.getFront(getBlockMetadata() % EnumFacing.values().length);

            for (ItemStack itemStack : getInventoryForDrop()) {
                List<ItemStack> items = placeItem(itemStack);
                if (items != null && !items.isEmpty()) {
                    for (ItemStack item : items) {
                        double x = getPos().getX() + 0.5 + direction.getFrontOffsetX() * 0.75;
                        double y = getPos().getY() + 0.5 + direction.getFrontOffsetY() * 0.75;
                        double z = getPos().getZ() + 0.5 + direction.getFrontOffsetZ() * 0.75;


                        if (direction.getFrontOffsetY() == 0) {
                            y -= 0.1;
                        }

                        EntityItem entityitem = new EntityItem(worldObj, x, y, z, item);

                        entityitem.motionX = direction.getFrontOffsetX() * 0.1;
                        entityitem.motionY = direction.getFrontOffsetY() * 0.1;
                        entityitem.motionZ = direction.getFrontOffsetZ() * 0.1;

                        entityitem.setPickupDelay(40);
                        worldObj.spawnEntityInWorld(entityitem);
                    }
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

    private static final int[] ROTATION_SIDE_MAPPING = {0, 0, 0, 2, 3, 1};

    @Override
    public void setInventorySlotContents(int id, ItemStack itemstack) {
        if (id <  getInventory().size()) {
            getInventory().set(id, itemstack);
        }else{
            getInventory().add(itemstack);
            inventoryCache.add(null);
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        return null;
    }


    @Override
    public String getName() {
        return ModBlocks.blockCableBreaker.getLocalizedName();
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(ModBlocks.blockCableBreaker.getLocalizedName());
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

    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (inventory != null && !broken) {
            boolean match = true;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.get(i);
                ItemStack itemStackCopy = inventoryCache.get(i);

                if (itemStackCopy != null && (itemStack == null || Item.getIdFromItem(itemStack.getItem()) != Item.getIdFromItem(itemStackCopy.getItem()) || itemStack.getItemDamage() != itemStackCopy.getItemDamage() || !ItemStack.areItemStackTagsEqual(itemStack, itemStackCopy) || itemStack.stackSize < itemStackCopy.stackSize)) {
                    match = false;
                    break;
                }
            }

           if (!match) {
               EnumFacing direction = EnumFacing.getFront(getBlockMetadata() % EnumFacing.values().length);

               int x = getPos().getX() + direction.getFrontOffsetX();
               int y = getPos().getY() + direction.getFrontOffsetY();
               int z = getPos().getZ() + direction.getFrontOffsetZ();

               BlockPos pos = new BlockPos(x, y, z);
               IBlockState state = worldObj.getBlockState(pos);
               Block block = state.getBlock();


               if (canBreakBlock(state, block, pos)) {
                   broken = true;
                   int meta = state.getBlock().getMetaFromState(state);
                   block.breakBlock(worldObj, pos, state);
//                   worldObj.playAuxSFX(2001, pos, Block.getIdFromBlock(block) + (meta << 12));
                   worldObj.setBlockToAir(pos);
               }

           }
        }
    }

    private boolean canBreakBlock(IBlockState state, Block block, BlockPos pos) {
        return block != null && Block.getIdFromBlock(block) != Block.getIdFromBlock(Blocks.BEDROCK) && block.getBlockHardness(state, worldObj, pos) >= 0;
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations() {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY, ClusterMethodRegistration.ON_BLOCK_ACTIVATED);
    }

    private static final String NBT_DIRECTION = "Direction";

    private boolean missingPlaceDirection;
    @Override
    protected void readContentFromNBT(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey(NBT_DIRECTION)) {
            setPlaceDirection(EnumFacing.getFront(tagCompound.getByte(NBT_DIRECTION)));
        }else{
            if (worldObj != null) {
                setPlaceDirection(EnumFacing.getFront(getBlockMetadata()));
            }else{
                missingPlaceDirection = true;
            }
        }
    }

    @Override
    protected void writeContentToNBT(NBTTagCompound tagCompound) {
        tagCompound.setByte(NBT_DIRECTION, (byte) (placeDirection != null ? placeDirection.getIndex() : 0));
    }

    private static final int UPDATE_BUFFER_DISTANCE = 5;
    private boolean hasUpdatedData;

    @SideOnly(Side.CLIENT)
    private void keepClientDataUpdated() {
        if (isPartOfCluster()) {
            return;
        }

        double distance = Minecraft.getMinecraft().thePlayer.getDistanceSq(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);

        if (distance > Math.pow(PacketHandler.BLOCK_UPDATE_RANGE, 2)) {
            hasUpdatedData = false;
        }else if(!hasUpdatedData && distance < Math.pow(PacketHandler.BLOCK_UPDATE_RANGE - UPDATE_BUFFER_DISTANCE, 2)) {
            hasUpdatedData = true;
            PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 0);
        }
    }

    @Override
    public void writeData(DataWriter dw, EntityPlayer player, boolean onServer, int id) {
        if (onServer) {
            if (placeDirection == null) placeDirection = BlockCableBreaker.getSide(getBlockMetadata()); //might be a cheap fix, but seams to some kind of sync bug between threads or something
            dw.writeData(placeDirection.getIndex(), DataBitHelper.PLACE_DIRECTION);
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
            int val = dr.readData(DataBitHelper.PLACE_DIRECTION);
            setPlaceDirection(EnumFacing.getFront(val));
            worldObj.notifyBlockUpdate(getPos(), getWorld().getBlockState(getPos()), getWorld().getBlockState(getPos()), 3);
            markDirty();
        }
    }

    @Override
    public int infoBitLength(boolean onServer) {
        return 0;
    }

    public EnumFacing getPlaceDirection() {
        return placeDirection;
    }

    public void setPlaceDirection(EnumFacing placeDirection) {
        if (this.placeDirection != placeDirection) {
            this.placeDirection = placeDirection;
            this.markDirty();

            if (!isPartOfCluster() && worldObj != null && !worldObj.isRemote) {
                PacketHandler.sendBlockPacket(this, null, 0);
            }
        }
    }


    public boolean isBlocked() {
        return blocked;
    }
}
