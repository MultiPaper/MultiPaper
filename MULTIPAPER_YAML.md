# multipaper.yml

`bungeeCordName`  
The name of this server on your bungeecord or velocity proxy. If you aren't
using a proxy, just set this to some arbitrary unique name for each server.
This can also be set using `java -DbungeeCordName=server1 -jar multipaper.jar`.

`multipaperMasterAddress`  
The address for the Master server in the form `127.0.0.1:35353`. Note that all
servers must have the same value for multipaperMasterAddress. You can not have
one server use '127.0.0.1' and another use '192.168.0.5'. They must both be set
to '192.168.0.5'. This can also be set using
`java -DmultipaperMasterAddress=127.0.0.1:35353 -jar multipaper.jar`.

`syncJsonFiles`  
Whether or not to sync the `ops.json`, `whitelist.json`, `banned-players.json`,
and `banned-ips.json` between servers.

`advertiseToBuiltInProxy`  
Whether to let players join this server using the Master's built-in proxy.

`syncScoreboards`  
Whether to sync scoreboard and team data between servers.

`filesToSyncOnStartup`  
Files or directories that should be synced onto the MultiPaper-Master.
Directories will be synced recursively. If two servers write to the same file
at the same time, the last server to write wins. These files will be downloaded
at startup. Note that file deletion is not synced. Files will have to be
manually deleted from all servers at present.

`filesToSyncInRealTime`  
The same as filesToSyncOnStartup, except files are downloaded whenever they are
modified. There is no guarantee that these files will be synced without delay.
Warning: SQLite and H2 database files cannot be synced with this method.

`filesToOnlyUploadOnServerStop`  
Only upload these files to the MultiPaper-Master when the server stops instead
of whenever the file is written to.

`filesToNotSync`  
Files or directories that should not be synced even if they're included in
`filesToSync` above.

