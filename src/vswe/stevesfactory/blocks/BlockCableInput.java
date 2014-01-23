package vswe.stevesfactory.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;


public class BlockCableInput extends BlockContainer {
    public BlockCableInput(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(Blocks.CABLE_INPUT_NAME_TAG);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityInput();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_input");
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        updateRedstone(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
        super.onNeighborBlockChange(world, x, y, z, id);

        updateRedstone(world, x, y, z);
    }

    private void updateRedstone(World world, int x, int y, int z) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if (tileEntity != null && tileEntity instanceof TileEntityInput) {
                TileEntityInput input = (TileEntityInput)tileEntity;

                input.triggerRedstone();
            }
        }
    }
}
