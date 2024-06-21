#!/bin/bash

PROJECT_NAME=$1
MODULE_NAME=$2

if [ $# -ne 2 ]
then
  echo "error params"
  exit
fi

scriptDir=$(cd "$(dirname "$0")"; pwd)
echo "full path to currently executed script is : ${scriptDir}"

BASE_DIR=`dirname ${scriptDir}`

cd $BASE_DIR/bin

SERVER_PID=`head -1 pid_$MODULE_NAME.txt`
echo "Now stopping the $PROJECT_NAME $MODULE_NAME app server of pid=$SERVER_PID"
PS_RESULT=`ps -p $SERVER_PID --no-heading`
if [ ! -z "$PS_RESULT" ] ; then
  echo "The server processor of pid=$SERVER_PID is running!!!"
  echo "Now kill -15 $SERVER_PID ..............................."
  kill -15 $SERVER_PID
  WAIT_TIMES=0
  STR=`ps -C java -f --width 1000 | grep $SERVER_PID | grep $MODULE_NAME | grep "$USER" `
  while [ ! -z "$STR" ]
  do
    if [ $WAIT_TIMES -ge 60 ]
    then
        echo "Now kill -9 $SERVER_PID ..............................."
        kill -9 $SERVER_PID
    fi
    sleep 1
    let WAIT_TIMES=WAIT_TIMES+1
    STR=`ps -C java -f --width 1000 | grep $SERVER_PID | grep $MODULE_NAME | grep "$USER" `
  done
  rm -rf pid_$MODULE_NAME.txt
  echo "Success, the $PROJECT_NAME $MODULE_NAME app server of pid=$SERVER_PID is stopped!!!"
else
  rm -rf pid_$MODULE_NAME.txt
  echo "The $PROJECT_NAME $MODULE_NAME app server processor of pid=$SERVER_PID is not running!!!"
fi
