package puregero.multipaper.server.handlers;

import lombok.extern.slf4j.Slf4j;
import puregero.multipaper.mastermessagingprotocol.datastream.OutboundDataStream;
import puregero.multipaper.mastermessagingprotocol.messages.masterbound.UploadFileMessage;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.mastermessagingprotocol.messages.serverbound.FileContentMessage;
import puregero.multipaper.server.FileLocker;
import puregero.multipaper.server.ServerConnection;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class UploadFileHandler {

    public static void handle(ServerConnection connection, UploadFileMessage message) {
        File file = new File("synced-server-files", message.path);
        FileLocker.createLockAsync(file).thenAcceptAsync(lock -> {
            try {
                FileLocker.writeBytes(file, message.data);
                file.setLastModified(message.lastModified);

                connection.sendReply(new BooleanMessageReply(true), message);

                CompletableFuture<Void> completableFuture = CompletableFuture.completedFuture(null);

                if (message.immediatelySyncToOtherServers) {
                    for (ServerConnection otherConnection : ServerConnection.getConnections()) {
                        if (otherConnection != connection) {
                            CompletableFuture<Void> otherFuture = new CompletableFuture<>();
                            OutboundDataStream dataStream = otherConnection.getDataStreamManager().createOutboundDataStream(otherConnection.getChannel());
                            otherConnection.send(new FileContentMessage(message.path, message.lastModified, dataStream.getStreamId()));
                            dataStream.copyFromAsync(new ByteArrayInputStream(message.data)).addListener(future -> {
                                if (future.cause() != null) {
                                    future.cause().printStackTrace();
                                }
                                otherFuture.complete(null);
                            });
                            completableFuture = CompletableFuture.allOf(completableFuture, otherFuture);
                        }
                    }
                }

                completableFuture.thenRun(() -> {
                    lock.complete(null);
                });
            } catch (IOException e) {
                log.error("Failed to write file", e);

                lock.complete(null);
            }
        });
    }
}
