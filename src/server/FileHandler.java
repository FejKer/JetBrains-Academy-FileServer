package server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class FileHandler {
    private static  Map<Integer, File> files = new HashMap<>();

    private static Integer lastId;

    public static Map<Integer, File> getFiles() {
        return files;
    }

    public static void setFiles(Map<Integer, File> files) {
        FileHandler.files = files;
    }

    public static int getLastId() {
        return lastId;
    }

    public static void setLastId(int lastId) {
        FileHandler.lastId = lastId;
    }

    public static void readMap() throws InterruptedException {
        try (FileInputStream fileInputStream = new FileInputStream("hashmap.txt");
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            files = (Map<Integer, File>) objectInputStream.readObject();
            lastId = files.size() + 1;
            for (File f:
                 files.values()) {
                if (!f.exists()) Files.createFile(f.toPath());
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveMap() throws InterruptedException {
        try (FileOutputStream fileOutputStream = new FileOutputStream("hashmap.txt");
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(files);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addFile(File fileName, byte[] content, DataOutputStream output) throws IOException {
        if (!files.containsValue(fileName)) {
            if (lastId == null) {
                lastId = 1;
            }
            Files.write(fileName.toPath(), content);
            output.writeUTF(String.valueOf(lastId));
            files.put(lastId++, fileName);
            output.writeUTF("200");
        } else {
            output.writeUTF("403");
        }
    }
    public static void getFileByName(File fileName, DataOutputStream output) throws IOException {
        if (files.containsValue(fileName)) {
            try (Scanner scanner = new Scanner(fileName)) {
                byte[] message = scanner.nextLine().getBytes();
                output.writeUTF("200");
                output.writeInt(message.length);
                output.write(message, 0, message.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            output.writeUTF("404");
        }

    }
    public static void getFileById(int id, DataOutputStream output) throws IOException {
        if (files.containsKey(id)) {
            try (Scanner scanner = new Scanner(files.get(id))) {
                byte[] message = scanner.nextLine().getBytes();
                output.writeUTF("200");
                output.writeInt(message.length);
                output.write(message, 0, message.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            output.writeUTF("404");
        }
    }
    public static String deleteFileByName(File fileName) throws IOException {
        if (files.containsValue(fileName) && Files.deleteIfExists(fileName.toPath())) {
            files.entrySet()
                    .removeIf(
                            entry -> (fileName
                                    .equals(entry.getValue())));
            return "200";
        } else {
            return "404";
        }
    }
    public static String deleteFileById(int id) throws IOException {
        if (files.containsKey(id) && Files.deleteIfExists(Path.of(files.get(id).toURI()))) {
            files.remove(id);
            return "200";
        } else {
            return "404";
        }
    }
}
