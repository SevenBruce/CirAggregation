@echo off 
setlocal enabledelayedexpansion 
set topic[0]=boudia\target\boudia-1.0-SNAPSHOT-jar-with-dependencies.jar 
set topic[1]=ciragg\target\ciragg-1.0-SNAPSHOT-jar-with-dependencies.jar
set topic[2]=homoagg\target\homoagg-1.0-SNAPSHOT-jar-with-dependencies.jar
set topic[3]=ni\target\ni-1.0-SNAPSHOT-jar-with-dependencies.jar

set command=java -jar --illegal-access=deny

for /l %%i in (0, 1, 99) do (
    for /l %%n in (0,1,3) do ( 
        %command% !topic[%%n]! 
    )
)