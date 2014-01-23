package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class BlockCable extends Block {
    public BlockCable(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(Blocks.CABLE_NAME_TAG);
        setHardness(0.4F);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable");
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

    public void updateInventories(World world, int blockX, int blockY, int blockZ) {
        List<WorldCoordinate> visited = new ArrayList<WorldCoordinate>();

        Queue<WorldCoordinate> queue = new PriorityQueue<WorldCoordinate>();
        WorldCoordinate start = new WorldCoordinate(blockX, blockY, blockZ, 0);
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            WorldCoordinate element = queue.poll();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
                            WorldCoordinate target = new WorldCoordinate(element.getX() + x, element.getY() + y, element.getZ() + z, element.getDepth() + 1);

                            if (!visited.contains(target)) {
                                visited.add(target);
                                if (element.getDepth() < TileEntityManager.MAX_CABLE_LENGTH){
                                    int id = world.getBlockId(target.getX(), target.getY(), target.getZ());
                                    if (id == Blocks.blockManager.blockID){
                                        TileEntity tileEntity = world.getBlockTileEntity(target.getX(), target.getY(), target.getZ());
                                        if (tileEntity != null && tileEntity instanceof TileEntityManager) {
                                            ((TileEntityManager)tileEntity).updateInventories();
                                        }
                                    }else if (id == Blocks.blockCable.blockID && target.getDepth() < TileEntityManager.MAX_CABLE_LENGTH) {
                                        queue.add(target);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }



    }
}
