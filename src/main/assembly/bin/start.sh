#!/bin/bash

#JDK ENV
export JAVA_HOME=/app/jdk1.8  #jdk安装目录
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib:$CLASSPATH
export JAVA_PATH=${JAVA_HOME}/bin:${JRE_HOME}/bin
export PATH=$PATH:${JAVA_PATH}

cd $(dirname $0)
BIN_DIR=$(pwd)
cd ..
DEPLOY_DIR=$(pwd)
#jar name 和文件夹同
JAR_NAME="${DEPLOY_DIR##*/}.jar"
CONF_DIR=$DEPLOY_DIR/conf
LIB_JARS=$DEPLOY_DIR/lib/*
# 进程状态
PROCESS=$DEPLOY_DIR/lib/$JAR_NAME
PIDS=$(ps -f | grep java | grep "$PROCESS" | awk '{print $2}')
if [ "$1" = "status" ]; then
  if [ -n "$PIDS" ]; then
    echo "The $JAR_NAME is running...!"
    echo "PID: $PIDS"
    exit 0
  else
    echo "The $JAR_NAME is stopped"
    exit 0
  fi
fi

if [ -n "$PIDS" ]; then
  echo "ERROR: The $JAR_NAME already started!"
  echo "PID: $PIDS"
  exit 1
fi

# 日志记录
LOGS_DIR=$DEPLOY_DIR/logs
if [ ! -d $LOGS_DIR ]; then
  mkdir $LOGS_DIR
fi
STDOUT_FILE=/dev/null

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "

JAVA_MEM_OPTS=""
BITS=$(java -version 2>&1 | grep -i 64-bit)
if [ -n "$BITS" ]; then
  JAVA_MEM_OPTS=" -server -Xmx1024m -Xms1024m -Xmn512m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
else
  JAVA_MEM_OPTS=" -server -Xms512m -Xmx512m -XX:PermSize=1024m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

CLASS_PATH=$CONF_DIR:$LIB_JARS
echo -e "Starting the $SERVER_NAME ...CLASS PATH $CLASS_PATH"
nohup java $JAVA_OPTS $JAVA_MEM_OPTS -jar $DEPLOY_DIR/lib/$JAR_NAME >$STDOUT_FILE 2>&1 &

echo "OK!"
PIDS=$(ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}')
echo "PID: $PIDS"
echo "tail -f $LOGS_DIR/all.log"
