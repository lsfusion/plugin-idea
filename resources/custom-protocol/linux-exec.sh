#!/bin/bash

#$1 is string like "lsfusion-protocol://idea**foo/bar/idea.sh&--line**12&path**foo/bar/file.lsf"
if [[ $1 == "lsfusion-protocol://"* ]]; then

  PARAMS_STRING=${1#lsfusion-protocol://} #get substring without "lsfusion-protocol://"
  PARAMS=(${PARAMS_STRING//&/ }) #split string by "&"

  IDEA_RUNNABLE=${PARAMS[0]#idea\*\*} # get idea path
  IDEA_RUNNABLE=${IDEA_RUNNABLE/\+\+/ } # replace "++" by " "

  LINE=${PARAMS[1]/\*\*/ } #replace "**" by " "

  TARGET_PATH=${PARAMS[2]#path\*\*} #get substring without "path**"
  TARGET_PATH=${TARGET_PATH/\+\+/ } # replace "++" by " "

  sh "$IDEA_RUNNABLE" $LINE "$TARGET_PATH"
fi