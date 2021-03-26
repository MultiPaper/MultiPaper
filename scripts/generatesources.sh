#!/usr/bin/env bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
. $(dirname $SOURCE)/init.sh


cd $basedir
paperVer=$(cat current-paper)

minecraftversion=$(cat $basedir/Paper/work/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
decompile="Paper/work/Minecraft/$minecraftversion/spigot"

mkdir -p mc-dev/src/net/minecraft

cd mc-dev
if [ ! -d ".git" ]; then
	git init
fi

rm src/net/minecraft/*.java
cp -r $basedir/$decompile/net/minecraft/* src/net/minecraft

base="$basedir/Paper/Paper-Server/src/main/java/net/minecraft"
cd $basedir/mc-dev/src/net/minecraft/
for file in $(/bin/ls $base)
do
	if [ -f "$file" ]; then
		rm -f "$file"
	fi
done
cd $basedir/mc-dev
git add . -A
git commit . -m "mc-dev"
git tag -a "$paperVer" -m "$paperVer" 2>/dev/null
pushRepo . $MCDEV_REPO $paperVer