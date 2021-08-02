@echo off

echo %1% |findstr /b "lsfusion-protocol://" >nul && (
    SET ref=%1%
    SET ref=%ref:lsfusion-protocol://=%
REM     replace delimiter because windows doesnt correct recognize "&" symbol
    SET ref=%ref:&=_delimiter_%
    SET LINE_WITH_PATH=%ref:*_delimiter_=%
    SET RAW_PATH=%LINE_WITH_PATH:*_delimiter_=%

    SET TARGET_PATH=%RAW_PATH:path**=%
    SET TARGET_PATH=%TARGET_PATH:"=%
    SET TARGET_PATH=%TARGET_PATH:++= %

    SETLOCAL EnableDelayedExpansion
        SET x=!LINE_WITH_PATH:_delimiter_%RAW_PATH%=!
        SET x=%x:--line**=%
    ENDLOCAL & SET LINE=%x%

    SETLOCAL EnableDelayedExpansion
        SET y=!ref:_delimiter_%LINE_WITH_PATH%=!
        SET y=%y:idea**=%
        SET y=%y:"=%
        SET y=%y:++= %
    ENDLOCAL & SET IDEA_RUNNABLE=%y%

    START "" "%IDEA_RUNNABLE%" --line %LINE% "%TARGET_PATH%"
)