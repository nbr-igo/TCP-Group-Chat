import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;


public class NetworkServer {
    
    private ServerSocket serverSocket; // Stores main server socket that listens for connections 

    public NetworkServer(ServerSocket serverSocket) { // Constructor that receives a serverSocket object
        this.serverSocket = serverSocket; // Assigns the passed-in socket to the class variable 
    }

    public void startServer(){ // Method used to start server
        try{
            while (!serverSocket.isClosed()){ // Continue running as long as server socket is open 
                Socket socket =  serverSocket.accept(); // Wait (block) until a client connects, then return a Socket object
                System.out.println("A new client has connected!"); // Print confirmation that a client connected 
                ClientHandler clientHandler = new ClientHandler(socket); // Create a new ClientHandler Object  to manage this client 

                Thread thread = new Thread(clientHandler); // Create a thread to run clientHandler
                thread.start(); // Call clientHandler.run
            }
        } catch (IOException e) {

        }
    }

    public void closeServerSocket() { // Mehtod used to safey shut down the server
        try {
            if (serverSocket != null) { // Check if the serverSocket is not null
                serverSocket.close(); // Close the server socket to stop accepting connections 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException { // Main method
        ServerSocket serverSocket = new ServerSocket(1234); // Create a server socket that listens on port 1234
        NetworkServer server = new NetworkServer(serverSocket); // Create a NetworkServer object using the created ServerSocket 
        server.startServer(); // Start the server
  
    }
}
