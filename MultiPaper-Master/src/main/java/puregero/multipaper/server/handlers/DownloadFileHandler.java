package puregero.multipaper.server.handlers;

import puregero.multipaper.mastermessagingprotocol.datastream.OutboundDataStream;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.DownloadFileMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.FileContentMessage;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DownloadFileHandler {

    public static void handle(ServerConnection connection, DownloadFileMessage message) {
        OutboundDataStream dataStream = connection.getDataStreamManager().createOutboundDataStream(connection.getChannel());
        File file = new File("synced-server-files", message.path);
        FileLocker.createLockAsync(file).thenAcceptAsync(lock -> {
            try {
                connection.sendReply(new FileContentMessage(message.path, file.lastModified(), dataStream.getStreamId()), message);
                dataStream.copyFromAsync(new FileInputStream(file)).addListener(future -> {
                    lock.complete(null);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
