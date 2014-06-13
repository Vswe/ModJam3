package vswe.stevesfactory.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.interfaces.ContainerBase;

public class PacketEventHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        DataReader dr = new DataReader(event.packet.payload().array());
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

            TileEntity te = player.worldObj.getTileEntity(x, y, z);
            if (te != null && te instanceof IPacketBlock) {
                int id = dr.readData(((IPacketBlock) te).infoBitLength(false));
                ((IPacketBlock)te).readData(dr, player, false, id);
            }
        }

        dr.close();
    }

    @SubscribeEvent
    public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        DataReader dr = new DataReader(event.packet.payload().array());
        EntityPlayer player = ((NetHandlerPlayServer)event.handler).playerEntity;

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

            TileEntity te = player.worldObj.getTileEntity(x, y, z);
            if (te != null && te instanceof IPacketBlock) {
                int id = dr.readData(((IPacketBlock) te).infoBitLength(true));
                ((IPacketBlock)te).readData(dr, player, true, id);
            }
        }

        dr.close();
    }

}
