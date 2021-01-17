MultiPaper
==

Fork of [Paper](https://github.com/PaperMC/Paper) allowing a multi-server single-world bungeecord setup.

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
