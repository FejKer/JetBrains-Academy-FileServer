package client;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class RequestSender {
    private static String response;
    private static StringBuilder request;
    private static final Path root = Path.of(
            System.getProperty("user.dir"),
            "src", "client", "data"
    );

    public static void addFile (DataOutputStream outputStream, DataInputStream inputStream, Scanner scanner) throws IOException {
        request = new StringBuilder("2 ");
        System.out.print("Enter filename: ");
        String fileName = scanner.nextLine();
        File file = new File(fileName);
        System.out.print("Enter name of the file to be saved on server: ");
        String fileNameOnServer = scanner.nextLine();
        if (fileNameOnServer.isEmpty()) fileNameOnServer = fileName;
        request.append(fileNameOnServer).append(" ");
        outputStream.writeUTF(request.toString());
        byte[] message = Files.readAllBytes(Path.of(String.valueOf(root), FileSystems.getDefault().getSeparator(), file.getPath()));
        outputStream.writeInt(message.length);
        outputStream.write(message, 0, message.length);
        System.out.println("The request was sent.");
        String id = inputStream.readUTF();
        response = inputStream.readUTF();
        if (response.equals("200")) {
            System.out.println("Response says that file is saved! ID = " + id);
        } else {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    public static void getFile (DataOutputStream outputStream, DataInputStream inputStream, Scanner scanner, boolean byId) throws IOException {
        request = new StringBuilder("1 ");
        if (byId) {
            request.append("2 ");
            System.out.print("Enter id: ");
        } else {
            request.append("1 ");
            System.out.print("Enter filename: ");
        }
        String input = scanner.next();
        request.append(input).append(" ");
        outputStream.writeUTF(request.toString());
        System.out.println("The request was sent.");
        response = inputStream.readUTF();
        if (response.equals("404")) {
            System.out.println("The response says that this file is not found!");
        } else {
            saveFile(outputStream, inputStream, scanner);
        }
    }

    public static void saveFile (DataOutputStream outputStream, DataInputStream inputStream, Scanner scanner) throws IOException {
        int length = inputStream.readInt();
        byte[] message = new byte[length];
        inputStream.readFully(message, 0 , message.length);
        System.out.println("The file was downloaded! Specify a name for it: ");
        Files.write(new File(Path.of(String.valueOf(root), FileSystems.getDefault().getSeparator(), scanner.next()).toUri()).toPath(), message);
    }

    public static void deleteFile (DataOutputStream outputStream, DataInputStream inputStream, Scanner scanner, boolean byId) throws IOException {
        request = new StringBuilder("3 ");
        if (byId) {
            request.append("2 ");
            System.out.print("Enter id: ");
        } else {
            request.append("1 ");
            System.out.print("Enter filename: ");
        }
        request.append(scanner.next()).append(" ");
        outputStream.writeUTF(request.toString());
        System.out.println("The request was sent.");
        response = inputStream.readUTF();
        if (response.equals("200")) {
            System.out.println("The response says that the file was successfully deleted!");
        } else {
            System.out.println("The response says that the file was not found!");
        }
    }
}
