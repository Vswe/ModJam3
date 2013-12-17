package vswe.stevesfactory.network;


import java.io.ByteArrayInputStream;
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
}
