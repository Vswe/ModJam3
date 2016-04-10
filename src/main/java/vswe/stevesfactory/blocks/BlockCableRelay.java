package vswe.stevesfactory.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableRelay extends BlockCableDirectionAdvanced {

    @Override
    public TileEntity createNewTileEntity(World world, int var2) {
        return new TileEntityRelay();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack item) {
        super.onBlockPlacedBy(world, pos, state, entity, item);

        TileEntityRelay relay = TileEntityCluster.getTileEntity(TileEntityRelay.class, world, pos);
        if (relay != null && isAdvanced(relay.getBlockMetadata()) && !world.isRemote) {
            relay.setOwner(entity);
        }
    }

    @Override
    protected Class<? extends TileEntityClusterElement> getTeClass() {
        return TileEntityRelay.class;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float xSide, float ySide, float zSide) {
        TileEntityRelay relay = TileEntityCluster.getTileEntity(TileEntityRelay.class, world, pos);
        if (relay != null && isAdvanced(relay.getBlockMetadata())) {
            if (!world.isRemote) {
                FMLNetworkHandler.openGui(player, StevesFactoryManager.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
            }

            return true;
        }else{
            return false;
        }
    }



}
