package vswe.stevesfactory.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesfactory.interfaces.ContainerBase;

public class PacketEventHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientPacket(final FMLNetworkEvent.ClientCustomPacketEvent event) {
        FMLClientHandler.instance().getClient().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processClientPacket(event);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    private void processClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        DataReader dr = new DataReader(event.getPacket().payload().array());
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;

        boolean useContainer = dr.readBoolean();

        if (useContainer) {
            int containerId = dr.readByte();
            Container container = player.openContainer;

            if (container != null && container.windowId == containerId && container instanceof ContainerBase) {
                if (dr.readBoolean()) {
                    ((ContainerBase) container).getTileEntity().readUpdatedData(dr, player);
                }else{
                    ((ContainerBase) container).getTileEntity().readAllData(dr , player);
                }

            }
        }else{
            int x = dr.readData(DataBitHelper.WORLD_COORDINATE);
            int y = dr.readData(DataBitHelper.WORLD_COORDINATE);
            int z = dr.readData(DataBitHelper.WORLD_COORDINATE);

            TileEntity te = player.worldObj.getTileEntity(new BlockPos(x, y, z));
            if (te != null && te instanceof IPacketBlock) {
                int id = dr.readData(((IPacketBlock) te).infoBitLength(false));
                ((IPacketBlock)te).readData(dr, player, false, id);
            }
        }

        dr.close();
    }

    @SubscribeEvent
    public void onServerPacket(final FMLNetworkEvent.ServerCustomPacketEvent event) {
        EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).playerEntity;
        player.getServerWorld().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                processServerPacket(event);
            }
        });
    }

    private void processServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        DataReader dr = new DataReader(event.getPacket().payload().array());
        EntityPlayer player = ((NetHandlerPlayServer)event.getHandler()).playerEntity;

        boolean useContainer = dr.readBoolean();

        if (useContainer) {
            int containerId = dr.readByte();
            Container container = player.openContainer;

            if (container != null && container.windowId == containerId && container instanceof ContainerBase) {
                ((ContainerBase) container).getTileEntity().readUpdatedData(dr, player);
                ((TileEntity)((ContainerBase) container).getTileEntity()).markDirty();
            }
        }else{
            int x = dr.readData(DataBitHelper.WORLD_COORDINATE);
            int y = dr.readData(DataBitHelper.WORLD_COORDINATE);
            int z = dr.readData(DataBitHelper.WORLD_COORDINATE);

            TileEntity te = player.worldObj.getTileEntity(new BlockPos(x, y, z));
            if (te != null && te instanceof IPacketBlock) {
                int id = dr.readData(((IPacketBlock) te).infoBitLength(true));
                ((IPacketBlock)te).readData(dr, player, true, id);
            }
        }

        dr.close();
    }

}
