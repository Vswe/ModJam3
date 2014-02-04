package vswe.stevesfactory.blocks;


import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.StevesFactoryManager;

import java.util.List;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableRelay extends BlockContainer {
    public BlockCableRelay(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityRelay();
    }

    @SideOnly(Side.CLIENT)
    private Icon activeIcon;
    @SideOnly(Side.CLIENT)
    private Icon advancedActiveIcon;
    @SideOnly(Side.CLIENT)
    private Icon inactiveIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        activeIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_relay");
        advancedActiveIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_relay_advanced");
        inactiveIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_idle");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int meta) {
        //pretend the meta is 3
        return getIconFromSideAndMeta(side,  addAdvancedMeta(3, meta));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);

        return getIconFromSideAndMeta(side, meta);
    }

    @SideOnly(Side.CLIENT)
    private Icon getIconFromSideAndMeta(int side, int meta) {
        return side == (getSideMeta(meta) % ForgeDirection.VALID_DIRECTIONS.length) ? isAdvanced(meta) ? advancedActiveIcon :  activeIcon : inactiveIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
        int meta = addAdvancedMeta(BlockPistonBase.determineOrientation(world, x, y, z, entity), item.getItemDamage());
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);

        if (isAdvanced(meta) && !world.isRemote) {
            TileEntityRelay relay = (TileEntityRelay)world.getBlockTileEntity(x, y, z);
            relay.setOwner(entity);
        }
    }

    @Override
    public void getSubBlocks(int id, CreativeTabs tabs, List list) {
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(id, 1, 8));
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
    public int getDamageValue(World world, int x, int y, int z) {
        return getAdvancedMeta(world.getBlockMetadata(x, y, z));
    }

    @Override
    public int damageDropped(int meta) {
        return getAdvancedMeta(meta);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide) {
        if (isAdvanced(world.getBlockMetadata(x, y, z))) {
            if (!world.isRemote) {
                FMLNetworkHandler.openGui(player, StevesFactoryManager.instance, 0, world, x, y, z);
            }

            return true;
        }else{
            return false;
        }
    }



}
