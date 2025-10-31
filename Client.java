//Represents each chat user that connects to the server
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    private Scanner scanner;


    public Client(Socket socket, String username){
        try {
            this.socket = socket;//store socket passed from main
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//send msg to server
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//read text from server 
            this.username = username;//storing clients chosen name
            this.scanner = new Scanner(System.in);

        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessage() {
    
        try {
            bufferedWriter.write(username);//input username 
            bufferedWriter.newLine();//end of username
            bufferedWriter.flush();//send username immediately

            while (socket.isConnected()){//constanlty on
                String messageToSend = scanner.nextLine();//so always ready to read text from the clients side

                if (messageToSend.equals("/exit")) {
                    System.out.println("Disconnected from server: You can no longer chat");
                    bufferedWriter.write(messageToSend);//storing the message in bytes in buffer
                    bufferedWriter.newLine();//marking the end of the message
                    bufferedWriter.flush();//send the message out 
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }

                bufferedWriter.write(messageToSend);//storing the message in bytes in buffer
                bufferedWriter.newLine();//marking the end of the message
                bufferedWriter.flush();//send the message out 
            }
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {//starts a thread to listen for messages from server 
            @Override
            public void run(){
                String msgFromGroupChat;

                while (socket.isConnected()){
                    try {
                        msgFromGroupChat = bufferedReader.readLine();//the msg from server is read and stored in msgFromGroupChat
                        if (msgFromGroupChat == null) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            break;
                        }     

                        System.out.println(msgFromGroupChat);//Prints server message on to console
                    } catch (IOException e) {
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }   
            }
        }).start();
    }


    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
            if (scanner != null) {//if socket exist close 
                scanner.close();
            }
        } catch (IOException e) {
            e.printStackTrace();//prints the errors and moves on
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your usernmae for the group chat:  ");
        String username = scanner.nextLine();//setting client username


        Socket socket = new Socket("localhost", 1234);//connect to the running server
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
        scanner.close();

    }

}




