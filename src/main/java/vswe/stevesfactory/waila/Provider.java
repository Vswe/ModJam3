package vswe.stevesfactory.waila;

import mcp.mobius.waila.api.*;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.*;

import java.util.List;

public class Provider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity te = accessor.getTileEntity();
        if (te != null && !isShiftDown()) {
            TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, te.getWorld(), te.getPos());
            if (camouflage != null ) {
                int id = camouflage.getId(accessor.getSide().getIndex());
                int meta = camouflage.getMeta(accessor.getSide().getIndex());

                if (id != 0) {
                    Block block = Block.getBlockById(id);
                    if (block != null) {
                        return new ItemStack(block, 1, block.damageDropped(block.getStateFromMeta(meta)));
                    }
                }
            }
        }

        return new ItemStack(accessor.getBlock(), 1, accessor.getBlock().damageDropped(accessor.getBlock().getStateFromMeta(accessor.getMetadata())));
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler) {
        return list;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler) {
        if (itemStack != null && itemStack.getItem() == iWailaDataAccessor.getStack().getItem()) {
            TileEntity te = iWailaDataAccessor.getTileEntity();
            if (te != null ) {
                if (te instanceof TileEntityCluster) {
                    TileEntityCluster cluster = (TileEntityCluster)te;

                    for (byte type : cluster.getTypes()) {
                        list.add(ClusterRegistry.getRegistryList().get(type).getItemStack().getDisplayName());
                    }
                }else if(te instanceof TileEntityOutput) {
                    TileEntityOutput emitter = (TileEntityOutput)te;

                    if (isShiftDown()) {
                        for (EnumFacing side: EnumFacing.values()) {
                            list.add(getEmitterSide(emitter, side, true));
                        }
                    }else{
                        list.add(getEmitterSide(emitter, iWailaDataAccessor.getSide(), false));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler) {
        return list;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        return tag;
    }

    @SideOnly(Side.CLIENT)
    private boolean isShiftDown() {
        return GuiScreen.isShiftKeyDown();
    }

    private String getEmitterSide(TileEntityOutput emitter, EnumFacing side, boolean full) {
        String str = (emitter.hasStrongSignalAtSide(side) ? Localization.STRONG_POWER.toString() : Localization.WEAK_POWER.toString()) + ": " + emitter.getStrengthFromSide(side) + " ";

        if (full) {
            str = Localization.getDirectionLocalization(side) + " " + str;
        }

        return str;
    }

    public static void callbackRegister(IWailaRegistrar registrar){
        Provider instance = new Provider();
        registrar.registerBodyProvider(instance, BlockCableCluster.class);
        registrar.registerBodyProvider(instance, BlockCableOutput.class);


        registrar.registerStackProvider(instance, BlockCableCluster.class);
        registrar.registerStackProvider(instance, BlockCableCamouflages.class);
    }



}