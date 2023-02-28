package org.example.model;

// The result given from the server.
public enum Result {
    // If the operation went successfully.
    OK((byte)0x00),
    // If the client timed out on getting a response.
    TIMEOUT((byte)0x0F),
    // Returned by the server when it gets the STOP operation.
    STOPPED((byte)0xF1),
    // If the division has a divisor of 0
    DIVIDE_BY_ZERO((byte)0xF0),
    // Generic/Unknown error.
    ERROR((byte)0xFF);

    // This variable and constructor are needed for the enum variants to store a related value.
    private final byte opr;

    private Result(byte i) {
        this.opr = i;
    }

    // Convert the enum variant to its related value.
    public byte getOpr() {
        return this.opr;
    }

    // Get an enum variant from a byte. Returns ERROR if there's no match.
    public static Result fromByte(byte opr) {
        for (Result i : values()) {
            if (i.getOpr() == opr) {
                return i;
            }
        }
        return ERROR;
    }
}
