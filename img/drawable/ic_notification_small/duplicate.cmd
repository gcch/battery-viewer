@echo off

:: 
:: Copyright (C) 2015 tag
:: 
:: Licensed under the Apache License, Version 2.0 (the "License");
:: you may not use this file except in compliance with the License.
:: You may obtain a copy of the License at
:: 
::      http://www.apache.org/licenses/LICENSE-2.0
:: 
:: Unless required by applicable law or agreed to in writing, software
:: distributed under the License is distributed on an "AS IS" BASIS,
:: WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
:: See the License for the specific language governing permissions and
:: limitations under the License.
:: 

SETLOCAL enabledelayedexpansion

cd %~dp0

set TARGET_FILENAME=ic_notification_small
set TARGET_FILEEXT=.png

set MIN=0
set STEP=1
set MAX=100
set DIGIT=3

set LENGTH=0
set ZEROFILLED_STRING=""



:ProcessBegin
set PROC_COUNTER=%MIN%

:Process
set DIR_NUM=%PROC_COUNTER%

echo String: !DIR_NUM!


echo StringLength
set STRING=%DIR_NUM%
set LENGTH=0
:StringLengthLoop
if not "%STRING%"=="" (
    set STRING=%STRING:~1%
    set /a LENGTH=%LENGTH%+1
    goto :StringLengthLoop
)
echo Length: %LENGTH%


set /a NUM_ZERO=%DIGIT%-%LENGTH%
echo zero: %NUM_ZERO%


set ZEROFILLED_STRING=!DIR_NUM!
for /L %%i in (1, 1, %NUM_ZERO%) do (
	set ZEROFILLED_STRING=0!ZEROFILLED_STRING!
)
echo Zerofill: %ZEROFILLED_STRING%

cp .\%TARGET_FILENAME%%TARGET_FILEEXT% .\%TARGET_FILENAME%_!ZEROFILLED_STRING!%TARGET_FILEEXT%

if %PROC_COUNTER% GEQ %MAX% (
	goto ProcessEnd
)

set /a PROC_COUNTER=%PROC_COUNTER%+%STEP%
goto Process


:ProcessEnd
ENDLOCAL

pause