import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.io.IOException;

public class ClientHandler implements Runnable { // Implements Runnable so each client can run in its own thread

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // Shared list that holds every ClientHandler connected to server
    // These are instance variables that store information for this specific client:
    private Socket socket; // Socket for this specific client 
    private BufferedReader bufferedReader; // Reads messages sent FROM this client 
    private BufferedWriter bufferedWriter; // Sends messages TO this client 
    private String clientUsername; // Stores this client's username
    private String groupName; // Tracks which group (chat room) the client is currently in 

    public ClientHandler(Socket socket) { // Constructor runs when a new client connects
        try {
            this.socket = socket; // Store the socket connection
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Create writer linked to client's output stream
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Create reader linked to client's input stream
            this.clientUsername = bufferedReader.readLine(); // First message client sends is their username
            this.groupName = "GLOBAL"; // Default group when client connects
            clientHandlers.add(this); // Add this clientHandler to shared list
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!" ); // Let everyone know a client joined
            System.out.println("SERVER: " + clientUsername + " has entered " + groupName + " Chat!!" ); // Log on server
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter); // If anything fails during setup, close resources
        }
    }

    @Override
    public void run() { // This mehod runs when thread starts
        String messageFromClient; // Stores incoming messages

        while (!socket.isClosed()){ // Keep reading while client is connected
            try {
                messageFromClient = bufferedReader.readLine(); // Read message from client 

                if (messageFromClient == null || messageFromClient.equals("/exit") ) { // If client disconnects or types exit command
                    closeEverything(socket, bufferedReader, bufferedWriter); // Close resources
                    break;
                }

                if (messageFromClient.startsWith("/join ")) { // If Client wants to join another chat room
                    String newGroup = messageFromClient.substring(6).trim(); // Extract new group name from command 
                    System.out.println("SERVER: " + clientUsername + " switched from " + groupName + " to " + newGroup); // Log gorup switch
                    broadcastMessage("SERVER: " + clientUsername + " has left " + groupName + " Chat"); // Inform current group user left
                    this.groupName = newGroup; // Update group name
                    broadcastMessage("SERVER: " + clientUsername + " has joined " + groupName + " Chat");// Inform new group user joined
                    continue; // Skip remainin logic and continue loop
                }

                if (messageFromClient.equals("/leave")) { // If Client wants to go back to origianl gorupchat "Global"
                    System.out.println("SERVER: "  + clientUsername + " left " + groupName + " and returned to GLOBAL"); // Log event
                    broadcastMessage("SERVER: " + clientUsername + " has left " + groupName + " Chat"); // Inform current group user left
                    this.groupName = "GLOBAL"; // Update group name
                    broadcastMessage("SERVER: " + clientUsername + " has left " + groupName + " Chat"); // Notify GLOBAL group
                    continue; // Skip remainin logic and continue loop
                }


                    broadcastMessage( clientUsername + ": " + messageFromClient); // Normal message send to others in same group
                    System.out.println("SERVER: [" + groupName + "] " + clientUsername + ": " + messageFromClient); // Log message on server console
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter); // If reading fails, close everything
                    break;
            }
        } 
    }

    public void broadcastMessage(String messageToSend) { // Sends message to all clients in the same group (except sender)
        for(ClientHandler clientHandler : clientHandlers){ // Loop through all connected clients
            try {
                if (!clientHandler.clientUsername.equals(clientUsername) && clientHandler.groupName.equals(this.groupName)){ // Do not send to self AND only send to same group
                    clientHandler.bufferedWriter.write(messageToSend); // Write message to that client
                    clientHandler.bufferedWriter.newLine(); // Mark end of message
                    clientHandler.bufferedWriter.flush(); // Force immediate sending
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter); // If sending fails, close this connection
            }
        }
    }

    public void removeClientHandler() { // Removes this client from shared list
        clientHandlers.remove(this); // Remove this instance 
        broadcastMessage("SERVER: " + clientUsername + " has gone offline!"); // Inform group user went offline
        System.out.println("SERVER: " + clientUsername + " has disconnected from " + groupName + " and has gone offline!!"); // Log disconnection

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) { // Mehtod closes all open resources safely
        removeClientHandler(); // Remove client first
        try {
            if (bufferedReader != null) { // If bufferedReader exist close 
                bufferedReader.close();
            }
            if (bufferedWriter != null) { // If bufferedWriter exist close 
                bufferedWriter.close();
            }
            if (socket != null) { // If socket exist close 
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Prints the errors and moves on
        }
    }
}
