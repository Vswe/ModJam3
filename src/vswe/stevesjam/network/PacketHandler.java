package vswe.stevesjam.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import vswe.stevesjam.blocks.BlockCable;
import vswe.stevesjam.blocks.Blocks;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.components.ComponentMenu;
import vswe.stevesjam.components.ComponentType;
import vswe.stevesjam.components.FlowComponent;
import vswe.stevesjam.interfaces.ContainerJam;


public class PacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

        DataReader dr = new DataReader(packet.data);


            boolean useContainer = dr.readBoolean();
            int containerId = 0;
            if (useContainer) {
                containerId = dr.readByte();
            }
            Container container = ((EntityPlayer)player).openContainer;

            if (!useContainer || (container != null && container.windowId == containerId && container instanceof ContainerJam)) {
                if (player instanceof EntityPlayerMP) {
                    readComponentPacketFromDataReader(dr, ((ContainerJam) container).getJam());
                }else {
                    ClientPacketHeader header = getHeaderFromId(dr.readData(DataBitHelper.CLIENT_HEADER));
                    switch (header) {
                        case ALL:
                            readAllData(dr, ((ContainerJam) container).getJam());
                            break;
                        case SPECIFIC:
                            readComponentPacketFromDataReader(dr, ((ContainerJam) container).getJam());
                            break;
                        case BLOCK:
                            //readBlockPacket(dr);
                    }

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

    public static void sendDataToListeningClients(ContainerJam container, DataWriter dw) {
        dw.sendPlayerPackets(container);
        dw.close();
    }


    public static void sendAllData(Container container, ICrafting crafting, TileEntityJam jam) {
        DataWriter dw = new DataWriter();

        dw.writeBoolean(true);
        dw.writeByte(container.windowId);
        dw.writeData(ClientPacketHeader.ALL.id, DataBitHelper.CLIENT_HEADER);
        writeAllData(dw, jam);

        sendDataToPlayer(crafting, dw);
    }

    public static void sendBlockData(int x, int y, int z) {
        DataWriter dw = new DataWriter();

        dw.writeBoolean(false);
        dw.writeData(ClientPacketHeader.BLOCK.id, DataBitHelper.CLIENT_HEADER);

        dw.writeData(x, DataBitHelper.WORLD_COORDINATE);
        dw.writeData(y, DataBitHelper.WORLD_COORDINATE);
        dw.writeData(z, DataBitHelper.WORLD_COORDINATE);

        dw.sendGlobalPlayerPacket();
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

    private static DataWriter getWriterForSpecificData(Container container) {
        DataWriter dw = new DataWriter();

        dw.writeBoolean(true);
        dw.writeByte(container.windowId);
        dw.writeData(ClientPacketHeader.SPECIFIC.id, DataBitHelper.CLIENT_HEADER);

        return dw;
    }

    private static DataWriter getWriterForServerPacket() {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;

        if (container != null) {
            DataWriter dw = new DataWriter();

            dw.writeBoolean(true);
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

    public static void sendUpdateInventoryPacket(ContainerJam container) {
        DataWriter dw = PacketHandler.getWriterForSpecificData(container);
        createNonComponentPacket(dw);
        sendDataToListeningClients(container, dw);
    }

    public static DataWriter getWriterForServerComponentPacket(FlowComponent component, ComponentMenu menu) {
        DataWriter dw = PacketHandler.getWriterForServerPacket();
        createComponentPacket(dw, component, menu);
        return dw;
    }

    public static DataWriter getWriterForClientComponentPacket(ContainerJam container, FlowComponent component, ComponentMenu menu) {
        DataWriter dw = PacketHandler.getWriterForSpecificData(container);
        createComponentPacket(dw, component, menu);
        return dw;
    }

    public static void readComponentPacketFromDataReader(DataReader dr, TileEntityJam jam) {
        boolean isSpecificComponent = dr.readBoolean();
        if (isSpecificComponent) {

            IComponentNetworkReader nr = getNetworkReaderForComponentPacket(dr, jam);

            if (nr != null) {
                nr.readNetworkComponent(dr);
            }
        }else{
            jam.updateInventories();
        }
    }


    private static IComponentNetworkReader getNetworkReaderForComponentPacket(DataReader dr, TileEntityJam jam) {
        int componentId = dr.readData(DataBitHelper.FLOW_CONTROL_COUNT);
        if (componentId >= 0 && componentId < jam.getFlowItems().size()) {
            FlowComponent component = jam.getFlowItems().get(componentId);

            if (dr.readBoolean()) {
                int menuId = dr.readData(DataBitHelper.FLOW_CONTROL_MENU_COUNT);
                if (menuId >= 0 && menuId < component.getMenus().size()) {
                    return component.getMenus().get(menuId);
                }
            }else{
                 return component;
            }
        }

        return null;
    }



    private static void writeAllData(DataWriter dw, TileEntityJam jam){
       dw.writeData(jam.getFlowItems().size(), DataBitHelper.FLOW_CONTROL_COUNT);
        for (FlowComponent flowComponent : jam.getFlowItems()) {
            dw.writeData(flowComponent.getX(), DataBitHelper.FLOW_CONTROL_X);
            dw.writeData(flowComponent.getY(), DataBitHelper.FLOW_CONTROL_Y);
            dw.writeData(flowComponent.getType().getId(), DataBitHelper.FLOW_CONTROL_TYPE_ID);

            for (ComponentMenu menu : flowComponent.getMenus()) {
                menu.writeData(dw, jam);
            }
        }
    }

    private static void readAllData(DataReader dr, TileEntityJam jam){
        jam.updateInventories();
        int flowControlCount = dr.readData(DataBitHelper.FLOW_CONTROL_COUNT);
        jam.getFlowItems().clear();
        for (int i = 0; i < flowControlCount; i++) {
            int x = dr.readData(DataBitHelper.FLOW_CONTROL_X);
            int y = dr.readData(DataBitHelper.FLOW_CONTROL_Y);
            int id = dr.readData(DataBitHelper.FLOW_CONTROL_TYPE_ID);

            FlowComponent flowComponent = new FlowComponent(jam, x, y, ComponentType.getTypeFromId(id));

            for (ComponentMenu menu : flowComponent.getMenus()) {
                menu.readData(dr, jam);
            }

            jam.getFlowItems().add(flowComponent);
        }
    }

    private enum ClientPacketHeader {
        ALL(0),
        SPECIFIC(1),
        BLOCK(2);

        private int id;

        private ClientPacketHeader(int id) {
            this.id = id;
        }
    }

    private ClientPacketHeader getHeaderFromId(int id) {
        for (ClientPacketHeader header : ClientPacketHeader.values()) {
            if (id == header.id) {
                return header;
            }
        }
        return  null;
    }
}
