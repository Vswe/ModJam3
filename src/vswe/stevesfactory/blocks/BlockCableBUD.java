package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

public class BlockCableBUD extends BlockContainer {
    public BlockCableBUD(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + Blocks.CABLE_BUD_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBUD();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_bud");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
        TileEntityBUD bud = TileEntityCluster.getTileEntity(TileEntityBUD.class, world, x, y, z);
        if (bud != null) {
            bud.onTrigger();
        }
    }


}
