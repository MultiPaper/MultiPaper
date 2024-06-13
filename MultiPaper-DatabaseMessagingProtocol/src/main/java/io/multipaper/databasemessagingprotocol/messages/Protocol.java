package io.multipaper.databasemessagingprotocol.messages;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Protocol<T extends Message<?>> {

    private final List<Class<? extends T>> MESSAGES = new ArrayList<>();
    private final List<Function<ExtendedByteBuf, T>> MESSAGE_DESERIALIZERS = new ArrayList<>();
    private long protocolHash = 0;

    protected void addMessage(Class<? extends T> clazz, Function<ExtendedByteBuf, T> deserializer) {
        MESSAGES.add(clazz);
        MESSAGE_DESERIALIZERS.add(deserializer);
        protocolHash = 0; // Invalidate the hash and force a recalculation
    }

    public int getMessageId(T message) {
        int id = MESSAGES.indexOf(message.getClass());
        if (id == -1) {
            System.err.println("Unknown message " + message);
            throw new IllegalArgumentException("Unknown message " + message);
        }
        return id;
    }

    public Function<ExtendedByteBuf, T> getDeserializer(int messageId) {
        return MESSAGE_DESERIALIZERS.get(messageId);
    }

    public long getProtocolHash() {
        if (protocolHash == 0) {
            for (int i = 0; i < MESSAGES.size(); i++) {
                Class<? extends T> clazz = MESSAGES.get(i);
                long value = clazz.getSimpleName().hashCode();
                for (Field field : clazz.getDeclaredFields()) {
                    value += field.getName().hashCode() ^ field.getType().getSimpleName().hashCode();
                }
                protocolHash += value * (i + 1);
            }
        }
        return protocolHash;
    }
}
