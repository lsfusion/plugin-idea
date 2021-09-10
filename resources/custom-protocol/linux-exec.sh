#!/bin/bash

IDEA_RUNNABLE=
PROJECT_MODULES=

#$1 is string like "lsfusion-protocol://--line**12&path**foo/bar/file.lsf"
#OR
#$1 is string like "lsfusion-protocol://--line**12&path**use_default_path/foo/bar/file.lsf"
if [[ $1 == "lsfusion-protocol://"* ]]; then

  PARAMS_STRING=${1#lsfusion-protocol://} #get substring without "lsfusion-protocol://"
  PARAMS=(${PARAMS_STRING//&/ }) #split string by "&"

  LINE=${PARAMS[0]/\*\*/ } #replace "**" by " "

  TARGET_PATH=${PARAMS[1]#path\*\*} #get substring without "path**"
  TARGET_PATH=${TARGET_PATH/\+\+/ } # replace "++" by " "

  if [[ "$TARGET_PATH" == "use_default_path"* ]]; then
    TARGET_PATH=${TARGET_PATH/use_default_path} #remove "use_default_path"

    for MODULE in ${PROJECT_MODULES[@]}; do
      FILE=$MODULE$TARGET_PATH
      if [[ -f "$FILE" ]]; then
        TARGET_PATH=$FILE
        break
      fi
    done
  fi

  sh "$IDEA_RUNNABLE" $LINE "$TARGET_PATH"
fi