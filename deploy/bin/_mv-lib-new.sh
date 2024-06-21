#!/bin/bash

if [ ! -d ../lib.old ] ; then
   mkdir -p ../lib.old
fi

mv -f ../lib/* ../lib.old
mv -f ../lib.new/* ../lib
