#!/usr/bin/env bash
# get base dir regardless of execution location
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
. $(dirname $SOURCE)/init.sh

nms="net/minecraft"
cd $basedir

files=$(cat "$basedir/patches/server/"* | grep "+++ b/src/main/java/net/minecraft/" | sort | uniq | sed 's/\+\+\+ b\/src\/main\/java\/net\/minecraft\///g')

nonnms=$(grep -R "new file mode" -B 1 "$basedir/patches/server/" | grep -v "new file mode" | grep -oE --color=none "net\/minecraft\/.*.java" | sed 's/.*\/net\/minecraft\///g')
function containsElement {
	local e
	for e in "${@:2}"; do
		[[ "$e" == "$1" ]] && return 0;
	done
	return 1
}
for f in $files; do
	containsElement "$f" ${nonnms[@]}
	if [ "$?" == "1" ]; then
		if [ ! -f "$basedir/Paper/Paper-Server/src/main/java/net/minecraft/$f" ]; then
		f="$(echo "$f" | sed 's/.java//g')"
			echo $nms/$f.java >> Paper/build-data/mcdev-imports.txt
		fi
	fi
done

###############################################################################################
###############################################################################################
#################### ADD TEMPORARY ADDITIONS HERE #############################################
###############################################################################################
###############################################################################################

# import FileName
# import world/level/saveddata/PersistentBase
# import network/protocol/game/PacketPlayOutPlayerInfo
# import network/protocol/game/PacketPlayInSpectate
# import advancements/CriterionProgress
