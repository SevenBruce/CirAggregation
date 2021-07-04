#!/bin/bash

version='1.0-SNAPSHOT'
suffix='jar-with-dependencies'
prefix='.m2/repository/edu/bjut/'
prog='boudia ciragg homoagg ni'
exec='java'
for x in $prog
do
    # java -jar $prefix$x/$version/$x-$version-$suffix.jar
    # java -jar $f
    echo $HOME/$prefix$x/$version/$x-$version-$suffix.jar
    java -jar $HOME/$prefix$x/$version/$x-$version-$suffix.jar
done