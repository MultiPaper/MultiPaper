package puregero.multipaper.databasemessagingprotocol.messages.databasebound;

import puregero.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class DownloadFileMessage extends DatabaseBoundMessage {

    public final String path;

    public DownloadFileMessage(String name) {
        this.path = name;
    }

    public DownloadFileMessage(ExtendedByteBuf byteBuf) {
        path = byteBuf.readString();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(path);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
