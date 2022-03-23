package puregero.multipaper.mastermessagingprotocol.messages.serverbound;

import puregero.multipaper.mastermessagingprotocol.ExtendedByteBuf;

public class FilesToSyncMessage extends ServerBoundMessage {

    public final FileToSync[] filesToSync;

    public FilesToSyncMessage(FileToSync[] filesToSync) {
        this.filesToSync = filesToSync;
    }

    public FilesToSyncMessage(ExtendedByteBuf byteBuf) {
        filesToSync = new FileToSync[byteBuf.readVarInt()];
        for (int i = 0; i < filesToSync.length; i ++) {
            filesToSync[i] = new FileToSync(
                    byteBuf.readString(),
                    byteBuf.readLong()
            );
        }
    }

    @Override
    public void write(ExtendedByteBuf byteBuf) {
        byteBuf.writeVarInt(filesToSync.length);
        for (FileToSync fileToSync : filesToSync) {
            byteBuf.writeString(fileToSync.getPath());
            byteBuf.writeLong(fileToSync.getLastModified());
        }
    }

    @Override
    public void handle(ServerBoundMessageHandler handler) {
        throw new UnsupportedOperationException("This message can only be handled in a reply");
    }

    public record FileToSync(String path, long lastModified) {
        public String getPath() {
            return path;
        }

        public long getLastModified() {
            return lastModified;
        }
    }
}
