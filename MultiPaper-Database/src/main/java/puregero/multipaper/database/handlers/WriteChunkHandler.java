package puregero.multipaper.database.handlers;

import puregero.multipaper.databasemessagingprotocol.messages.databasebound.WriteChunkMessage;
import puregero.multipaper.databasemessagingprotocol.messages.serverbound.BooleanMessageReply;
import puregero.multipaper.database.ChunkLockManager;
import puregero.multipaper.database.EntitiesLockManager;
import puregero.multipaper.database.ServerConnection;
import puregero.multipaper.database.util.RegionFileCache;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.SpecificTag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WriteChunkHandler {
    public static void handle(ServerConnection connection, WriteChunkMessage message) {
        if (message.isTransientEntities) {
            handleTransientEntities(connection, message);
            return;
        }

        writeData(connection, message, message.data);
    }

    private static void writeData(ServerConnection connection, WriteChunkMessage message, byte[] data) {
        RegionFileCache.putChunkDeflatedDataAsync(ReadChunkHandler.getWorldDir(message.world, message.path), message.cx, message.cz, data).thenRun(() -> {
            if (message.path.equals("region")) {
                ChunkLockManager.writtenChunk(message.world, message.cx, message.cz);
            }

            if (message.path.equals("entities")) {
                EntitiesLockManager.writtenChunk(message.world, message.cx, message.cz);
            }

            connection.sendReply(new BooleanMessageReply(true), message);
        });
    }

    private static void handleTransientEntities(ServerConnection connection, WriteChunkMessage message) {
        RegionFileCache.getChunkDeflatedDataAsync(ReadChunkHandler.getWorldDir(message.world, message.path), message.cx, message.cz).thenAccept(data -> {
            CompoundTag transientEntities = CompoundTag.read(new DataInputStream(new ByteArrayInputStream(message.data))).asCompound();

            if (data != null) {
                CompoundTag existingEntities = CompoundTag.read(new DataInputStream(new ByteArrayInputStream(data))).asCompound();
                merge(existingEntities, transientEntities);
            }

            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                transientEntities.write(new DataOutputStream(buffer));
                writeData(connection, message, buffer.toByteArray());
            } catch (IOException e) {
                // Should be unreachable
                throw new RuntimeException(e);
            }
        });
    }

    private static void merge(CompoundTag from, CompoundTag to) {
        if (from == null) {
            return;
        }

        ListTag entitiesFrom = from.get("Entities").asList();
        if (entitiesFrom == null || entitiesFrom.isEmpty()) {
            return;
        }

        ListTag entitiesTo = to.get("Entities").asList();
        to.set("Entities", entitiesFrom);
        for (SpecificTag tag : entitiesTo) {
            // The 'from' entities must appear in the list before the 'to' entities
            entitiesFrom.add(tag);
        }
    }
}
