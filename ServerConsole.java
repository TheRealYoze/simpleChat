import java.util.Scanner;

import common.ChatIF;

public class ServerConsole implements ChatIF {

    /**
     * The instance of the server of that ServerConsole.
     */
    EchoServer server;

    /**
     * Scanner to read from the console
     */
    Scanner fromConsole;

    /**
     * Constructor. Only takes the EchoServer
     * 
     * @param server
     */
    public ServerConsole(EchoServer server) {
        this.server = server;

        // To initialize the scanner
        this.fromConsole = new Scanner(System.in);
    }

    /**
     * This method waits for input from the console. Once it is received, it
     * displays it to the server's console.
     */
    public void accept() {
        try {
            String message;

            while (true) {
                message = fromConsole.nextLine();

                // Prevents the program from crashing
                // by making sure the message is not null
                if (message.equals("")) {
                    accept();
                }

                // First see if there is a command
                if (message.charAt(0) == '#') {

                    // Looks a the fist word of the message
                    switch (message.split(" ")[0]) {

                    case "#quit":
                        System.exit(0);
                        break;

                    case "#stop":

                        // Stop listening for new connections
                        this.server.stopListening();
                        break;

                    case "#close":

                        // Stop listening for new connections
                        // and disconnects every user
                        this.server.sendToAllClients("WARNING - The server has stopped listening for connections\n"
                                + "SERVER SHUTTING DOWN! DISCONNECTING!");
                        this.server.close();
                        break;

                    case "#setport":
                        try {

                            // If server not listening and nobody is connected to it
                            if (!this.server.isListening() && this.server.getNumberOfClients() == 0) {

                                // Displays the second part of the message
                                // which is the future port number
                                display("Setting port number to " + message.split(" ")[1]);

                                // Sets the port number
                                this.server.setPort(Integer.parseInt(message.split(" ")[1]));

                                // Shows success
                                display("Done! Your port number is now set to " + this.server.getPort());
                            }

                            // If the server is still listening for new connections
                            // and there are some clients connected to it
                            else if (this.server.isListening() && this.server.getNumberOfClients() != 0) {
                                display("Some clients are still connected to the server and you are still listening for connections."
                                        + "\nMake sure to stop listening for new connections and"
                                        + " disconnect all of your clients using the command '#close'.");
                            }

                            // If the server is still listening for new connections
                            else if (this.server.isListening()) {
                                display("The server is still listening for connections.\nMake sure to stop listening "
                                        + "for new connections using the command '#stop'.");
                            }

                            // If there are some clients connected to the server
                            else if (this.server.getNumberOfClients() != 0) {
                                display("Some clients are still connected to the server.\nMake sure to disconnect "
                                        + "them all using the command '#close'");
                            }
                        }

                        // If no port number was added
                        catch (ArrayIndexOutOfBoundsException e) {
                            display("Make sure to also include your desired port at the end of your command.");

                        }

                        // Any other errors
                        catch (Exception ex) {
                            display("There was a problem with the port number you entered. Make sure its a number.");
                        }
                        break;

                    case "#start":

                        // Makes the server listen for new connections
                        if (!this.server.isListening()) {
                            this.server.listen();
                        }

                        // If the server is already listening for new connections
                        else {
                            display("Server already listening for new connections on port " + this.server.getPort());
                        }
                        break;

                    case "#getport":
                        display("Your current port is " + this.server.getPort() + ".");
                        break;

                    default:
                        display("'" + message + "' doesn't match any commands.");
                    }
                }

                // If there is no command, send to all clients
                else {
                    server.sendToAllClients("SERVER MESSAGE > " + message);
                }
            }
        }

        // Any other error
        catch (Exception ex) {
            display("Unexpected error while reading from console!");
        }
    }

    /**
     * This method overrides the method in the ChatIF interface. It displays a
     * message onto the screen.
     *
     * @param message The string to be displayed.
     */
    public void display(String message) {
        System.out.println(message);
    }
}
