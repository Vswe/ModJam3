package vswe.stevesfactory.blocks;


import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesfactory.GeneratedInfo;
import vswe.stevesfactory.StevesFactoryManager;

public class BlockManager extends BlockContainer {
    public BlockManager(int id) {
        super(id, Material.iron);

        setUnlocalizedName(Blocks.MANAGER_NAME_TAG);
        setStepSound(Block.soundMetalFootstep);
        setCreativeTab(Blocks.creativeTab);
        setHardness(2F);
    }


    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityManager();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, StevesFactoryManager.instance, 0, world, x, y , z);
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    private Icon sideIcon;
    @SideOnly(Side.CLIENT)
    private Icon topIcon;
    @SideOnly(Side.CLIENT)
    private Icon botIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        sideIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":manager_side");
        topIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":manager_top");
        botIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":manager_bot");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int meta) {
        if (side == 0) {
            return botIcon;
        }else if(side == 1) {
            return topIcon;
        }else{
            return sideIcon;
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        updateInventories(world, x, y, z);  
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
        super.onNeighborBlockChange(world, x, y, z, id);

        updateInventories(world, x, y, z);
    }



    @Override
    public void breakBlock(World world, int x, int y, int z, int id, int meta) {
        super.breakBlock(world, x, y, z, id, meta);

        updateInventories(world, x, y, z);
    }

    private void updateInventories(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof TileEntityManager) {
            ((TileEntityManager)tileEntity).updateInventories();
        }
    }


   @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
       if (GeneratedInfo.inDev) {
            System.out.println("Picked" + world.isRemote);
            TileEntity te = world.getBlockTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityManager) {
                TileEntityManager manager = (TileEntityManager)te;

                if (manager.xCoord != x || manager.yCoord != y || manager.zCoord != z) {
                    return null;
                }

                ItemStack itemStack = super.getPickBlock(target, world, x, y, z);
                if (itemStack != null) {
                    NBTTagCompound tagCompound = itemStack.getTagCompound();
                    if (tagCompound == null) {
                        tagCompound = new NBTTagCompound();
                        itemStack.setTagCompound(tagCompound);
                    }

                    NBTTagCompound info = new NBTTagCompound();
                    tagCompound.setCompoundTag("Manager", info);
                    manager.writeContentToNBT(info, true);

                    System.out.println("write");
                }
                return itemStack;
            }

            System.out.println("failed to write");
            return  null;
       }else{
           return super.getPickBlock(target, world, x, y, z);
       }
    }


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
        if (GeneratedInfo.inDev) {
            System.out.println("Placed" + world.isRemote);
            TileEntity te = world.getBlockTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityManager) {
                TileEntityManager manager = (TileEntityManager)te;
                if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("Manager")) {
                    manager.readContentFromNBT(itemStack.getTagCompound().getCompoundTag("Manager"), true);
                    System.out.println("read");
                }else{
                    System.out.println("no data");
                }
            }
        }else{
            super.onBlockPlacedBy(world, x, y, z, entity, itemStack);
        }
    }

}
