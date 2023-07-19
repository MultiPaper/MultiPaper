package puregero.multipaper.mastermessagingprotocol.messages;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Protocol<T extends Message<?>> {

    private final List<Class<? extends T>> MESSAGES = new ArrayList<>();

    private final List<Function<ExtendedByteBuf, T>> MESSAGE_DESERIALIZERS = new ArrayList<>();

    protected void addMessage(Class<? extends T> clazz, Function<ExtendedByteBuf, T> deserializer) {
        MESSAGES.add(clazz);
        MESSAGE_DESERIALIZERS.add(deserializer);
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
}
