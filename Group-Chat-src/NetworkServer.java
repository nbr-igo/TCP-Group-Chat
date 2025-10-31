//Starts the server and accepts new clients
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;


public class NetworkServer {
    private ServerSocket serverSocket;//private variable to store the server’s listening socket 

    public NetworkServer(ServerSocket serverSocket) {//Constructor that receives socket and saves it to the class
        this.serverSocket = serverSocket;//Assigns the passed-in socket to the class variable so methods can use it later
    }

    public void startServer(){
        try{
            while (!serverSocket.isClosed()){//while server is not closed 
                Socket socket =  serverSocket.accept();//Accept clients and returns a new Socket object 
                System.out.println("A new client has connected!");//Inform that a client connected
                ClientHandler clientHandler = new ClientHandler(socket);//Create a new ClientHandler instance/Object and passes socket value to clientHandler class

                Thread thread = new Thread(clientHandler);//Create a thread for the clientHandler instance
                thread.start();//call clientHandler.run
            }
        } catch (IOException e) {

        }
    }

    public void closeServerSocket() {//checks if the serverSocket exists, and if so, close it
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);//create a server socket that listens on port 1234
        NetworkServer server = new NetworkServer(serverSocket);//Networkserver, given ServerSocket object resposible for listening for client connections
        server.startServer();//start the server
  
    }
}
