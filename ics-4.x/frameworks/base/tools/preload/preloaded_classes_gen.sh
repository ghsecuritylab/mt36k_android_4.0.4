#!/bin/bash
# Program:
#       This program is to generate new preloaded-classes based on "logcat log"
# History:
# 2012/05/10: v1

ERR_PREFIX="[ERR]"
CMD_PREFIX="[CMD]"

if [ "$1" == "" ]; then
	echo $PREFIX"Usage: preloded_class_gen.sh [file name]"
	exit -1
fi

echo $CMD_PREFIX"check if input-file is existed...."
ls -l $1
if [ $? != 0 ]; then
    echo $ERR_PREFIX"Input file not existed or wrong..."
    exit -1
fi

echo $CMD_PREFIX"Compile java class: Compile.java"

javac Compile.java
if [ $? != 0 ]; then
	echo $ERR_PREFIX"Compiling Compile.java is FAILED !!"
	exit -1
fi

javac WritePreloadedClassFile.java
if [ $? != 0 ]; then
	echo $ERR_PREFIX"Compiling WritePreloadedClassFile is FAILED!!"
	exit -1
fi


echo $CMD_PREFIX"make temp ./frameworks/base directory"
  . logcat.txt ... path: frameworks/base/tools/preload
  mkdir frameworks
  mkdir frameworks/base

echo $CMD_PREFIX"Start Generating preloaded-classes -- Step 1"
java Compile $1 logcat.compiled
if [ $? != 0 ]; then
    echo $ERR_PREFIX"Generating Step 1 FAILED!!"
    exit -1
fi
 
echo $CMD_PREFIX"Start Generating preloaded-classes -- Step 2"
java WritePreloadedClassFile logcat.compiled 0 0
if [ $? != 0 ]; then
    echo $ERR_PREFIX"Generating Step 1 FAILED!!"
    exit -1
fi

echo $CMD_PREFIX"Reserve the original preloaded-classes as 'preloaded-classes.ori" 
cp ../../../../frameworks/base/preloaded-classes ../../../../frameworks/base/preloaded-classes.ori
if [ $? != 0 ]; then
    echo $ERR_PREFIX"Reserve the original preloaded-classes FAILED!!"
    exit -1
fi

echo $CMD_PREFIX"Copy new preloaded-classes to Android path: ../../../../frameworks/base" 
cp ./frameworks/base/preloaded-classes ../../../../frameworks/base
if [ $? != 0 ]; then
    echo $ERR_PREFIX"Copy new preloaded-classes FAILED!!"
    exit -1
fi
  
echo $CMD_PREFIX"Clean all temporary files......"
rm *.class
rm -rf ./frameworks

