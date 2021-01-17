MultiPaper
==

Paper fork that enables a server admin to run a single world across multiple
servers. Multiple MultiPaper servers sit behind a BungeeCord proxy and use a
MultiPaper-Master to coordinate with eachother and store server data. While the
MultiPaper-Master can be run as a standalone server, it is usually run as a
BungeeCord plugin, which has some benefits including being able to send players
to the least busiest server when they first join.

MultiPaper syncs between servers:
  
  * OP-list, whitelist and banlist
  * Playerdata, statistics and advancements
  * Chunks, POIs, level.dat and maps
  
MultiPaper requires:
  * A BungeeCord proxy (forks such as Waterfall also work)
  * A MultiPaper-Master (found in `MultiPaper-Master/target`)
    * Runs as either a standalone server
    * Or as a BungeeCord plugin
  * MultiPaper
  
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
    * Put the address of the MultiPaper-Master into `multipaperserver.txt`
  
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
- You need `jdk` 11+ installed to compile (and `jre` 11+ to run)
- Anything else that `paper` requires to build

If all you want is a paperclip server jar, just run `./multipaper jar`

Otherwise, to setup the `MultiPaper-API` and `MultiPaper-Server` repo, just run the following command
in your project root `./multipaper patch` additionally, after you run `./multipaper patch` you can run `./multipaper build` to build the 
respective api and server jars.

`./multipaper patch` should initialize the repo such that you can now start modifying and creating
patches. The folder `MultiPaper-API` is the api repo and the `MultiPaper-Server` folder
is the server repo and will contain the source files you will modify.

#### Creating a patch
Patches are effectively just commits in either `MultiPaper-API` or `MultiPaper-Server`.
To create one, just add a commit to either repo and run `./multipaper rb`, and a
patch will be placed in the patches folder. Modifying commits will also modify its
corresponding patch file.

## License
The PATCHES-LICENSE file describes the license for api & server patches,
found in `./patches` and its subdirectories except when noted otherwise.

Everything else is licensed under the MIT license, except when note otherwise.
See https://github.com/starlis/empirecraft and https://github.com/electronicboy/byof
for the license of material used/modified by this project.

### Note

The fork is based off of aikar's EMC framework found [here](https://github.com/starlis/empirecraft)
