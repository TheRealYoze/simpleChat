// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
    // Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {
        super(port);
    }

    // Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {

        // Displays the incoming message
        System.out.println("Message received: " + msg + " from " + client.getInfo("loginID") + ".");

        // Analyzes if a login was requested
        switch (msg.toString().split(" ")[0]) {

        case "#login":

            // Analyzes if it is not the first time the loginID was used
            if (client.getInfo("loginID") != null) {
                try {

                    // If it wasn't the first time, it displays an error message
                    // and terminates the connection with the user
                    client.sendToClient(
                            "The '#login' command should only be used during the login.\nTerminating connection.");
                    client.close();
                } catch (IOException e) {
                    System.out.println("Couln't kickout the client...");
                }
            }

            // If it is the first time the command is used
            else {
                try {

                    // Sets the second part of the message to be the
                    // user's loginID
                    client.setInfo("loginID", msg.toString().split(" ")[1]);

                    // Displays the login message to the server console
                    System.out.println(client.getInfo("loginID") + " has logged on.");

                    // Displays the login message to all connected clients
                    this.sendToAllClients("> " + client.getInfo("loginID") + " has logged on.");
                }

                // If no loginID was provided
                catch (ArrayIndexOutOfBoundsException e) {
                    try {
                        client.sendToClient("No login ID was provided. Terminating connection.");
                        client.close();
                    }

                    // Any other error
                    catch (IOException ex) {
                        System.out.println("An unknown error occured.");
                    }
                }
            }
            break;

        case "#logoff":

            // Sends a message to all clients showing who disconnected
            this.sendToAllClients("> " + client.getInfo("loginID") + " has disconnected.");

            // Displays a message on the server console showing who disconnected
            this.clientDisconnected(client);
            break;

        case "#quit":

            // Sends a message to all clients showing who quit
            this.sendToAllClients("> " + client.getInfo("loginID") + " has disconnected.");

            // Displays a message on the server console showing who quit
            this.clientDisconnected(client);
            break;

        default:
            this.sendToAllClients("> " + client.getInfo("loginID") + ": " + msg);
        }
    }

    /**
     * This method overrides the one in the superclass. Called when the server
     * starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass. Called when the server stops
     * listening for connections.
     */
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    /**
     * This method displays a message on the server console to show that a new
     * client was connected
     */
    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("A new client is attempting to connect to the server.");

    }

    /**
     * This method displays a message on the server console to show that a client
     * was disconnected
     */
    @Override
    synchronized protected void clientDisconnected(ConnectionToClient client) {
        System.out.println(client.getInfo("loginID") + " has disconnected.");
    }

    /**
     * This method closes the connection with a client if there was an exception
     */
    @Override
    synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
        try {
            client.close();
        } catch (IOException e) {
            System.out.println("An error occured with a client.");
        }
    }

    // Class methods ***************************************************

    /**
     * This method is responsible for the creation of the server instance (there is
     * no UI in this phase).
     *
     * @param args[0] The port number to listen on. Defaults to 5555 if no argument
     *                is entered.
     */
    public static void main(String[] args) {
        int port = 0; // Port to listen on

        try {
            port = Integer.parseInt(args[0]); // Get port from command line
        } catch (Throwable t) {
            port = DEFAULT_PORT; // Set port to 5555
        }

        // Creates the server
        EchoServer sv = new EchoServer(port);

        try {
            sv.listen(); // Start listening for connections
            ServerConsole serverConsole = new ServerConsole(sv); // Creates the server console
            serverConsole.accept();
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }
}
//End of EchoServer class
