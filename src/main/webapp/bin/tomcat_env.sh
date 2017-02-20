#!/bin/bash

export MODULE_NAME=$(echo $DEPLOY_DIR | awk -F'/' '{print $NF}')
export CATALINA_HOME=/home/work/apache-tomcat-7.0.${MODULE_NAME}
export CATALINA_BASE=/home/work/apache-tomcat-7.0.${MODULE_NAME}

CATALINA_OPTS="-Djava.util.logging.config.file=${CATALINA_HOME}/conf/logging.properties -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djava.endorsed.dirs=${CATALINA_HOME}/endorsed -Dcatalina.base=${CATALINA_BASE} -Dcatalina.home=${CATALINA_HOME}"
MAIN_CLASS=org.apache.catalina.startup.Bootstrap
