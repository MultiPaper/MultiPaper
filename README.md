MultiPaper
==

[Airplane](https://github.com/TECHNOVE/Airplane) fork
that enables a server admin to run a single world across multiple
servers. Multiple MultiPaper servers run the same work and use a
MultiPaper-Master to coordinate with eachother and store server data. While the
MultiPaper-Master is usually run as a standalone server, it can also be run as a
BungeeCord plugin, which has some benefits including being able to send players
to the least busiest server when they first join.

MultiPaper 2.0:

- Works like a CDN
    - Each server caches chunks that are needed by the players it's serving
    - The servers also work together to ensure every chunk gets ticked
    - Does not need BungeeCord, just a way to distribute players evenly across
      the server

- Master server
    - Stores the world and data on it
    - Coordinates the servers
        - Decides who gets to tick the chunk (first in first served basis)
    - Prevents conflicts
        - Ensures there's no conflicting entity ids
        - Syncs map and other data between the servers
    - Runs as a standalone process
        - For convenience, it can also run as a BungeeCord plugin

- Every server has a copy of every player on the server
    - Players that are on different servers are ExternalPlayers
- ExternalPlayers do not load chunks, but can receive packets if there's another
  player nearby to load the chunks, and forwards these packets to the
  ExternalPlayer's server so that they are sent to the real player
- Servers do not send packets for chunks that they are not ticking

How chunk syncing works:

- When a server reads a chunk, it asks the Master to load the chunk from the
  region file. If another server has ownership of the chunk, the Master will
  tell the server to load the chunk from the other server and subscribe to it.
- If the chunk has no owner and is loaded, it won't immediately be taken
  ownership of as it could be an edge chunk that won't get ticked.
- When the server wants to tick an unowned chunk, it won't but will send a
  request to the Master to take ownership of it. If ownership is granted, the
  chunk will be ticked next tick. If ownership is denied since another server
  owns it, the server will redownload the chunk from that server and subscribe
  to it.
- Each tick the server will keep track of what chunks it has ticked. If a chunk
  it owns hasn't been ticked, it'll remove ownership of the chunk. The Master
  will check if any other servers are subscribed to the chunk, and update them
  with a new owner.
- If a server is subscribed to another server's chunk, that means any block
  changes will be sent to that server to keep it in sync.
  
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
  * Due to the nature of BungeeCord, when a player swaps servers, 
    `PlayerJoinEvent` will usually be called on the arriving server before
    `PlayerQuitEvent` is called on the departing server.
  * `Bukkit.broadcastMessage` will send the message to all players on all
    MultiPaper servers, while `Bukkit.getOnlinePlayers` will only return the
    players on your singular MultiPaper server.

## How To (Server Admins)
MultiPaper uses the same paperclip jar system that Paper uses.

You can also [build it yourself](https://github.com/PureGero/MultiPaper#building)

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

## Tasks

```
Paperweight tasks
-----------------
./gradlew applyPatches
./gradlew cleanCache - Delete the project setup cache and task outputs.
./gradlew patchPaperApi
./gradlew patchPaperServer
./gradlew rebuildPaperApi
./gradlew rebuildPaperServer
./gradlew rebuildPatches
./gradlew runDev - Spin up a non-shaded non-remapped test server
./gradlew runShadow - Spin up a test server from the shadowJar archiveFile
```

### Note

The fork uses PaperMC's paperweight framework found [here](https://github.com/PaperMC/paperweight)
