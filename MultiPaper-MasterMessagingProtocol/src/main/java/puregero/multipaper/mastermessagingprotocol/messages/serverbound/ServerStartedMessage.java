package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class ServerStartedMessage extends ServerBoundMessage {

    public final String host;
    public final int port;

    public ServerStartedMessage(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ServerStartedMessage(ExtendedByteBuf byteBuf) {
        host = byteBuf.readString();
        port = byteBuf.readInt();
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(host);
        byteBuf.writeInt(port);
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        handler.handle(this);
    }
}
