package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.ArrayList;
import java.util.List;


public class BlockCableCluster extends BlockCamouflageBase {
    protected BlockCableCluster() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(soundTypeMetal);
        setHardness(2F);
    }



    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon frontIcon;
    @SideOnly(Side.CLIENT)
    private IIcon sideIconAdv;
    @SideOnly(Side.CLIENT)
    private IIcon frontIconAdv;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        sideIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_cluster");
        frontIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_cluster_front");
        sideIconAdv = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_cluster_adv");
        frontIconAdv = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_cluster_adv_front");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        //pretend the meta is 3
        return getIconFromSideAndMeta(side, addAdvancedMeta(3, meta));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getDefaultIcon(int side, int blockMeta, int camoMeta) {
        return getIconFromSideAndMeta(side, blockMeta);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block oldBlock, int oldMeta) {
        ItemStack itemStack = getItemStack(world, x, y, z, oldMeta);

        if (itemStack != null) {
            dropBlockAsItem(world, x, y, z, itemStack);
        }

        super.breakBlock(world, x, y, z, oldBlock, oldMeta);

        if (isAdvanced(world.getBlockMetadata(x, y, z))) {
            ModBlocks.blockCable.updateInventories(world, x, y, z);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        ItemStack itemStack = getItemStack(world, x, y, z, world.getBlockMetadata(x, y, z));
        if (itemStack != null) {
            return itemStack;
        }

        return super.getPickBlock(target, world, x, y, z) ;
    }

    private ItemStack getItemStack(World world, int x, int y, int z, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te != null && te instanceof  TileEntityCluster) {
            TileEntityCluster cluster = (TileEntityCluster)te;
            ItemStack itemStack = new ItemStack(ModBlocks.blockCableCluster, 1, damageDropped(meta));
            NBTTagCompound compound = new NBTTagCompound();
            itemStack.setTagCompound(compound);
            NBTTagCompound cable = new NBTTagCompound();
            compound.setTag(ItemCluster.NBT_CABLE, cable);
            cable.setByteArray(ItemCluster.NBT_TYPES, cluster.getTypes());
            return itemStack;
        }

        return null;
    }


    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<ItemStack>(); //TODO Drop items here, not sure how to though since the TE is gone. please help
    }

    @SideOnly(Side.CLIENT)
    private IIcon getIconFromSideAndMeta(int side, int meta) {
        return side == getSideMeta(meta) % ForgeDirection.VALID_DIRECTIONS.length ? isAdvanced(meta) ? frontIconAdv : frontIcon : isAdvanced(meta) ? sideIconAdv : sideIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCluster();
    }

    private TileEntityCluster getTe(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof  TileEntityCluster) {
            return (TileEntityCluster)te;
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
        int meta = addAdvancedMeta(BlockPistonBase.determineOrientation(world, x, y, z, entity), itemStack.getItemDamage());
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);

        TileEntityCluster cluster = getTe(world, x, y, z);

        if (cluster != null) {
            cluster.loadElements(itemStack);

            cluster.onBlockPlacedBy(entity, itemStack);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntityCluster cluster = getTe(world, x, y, z);

        if (cluster != null) {
            cluster.onNeighborBlockChange(block);
        }

        if (isAdvanced(world.getBlockMetadata(x, y, z))) {
            ModBlocks.blockCable.updateInventories(world, x, y, z);
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityCluster cluster = getTe(world, x, y, z);

        if (cluster != null) {
            return cluster.canConnectRedstone(side);
        }

        return false;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        TileEntityCluster cluster = getTe(world, x, y, z);

        if (cluster != null) {
            cluster.onBlockAdded();
        }

        if (isAdvanced(world.getBlockMetadata(x, y, z))) {
            ModBlocks.blockCable.updateInventories(world, x, y, z);
        }
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityCluster cluster = getTe(world, x, y, z);

        if (cluster != null) {
            return cluster.shouldCheckWeakPower(side);
        }

        return false;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityCluster cluster = getTe(world, x, y, z);

        if (cluster != null) {
            return cluster.isProvidingWeakPower(side);
        }

        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityCluster cluster = getTe(world, x, y, z);

        if (cluster != null) {
            return cluster.isProvidingStrongPower(side);
        }

        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntityCluster cluster = getTe(world, x, y, z);

        if (cluster != null) {
            return cluster.onBlockActivated(player, side, hitX, hitY, hitZ);
        }

        return false;
    }



    @Override
    public void getSubBlocks(Item item, CreativeTabs tabs, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 8));
    }

    public boolean isAdvanced(int meta) {
        return (meta & 8) != 0;
    }

    public int getSideMeta(int meta) {
        return meta & 7;
    }

    private int addAdvancedMeta(int meta, int advancedMeta) {
        return meta | (advancedMeta & 8);
    }

    private int getAdvancedMeta(int meta) {
        return addAdvancedMeta(0, meta);
    }

    @Override
    public int damageDropped(int meta) {
        return getAdvancedMeta(meta);
    }

}
