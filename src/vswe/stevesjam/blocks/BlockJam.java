package vswe.stevesjam.blocks;


import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockJam extends BlockContainer {
    public BlockJam(int id) {
        super(id, Material.iron);
    }


    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityJam();
    }

}
