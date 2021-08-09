#!/bin/bash

version='1.0-SNAPSHOT'
suffix='jar-with-dependencies'
prefix='.m2/repository/edu/bjut/'
prog='boudia ciragg homoagg ni'
exec='java'
for i in {1..100}
do
    echo $i
    for x in $prog
    do
        echo $HOME/$prefix$x/$version/$x-$version-$suffix.jar
        java -jar $HOME/$prefix$x/$version/$x-$version-$suffix.jar
    done
done