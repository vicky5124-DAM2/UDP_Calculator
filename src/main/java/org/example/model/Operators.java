package org.example.model;

public enum Operators {
    // Operator to add 2 values.
    ADD((byte)0x00),
    // Operator to add 2 values.
    SUBTRACT((byte)0x01),
    // Operator to multiply 2 values.
    MULTIPLY((byte)0x02),
    // Operator to divide 2 values.
    DIVIDE((byte)0x03),
    // Operator to tell the server to stop and close.
    STOP((byte)0xff),
    // If generated from bytes, this is the default value.
    UNKNOWN((byte)0xfe);

    // This variable and constructor are needed for the enum variants to store a related value.
    private final byte opr;

    private Operators(byte i) {
        this.opr = i;
    }

    // Convert the enum variant to its related value.
    public byte getOpr() {
        return this.opr;
    }

    // Get an enum variant from a byte. Returns UNKNOWN if there's no match.
    public static Operators fromByte(byte opr) {
        for (Operators i : values()) {
            if (i.getOpr() == opr) {
                return i;
            }
        }
        return UNKNOWN;
    }

    // Get an enum variant from an operator character. Returns UNKNOWN if there's no match.
    public static Operators matchSymbol(char c) {
        switch (c) {
            case '+':
                return ADD;
            case '-':
                return SUBTRACT;
            case '*':
                return MULTIPLY;
            case '/':
                return DIVIDE;
            default:
                return UNKNOWN;
        }
    }
}