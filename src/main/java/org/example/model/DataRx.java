package org.example.model;

import java.nio.ByteBuffer;
import java.util.Arrays;

// The abstracted DataClass of the data received by the client, send by the server.
public class DataRx {
    // The result containing the possible error (...) or a successful execution (OK).
    private final Result result;

    // The resulting value.
    // It is always 0 unless result is `OK`.
    // The data type is int as it is the resulting data type when adding 2 shorts.
    private final int value;
    //private final long value;

    // We can generate this DataClass from a result with the default value (used for anything that's not OK)
    public DataRx(Result result) {
        this.result = result;
        this.value = 0;
    }

    // We can also generate this DataClass from the resulting value, setting the result to OK.
    public DataRx(int value) {
    //public DataRx(long value) {
        this.result = Result.OK;
        this.value = value;
    }

    // Or we can generate this DataClass from the bytes the server returns.
    public DataRx(byte[] data) {
        this.result = Result.fromByte(data[0]);
        this.value = ByteBuffer.wrap(Arrays.copyOfRange(data, 1, 5)).getInt();
    }

    public Result getResult() {
        return this.result;
    }

    public int getValue() {
    //public long getValue() {
        return this.value;
    }

    // Convert the abstraction layer to the bytes the server transmits.
    public byte[] toBytes() {
        return new byte[]{
            // The result has a 1:1 conversion to bytes.
            this.result.getOpr(),
            // But the resulting integer need to be converted to the bytes its composed bytes.
            // We do this with Big Endian, as that's what ByteBuffer needs.

            // If this.value is `long`, uncomment this.
            // The teacher server uses a long for the result rather than an integer.
            //(byte) ((this.value >> 64) & 0xff),
            //(byte) ((this.value >> 48) & 0xff),
            //(byte) ((this.value >> 40) & 0xff),
            //(byte) ((this.value >> 32) & 0xff),

            (byte) ((this.value >> 24) & 0xff),
            (byte) ((this.value >> 16) & 0xff),
            (byte) ((this.value >> 8) & 0xff),
            (byte) ((this.value >> 0) & 0xff),
        };
    }
}
