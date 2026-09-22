#!/bin/bash
# Bridge for the lsfusion-protocol:// links of lsFusion clients before 7.0, registered as the scheme's handler by
# LSF > Install Debug Protocol. It forwards the link to the IDEA plugin's /api/lsfusion-open on the IDE's built-in
# web server (port 63342, the next free one per running IDE - the port must stay at its default), which finds the
# module file itself.
# $1 is "lsfusion-protocol://--line**12&path**<absolute or use_default_path>/foo/bar/File.lsf", spaces as "++".
url=${1#lsfusion-protocol://}
[ "$url" = "$1" ] && exit 1
# a browser percent-encodes the link it hands to the OS; the client wrote the path's own spaces as '++'
url=$(printf %b "${url//%/\\x}")
line=${url#--line\*\*}
line=${line%%&*}
path=${url#*&path\*\*}
path=${path//++/ }
path=${path#[A-Za-z]:} # a Windows drive letter is of no use to the plugin
# the plugin wants the path relative to the source root; an absolute path that is not under one (a project
# directory typed into the old client's settings, possibly one that only exists on the server) is reduced to its
# last two segments, which the plugin matches as a suffix (the module directory name tells namesakes apart)
case "$path" in
  */src/main/lsfusion/*) path=${path#*/src/main/lsfusion/} ;;
  use_default_path*) path=${path#use_default_path}; path=${path#/} ;;
  /*) dir=${path%/*}; dir=${dir##*/}; path=${dir:+$dir/}${path##*/} ;;
esac

for ((port = 63342; port <= 63352; port++)); do
  # the IDE may take a while: it asks the user whether to trust this client before answering the first request
  status=$(curl -s -o /dev/null -w '%{http_code}' --connect-timeout 1 --max-time 60 --get \
    --data-urlencode "path=$path" --data-urlencode "line=$line" "http://localhost:$port/api/lsfusion-open")
  [ "$status" = "200" ] && exit 0
done
exit 1
