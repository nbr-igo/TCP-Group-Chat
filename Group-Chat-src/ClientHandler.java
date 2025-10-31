import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.io.IOException;

public class ClientHandler implements Runnable {//Runnable meaning can be ren by its own thread

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();//shared list that holds every ClientHandler connected to server
    //These are instance variables that store information for this specific client:
    private Socket socket;//socket → The network connection to that client.
    private BufferedReader bufferedReader;//bufferedReader → Reads messages incoming
    private BufferedWriter bufferedWriter;//bufferedWriter → Sends messages to the client.
    private String clientUsername;//clientUsername → Stores their username.
    private String groupName;//groupName → Keeps track of which chat room (group) they are in.

    public ClientHandler(Socket socket) {//ClientHandler method with socket input
        try {
            this.socket = socket;//store socket passed from server
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//creates output stream to write data over socket
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//creates input stream to read data over socket
            this.clientUsername = bufferedReader.readLine();//reads username the client sends first
            this.groupName = "GLOBAL";//initial group
            clientHandlers.add(this);//add this clientHandler to the shared list
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!" );//Let everyone know a client joined
            System.out.println("SERVER: " + clientUsername + " has entered " + groupName + " Chat!!" );//log on the server
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);//anything goes wrong, close socket and I/O Streams
        }
    }

    @Override
    public void run() {//Is constantly reading messages from the client // this is what the thread actually does  
        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();

                if (messageFromClient == null || messageFromClient.equals("/exit") ) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }

                if (messageFromClient.startsWith("/join ")) {// Client switches chat rooms
                    String newGroup = messageFromClient.substring(6).trim();
                    System.out.println("SERVER: " + clientUsername + " switched from " + groupName + " to " + newGroup);
                    broadcastMessage("SERVER: " + clientUsername + " has left " + groupName + " Chat");
                    this.groupName = newGroup;
                    broadcastMessage("SERVER: " + clientUsername + " has joined " + groupName + " Chat");
                    continue;
                }

                if (messageFromClient.equals("/leave")) {// Client switches back to origianl gorupchat "Global"
                    System.out.println("SERVER: "  + clientUsername + " left " + groupName + " and returned to GLOBAL");
                    broadcastMessage("SERVER: " + clientUsername + " has left " + groupName + " Chat");
                    this.groupName = "GLOBAL";
                    broadcastMessage("SERVER: " + clientUsername + " has left " + groupName + " Chat");
                    continue;
                }


                    broadcastMessage( clientUsername + ": " + messageFromClient);
                    System.out.println("SERVER: [" + groupName + "] " + clientUsername + ": " + messageFromClient);;
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
            }
        } 
    }

    public void broadcastMessage(String messageToSend) {//Will send msg to every other client connected to the saeme group chat
        for(ClientHandler clientHandler : clientHandlers){//Will not send it to itself
            try {
                if (!clientHandler.clientUsername.equals(clientUsername) && clientHandler.groupName.equals(this.groupName)){
                    clientHandler.bufferedWriter.write(messageToSend);//to send the message in the form of bytes through the internet connection(the socket)
                    clientHandler.bufferedWriter.newLine();//used to mark the end of the message
                    clientHandler.bufferedWriter.flush();//force data in memory to send right away through socket
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);//this is just to remove this ClientHandler from ClientHandlers
        broadcastMessage("SERVER: " + clientUsername + " has gone offline!");
        System.out.println("SERVER: " + clientUsername + " has disconnected from " + groupName + " and has gone offline!!");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {//if bufferedReader exist close 
                bufferedReader.close();
            }
            if (bufferedWriter != null) {//if bufferedWriter exist close 
                bufferedWriter.close();
            }
            if (socket != null) {//if socket exist close 
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();//prints the errors and moves on
        }
    }
}
