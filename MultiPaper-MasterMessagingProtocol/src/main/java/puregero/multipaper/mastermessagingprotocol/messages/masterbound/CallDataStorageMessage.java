package puregero.multipaper.mastermessagingprotocol.messages.masterbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class CallDataStorageMessage extends MasterBoundMessage {

    public final String key;
    public final Action action;
    public final String value;

    public CallDataStorageMessage(String key, Action action, String value) {
        this.key = key;
        this.action = action;
        this.value = value;
    }

    public CallDataStorageMessage(ExtendedByteBuf byteBuf) {
        key = byteBuf.readString();
        action = Action.values()[byteBuf.readVarInt()];
        if (byteBuf.readBoolean()) {
            value = byteBuf.readString();
        } else {
            value = null;
        }
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeString(key);
        byteBuf.writeVarInt(action.ordinal());
        byteBuf.writeBoolean(value != null);
        if (value != null) {
            byteBuf.writeString(value);
        }
    }

    @Override
    public void handle(MasterBoundMessageHandler handler) {
        handler.handle(this);
    }

    public static enum Action {
        GET,
        SET,
        ADD;
    }
}
