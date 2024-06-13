package io.multipaper.databasemessagingprotocol.messages.serverbound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ServerInfoListMessage extends ServerBoundMessage {

    public final Collection<Server> servers;

    public ServerInfoListMessage(Collection<Server> servers) {
        this.servers = servers;
    }

    public ServerInfoListMessage(ExtendedByteBuf byteBuf) {
        int length = byteBuf.readVarInt();
        servers = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            servers.add(new Server(byteBuf.readString(), byteBuf.readUUID(), byteBuf.readVarInt(), byteBuf.readFloat(), byteBuf.readBoolean()));
        }
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeVarInt(servers.size());
        for (Server server : servers) {
            byteBuf.writeString(server.name());
            byteBuf.writeUUID(server.uuid());
            byteBuf.writeVarInt(server.averageTickTime());
            byteBuf.writeFloat(server.tps());
            byteBuf.writeBoolean(server.isAlive());
        }
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }

    public record Server(String name, UUID uuid, int averageTickTime, float tps, boolean isAlive) {}
}
