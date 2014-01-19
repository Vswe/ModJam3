package vswe.stevesfactory.network;


import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;

public class DataReader {

    private ByteArrayInputStream stream;
    private int byteBuffer;
    private int bitCountBuffer;

    public DataReader(byte[] data) {
        stream = new ByteArrayInputStream(data);
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

                byteBuffer = stream.read();
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
                return CompressedStreamTools.decompress(bytes);
            }catch (IOException ex) {
                return null;
            }
        }else{
            return null;
        }
    }
}
