package puregero.multipaper.server.handlers;

import java.util.HashMap;

public class Handlers {

    private static final HashMap<String, Handler> handlers = new HashMap<>();

    static {
        handlers.put("readChunk", new ReadChunkHandler());
        handlers.put("forceReadChunk", new ForceReadChunkHandler());
        handlers.put("writeChunk", new WriteChunkHandler());
        handlers.put("lockChunk", new LockChunkHandler());
        handlers.put("unlockChunk", new UnlockChunkHandler());
        handlers.put("unsubscribeChunk", new UnsubscribeChunkHandler());
        handlers.put("readLevel", new ReadLevelHandler());
        handlers.put("writeLevel", new WriteLevelHandler());
        handlers.put("readJson", new ReadJsonHandler());
        handlers.put("writeJson", new WriteJsonHandler());
        handlers.put("readPlayer", new ReadPlayerHandler());
        handlers.put("writePlayer", new WritePlayerHandler());
        handlers.put("readUid", new ReadUidHandler());
        handlers.put("writeUid", new WriteUidHandler());
        handlers.put("writeTickTime", new WriteTickTimeHandler());
        handlers.put("readAdvancements", new ReadAdvancementsHandler());
        handlers.put("writeAdvancements", new WriteAdvancementsHandler());
        handlers.put("readStats", new ReadStatsHandler());
        handlers.put("writeStats", new WriteStatsHandler());
        handlers.put("readData", new ReadDataHandler());
        handlers.put("writeData", new WriteDataHandler());
        handlers.put("start", new StartHandler());
        handlers.put("getEntityCounterStartValue", new GetEntityCounterStartValue());
    }

    public static Handler get(String handler) {
        return handlers.get(handler);
    }

}
