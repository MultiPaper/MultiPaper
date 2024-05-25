package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ReadJsonMessage extends DatabaseBoundMessage {

    public final String file;

    public ReadJsonMessage(String file) {
        this.file = file;
    }

    public ReadJsonMessage(ExtendedByteBuf byteBuf) {
        file = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(file);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
