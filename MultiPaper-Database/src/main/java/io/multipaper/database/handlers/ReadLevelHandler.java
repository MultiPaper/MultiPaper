package io.multipaper.database.handlers;

import io.multipaper.database.FileLocker;
import io.multipaper.database.ServerConnection;
import io.multipaper.database.ServerInfo;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.databasemessagingprotocol.messages.databasebound.ReadLevelMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.DataMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ReadLevelHandler {
    private static final long EMPTY_LEVEL_DAT_GENERATION_TIMEOUT = 300 * 1000L; // ms

    private static ServerInfo generatingEmptyLevelDatServer = null;
    private static long generatingEmptyLevelDatStartTime = 0;

    private static synchronized void handleEmptyLevelDat(ServerConnection connection, ReadLevelMessage message) {
        if (generatingEmptyLevelDatServer == null || generatingEmptyLevelDatServer.equals(connection.getServerInfo()) || generatingEmptyLevelDatStartTime < System.currentTimeMillis() - EMPTY_LEVEL_DAT_GENERATION_TIMEOUT) {
            System.out.println(connection.getServerInfo().getName() + " has requested " + message.world + "'s level.dat, but it is empty. We will assume this server will generate it.");
            generatingEmptyLevelDatStartTime = System.currentTimeMillis();
            generatingEmptyLevelDatServer = connection.getServerInfo();
            connection.send(new DataMessage(new byte[0]));
            return;
        }

        System.out.println(connection.getServerInfo().getName() + " has requested " + message.world + "'s level.dat, but it is empty. Retying in 10 seconds once " + generatingEmptyLevelDatServer + " has generated it...");
        CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> handle(connection, message));
    }

    public static void handle(ServerConnection connection, ReadLevelMessage message) {
        AsyncIO.runAsync(() -> {
            try {
                try {
                    byte[] b = FileLocker.readBytes(new File(message.world, "level.dat"));
                    connection.send(new DataMessage(b));
                } catch (NoSuchFileException e) {
                    handleEmptyLevelDat(connection, message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
