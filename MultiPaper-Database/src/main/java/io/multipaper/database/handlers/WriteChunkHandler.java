package io.multipaper.database.handlers;

import io.multipaper.database.ServerConnection;
import io.multipaper.database.util.AsyncIO;
import io.multipaper.database.util.RegionFileCache;
import io.multipaper.databasemessagingprotocol.messages.databasebound.WriteChunkMessage;
import io.multipaper.databasemessagingprotocol.messages.serverbound.VoidMessage;
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
        AsyncIO.runAsync(() -> {
            if (message.isTransientEntities) {
                handleTransientEntities(message);
                connection.send(new VoidMessage());
                return;
            }

            writeData(message, message.data);
            connection.send(new VoidMessage());
        });
    }

    private static void writeData(WriteChunkMessage message, byte[] data) {
        RegionFileCache.putChunkDeflatedData(ReadChunkHandler.validateFile(message.path), message.cx, message.cz, data);
    }

    private static void handleTransientEntities(WriteChunkMessage message) {
        byte[] data = RegionFileCache.getChunkDeflatedData(ReadChunkHandler.validateFile(message.path), message.cx, message.cz);

        CompoundTag transientEntities = CompoundTag.read(new DataInputStream(new ByteArrayInputStream(message.data))).asCompound();

        if (data != null) {
            CompoundTag existingEntities = CompoundTag.read(new DataInputStream(new ByteArrayInputStream(data))).asCompound();
            merge(existingEntities, transientEntities);
        }

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            transientEntities.write(new DataOutputStream(buffer));
            writeData(message, buffer.toByteArray());
        } catch (IOException e) {
            // Should be unreachable
            throw new RuntimeException(e);
        }
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
