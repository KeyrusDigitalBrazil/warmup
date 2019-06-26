#!/bin/sh
# Upload Offline NPM Ancillary Module
# Supports: Linux or Mac
# 1. Downloads the npm-ancillary-module artifact from either the release repository or the snapshot repository. 
# 2. Performs "npm install on the npm resource folder"
# 3. Zips the module and uploads to artifactory (release or snapshot)

OS_NAME=$(uname -s)
WORKSPACE=$(pwd)/build

REPOSITORY_ID="hybris-repository"

RELEASE_REPO="https://repository.hybris.com/hybris-release"
SNAPSHOT_REPO="https://repository.hybris.com/hybris-snapshot"

PROJECT_GROUPID="de.hybris.platform"
PROJECT_ARTIFACTID="npm-ancillary-module"
ARTIFACT_VERSION=$1

if [[ "${ARTIFACT_VERSION}" == "" ]] ;
then
    echo "Usage ./upload-offline-npm-ancillary.sh ARTIFACT_VERSION"
    echo "Example: ./upload-offline-npm-ancillary.sh 6.6.0.0-RC4-SNAPSHOT"
    exit -1
fi

if [[ "${ARTIFACT_VERSION}" == *SNAPSHOT ]] ;
then
    TARGET_REPOSITORY=$SNAPSHOT_REPO
else
    TARGET_REPOSITORY=$RELEASE_REPO
fi

NPM_RESOURCE_HOME=${WORKSPACE}/hybris/bin/ext-content/npmancillary/resources/npm

if [ "${OS_NAME}" = "Darwin" ] ; then
    NODE_HOME=${WORKSPACE}/${NPM_RESOURCE_HOME}/node/node-v10.7.0-darwin-x64
    OFFLINE_PROJECT_ARTIFACT_ID=offline-darwin-${PROJECT_ARTIFACTID}
elif [ "${OS_NAME}" = "Linux" ] ; then
    NODE_HOME=${WORKSPACE}/${NPM_RESOURCE_HOME}/node/node-v10.7.0-linux-x64
    OFFLINE_PROJECT_ARTIFACT_ID=offline-linux-${PROJECT_ARTIFACTID}
fi

echo """
Running upload-offline-npm-ancillary.sh

OS_NAME: ${OS_NAME}
WORKSPACE: ${WORKSPACE}

REPOSITORY_ID: ${REPOSITORY_ID}

RELEASE_REPO: ${RELEASE_REPO}
SNAPSHOT_REPO: ${SNAPSHOT_REPO}

PROJECT_GROUPID: ${PROJECT_GROUPID}
PROJECT_ARTIFACTID: ${PROJECT_ARTIFACTID}
ARTIFACT_VERSION: ${ARTIFACT_VERSION}

TARGET_REPOSITORY: ${TARGET_REPOSITORY}

NODE_HOME: ${NODE_HOME}
OFFLINE_PROJECT_ARTIFACT_ID: ${OFFLINE_PROJECT_ARTIFACT_ID}
"""

# ----- main script
# Create workspace and download artifact
rm -rf $WORKSPACE
mkdir -p $WORKSPACE
cd $WORKSPACE

mvn org.apache.maven.plugins:maven-dependency-plugin:2.4:get \
    -Dartifact=${PROJECT_GROUPID}:${PROJECT_ARTIFACTID}:${ARTIFACT_VERSION}:zip \
    -Ddest=${WORKSPACE}/${PROJECT_ARTIFACTID}-${ARTIFACT_VERSION}.zip

unzip ${PROJECT_ARTIFACTID}-${ARTIFACT_VERSION}.zip

#Run npm install from npm resource home folder

export PATH=$PATH:$NODE_HOME
cd ${NPM_RESOURCE_HOME}
npm install

# Go back to workspace folder, zip contents of the original artifact
cd $WORKSPACE
rm ${PROJECT_ARTIFACTID}-${ARTIFACT_VERSION}.zip
zip -r ${OFFLINE_PROJECT_ARTIFACT_ID}-${ARTIFACT_VERSION}.zip .

mvn deploy:deploy-file -DrepositoryId=hybris-repository -Durl=${TARGET_REPOSITORY} -Dfile=$WORKSPACE/${OFFLINE_PROJECT_ARTIFACT_ID}-${ARTIFACT_VERSION}.zip -DgroupId=${PROJECT_GROUPID} -Dversion=${ARTIFACT_VERSION} -DartifactId=${OFFLINE_PROJECT_ARTIFACT_ID} -DgeneratePom=true

# Clean workspace
cd $WORKSPACE/..
rm -rf build
