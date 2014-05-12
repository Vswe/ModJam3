package vswe.stevesfactory.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.interfaces.ContainerBase;

public class SFMPacket extends AbstractPacket {

    public byte[] data = new byte[0];

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buf) {
        buf.writeBytes(data);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buf) {
        data = new byte[buf.readableBytes()];
        buf.readBytes(data);
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
        DataReader dr = new DataReader(data);

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

    @Override
    public void handleServerSide(EntityPlayer player) {
        DataReader dr = new DataReader(data);

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
