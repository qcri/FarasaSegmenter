@echo off

set FarasaDataDir=%~dp0..\FarasaData\
REM echo %FarasaDataDir%

set _argcActual=0

if %1 EQU -h (
    goto:ShowUsage
) 

if %1 EQU --help (
    goto:ShowUsage
) 

for %%i in (%*) do set /A _argcActual+=1

if %_argcActual% EQU 0 (

  REM echo call farasa with 0 arg
  java -Dfile.encoding=UTF-8 -jar dist\Farasa.jar 
  goto:_EOF

)

if %_argcActual% EQU 1 (

  REM echo call farasa with one arg ""%1""
  java -Dfile.encoding=UTF-8 -jar dist/Farasa.jar < %1
  goto:_EOF
  
)

if %_argcActual% EQU 2 (

  REM echo call farasa with -i %1 -o %2
  java -Dfile.encoding=UTF-8 -jar dist/Farasa.jar -i %1 -o %2
  goto:_EOF
  
) 


:ShowUsage

echo Farasa - Arabic tokenizer
echo. 
echo SYNOPSIS
echo.  
echo farasa.bat InputFile OutputFile
echo farasa.bat InputFile
echo farasa.bat ^< InputFile 
echo. 
echo arguments:
echo    InputFile   :  Text Input file encoded in UTF-8
echo    OutputFile  :  Output file
echo.
echo DESCRIPTION
echo.
echo This script will run the QCRI Farasa package for tokenizing Arabic text.
echo.
echo AUTHOR
echo. 
echo ^<Kareem Darwish^> (kdarwish@qf.org.qa)
echo. 
echo COPYRIGHT
echo. 
echo Copyright (C) ^<2015^>, QCRI.
echo. 
goto:_EOF


goto :eof


:_EOF

echo.

cmd /c exit 

