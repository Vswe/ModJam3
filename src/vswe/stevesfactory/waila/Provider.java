package vswe.stevesfactory.waila;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.*;

import java.util.List;

public class Provider implements IWailaDataProvider {
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity te = accessor.getTileEntity();
        if (te != null && !isShiftDown()) {
            TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, te.worldObj, te.xCoord, te.yCoord, te.zCoord);
            if (camouflage != null ) {
                int id = camouflage.getId(accessor.getPosition().sideHit);
                int meta = camouflage.getMeta(accessor.getPosition().sideHit);

                if (id != 0) {
                    Block block = Block.blocksList[id];
                    if (block != null) {
                        return new ItemStack(block, 1, block.damageDropped(meta));
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (itemStack != null && itemStack.itemID == accessor.getBlockID()) {
            TileEntity te = accessor.getTileEntity();
            if (te != null ) {
                if (te instanceof TileEntityCluster) {
                    TileEntityCluster cluster = (TileEntityCluster)te;

                    for (byte type : cluster.getTypes()) {
                        currenttip.add(ClusterRegistry.getRegistryList().get(type).getItemStack(false).getDisplayName());
                    }
                }else if(te instanceof TileEntityOutput) {
                    TileEntityOutput emitter = (TileEntityOutput)te;

                    if (isShiftDown()) {
                        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
                            currenttip.add(getEmitterSide(emitter, i, true));
                        }
                    }else{
                        currenttip.add(getEmitterSide(emitter, accessor.getPosition().sideHit, false));
                    }
                }
            }
        }
        return currenttip;
    }

    @SideOnly(Side.CLIENT)
    private boolean isShiftDown() {
        return GuiScreen.isShiftKeyDown();
    }

    private String getEmitterSide(TileEntityOutput emitter, int side, boolean full) {
        String str = (emitter.hasStrongSignalAtSide(side) ? Localization.STRONG_POWER.toString() : Localization.WEAK_POWER.toString()) + ": " + emitter.getStrengthFromSide(side) + " ";

        if (full) {
            str = Localization.getForgeDirectionLocalization(side) + " " + str;
        }

        return str;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currenttip;
    }

    public static void callbackRegister(IWailaRegistrar registrar){
        Provider instance = new Provider();
        registrar.registerBodyProvider(instance, Blocks.blockCableCluster.blockID);
        registrar.registerBodyProvider(instance, Blocks.blockCableOutput.blockID);

        registrar.registerStackProvider(instance, Blocks.blockCableCluster.blockID);
        registrar.registerStackProvider(instance, Blocks.blockCableCamouflage.blockID);
    }

}
