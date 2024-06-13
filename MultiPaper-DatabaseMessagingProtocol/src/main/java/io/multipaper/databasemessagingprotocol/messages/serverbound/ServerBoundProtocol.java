package io.multipaper.databasemessagingprotocol.messages.serverbound;

import io.multipaper.databasemessagingprotocol.messages.Protocol;

public class ServerBoundProtocol extends Protocol<ServerBoundMessage> {

    public ServerBoundProtocol() {
        addMessage(BooleanMessage.class, BooleanMessage::new);
        addMessage(DataMessage.class, DataMessage::new);
        addMessage(ServerInfoListMessage.class, ServerInfoListMessage::new);
        addMessage(SetSecretMessage.class, SetSecretMessage::new);
        addMessage(VoidMessage.class, VoidMessage::new);
    }

}
