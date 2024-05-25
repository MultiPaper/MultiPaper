package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class StartMessage extends DatabaseBoundMessage {

    public final String host;
    public final int port;

    public StartMessage(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public StartMessage(ExtendedByteBuf byteBuf) {
        host = byteBuf.readString();
        port = byteBuf.readInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(host);
        byteBuf.writeInt(port);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
