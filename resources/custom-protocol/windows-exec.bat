@echo off

SET IDEA_RUNNABLE=
SET PROJECT_MODULES=

@REM %1% is string like "lsfusion-protocol://--line**12&path**foo/bar/file.lsf"
@REm OR
@REM %1% is string like "lsfusion-protocol://--line**12&path**use_default_path/foo/bar/file.lsf"

SET ATTR_STRING=%1%
if not x%ATTR_STRING:lsfusion-protocol://=%==x%ATTR_STRING% (
    goto PARSE_PARAMS
) else (
    goto :eof
)

:PARSE_PARAMS
    SET ATTR_STRING=%ATTR_STRING:lsfusion-protocol://=%

    @REM replace delimiter because windows doesnt correct recognize "&" symbol
    SET ATTR_STRING=%ATTR_STRING:&=_delimiter_%
    SET RAW_PATH=%ATTR_STRING:*_delimiter_=%

    @REM get path and return spaces back
    SET TARGET_PATH=%RAW_PATH:path**=%
    SET TARGET_PATH=%TARGET_PATH:"=%
    SET TARGET_PATH=%TARGET_PATH:++= %

    @REM get line
    SETLOCAL EnableDelayedExpansion
        SET x=!ATTR_STRING:_delimiter_%RAW_PATH%=!
        SET x=%x:--line**=%
        SET x=%x:"=%
    ENDLOCAL & SET LINE=%x%

@REM Check absolute or relative path is used
if not x%TARGET_PATH:use_default_path=%==x%TARGET_PATH% (
    goto USE_DEFAULT_PATH
) else (
    goto :CALL_IDEA
)

:USE_DEFAULT_PATH
    @REM Find file in modules
    SETLOCAL EnableDelayedExpansion
        SET TARGET_PATH=%TARGET_PATH:use_default_path=%

        for %%m in (%PROJECT_MODULES%) do (
            SET MODULE_PATH=%%m
            SET MODULE_PATH=!MODULE_PATH:"=!
            SET "FILE=!MODULE_PATH!%TARGET_PATH%"
            if exist !FILE! (
                SET TARGET_PATH=!FILE!
                goto CALL_IDEA
            )
        )
        goto :eof
     ENDLOCAL

:CALL_IDEA
    START "" %IDEA_RUNNABLE% --line %LINE% "%TARGET_PATH%"