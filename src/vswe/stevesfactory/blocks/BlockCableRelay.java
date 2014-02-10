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
public class BlockCableRelay extends BlockCableDirectionAdvanced {
    public BlockCableRelay(int id) {
        super(id);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityRelay();
    }

    @Override
    protected String getFrontTextureName(boolean isAdvanced) {
        return isAdvanced ? "cable_relay_advanced" : "cable_relay";
    }

    @Override
    protected String getSideTextureName(boolean isAdvanced) {
        return "cable_idle";
    }


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
        super.onBlockPlacedBy(world, x, y, z, entity, item);

        TileEntityRelay relay = TileEntityCluster.getTileEntity(TileEntityRelay.class, world, x, y, z);
        if (relay != null && isAdvanced(relay.getBlockMetadata()) && !world.isRemote) {
            relay.setOwner(entity);
        }
    }

    @Override
    protected Class<? extends TileEntityClusterElement> getTeClass() {
        return TileEntityRelay.class;
    }


    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide) {
        TileEntityRelay relay = TileEntityCluster.getTileEntity(TileEntityRelay.class, world, x, y, z);
        if (relay != null && isAdvanced(relay.getBlockMetadata())) {
            if (!world.isRemote) {
                FMLNetworkHandler.openGui(player, StevesFactoryManager.instance, 0, world, x, y, z);
            }

            return true;
        }else{
            return false;
        }
    }



}
