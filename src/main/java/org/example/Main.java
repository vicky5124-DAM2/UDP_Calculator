package org.example;

import org.example.model.DataRx;
import org.example.model.DataTx;
import org.example.model.Operators;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

public class Main {
    public static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    // Create a logger used for the Server and the Client "tests".
    public static void initLogger() {
        log.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();

        Formatter formatter = new LogFormatter();
        handler.setFormatter(formatter);

        log.addHandler(handler);

        log.info("Started up!");
    }

    // This is the main exercise, to run it, you need to first run `Server.main`.
    // The exercise says this should be `runClient()` but it is best for it to be in main,
    // as `Client` is standalone.
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to the calculator :D");

        // Create a new UDP Client.
        Client client = new Client("localhost", (short) 5555);
        // And a Scanner for the user input.
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Ask for a number.
            // The exercise says the number should be 2 bytes, that means the data type should be short.
            System.out.print("Enter the first number (-32768 -> 32767): ");
            short first;
            while (true) {
                try {
                    first = scanner.nextShort();
                    break;
                } catch (InputMismatchException ignored) {
                    scanner.next();
                    System.out.print("Invalid number!\nEnter the first number (-32768 -> 32767): ");
                }
            }

            System.out.print("Enter the operator [+ - * /]: ");

            char rawOperator;
            while (true) {
                rawOperator = scanner.next().charAt(0);

                // Get the operator character, while the inputted character is not a supported operator.
                if (Operators.matchSymbol(rawOperator) == Operators.UNKNOWN) {
                    System.out.print("Unsupported operator!\nEnter the operator [+ - * /]: ");
                } else {
                    break;
                }
            }

            // When it is valid, get the enumeration variant of that operator.
            Operators operator = Operators.matchSymbol(rawOperator);

            // Get the second numerical value.
            System.out.print("Enter the second number (-32768 -> 32767): ");
            short second;
            while (true) {
                try {
                    second = scanner.nextShort();
                    break;
                } catch (InputMismatchException ignored) {
                    scanner.next();
                    System.out.print("Invalid number!\nEnter the second number (-32768 -> 32767): ");
                }
            }

            // Eat the newline from inputting the last number.
            scanner.nextLine();

            // I created a few layers of abstraction to make the client code easier to read and manage,
            // as it converts the bytes transmitted (TX) and received (RX) to/from the server into a DataClass.
            DataTx data = new DataTx(operator, first, second);

            // Send the data to the server...
            DataRx response = client.send(data);

            // And match the result.
            // More information about the Result variants in the defining file.
            switch (response.getResult()) {
                case OK:
                    System.out.printf("Calculated OK: %d | %X\n", response.getValue(), response.getValue());
                    break;
                case TIMEOUT:
                    System.out.println("Timed out!");
                    break;
                case DIVIDE_BY_ZERO:
                    System.out.println("Cannot divide by zero!");
                    break;
                case ERROR:
                    System.out.println("Unknown error.");
                    break;
                case STOPPED:
                    System.out.println("Server stopped.");
                    break;
            }

            // Ask if the user wants to do another operation.
            System.out.print("Do you want to do another operation? [Yn]: ");

            // Default to "yes" as the exercise says.
            char cont = 'y';
            try {
                cont = scanner.nextLine().charAt(0);
            } catch (Exception ignore) {}

            if (cont == 'n') {
                // Only ask about closing down the server if the user wants to stop doing operations.
                System.out.print("Do you also want to stop the server? [yN]: ");

                // Default to no, so the server doesn't need to be restarted all the time.
                char stopServer = 'n';
                try {
                    stopServer = scanner.nextLine().charAt(0);
                } catch (Exception ignore) {}

                if (stopServer == 'y') {
                    System.out.println("Stopping server...");
                    data = new DataTx(Operators.STOP, (short) 0, (short) 0);
                    client.send(data);
                    // Close the datagram socket. This stops all `receive()` operations that may be happening in other threads.
                    client.close();
                }

                System.out.println("Stopped.");
                break;
            }
        }
    }
}