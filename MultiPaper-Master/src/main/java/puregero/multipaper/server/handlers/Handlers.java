package puregero.multipaper.server.handlers;

import java.util.HashMap;

public class Handlers {

    private static HashMap<String, Handler> handlers = new HashMap<>();

    static {
        handlers.put("readChunk", new ReadChunkHandler());
        handlers.put("writeChunk", new WriteChunkHandler());
        handlers.put("readLevel", new ReadLevelHandler());
        handlers.put("writeLevel", new WriteLevelHandler());
        handlers.put("readJson", new ReadJsonHandler());
        handlers.put("writeJson", new WriteJsonHandler());
        handlers.put("readPlayer", new ReadPlayerHandler());
        handlers.put("writePlayer", new WritePlayerHandler());
        handlers.put("readUid", new ReadUidHandler());
        handlers.put("writeUid", new WriteUidHandler());
        handlers.put("writeTickTime", new WriteTickTimeHandler());
        handlers.put("lockPlayer", new LockPlayerHandler());
        handlers.put("releasePlayer", new ReleasePlayerHandler());
        handlers.put("willSaveChunk", new WillSaveChunkHandler());
        handlers.put("chunkNotSaving", new ChunkNotSavingHandler());
        handlers.put("readAdvancements", new ReadAdvancementsHandler());
        handlers.put("writeAdvancements", new WriteAdvancementsHandler());
        handlers.put("lockAdvancements", new LockAdvancementsHandler());
        handlers.put("releaseAdvancements", new ReleaseAdvancementsHandler());
        handlers.put("readStats", new ReadStatsHandler());
        handlers.put("writeStats", new WriteStatsHandler());
        handlers.put("lockStats", new LockStatsHandler());
        handlers.put("releaseStats", new ReleaseStatsHandler());
        handlers.put("broadcastPacket", new BroadcastPacketHandler());
        handlers.put("readData", new ReadDataHandler());
        handlers.put("writeData", new WriteDataHandler());
        handlers.put("playerList", new PlayerListHandler());
        handlers.put("start", new StartHandler());
        handlers.put("loadedChunkList", new LoadedChunkListHandler());
        handlers.put("playerUpdate", new PlayerUpdateHandler());
    }

    public static Handler get(String handler) {
        return handlers.get(handler);
    }

}
