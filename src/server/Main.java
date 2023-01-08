package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class Main {
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {
        if (new File("hashmap.txt").exists()) {
            FileHandler.readMap();
        }
        System.out.println("Server started!");
        new Runnable() {
            @Override
            public void run() {
                String msg = "";
                String response = "";
                String[] msgArray = new String[0];
                try (ServerSocket server = new ServerSocket(PORT)) {
                    while (true) {
                        try (
                                Socket socket = server.accept();
                                DataInputStream input = new DataInputStream(socket.getInputStream());
                                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
                        ) {
                            Path root = Path.of(
                                    System.getProperty("user.dir"),
                                    "src", "server", "data"
                            );
                            msg = input.readUTF();
                            if (msg.equals("exit")) {
                                FileHandler.saveMap();
                                break;
                            }
                            msgArray = msg.split(" ");
                            switch (msgArray[0]) {
                                case "1" -> {
                                    if (msgArray[1].equals("2")) {
                                        if (msgArray.length == 2) {
                                            output.writeUTF("404");
                                        } else {
                                            FileHandler.getFileById(Integer.parseInt(msgArray[2]), output);
                                        }
                                    } else {
                                        if (msgArray.length == 2) {
                                            output.writeUTF("404");
                                        } else {
                                            FileHandler.getFileByName(new File(root + FileSystems.getDefault().getSeparator() + msgArray[2]), output);
                                        }
                                    }
                                }
                                case "2" -> {
                                    byte[] message = new byte[input.readInt()];
                                    input.readFully(message, 0, message.length);
                                    FileHandler.addFile(new File(root + FileSystems.getDefault().getSeparator() + msgArray[1]), message, output);
                                }
                                case "3" -> {
                                    if (msgArray[1].equals("2")) {
                                        msg = FileHandler.deleteFileById(Integer.parseInt(msgArray[2]));
                                    } else {
                                        msg = FileHandler.deleteFileByName(new File(root + FileSystems.getDefault().getSeparator() + msgArray[2]));
                                    }
                                    output.writeUTF(msg);
                                }
                            }
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}