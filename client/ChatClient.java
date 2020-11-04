// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
    // Instance variables **********************************************

    /**
     * The interface type variable. It allows the implementation of the display
     * method in the client.
     */
    ChatIF clientUI;

    /**
     * The string that stores the loginID of the users
     */
    String loginID;

    // Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param host     The server to connect to.
     * @param port     The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String loginID, String host, int port, ChatIF clientUI) throws IOException {
        super(host, port); // Call the superclass constructor
        this.clientUI = clientUI;
        this.loginID = loginID;
        openConnection();
    }

    // Instance methods ************************************************

    /**
     * This method sets the loginID
     */
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        clientUI.display(msg.toString());
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {

        // Tries to send the message to the server
        try {
            sendToServer(message);
        } catch (IOException e) {
            clientUI.display("Could not send message to server. Terminating client.");
            quit();
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {
        }
        System.exit(0);
    }

    /**
     * This method displays a message telling that the connection was closed
     */
    @Override
    public void connectionClosed() {
        clientUI.display("Connection was closed.");
    }

    /**
     * This method displays a message telling that there was a problem with the
     * connection
     */
    @Override
    public void connectionException(Exception exception) {
        clientUI.display("Abnormal termination of connection.");
    }

    /**
     * This method is used to login the user each time he connects to the server
     */
    @Override
    public void connectionEstablished() {
        handleMessageFromClientUI("#login " + this.loginID);
    }

}
//End of ChatClient class
