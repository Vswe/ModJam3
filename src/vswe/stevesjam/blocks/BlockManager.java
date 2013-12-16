package vswe.stevesjam.blocks;


import cpw.mods.fml.common.network.FMLNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesjam.StevesJam;

public class BlockManager extends BlockContainer {
    public BlockManager(int id) {
        super(id, Material.iron);

        setUnlocalizedName(Blocks.MANAGER_NAME_TAG);
        setStepSound(Block.soundMetalFootstep);
        setCreativeTab(CreativeTabs.tabRedstone);
    }


    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityManager();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, StevesJam.instance, 0, world, x, y , z);
        }

        return true;
    }




    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesJam.RESOURCE_LOCATION + ":jam");
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        updateRedstone(world, x, y, z);
        updateInventories(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
        super.onNeighborBlockChange(world, x, y, z, id);



        updateRedstone(world, x, y, z);
        updateInventories(world, x, y, z);
    }

    private void updateRedstone(World world, int x, int y, int z) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if (tileEntity != null && tileEntity instanceof TileEntityManager) {
                TileEntityManager jam = (TileEntityManager)tileEntity;

                jam.triggerRedstone(world.isBlockIndirectlyGettingPowered(x, y, z));
            }
        }
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


}
