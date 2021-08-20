MultiPaper
==

Paper fork that enables a server admin to run a single world across multiple
servers. Multiple MultiPaper servers sit behind a BungeeCord proxy and use a
MultiPaper-Master to coordinate with eachother and store server data. While the
MultiPaper-Master can be run as a standalone server, it is usually run as a
BungeeCord plugin, which has some benefits including being able to send players
to the least busiest server when they first join.

MultiPaper syncs between servers:
  
  * OP-list, whitelist, and banlist
  * Playerdata, statistics, and advancements
  * Chunks, POIs, entities, level.dat, and maps
  
MultiPaper requires:
  * A BungeeCord proxy (forks such as Waterfall also work)
  * A MultiPaper-Master (found in `MultiPaper-Master/build/libs`)
    * Runs as either a standalone server
    * Or as a BungeeCord plugin
  * MultiPaper (a fork of Paper)
  
How it works:
  * Swaps players between servers to ensure chunks are only loaded on one server
    at a time
  
Setting up MultiPaper
------
  * Place your worlds inside the directory being used for MultiPaper-Master
  * Start the MultiPaper-Master by either:
    * Standalone: `java -jar multipaper-master.jar <port>`
    * BungeeCord plugin: Set the port in `plugins/MultiPaperProxy/config.yml`
  * In each MultiPaper server:
    * Put the name of the server used in BungeeCord into `bungeecordname.txt`
    * Put the address and port of the MultiPaper-Master into `multipaperserver.txt`
  
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
