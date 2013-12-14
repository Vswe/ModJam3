package vswe.stevesjam.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import vswe.stevesjam.StevesJam;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.interfaces.ContainerJam;

import java.io.*;


public class PacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

        DataReader dr = new DataReader(packet.data);


        int containerId = dr.readByte();
        Container container = ((EntityPlayer)player).openContainer;

        if (container != null && container.windowId == containerId && container instanceof ContainerJam) {
            readData(dr, ((ContainerJam)container).getJam());
        }

        dr.close();
    }
    public static void sendAllData(Container container, ICrafting crafting, TileEntityJam jam) {
        if (crafting instanceof Player) {
            Player player = (Player)crafting;
            DataWriter dw = new DataWriter();

            dw.writeByte(container.windowId);
            writeData(dw, jam);
            dw.sendPacket(player);
            dw.close();
        }
    }



    private static void writeData(DataWriter dw, TileEntityJam jam){
        dw.writeData(0b11, 2);
        dw.writeData(0b10101, 5);
        dw.writeData(30000, 15);
        dw.writeData(0b0, 1);
        dw.writeData(0b1, 1);
        dw.writeData(0b10101, 5);
    }

    private static void readData(DataReader dr, TileEntityJam jam){
        dr.readData(2);
        dr.readData(5);
        dr.readData(15);
        dr.readData(1);
        dr.readData(1);
        dr.readData(5);
    }
}
