package vswe.stevesfactory.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesfactory.blocks.TileEntityInterface;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.*;
import vswe.stevesfactory.interfaces.ContainerBase;
import vswe.stevesfactory.interfaces.ContainerManager;


public class PacketHandler implements IPacketHandler {
    public static final double BLOCK_UPDATE_RANGE = 128;


    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

        DataReader dr = new DataReader(packet.data);

        boolean useContainer = dr.readBoolean();

        if (useContainer) {
            int containerId = dr.readByte();
            Container container = ((EntityPlayer)player).openContainer;

            if (container != null && container.windowId == containerId && container instanceof ContainerBase) {
                boolean onServer = player instanceof EntityPlayerMP;
                if (onServer || dr.readBoolean()) {
                    ((ContainerBase) container).getTileEntity().readUpdatedData(dr, (EntityPlayer)player);
                }else{
                    ((ContainerBase) container).getTileEntity().readAllData(dr , (EntityPlayer)player);
                }

                if (onServer) {
                    ((ContainerBase) container).getTileEntity().onInventoryChanged();
                }
            }
        }else{
            int x = dr.readData(DataBitHelper.WORLD_COORDINATE);
            int y = dr.readData(DataBitHelper.WORLD_COORDINATE);
            int z = dr.readData(DataBitHelper.WORLD_COORDINATE);

            TileEntity te = ((EntityPlayer)player).worldObj.getBlockTileEntity(x, y, z);
            if (te != null && te instanceof IPacketBlock) {
                boolean onServer = player instanceof EntityPlayerMP;
                int id = dr.readData(((IPacketBlock) te).infoBitLength(onServer));
                ((IPacketBlock)te).readData(dr, ((EntityPlayer) player), onServer, id);
            }
        }




        dr.close();
    }

    public static void sendDataToPlayer(ICrafting crafting, DataWriter dw) {
        if (crafting instanceof Player) {
            Player player = (Player)crafting;

            dw.sendPlayerPacket(player);
            dw.close();
        }
    }



    public static void sendDataToServer(DataWriter dw) {
        dw.sendServerPacket();
        dw.close();
    }

    public static void sendDataToListeningClients(ContainerBase container, DataWriter dw) {
        dw.sendPlayerPackets(container);
        dw.close();
    }


    public static void sendAllData(Container container, ICrafting crafting, TileEntityInterface te) {
        DataWriter dw = new DataWriter();

        dw.writeBoolean(true); //use container
        dw.writeByte(container.windowId);
        dw.writeBoolean(false); //all data
        te.writeAllData(dw);

        sendDataToPlayer(crafting, dw);
    }



   /* public static void readBlockPacket(DataReader dr) {
        int x = dr.readData(DataBitHelper.WORLD_COORDINATE);
        int y = dr.readData(DataBitHelper.WORLD_COORDINATE);
        int z = dr.readData(DataBitHelper.WORLD_COORDINATE);

        World world = Minecraft.getMinecraft().theWorld;
        if (world.getBlockId(x, y, z) == Blocks.blockCable.blockID) {
            Blocks.blockCable.update(world, x, y, z);
        }
    }*/

    public static DataWriter getWriterForUpdate(Container container) {
        DataWriter dw = new DataWriter();

        dw.writeBoolean(true); //use container
        dw.writeByte(container.windowId);
        dw.writeBoolean(true); //updated data

        return dw;
    }




    private static DataWriter getWriterForSpecificData(Container container) {
        DataWriter dw = new DataWriter();

        dw.writeBoolean(true); //use container
        dw.writeByte(container.windowId);
        dw.writeBoolean(true); //updated data
        dw.writeBoolean(false); //not new

        return dw;
    }

    @SideOnly(Side.CLIENT)
    public static DataWriter getWriterForServerPacket() {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;

        if (container != null) {
            DataWriter dw = new DataWriter();
            dw.writeBoolean(true); //use container
            dw.writeByte(container.windowId);

            return dw;
        }else{
            return null;
        }
    }

    private static void createNonComponentPacket(DataWriter dw) {
        dw.writeBoolean(false); //this is a packet that has nothing to do with a specific FlowComponent
    }

    private static void createComponentPacket(DataWriter dw, FlowComponent component, ComponentMenu menu) {
        dw.writeBoolean(true); //this is a packet for a specific FlowComponent
        dw.writeData(component.getId(), DataBitHelper.FLOW_CONTROL_COUNT);

        if (menu != null) {
            dw.writeBoolean(true); //this is packet for a specific menu
            dw.writeData(menu.getId(), DataBitHelper.FLOW_CONTROL_MENU_COUNT);
        }else{
            dw.writeBoolean(false); //this is a packet that has nothing to do with a menu
        }
    }

    public static void sendUpdateInventoryPacket(ContainerManager container) {
        DataWriter dw = PacketHandler.getWriterForSpecificData(container);
        createNonComponentPacket(dw);
        dw.writeBoolean(true);
        sendDataToListeningClients(container, dw);
    }

    public static DataWriter getWriterForServerComponentPacket(FlowComponent component, ComponentMenu menu) {
        DataWriter dw = PacketHandler.getWriterForServerPacket();
        createComponentPacket(dw, component, menu);
        return dw;
    }

    public static DataWriter getWriterForClientComponentPacket(ContainerManager container, FlowComponent component, ComponentMenu menu) {
        DataWriter dw = PacketHandler.getWriterForSpecificData(container);
        createComponentPacket(dw, component, menu);
        return dw;
    }




    public static void writeAllComponentData(DataWriter dw, FlowComponent flowComponent) {
        dw.writeData(flowComponent.getX(), DataBitHelper.FLOW_CONTROL_X);
        dw.writeData(flowComponent.getY(), DataBitHelper.FLOW_CONTROL_Y);
        dw.writeData(flowComponent.getType().getId(), DataBitHelper.FLOW_CONTROL_TYPE_ID);
        dw.writeString(flowComponent.getComponentName(), DataBitHelper.NAME_LENGTH);

        for (ComponentMenu menu : flowComponent.getMenus()) {
            menu.writeData(dw);
        }

        for (int i = 0; i < flowComponent.getConnectionSet().getConnections().length; i++) {
            Connection connection = flowComponent.getConnection(i);
            dw.writeBoolean(connection != null);
            if (connection != null) {
                dw.writeData(connection.getComponentId(), DataBitHelper.FLOW_CONTROL_COUNT);
                dw.writeData(connection.getConnectionId(), DataBitHelper.CONNECTION_ID);

                dw.writeData(connection.getNodes().size(), DataBitHelper.NODE_ID);
                for (Point point : connection.getNodes()) {
                    dw.writeData(point.getX(), DataBitHelper.FLOW_CONTROL_X);
                    dw.writeData(point.getY(), DataBitHelper.FLOW_CONTROL_Y);
                }
            }
        }

        flowComponent.getManager().updateVariables();
    }



    public static DataWriter getButtonPacketWriter() {
        DataWriter dw = getWriterForServerPacket();
        createNonComponentPacket(dw);
        return dw;
    }

    public static void sendNewFlowComponent(ContainerManager container, FlowComponent component) {
        DataWriter dw = new DataWriter();

        dw.writeBoolean(true); //use container
        dw.writeByte(container.windowId);
        dw.writeBoolean(true); //updated data
        dw.writeBoolean(true); //new data;

        writeAllComponentData(dw, component);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    public static void sendRemovalPacket(ContainerManager container, int idToRemove) {
        DataWriter dw = PacketHandler.getWriterForSpecificData(container);
        createNonComponentPacket(dw);
        dw.writeBoolean(false);
        dw.writeData(idToRemove, DataBitHelper.FLOW_CONTROL_COUNT);
        sendDataToListeningClients(container, dw);
    }

    public static void sendBlockPacket(IPacketBlock block, EntityPlayer player, int id) {
        if (block instanceof TileEntity) {
            TileEntity te = (TileEntity)block;
            boolean onServer = player == null || !player.worldObj.isRemote;

            DataWriter dw = new DataWriter();
            dw.writeBoolean(false); //no container
            dw.writeData(te.xCoord, DataBitHelper.WORLD_COORDINATE);
            dw.writeData(te.yCoord, DataBitHelper.WORLD_COORDINATE);
            dw.writeData(te.zCoord, DataBitHelper.WORLD_COORDINATE);
            int length = block.infoBitLength(onServer);
            if (length != 0) {
                dw.writeData(id, length);
            }
            block.writeData(dw, player, onServer, id);

            if (!onServer) {
                dw.sendServerPacket();
            }else if(player != null) {
                dw.sendPlayerPacket((Player)player);
            }else{
                dw.sendPlayerPackets(te.xCoord + 0.5, te.yCoord, te.zCoord, BLOCK_UPDATE_RANGE, te.worldObj.provider.dimensionId);
            }
        }
    }
}
