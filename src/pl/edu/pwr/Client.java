package pl.edu.pwr;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.*;
import java.util.Base64;
import java.util.Scanner;
public class Client {
    private static String key="";

    public static void main(String[] args){
        final Socket clientSocket;
        final BufferedReader in;
        final PrintWriter out;
        final Scanner sc = new Scanner(System.in);

        try {
            clientSocket = new Socket("localhost", 5000);
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread sender = new Thread(new Runnable() {
                String msg ;

                @Override
                public void run() {
                    out.println(RSAutils.publicKey);

                    out.flush();


                    msg = sc.nextLine();
                    while(msg!=null){
                        msg = sc.nextLine();
                        out.println(msg);
                        try {
                            out.println(Base64.getEncoder().encodeToString(RSAutils.encrypt(msg, key)));
                        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                        out.flush();
                    }
                }
            });
            sender.start();

            Thread receiver = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {

                    try {
                        key = in.readLine();
                        msg = in.readLine();
                        while(msg!=null){
                            System.out.println("Server : " + RSAutils.decrypt(msg, RSAutils.privateKey));
                            msg = in.readLine();
                        }
                        System.out.println("Połączenie przerwane");
                        out.close();
                        clientSocket.close();
                    } catch (IOException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiver.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}