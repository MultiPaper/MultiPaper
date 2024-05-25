package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStringMapMessageReply extends ServerBoundMessage {

    public final Map<String, String> result;

    public KeyValueStringMapMessageReply(Map<String, String> result) {
        this.result = result;
    }

    public KeyValueStringMapMessageReply(ExtendedByteBuf byteBuf) {
        result = new ConcurrentHashMap<>();
        int count = byteBuf.readVarInt();
        for (int i = 0; i < count; i++) {
            result.put(byteBuf.readString(), byteBuf.readString());
        }
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        Set<Map.Entry<String, String>> entries = result.entrySet();
        byteBuf.writeVarInt(entries.size());
        for (Map.Entry<String, String> entry : entries) {
            byteBuf.writeString(entry.getKey());
            byteBuf.writeString(entry.getValue());
        }
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        throw new UnsupportedOperationException("This message can only be handled in a reply");
    }
}
