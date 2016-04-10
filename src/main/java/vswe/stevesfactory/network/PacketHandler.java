package vswe.stevesfactory.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.stevesfactory.blocks.ITileEntityInterface;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.ComponentMenu;
import vswe.stevesfactory.components.Connection;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.components.Point;
import vswe.stevesfactory.interfaces.ContainerBase;
import vswe.stevesfactory.interfaces.ContainerManager;


public class PacketHandler {
    public static final double BLOCK_UPDATE_RANGE = 128;

    public static void sendDataToPlayer(ICrafting crafting, DataWriter dw) {
        if (crafting instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)crafting;

            dw.sendPlayerPacket(player);
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


    public static void sendAllData(Container container, ICrafting crafting, ITileEntityInterface te) {
        DataWriter dw = new DataWriter();

        dw.writeBoolean(true); //use container
        dw.writeByte(container.windowId);
        dw.writeBoolean(false); //all data
        te.writeAllData(dw);

        sendDataToPlayer(crafting, dw);
        dw.close();
    }



   /* public static void readBlockPacket(DataReader data) {
        int x = data.readData(DataBitHelper.WORLD_COORDINATE);
        int y = data.readData(DataBitHelper.WORLD_COORDINATE);
        int z = data.readData(DataBitHelper.WORLD_COORDINATE);

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
    private static DataWriter getBaseWriterForServerPacket() {
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

    @SideOnly(Side.CLIENT)
    public static DataWriter getWriterForServerPacket() {
        DataWriter dw = getBaseWriterForServerPacket();

        dw.writeBoolean(false); //no action

        return dw;
    }
    @SideOnly(Side.CLIENT)
    public static DataWriter getWriterForServerActionPacket() {
        DataWriter dw = getBaseWriterForServerPacket();

        dw.writeBoolean(true); //action

        return dw;
    }

    private static void createNonComponentPacket(DataWriter dw) {
        dw.writeBoolean(false); //this is a packet that has nothing to do with a specific FlowComponent
    }

    private static void createComponentPacket(DataWriter dw, FlowComponent component, ComponentMenu menu) {
        dw.writeBoolean(true); //this is a packet for a specific FlowComponent
        dw.writeComponentId(component.getManager(), component.getId());

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
        if (flowComponent.getParent() != null) {
            dw.writeBoolean(true);
            dw.writeComponentId(flowComponent.getManager(), flowComponent.getParent().getId());
        }else{
            dw.writeBoolean(false);
        }
        for (ComponentMenu menu : flowComponent.getMenus()) {
            menu.writeData(dw);
        }

        for (int i = 0; i < flowComponent.getConnectionSet().getConnections().length; i++) {
            Connection connection = flowComponent.getConnection(i);
            dw.writeBoolean(connection != null);
            if (connection != null) {
                dw.writeComponentId(flowComponent.getManager(), connection.getComponentId());
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

        dw.close();
    }

    public static void sendRemovalPacket(ContainerManager container, int idToRemove) {
        DataWriter dw = PacketHandler.getWriterForSpecificData(container);
        createNonComponentPacket(dw);
        dw.writeBoolean(false);
        dw.writeComponentId((TileEntityManager)container.getTileEntity(), idToRemove);
        sendDataToListeningClients(container, dw);

        dw.close();
    }

    public static void sendBlockPacket(IPacketBlock block, EntityPlayer player, int id) {
        if (block instanceof TileEntity) {
            TileEntity te = (TileEntity)block;
            BlockPos pos = te.getPos();
            boolean onServer = player == null || !player.worldObj.isRemote;

            DataWriter dw = new DataWriter();
            dw.writeBoolean(false); //no container
            dw.writeData(pos.getX(), DataBitHelper.WORLD_COORDINATE);
            dw.writeData(pos.getY(), DataBitHelper.WORLD_COORDINATE);
            dw.writeData(pos.getZ(), DataBitHelper.WORLD_COORDINATE);
            int length = block.infoBitLength(onServer);
            if (length != 0) {
                dw.writeData(id, length);
            }
            block.writeData(dw, player, onServer, id);

            if (!onServer) {
                dw.sendServerPacket();
            }else if(player != null) {
                dw.sendPlayerPacket((EntityPlayerMP)player);
            }else{
                dw.sendPlayerPackets(pos.getX() + 0.5, pos.getY(), pos.getZ(), BLOCK_UPDATE_RANGE, te.getWorld().provider.getDimension());
            }

            dw.close();
        }
    }
}
