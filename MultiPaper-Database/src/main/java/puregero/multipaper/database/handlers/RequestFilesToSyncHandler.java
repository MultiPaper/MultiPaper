package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.RequestFilesToSyncMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.FilesToSyncMessage;
import puregero.multipaper.database.ServerConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RequestFilesToSyncHandler {
    public static void handle(ServerConnection connection, RequestFilesToSyncMessage message) {
        List<File> files = new ArrayList<>();

        appendFilesRecursively(new File("synced-server-files"), files);

        FilesToSyncMessage.FileToSync[] filesToSync = new FilesToSyncMessage.FileToSync[files.size()];
        for (int i = 0; i < filesToSync.length; i ++) {
            File file = files.get(i);
            filesToSync[i] = new FilesToSyncMessage.FileToSync(
                file.getPath().replace("synced-server-files/", "").replace("synced-server-files\\", ""),
                file.lastModified()
            );
        }

        connection.sendReply(new FilesToSyncMessage(filesToSync), message);
    }

    private static void appendFilesRecursively(File file, List<File> files) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                appendFilesRecursively(child, files);
            }
        } else if (file.isFile()) {
            files.add(file);
        }
    }
}
