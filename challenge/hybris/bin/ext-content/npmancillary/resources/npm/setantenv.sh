#!/bin/bash 
OWN_NAME=setantenv.sh

if [ "$0" == "./$OWN_NAME" ]; then
	echo * Please call as ". ./$OWN_NAME", not ./$OWN_NAME !!!---
	echo * Also please DO NOT set back the executable attribute
	echo * On this file. It was cleared on purpose.
	
	chmod -x ./$OWN_NAME
	exit
fi
PLATFORM_HOME=`pwd`
export -p PLATFORM_HOME
export -p ANT_OPTS="-Xmx512m -XX:MaxPermSize=256M"
export -p ANT_HOME=$PLATFORM_HOME/apache-ant-1.9.1
chmod +x "$ANT_HOME/bin/ant"
chmod +x "$PLATFORM_HOME/license.sh"
export -p PATH=$ANT_HOME/bin:$PATH

export -p NPM_HOME=$PLATFORM_HOME/npm
export -p NPM_BINARIES_HOME=$NPM_HOME/node_modules

if [ "$(uname)" == "Darwin" ]; then
    export -p NODE_HOME=$NPM_HOME/node/node-v0.12.2-darwin-x64
elif [ "$(expr substr $(uname -s) 1 5)" == "Linux" ]; then
    export -p NODE_HOME=$NPM_HOME/node/node-v0.12.3-linux-x64
fi
export -p PATH=$NPM_BINARIES_HOME/.bin:$NODE_HOME/bin:$PATH

#need be added only with for Unix otherwise windows fails to launch the .exe counterpart
cp $NPM_HOME/chromedriver $NPM_HOME/node_modules/protractor/selenium