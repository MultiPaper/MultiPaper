package io.multipaper.databasemessagingprotocol.messages.databasebound;

import io.multipaper.databasemessagingprotocol.ExtendedByteBuf;

public class ReadChunkMessage extends DatabaseBoundMessage {

    public final String path;
    public final int cx;
    public final int cz;

    public ReadChunkMessage(String path, int cx, int cz) {
        this.path = path;
        this.cx = cx;
        this.cz = cz;
    }

    public ReadChunkMessage(ExtendedByteBuf byteBuf) {
        path = byteBuf.readString();
        cx = byteBuf.readInt();
        cz = byteBuf.readInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(path);
        byteBuf.writeInt(cx);
        byteBuf.writeInt(cz);
    }

    @Override
    public void handle(DatabaseBoundMessageHandler handler) {
        handler.handle(this);
    }
}
