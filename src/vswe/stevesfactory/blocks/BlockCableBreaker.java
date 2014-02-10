package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableBreaker extends BlockContainer {
    public BlockCableBreaker(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + Blocks.CABLE_BREAKER_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBreaker();
    }

    @SideOnly(Side.CLIENT)
    private Icon outIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_idle");
        outIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_breaker");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int meta) {
        //pretend the meta is 3
        return getIconFromSideAndMeta(side, 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);

        return getIconFromSideAndMeta(side, meta);
    }

    @SideOnly(Side.CLIENT)
    private Icon getIconFromSideAndMeta(int side, int meta) {
        return side == meta % ForgeDirection.VALID_DIRECTIONS.length ? outIcon : blockIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);

        TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, x, y, z);
        if (breaker != null) {
            breaker.setMetaData(meta);
        }
    }
}
