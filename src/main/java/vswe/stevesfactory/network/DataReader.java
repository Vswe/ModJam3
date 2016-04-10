package vswe.stevesfactory.network;


import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import vswe.stevesfactory.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataReader {

    private InputStream stream;
    private int byteBuffer;
    private int bitCountBuffer;

    DataReader(byte[] data) {
        stream = new ByteArrayInputStream(data);
    }

    DataReader(InputStream stream) {
        this.stream = stream;
    }

    public int readByte() {
        return readData(8);
    }

    public boolean readBoolean() {
        return readData(DataBitHelper.BOOLEAN) != 0;
    }

    public int readData(DataBitHelper bitCount) {
        return readData(bitCount.getBitCount());
    }

    public int readData(int bitCount) {
        int data = 0;
        int readBits = 0;

        while (true) {
            int bitsLeft = bitCount - readBits;
            if (bitCountBuffer >= bitsLeft) {
                data |= (byteBuffer & ((int)Math.pow(2, bitsLeft) - 1)) << readBits;
                byteBuffer >>>= bitsLeft;
                bitCountBuffer -= bitsLeft;
                readBits += bitsLeft;
                break;
            }else{
                data |= byteBuffer << readBits;
                readBits += bitCountBuffer;

                try {
                    byteBuffer = stream.read();
                }catch (IOException ignored) {
                    byteBuffer = 0;
                }
                bitCountBuffer = 8;
            }
        }



        return data;
    }

    public void close() {
        try {
            stream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readString(DataBitHelper bits) {
        int length = readData(bits);
        if (length == 0) {
            return null;
        }else{
            byte[] bytes = new byte[length];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte)readByte();
            }
            return new String(bytes);
        }
    }

    public NBTTagCompound readNBT(){
        if (readBoolean()) {
            byte[] bytes = new byte[readData(DataBitHelper.NBT_LENGTH)];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte)readByte();
            }

            try {
                return Utils.readCompressed(bytes, new NBTSizeTracker(2097152L));
            }catch (IOException ex) {
                return null;
            }
        }else{
            return null;
        }
    }

    private boolean idRead;
    private int idBits;
    public int readComponentId() {
        if (!idRead) {
            if (readBoolean()) {
                idBits = readData(DataBitHelper.BIT_COUNT);
            }else{
                idBits = DataBitHelper.FLOW_CONTROL_COUNT.getBitCount();
            }

            idRead = true;
        }

        return readData(idBits);
    }

    private boolean invRead;
    private int invBits;
    public int readInventoryId() {
        if (!invRead) {
            if (readBoolean()) {
                invBits = readData(DataBitHelper.BIT_COUNT);
            }else{
                invBits = DataBitHelper.MENU_INVENTORY_SELECTION.getBitCount();
            }

            invRead = true;
        }

        return readData(invBits);
    }

}
