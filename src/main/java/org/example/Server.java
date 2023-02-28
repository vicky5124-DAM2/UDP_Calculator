package org.example;

import org.example.model.DataRx;
import org.example.model.DataTx;
import org.example.model.Result;

import java.io.IOException;
import java.net.*;

public class Server {
    // The socket the server will use to receive and transmit data.
    private final DatagramSocket socket;

    // Create a server that listens to a custom port, on the address 0.0.0.0
    public Server(short port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    // Initialize the server to make it start listening to requests.
    public void init() throws IOException {
        // Create a buffer where the server will receive data to.
        byte[] buffer = new byte[16];

        while (true) {
            // Create a new packet where to receive the data the client will send.
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            // And receive the data to that packet.
            socket.receive(packet);

            // Get the address and port of the client, to know who sent it.
            // This will be used later to send a response to the client.
            InetAddress clientAddr = packet.getAddress();
            int clientPort = packet.getPort();

            Main.log.fine(String.format("Received packet from %s:%d! Processing...", clientAddr.toString(), clientPort));

            // From the data received, do the calculation.
            byte[] data = this.processData(packet.getData());

            // Create a packet with the result...
            packet = new DatagramPacket(data, data.length, clientAddr, clientPort);

            // and send it to the client that requested it.
            socket.send(packet);

            // If the client requested for the server to be stopped...
            if (data[0] == Result.STOPPED.getOpr()) {
                Main.log.info("Stopping server...");
                // close the socket...
                this.socket.close();
                // and stop the listening loop.
                break;
            }
        }
    }

    private byte[] processData(byte[] data) {
        // We can also use the abstraction layer on the server to make the operations easier to read and write!
        DataTx parsedData = new DataTx(data);
        DataRx resultData;

        switch (parsedData.getOpr()) {
            case ADD:
                resultData = new DataRx(parsedData.getFirstValue() + parsedData.getSecondValue());
                break;
            case SUBTRACT:
                resultData = new DataRx(parsedData.getFirstValue() - parsedData.getSecondValue());
                break;
            case MULTIPLY:
                resultData = new DataRx(parsedData.getFirstValue() * parsedData.getSecondValue());
                break;
            case DIVIDE:
                if (parsedData.getSecondValue() == 0) {
                    resultData = new DataRx(Result.DIVIDE_BY_ZERO);
                } else {
                    resultData = new DataRx(parsedData.getFirstValue() / parsedData.getSecondValue());
                }
                break;
            case STOP:
                resultData = new DataRx(Result.STOPPED);
                break;
            default:
                resultData = new DataRx(Result.ERROR);
                break;
        }

        return resultData.toBytes();
    }

    // Start the server.
    public static void main(String[] args) throws IOException {
        Main.initLogger();
        Server server = new Server((short) 5555);
        server.init();
    }
}
