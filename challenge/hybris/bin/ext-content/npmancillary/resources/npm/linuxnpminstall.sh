BASEDIR=$(dirname "$0")
export PATH=$BASEDIR/node/node-v10.7.0-linux-x64/bin:$PATH
rm -rf $BASEDIR/node_modules
cd $BASEDIR
npm cache clean --force
npm install