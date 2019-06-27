BASEDIR=$(dirname "$0")
NODE_HOME=$BASEDIR/node/node-v10.7.0-$1-x64
export PATH=$NODE_HOME/bin:$PATH
rm -rf $NODE_HOME/bin/npm
ln -sf ../lib/node_modules/npm/bin/npm-cli.js $NODE_HOME/bin/npm
chmod -R +x $NODE_HOME
chmod -R +x $BASEDIR
