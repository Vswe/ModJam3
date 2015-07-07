package vswe.stevesfactory.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import vswe.stevesfactory.GeneratedInfo;
import vswe.stevesfactory.StevesFactoryManager;

public class BlockManager extends BlockContainer {
    public BlockManager() {
        super(Material.iron);

        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + ModBlocks.MANAGER_UNLOCALIZED_NAME);
        setStepSound(soundTypeMetal);
        setCreativeTab(ModBlocks.creativeTab);
        setHardness(2F);
    }

    public static final IProperty LIMITLESS = PropertyBool.create("limitless");

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, LIMITLESS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(LIMITLESS, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (Boolean) state.getValue(LIMITLESS) ? 1 : 0;
    }


    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityManager();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float xSide, float ySide, float zSide) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, StevesFactoryManager.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);

        updateInventories(world, pos);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        super.onNeighborBlockChange(world, pos, state, block);

        updateInventories(world, pos);
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);

        updateInventories(world, pos);
    }

    private void updateInventories(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileEntityManager) {
            ((TileEntityManager)tileEntity).updateInventories();
        }
    }


   @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
       if (GeneratedInfo.inDev) {
            System.out.println("Picked" + world.isRemote);
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityManager) {
                TileEntityManager manager = (TileEntityManager)te;

                if (manager.getPos().getX() != pos.getX() || manager.getPos().getY() != pos.getY() || manager.getPos().getZ() != pos.getZ()) {
                    return null;
                }

                ItemStack itemStack = super.getPickBlock(target, world, pos, player);
                if (itemStack != null) {
                    NBTTagCompound tagCompound = itemStack.getTagCompound();
                    if (tagCompound == null) {
                        tagCompound = new NBTTagCompound();
                        itemStack.setTagCompound(tagCompound);
                    }

                    NBTTagCompound info = new NBTTagCompound();
                    tagCompound.setTag("Manager", info);
                    manager.writeContentToNBT(info, true);

                    System.out.println("write");
                }
                return itemStack;
            }

            System.out.println("failed to write");
            return  null;
       }else{
           return super.getPickBlock(target, world, pos, player);
       }
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack itemStack) {
        if (GeneratedInfo.inDev) {
            System.out.println("Placed" + world.isRemote);
            TileEntity te = world.getTileEntity(pos);
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
            super.onBlockPlacedBy(world, pos, state, entity, itemStack);
        }
    }

}
