# multipaper.yml

## master-connection

`my-name`  
The name of this server on your bungeecord or velocity proxy. If you aren't
using a proxy, just set this to some arbitrary unique name for each server.
This can also be set using
`java -Dmultipaper.master-connection.my-name=server1 -jar multipaper.jar`.

`master-address`  
The address for the Master server in the form `127.0.0.1:35353`. Note that all
servers must have the same value for multipaperMasterAddress. You can not have
one server use '127.0.0.1' and another use '192.168.0.5'. They must both be set
to '192.168.0.5'. This can also be set using
`java -Dmultipaper.master-connection.master-address=127.0.0.1:35353 -jar multipaper.jar`.

`advertise-to-built-in-proxy`  
Whether to let players join this server using the Master's built-in proxy.

## optimizations

`disable-safety-redstone-chunk-lock`
When executing a redstone contraption that is run across multiple servers,
MultiPaper syncs these servers together such that the redstone contraption can
run without getting out of sync. Disabling this can prevent lag spikes caused
by this system, but can also mess up cross-server redstone.

`dont-save-just-for-lighting-updates`
When loading chunks, lighting updates are frequently applied to them even if
the chunks are pre-generated, causing unnecessary savings of the chunks.
Enabling this option can significantly reduce the number of chunks being saved
unnecessarily.

`reduce-player-position-updates-in-unloaded-chunks`
Enabling this will prevent a player's position being updated every tick on
external servers that don't have the player's chunk loaded. This will mean that
if you teleport to the player, you will be teleported to an outdated location.
`Player.getLocation()` will also return an outdated location for external
players in unloaded chunks.

`ticks-per-inactive-entity-tracking`  
Ticks per tracking of inactive entities. The default value is 1, meaning
inactive entities will be tracked every tick (this is vanilla behaviour).
Increasing this value will reduce the cpu consumed by entities that are
outside the simulation distance, and have been deemed inactive.

`use-event-based-io`  
Setting this to `false` will use a single thread that must wait upon each IO
operation. If one of these IO operations depends on another server, and that
server is lagging, this will cause all other IO operations to wait for this
lagging server.

Setting this to `true` will use async event-based IO operations. This means
more than one IO operation can be run at once, and thus your IO operations
won't all have to wait for that one laggy server.

## peer-connection

`compression-threshold`  
The number of bytes before data being sent between peered servers is compressed
(recommended is 1024 bytes when using compression). Note that this number of
bytes may count multiple packets, not just one, depending on your
interServerConsolidationDelay. Set this value to 0 to disable compression.

`consolidation-delay`  
The delay between packets being written between peered servers, and them
actually being sent (recommended is 2ms when using compression). This allows
for packets to be grouped together for compression. Set this value to 0 to
disable the delay.

## sync-settings

`sync-json-files`  
Whether or not to sync the `ops.json`, `whitelist.json`, `banned-players.json`,
and `banned-ips.json` between servers.

`sync-permissions`  
Whether to sync player permissions between servers. This can be quite resource
intensive at higher player counts, and can be disabled if you do not use
permissions, or have a permissions manager that already syncs the permissions
between servers.

`sync-scoreboards`  
Whether to sync scoreboard and team data between servers.

`use-local-player-count-for-server-is-full-kick`  
Kick connecting players whether the player count summed across all servers
exceeds the max-players value (false, default), or whether the player count on
just the local server exceeds the max-players value (true).

### files

`files-to-not-sync`  
Files or directories that should not be synced even if they're included in
`files-to-sync-on-startup` or `files-to-sync-in-real-time`.

`files-to-only-upload-on-server-stop`  
Only upload these files to the MultiPaper-Master when the server stops instead
of whenever the file is written to.

`files-to-sync-in-real-time`  
The same as `files-to-sync-on-startup`, except files are downloaded whenever
they are modified. There is no guarantee that these files will be synced
without delay. Warning: SQLite and H2 database files cannot be synced with
this method.

`files-to-sync-on-startup`  
Files or directories that should be synced onto the MultiPaper-Master.
Directories will be synced recursively. If two servers write to the same file
at the same time, the last server to write wins. These files will be downloaded
at startup. Note that file deletion is not synced. Files will have to be
manually deleted from all servers at present.

`log-file-syncs`
Whether or not a log should be printed everytime a file is synced with the
master database.
