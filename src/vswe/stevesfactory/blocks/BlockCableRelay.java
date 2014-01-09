package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableRelay extends BlockContainer {
    public BlockCableRelay(int id) {
        super(id, Material.iron);
        setCreativeTab(CreativeTabs.tabRedstone);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(Blocks.CABLE_RELAY_NAME_TAG);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityRelay();
    }

    @SideOnly(Side.CLIENT)
    private Icon inactiveIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_relay");
        inactiveIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_idle");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int meta) {
        //pretend the meta is 2
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
        return side == (meta % ForgeDirection.VALID_DIRECTIONS.length) ?  blockIcon : inactiveIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }


    private TileEntityRelay getTileEntity(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof TileEntityRelay) {
            return (TileEntityRelay)te;
        }else{
            return null;
        }
    }
}
