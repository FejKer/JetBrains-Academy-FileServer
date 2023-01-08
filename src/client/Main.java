package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int PORT = 8080;
    private static final String IP = "127.0.0.1";

    public static void main (String[] args) throws IOException, InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        StringBuilder input = new StringBuilder();
        try (Socket socket = new Socket(IP, PORT);
             Scanner scanner = new Scanner(System.in);
             DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
            String temp = scanner.nextLine();
            if (temp.equals("exit")) {
                outputStream.writeUTF("exit");
                System.out.println("The request was sent");
            }
            switch (temp) {
                case "1" -> {
                    System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                    boolean byId = scanner.nextInt() == 2;
                    RequestSender.getFile(outputStream, inputStream, scanner, byId);
                }
                case "2" -> RequestSender.addFile(outputStream, inputStream, scanner);
                case "3" -> {
                    System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id): ");
                    boolean byId = scanner.nextInt() == 2;
                    RequestSender.deleteFile(outputStream, inputStream, scanner, byId);
                }
            }
        }
        TimeUnit.SECONDS.sleep(1);
    }
}
