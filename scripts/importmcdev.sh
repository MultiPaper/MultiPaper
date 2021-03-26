#!/usr/bin/env bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
	DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
	SOURCE="$(readlink "$SOURCE")"
	[[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
. $(dirname $SOURCE)/init.sh

workdir=$basedir/Paper/work
minecraftversion=$(cat $basedir/Paper/work/BuildData/info.json | grep minecraftVersion | cut -d '"' -f 4)
decompiledir=$workdir/Minecraft/$minecraftversion/spigot

nms="net/minecraft"
export MODLOG=""
cd $basedir

export importedmcdev=""
function import {
  export importedmcdev="$importedmcdev $1"
	file="${1}.java"
	target="$basedir/Paper/Paper-Server/src/main/java/$nms/$file"
	base="$decompiledir/$nms/$file"

	if [ ! -f "$target" ]; then
	  export MODLOG="$MODLOG  Imported $file from mc-dev\n";
		echo "$(bashColor 1 32) Copying $(bashColor 1 34)$base $(bashColor 1 32)to$(bashColor 1 34) $target $(bashColorReset)"
		mkdir -p "$(dirname "$target")"
		cp "$base" "$target" || exit 1
	else
	  echo "$(bashColor 1 33) UN-NEEDED IMPORT STATEMENT:$(bashColor 1 34) $file $(bashColorReset)"
	fi
}

function importLibrary {
    group=$1
    lib=$2
    prefix=$3
    shift 3
    for file in "$@"; do
        file="$prefix/$file"
        target="$basedir/Paper/Paper-Server/src/main/java/${file}"
        targetdir=$(dirname "$target")
        mkdir -p "${targetdir}"
        base="$workdir/Minecraft/$minecraftversion/libraries/${group}/${lib}/$file"
        if [ ! -f "$base" ]; then
            echo "Missing $base"
            exit 1
        fi
        export MODLOG="$MODLOG  Imported $file from $lib\n";
        sed 's/\r$//' "$base" > "$target" || exit 1
    done
}

(
	cd "$basedir/Paper/Paper-Server/"
	lastlog=$(git log -1 --oneline)
	if [[ "$lastlog" = *"EMC-Extra mc-dev Imports"* ]]; then
		git reset --hard HEAD^
	fi
)

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
			if [ ! -f "$decompiledir/$nms/$f.java" ]; then
				echo "$(bashColor 1 31) ERROR!!! Missing NMS$(bashColor 1 34) $f $(bashColorReset)";
				error=true
			else
				import $f
			fi
		fi
	fi
done
if [ -n "$error" ]; then
  exit 1
fi

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

########################################################
########################################################
########################################################
#              LIBRARY IMPORTS
# These must always be mapped manually, no automatic stuff
#
# importLibrary    # group    # lib          # prefix               # many files
# importLibrary com.mojang datafixerupper com/mojang/datafixers/types Type.java

# dont forget \ at end of each line but last

########################################################
########################################################
########################################################
(
	cd $basedir/Paper/Paper-Server/
	rm -rf nms-patches
	git add src -A
	echo -e "EMC-Extra mc-dev Imports\n\n$MODLOG" | git commit src -F -
)
