#!/bin/bash

function mimetype ()
{
	echo "Content-Type: $1"
	echo ""
}

mimetype "text/plain"

mode="cowsay"; # cowsay ou cowthink
text="Hello"; # n importe quoi

while [ $# -gt 0 ]; do
	case $1 in
		"mode")
			mode=$2
			shift 2
			;;
		"text")
			text=$2
			shift 2
			;;
		*)
			shift
			;;
	esac
done

eval "${mode} ${text}"

exit 0