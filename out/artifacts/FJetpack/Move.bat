@echo off
color 0a
mode 25,10
move /Y FJetpack.jar G:\Server\Minecraft\Paper-1.17.1\plugins
IF %ERRORLEVEL% EQU 1 echo this is already moved
timeout 3 > nul