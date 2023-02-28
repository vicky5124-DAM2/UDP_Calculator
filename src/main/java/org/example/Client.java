package org.example;

import org.example.model.DataRx;
import org.example.model.DataTx;
import org.example.model.Operators;
import org.example.model.Result;

import java.io.IOException;
import java.net.*;

public class Client {
    // The network port the server is listening to.
    // Ports are 2 bytes, so the data type is short.
    private final short port;
    // The network address the server is on.
    private final InetAddress address;
    // The socket used to transmit and receive data.
    private final DatagramSocket socket = new DatagramSocket();

    public Client(String address, short port) throws SocketException, UnknownHostException {
        this.address = InetAddress.getByName(address);
        this.port = port;

        // Default timeout of 60 seconds.
        // Can be changed via `this.setTimeout()`.
        this.socket.setSoTimeout(60_000);
    }

    // Sends data to the server.
    public DataRx send(DataTx message) throws IOException {
        // Get the bytes the server wants from the abstracted data.
        byte[] msg = message.toBytes();
        // And create a packet with the data.
        DatagramPacket packet = new DatagramPacket(msg, msg.length, this.address, this.port);

        // Send the packet to the server.
        socket.send(packet);

        // Create a buffer where we'll receive the response packet.
        byte[] buffer = new byte[16];
        packet = new DatagramPacket(buffer, buffer.length);

        // Try to receive the packet.
        try {
            socket.receive(packet);
            // If the client received valid data, convert it to the abstracted dataclass and return it to the user.
            return new DataRx(packet.getData());
        } catch (SocketTimeoutException e) {
            // If the client times out, return to the user a TIMEOUT error.
            return new DataRx(Result.TIMEOUT);
        }
    }

    // Sets the request response timeout.
    public void setTimeout(int timeout) throws SocketException {
        this.socket.setSoTimeout(timeout);
    }

    // Closes this datagram socket.
    public void close() {
        this.socket.close();
    }

    // Tests!
    public static void main(String[] args) throws IOException {
        Main.initLogger();

        Client client = new Client("localhost", (short) 5555);
        // Set a lot timeout so the timeout test can run in a reasonable time.
        client.setTimeout(500);

        DataTx[] data = {
            new DataTx(Operators.ADD, (short) 5, (short) 5),
            new DataTx(Operators.SUBTRACT, (short) 5, (short) 5),
            new DataTx(Operators.MULTIPLY, (short) 5, (short) 5),
            new DataTx(Operators.DIVIDE, (short) 5, (short) 5),

            new DataTx(Operators.ADD, (short) 0xff, (short) 0xff),
            new DataTx(Operators.SUBTRACT, (short) 0xff, (short) 0xff),
            new DataTx(Operators.MULTIPLY, (short) 0xff, (short) 0xff),
            new DataTx(Operators.DIVIDE, (short) 0xff, (short) 0xff),

            new DataTx(Operators.ADD, Short.MAX_VALUE, Short.MAX_VALUE),
            new DataTx(Operators.SUBTRACT, Short.MAX_VALUE, Short.MAX_VALUE),
            new DataTx(Operators.MULTIPLY, Short.MAX_VALUE, Short.MAX_VALUE),
            new DataTx(Operators.DIVIDE, Short.MAX_VALUE, Short.MAX_VALUE),

            // Divide by zero error Test.
            new DataTx(Operators.DIVIDE, (short) 1, (short) 0),

            // Stop Test.
            DataTx.stopOperator(),

            // Timeout Test.
            new DataTx(Operators.ADD, (short) 0, (short) 0),
        };

        for (DataTx i : data) {
            DataRx response = client.send(i);

            switch (response.getResult()) {
                case OK:
                    Main.log.info(String.format("Calculated OK: %d | %X", response.getValue(), response.getValue()));
                    break;
                case TIMEOUT:
                    Main.log.severe("Timed out!");
                    break;
                case DIVIDE_BY_ZERO:
                    Main.log.severe("Cannot divide by zero!");
                    break;
                case ERROR:
                    Main.log.severe("Unknown error.");
                    break;
                case STOPPED:
                    Main.log.warning("Server stopped.");
                    break;
            }
        }
    }
}
