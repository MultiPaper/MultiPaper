package puregero.multipaper.databasemessagingprotocol.messages.serverbound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ServerInfoUpdateMessage extends ServerBoundMessage {

    public final String name;
    public final int averageTickTime;
    public final float tps;

    public ServerInfoUpdateMessage(String name, int averageTickTime, float tps) {
        this.name = name;
        this.averageTickTime = averageTickTime;
        this.tps = tps;
    }

    public ServerInfoUpdateMessage(ExtendedByteBuf byteBuf) {
        name = byteBuf.readString();
        averageTickTime = byteBuf.readInt();
        tps = byteBuf.readFloat();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(name);
        byteBuf.writeInt(averageTickTime);
        byteBuf.writeFloat(tps);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
