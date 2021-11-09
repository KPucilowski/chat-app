package
        pl.edu.pwr;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;


public class Server {

    public static void main(String[] args) throws IOException {
        final ServerSocket serverSocket ;
        Socket clientSocket ;
        int id = 1;
        serverSocket = new ServerSocket(5000);
        while (true)
        {
            clientSocket = serverSocket.accept();
            ClientThread clientThread = new ClientThread(clientSocket, id++);
            clientThread.start();
        }
    }
}
class ClientThread extends Thread {

    Socket clientSocket;
    int clientID ;
    final Scanner sc = new Scanner(System.in);

    ClientThread(Socket s, int i)
    {
        clientSocket = s;
        clientID = i;
    }
    public void run() {
        System.out.println("Client ID - " + clientID + " : Address - " + clientSocket.getInetAddress().getHostName());
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            out.println(RSAutils.publicKey);
            out.flush();

            String key = in.readLine();
            //  System.out.println(" klucz clienta " +clientID + " " +  key );

            while (true) {
                String clientCommand = in.readLine();
                System.out.println("Client " + clientID +" : "+ RSAutils.decrypt(clientCommand, RSAutils.privateKey));
                out.flush();

                if (clientCommand.equals("q")) {
                    System.out.print("Client " + clientID + " rozłączył się");
                }
                else {
                    String command = sc.nextLine();
                    out.println(Base64.getEncoder().encodeToString(RSAutils.encrypt(command, key)));
                    out.flush();
                }
            }
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException ioException) {
            ioException.printStackTrace();
        }
    }
}