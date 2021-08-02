@echo off

REM script to exucute add to registry with admin rights from https://stackoverflow.com/questions/1894967/how-to-request-administrator-access-inside-a-batch-file

:: BatchGotAdmin
:-------------------------------------
REM  --> Check for permissions
    IF "%PROCESSOR_ARCHITECTURE%" EQU "amd64" (
>nul 2>&1 "%SYSTEMROOT%\SysWOW64\cacls.exe" "%SYSTEMROOT%\SysWOW64\config\system"
) ELSE (
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
)

REM --> If error flag set, we do not have admin.
if '%errorlevel%' NEQ '0' (
    echo Requesting administrative privileges...
    goto UACPrompt
) else ( goto gotAdmin )

:UACPrompt
    echo Set UAC = CreateObject^("Shell.Application"^) >  "%temp%\getadmin.vbs"
    echo UAC.ShellExecute "cmd.exe", "/c """"%~s0"" "%1"""", "", "runas", 1 >> "%temp%\getadmin.vbs"
    "%temp%\getadmin.vbs"
    del "%temp%\getadmin.vbs"
    exit /B

:gotAdmin
    pushd "%CD%"
    CD /D "%~dp0"
:--------------------------------------

set quotedAttr=%1%
set quotedAttr=%quotedAttr:"=\"%

reg add HKEY_CLASSES_ROOT\lsfusion-protocol /t REG_SZ /d "LSFusion custom protocol" /f
reg add HKEY_CLASSES_ROOT\lsfusion-protocol /v "URL Protocol" /t REG_SZ /d "" /f
reg add HKEY_CLASSES_ROOT\lsfusion-protocol\shell /f
reg add HKEY_CLASSES_ROOT\lsfusion-protocol\shell\open /f
reg add HKEY_CLASSES_ROOT\lsfusion-protocol\shell\open\command /t REG_SZ /d "%quotedAttr% \"%%1\"" /f

DEL "%~f0"


