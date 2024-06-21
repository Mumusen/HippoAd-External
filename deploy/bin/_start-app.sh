#!/bin/bash

PROJECT_NAME=$1
MODULE_NAME=$2
MODULE_VERSION=$3
MODULE_RUNNER=$4

if [ $# -ne 4 ]
then
  echo "error params"
  exit
fi

echo "Now start the $PROJECT_NAME $MODULE_NAME-$MODULE_VERSION app server!!!"

scriptDir=$(cd "$(dirname "$0")"; pwd)
echo "full path to currently executed script is : ${scriptDir}"

BASE_DIR=`dirname $scriptDir`
BIN_DIR=$BASE_DIR/bin
CONF_DIR=$BASE_DIR/conf
LIB_DIR=$BASE_DIR/lib
LOG_DIR=$BASE_DIR/logs
DATA_DIR=$BASE_DIR/data

./_env.sh

export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$CLASSPATH

JAVA_OPTS=" -server -Xms1024m -Xmx1024m -Xmn384m -Xss256k -XX:MetaspaceSize=64m -XX:MaxMetaspaceSize=128m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError "


if [ ! -d $LOG_DIR ] ; then
   mkdir -p $LOG_DIR
fi

if [ ! -d $DATA_DIR ] ; then
   mkdir -p $DATA_DIR
fi

cd $BIN_DIR

PID_FILE=`ls | grep pid_$MODULE_NAME.txt`

if [ ! -z "$PID_FILE" ] ; then
  echo "Oops, pid_$MODULE_NAME.txt is exist, so execute the stop script!!!"
  /bin/bash ./_stop-app.sh $PROJECT_NAME $MODULE_NAME
fi

nohup $JAVA_HOME/bin/java $JAVA_OPTS -Dio.netty.leakDetection.level=SIMPLE -Dnetworkaddress.cache.ttl=30 -Dsun.net.inetaddr.ttl=30 -Dproject.home=$BASE_DIR -Dlog.home=$LOG_DIR/$MODULE_NAME/$MODULE_NAME -Dlogging.config=$CONF_DIR/logback.xml -Dmodule.name=$MODULE_NAME -jar ../lib/$MODULE_NAME-$MODULE_VERSION.jar $MODULE_RUNNER --spring.config.location=$CONF_DIR/$MODULE_NAME.yml 1>$LOG_DIR/stdout_$MODULE_NAME.log 2>>$LOG_DIR/stdout_$MODULE_NAME.log &

echo $! >./pid_$MODULE_NAME.txt
echo "Success, the $PROJECT_NAME $MODULE_NAME-$MODULE_VERSION app server is started and pid=$!"
