import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Scanner;

public class Client {
    private Socket socket; // Socket used to establish connection with server
    private BufferedWriter bufferedWriter; // Writer used to send message to server
    private BufferedReader bufferedReader; // Reader used to receive messages form server
    private String username; // Username chosen by the client
    private Scanner scanner; // Scanner to read console input 


    public Client(Socket socket, String username, Scanner scanner){
        try {
            this.socket = socket; // Store socket reference
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Create a writer connected to the socket's output stream
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Create a reader connected to the socket's input stream
            this.username = username; // Storing client's chosen name
            this.scanner = scanner; // Store scanner reference

        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessage() { // Method sends messages from client to server
    
        try {
            bufferedWriter.write(username); // Send username first so server can register client 
            bufferedWriter.newLine(); // End of username
            bufferedWriter.flush(); // Send username immediately

            while (!socket.isClosed()){ // Keep sending messages while connected
                String messageToSend = scanner.nextLine(); // Read message from console

                if (messageToSend.equals("/exit")) { // Exit Command handaling
                    System.out.println("Disconnected from server: You can no longer chat");
                    bufferedWriter.write(messageToSend); // Storing the message in bytes in buffer
                    bufferedWriter.newLine(); // Marking the end of the message
                    bufferedWriter.flush(); // Send the message out 
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }

                bufferedWriter.write(messageToSend); // Storing the message in bytes in buffer
                bufferedWriter.newLine(); // Marking the end of the message
                bufferedWriter.flush(); // Send the message out 
            }
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenForMessage() { // Method listens for incoming messages from server
        new Thread(new Runnable() { // Starts a thread to listen for messages from server 
            @Override
            public void run(){
                String msgFromGroupChat; // Variable to store incoming message

                while (!socket.isClosed()){ // Keep listening while connected
                    try {
                        msgFromGroupChat = bufferedReader.readLine(); // Read incoming message 
                        if (msgFromGroupChat == null) { // Server closed connection
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            break;
                        }     

                        System.out.println(msgFromGroupChat); // Prints server message on to console
                    } catch (IOException e) {
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }   
            }
        }).start(); // Start listening thread
    }


    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) { // Mehtod closes all open resources safely
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
            if (scanner != null) { // If socket exist close 
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Prints the errors and moves on
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your usernmae for the group chat:  "); // Prompt user 
        String username = scanner.nextLine(); // Read client username


        Socket socket = new Socket("localhost", 1234); // Connect to the running server
        Client client = new Client(socket, username, scanner); // Create Client object
        client.listenForMessage(); // Start listening thread
        client.sendMessage(); // Start sending messages
        scanner.close(); // Close scanner after program ends 

    }

}




