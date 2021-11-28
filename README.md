MultiPaper
==

1.18 [Airplane](https://github.com/TECHNOVE/Airplane) fork
that enables a server admin to run a single world across multiple
servers. Multiple MultiPaper servers run the same world and use a
MultiPaper-Master to coordinate with eachother and store server data. While the
MultiPaper-Master is usually run as a standalone server, it can also be run as a
BungeeCord plugin, which has some benefits including being able to send players
to the least busiest server when they join.

MultiPaper 2.0:

- Works like a CDN
    - Each server caches chunks that are needed by the players it's serving
    - The servers work together to ensure every chunk gets ticked
    - Does not need BungeeCord, just some method to evenly distribute players
      across the servers

- MultiPaper-Master
    - Stores the world and data on it
    - Coordinates the servers
        - Decides who gets to tick the chunk (first in first served basis)
    - Runs as a standalone process
        - For your convenience, it can also run as a BungeeCord plugin

How chunk syncing works:

- When a server reads a chunk, it asks the Master to load the chunk from the
  region file. If another server has ownership of the chunk, the Master will
  tell the server to load the chunk from the other server.
- If the chunk has no owner and is loaded, it won't immediately be taken
  ownership of as it could be an edge chunk that won't get ticked.
- When the server wants to tick an unowned chunk, it won't, but will instead
  send a request to the Master to take ownership of it. If ownership is
  granted, the chunk will be ticked on next tick. If ownership is denied since
  another server owns it, the server will redownload the chunk from that
  server.
- When a server has a chunk laoded into memory, it will be subscribed to
  any changes made in that chunk. That means if any server changes a block
  within the chunk, it will be updated on all servers subscribed to that chunk.

Commands
------
MultiPaper includes a few commands mainly for debug purposes:

`/servers`  
List all servers running on this MultiPaper cluster. Includes performance
indicators such as TPS, tick duration, and player count.

`/mpdebug`  
Toggle a debug visualisation showing chunks that your server is ticking (aqua)
and chunks being ticked by another server (red). The server ticking the chunk
you are standing in is displayed above the action bar.

Setting up MultiPaper
------
  * Place your worlds inside the directory being used for MultiPaper-Master
  * Start the MultiPaper-Master by either:
    * Standalone: `java -jar multipaper-master.jar <port>`
    * BungeeCord plugin: Set the port in `plugins/MultiPaperProxy/config.yml`
  * In each MultiPaper server:
    * Put the name of the server used in BungeeCord's config.yml into `bungeecordname.txt`
      * If you're not using BungeeCord, just make it some unique identifier
      * eg. `survival1`
    * Put the address and port of the MultiPaper-Master into `multipaperserver.txt`
      * eg. `localhost:35353`
  
Using MultiPaper with plugins
------
For a plugin to work with MultiPaper, it needs to support multiple servers. A
good indication of this, but not a guarantee, is if a plugin uses a MySQL
database.

Developing a plugin for MultiPaper
------
To make a plugin compatible with MultiPaper, no data must be stored on the
server itself and must instead be stored on an external server such as a MySQL
database.

Some other things to look out for:

  * Caches can prevent the plugin from getting the most up to date data.
  * `PlayerJoinEvent` and `PlayerQuitEvent` will only be called on one server,
    however other events for the player could be called on any server.
  * `Bukkit.broadcastMessage` will send the message to all players on all
    MultiPaper servers.
  * `Bukkit.getOnlinePlayers` will return the players on all MultiPaper
    servers, an API will be made available for determining which players are
    on your server, and which players are on other servers.

## Building
Requirements:
- You need `git` installed, with a configured user name and email. 
   On windows you need to run from git bash.
- You need `maven` installed
- You need `jdk` 16+ installed to compile (and `jre` 16+ to run)

Build instructions:
1. Patch paper with: `./gradlew applyPatches`
2. Build the multipaper jar with: `./gradlew paperclipJar`
3. Get the multipaper jar from `build/libs`
4. Build the multipaper-master jar with: `cd MultiPaper-Master && mvn package`
5. Get the multipaper-master jar from `MultiPaper-Master/target`

## Publishing to maven local
Publish to your local maven repository with: `./gradlew publishToMavenLocal`

### Note

The fork uses PaperMC's paperweight framework found [here](https://github.com/PaperMC/paperweight)
