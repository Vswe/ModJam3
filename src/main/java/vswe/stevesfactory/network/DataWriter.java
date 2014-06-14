package vswe.stevesfactory.network;


import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.interfaces.ContainerBase;
import vswe.stevesfactory.settings.Settings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static vswe.stevesfactory.StevesFactoryManager.CHANNEL;
import static vswe.stevesfactory.StevesFactoryManager.packetHandler;

public class DataWriter {
    private int byteBuffer;
    private int bitCountBuffer;
    private OutputStream stream;

    DataWriter() {
        stream = new ByteArrayOutputStream();
    }

    DataWriter(OutputStream stream) {
        this.stream = stream;
    }

    public void writeByte(int data) {
        writeData(data, 8);
    }

    public void writeBoolean(boolean data) {
        writeData(data ? 1 : 0, DataBitHelper.BOOLEAN);
    }

    public void writeData(int data, DataBitHelper bitCount) {
        writeData(data, bitCount.getBitCount());
    }


    public void writeData(int data, int bitCount) {
        long mask = (long)Math.pow(2, bitCount) - 1;

        data &= mask;

        while (true) {
            if (bitCountBuffer + bitCount >= 8) {
                int bitsToAdd = 8 - bitCountBuffer;
                int addMask = (int)Math.pow(2, bitsToAdd) - 1;
                int addData = data & addMask;
                data >>>= bitsToAdd;
                addData <<= bitCountBuffer;
                byteBuffer |= addData;

                try {
                    stream.write(byteBuffer);
                }catch (IOException ignored) {}

                byteBuffer = 0;
                bitCount -= bitsToAdd;
                bitCountBuffer = 0;
            }else{
                byteBuffer |= data << bitCountBuffer;
                bitCountBuffer += bitCount;
                break;
            }
        }
    }

    private FMLProxyPacket createPacket() {
       writeFinalBits();
       ByteBuf buf = Unpooled.copiedBuffer(((ByteArrayOutputStream)stream).toByteArray());
       return new FMLProxyPacket(buf, CHANNEL);
    }

    void sendPlayerPackets(double x, double y, double z, double r, int dimension){
        packetHandler.sendToAllAround(createPacket(), new TargetPoint(dimension, x, y, z, r));
    }

    void sendPlayerPacket(EntityPlayerMP player){
        packetHandler.sendTo(createPacket(), player);
    }

    void sendServerPacket() {
        packetHandler.sendToServer(createPacket());
    }
    
    void sendPlayerPackets(ContainerBase container) {
        for (ICrafting crafting : container.getCrafters()) {
            if (crafting instanceof EntityPlayer) {
                EntityPlayerMP player = (EntityPlayerMP) crafting;
                packetHandler.sendTo(createPacket(), player);
            }
        }
    }

    public void writeString(String str, DataBitHelper bits) {
        if (str != null) {
            byte[] bytes = str.getBytes();
            writeData(bytes.length, bits);
            int l = str.length() & ((int)Math.pow(2, bits.getBitCount()) - 1);

            for (int i = 0; i < l; i++) {
                writeByte(bytes[i]);
            }
        }else{
            writeData(0, bits);
        }
    }

    public void writeNBT(NBTTagCompound nbtTagCompound){
        byte[] bytes = null;

        if (nbtTagCompound != null) {
            try {
                bytes = CompressedStreamTools.compress(nbtTagCompound);
            }catch (IOException ex) {
                bytes = null;
            }
        }

        writeBoolean(bytes != null);
        if (bytes != null) {
            writeData(bytes.length, DataBitHelper.NBT_LENGTH);
            for (byte b : bytes) {
                writeByte(b);
            }
        }
    }

    private boolean idWritten;
    private int idBits;
    public void writeComponentId(TileEntityManager manager, int id) {
        if (!idWritten) {
            if (Settings.isLimitless(manager) && manager.getFlowItems().size() > TileEntityManager.MAX_COMPONENT_AMOUNT)  {
                writeBoolean(true);
                int count = manager.getFlowItems().size();
                idBits = (int)(Math.log10(count + 1) / Math.log10(2)) + 1;
                writeData(idBits, DataBitHelper.BIT_COUNT);
            }else{
                writeBoolean(false);
                idBits = DataBitHelper.FLOW_CONTROL_COUNT.getBitCount();
            }

            idWritten = true;
        }

        writeData(id, idBits);
    }

    private boolean invWritten;
    private int invBits;
    public void writeInventoryId(TileEntityManager manager, int id) {
        if (!invWritten) {
            manager.updateFirst();
            if (Settings.isLimitless(manager) && manager.getConnectedInventories().size() > TileEntityManager.MAX_CONNECTED_INVENTORIES)  {
                writeBoolean(true);
                int count = manager.getConnectedInventories().size();
                invBits = (int)(Math.log10(count + 1) / Math.log10(2)) + 1;
                writeData(invBits, DataBitHelper.BIT_COUNT);
            }else{
                writeBoolean(false);
                invBits = DataBitHelper.MENU_INVENTORY_SELECTION.getBitCount();
            }

            invWritten = true;
        }

        writeData(id, invBits);
    }

    void writeFinalBits() {
        if (bitCountBuffer > 0) {
            try {
                stream.write(byteBuffer);
                bitCountBuffer = 0;
            } catch (IOException ignored) {}
        }
    }

    void close() {
        try {
            stream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
