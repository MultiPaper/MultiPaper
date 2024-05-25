package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class SetPortMessage extends DatabaseBoundMessage {

    public final int port;

    public SetPortMessage(int port) {
        this.port = port;
    }

    public SetPortMessage(ExtendedByteBuf byteBuf) {
        port = byteBuf.readInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeInt(port);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
