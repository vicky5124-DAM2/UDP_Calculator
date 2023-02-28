package org.example.model;

import java.nio.ByteBuffer;
import java.util.Arrays;

// The abstracted DataClass of the data transmitted by the client, received by the server.
public class DataTx {
    // The operator the sever should use for the calculation.
    private final Operators opr;
    // The first value used for the calculation.
    private final short firstValue;
    // The second value used for the calculation.
    private final short secondValue;

    // We can generate this DataClass from the actual values.
    public DataTx(Operators opr, short firstValue, Short secondValue) {
        this.opr = opr;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    // Or from the bytes the server wants to receive.
    public DataTx(byte[] data) {
        this.opr = Operators.fromByte(data[0]);
        this.firstValue = ByteBuffer.wrap(Arrays.copyOfRange(data, 1, 3)).getShort();
        this.secondValue = ByteBuffer.wrap(Arrays.copyOfRange(data, 3, 5)).getShort();
    }

    // Custom constructor for STOP, as it doesn't require values.
    public static DataTx stopOperator() {
        return new DataTx(Operators.STOP, (short) 0, (short) 0);
    }

    public Operators getOpr() {
        return this.opr;
    }

    public short getFirstValue() {
        return this.firstValue;
    }

    public short getSecondValue() {
        return this.secondValue;
    }

    // Convert the abstraction layer to the bytes the server wants to receive.
    public byte[] toBytes() {
        return new byte[] {
            // The operator has a 1:1 conversion to bytes.
            this.opr.getOpr(),
            // But the 2 `short` typed values need to be converted to the bytes.
            // We do this with Big Endian, as that's what ByteBuffer needs.
            (byte)((this.firstValue >> 8) & 0xff),
            (byte)((this.firstValue >> 0) & 0xff),
            (byte)((this.secondValue >> 8) & 0xff),
            (byte)((this.secondValue >> 0) & 0xff),
        };
    }

}
