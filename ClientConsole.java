// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import client.*;
import common.*;

/**
 * This class constructs the UI for a chat client. It implements the chat
 * interface in order to activate the display() method. Warning: Some of the
 * code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version September 2020
 */
public class ClientConsole implements ChatIF {
    // Class variables *************************************************

    /**
     * The default port to connect on.
     */
    final public static int DEFAULT_PORT = 5555;

    // Instance variables **********************************************

    /**
     * The instance of the client that created this ConsoleChat.
     */
    ChatClient client;

    /**
     * Scanner to read from the console
     */
    Scanner fromConsole;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the ClientConsole UI.
     *
     * @param host The host to connect to.
     * @param port The port to connect on.
     */
    public ClientConsole(String loginID, String host, int port) {
        try {
            client = new ChatClient(loginID, host, port, this);

        } catch (IOException exception) {
            System.out.println("Cannot open connection. Awaiting command.");
        }

        // Create scanner object to read from console
        fromConsole = new Scanner(System.in);
    }

    // Instance methods ************************************************

    /**
     * This method waits for input from the console. Once it is received, it sends
     * it to the client's message handler.
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

                        // Tries to close normally
                        // If it can't, it force closes
                        try {
                            client.handleMessageFromClientUI(message);
                            client.quit();
                        } catch (NullPointerException e) {
                            System.exit(0);
                        }
                        break;

                    case "#logoff":

                        // Disconnects the user
                        try {
                            client.handleMessageFromClientUI(message);
                            client.closeConnection();
                        } catch (IOException e) {
                            display("The connection could not be closed.");
                        }
                        break;

                    case "#sethost":
                        try {

                            // Makes sure the client is not connected
                            try {
                                if (client.isConnected()) {
                                    display("Make sure to disconnect using the command '#logoff' before "
                                            + "\nchanging your host name.");
                                    break;
                                }
                            }

                            // If any problem occurs
                            catch (Exception e) {
                                // Do nothing
                            }

                            // Displays the second part of the message
                            // which is the future host name
                            display("Setting host name to '" + message.split(" ")[1] + "'");

                            // Sets the host name
                            client.setHost(message.split(" ")[1]);

                            // Shows success
                            display("Done! Your host name is now set to '" + message.split(" ")[1] + "'");
                        }

                        // If no host name was given
                        catch (ArrayIndexOutOfBoundsException e) {
                            display("Make sure to also include your desired host name at the end"
                                    + " of your command.");
                        }

                        // If it couldn't connect to the client
                        catch (NullPointerException en) {
                            display("Done! Your host name is now set to '" + message.split(" ")[1] + "'");
                        }

                        // Any other problem
                        catch (Exception ex) {
                            display("There was a problem with the host name you entered.");
                        }
                        break;

                    case "#setport":
                        try {

                            // Makes sure the client is not connected
                            try {
                                if (client.isConnected()) {
                                    display("Make sure to disconnect using the command '#logoff' before "
                                            + "\nchanging your port number.");
                                    break;
                                }
                            }

                            // If any problem occurs
                            catch (Exception e) {
                                // Do nothing
                            }

                            // Displays the second part of the message
                            // which is the future port number
                            display("Setting the port number to " + message.split(" ")[1]);

                            // Sets the port number
                            client.setPort(Integer.parseInt(message.split(" ")[1]));

                            // Shows success
                            display("Done! Your port is now set to " + message.split(" ")[1]);
                        }

                        // If no port number was given
                        catch (ArrayIndexOutOfBoundsException e) {
                            display("Make sure to also include your desired port at the end of your command.");
                        }

                        // If it couldn't connect to the client
                        catch (NullPointerException en) {
                            display("Done! Your port is now set to " + message.split(" ")[1]);
                        }

                        // Any other problem
                        catch (Exception ex) {
                            display("There was a problem with the port number you entered. Make sure its a number.");
                        }
                        break;

                    case "#login":
                        // If client is not connected
                        if (!client.isConnected()) {
                            try {

                                // If a user name was provided it changes the
                                // loginID so it can use the new user name provided
                                if (message.split(" ").length == 2) {
                                    client.setLoginID(message.split(" ")[1]);
                                }

                                // Otherwise, it connects with the user name
                                // previously provided
                                client.openConnection();
                            } catch (IOException e) {
                                display("The server could not be reached.");
                            }
                        }

                        // If client is already connected
                        else {
                            display("You are already logged in. You can't login again.");
                        }
                        break;

                    case "#gethost":
                        display("Your current hostname is '" + client.getHost() + "'.");
                        break;

                    case "#getport":
                        display("Your current port is " + client.getPort() + ".");
                        break;

                    default:
                        display("'" + message + "' doesn't match any commands.");
                    }
                }

                // If no command was given, proceed to sending the message to the client
                else {
                    try {
                        client.handleMessageFromClientUI(message);
                    } catch (NullPointerException e) {
                        display("Couldn't connect to server.\n"
                                + "Enter '#login <loginID>', or if you already logged in '#login'\n"
                                + "to try to connect to the server.");
                    }
                }
            }
        }

        // If it couldn't connect to the server/client
        catch (Exception ex) {
            display("Couldn't connect to server. Enter '#quit' to exit.");
            accept();
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

    // Class methods ***************************************************

    /**
     * This method is responsible for the creation of the Client UI.
     *
     * @param args[0] The loginID.
     * @param args[1] The host name.
     * @param args[2] The port number.
     */
    public static void main(String[] args) {
        String host = "";
        String loginID = "";
        int port;

        // For the loginID
        try {
            loginID = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ERROR - No login ID specified. Connection aborted.");
            System.exit(0);
        }

        // For the host name
        try {
            host = args[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            host = "localhost";
        }

        // For the port
        try {
            port = Integer.parseInt(args[2]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            port = DEFAULT_PORT;
        }

        // Creates the chat
        ClientConsole chat = new ClientConsole(loginID, host, port);
        chat.accept(); // Wait for console data
    }
}
//End of ConsoleChat class
