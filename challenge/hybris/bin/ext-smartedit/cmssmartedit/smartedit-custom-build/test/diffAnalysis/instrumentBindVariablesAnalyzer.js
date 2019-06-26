/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
const fs = require('fs-extra');
const saveResult = require('./utils').saveResult;
const getLines = require('./utils').getLines;
const bundlePaths = global.smartedit.bundlePaths;

function prepareDataObject(fileName, callback) {
    var dataObject = {};
    getLines(fileName).forEach((line) => {
        if (line !== "") {
            try {
                const obj = JSON.parse(line.replace(/\{\{([\w]+)\}\}/, "$1"));
                dataObject[obj.directiveName] = obj ? obj : null;
            } catch (e) {
                console.error("not parsable ", line);
            }
        }
    });

    callback(dataObject);
};

module.exports = {

    execute: function(oldVersion, newVersion) {

        const OLD_VERSION_FILE_NAME = bundlePaths.report.instrument_directives_file.replace("VERSION", oldVersion);
        const NEW_VERSION_FILE_NAME = bundlePaths.report.instrument_directives_file.replace("VERSION", newVersion);

        prepareDataObject(NEW_VERSION_FILE_NAME, function(newVersionObject) {
            prepareDataObject(OLD_VERSION_FILE_NAME, function(oldVersionObject) {
                let notInNewVersionData = "";
                let diffBindVariables = "";
                Object.keys(oldVersionObject).forEach((componentName) => {
                    const oldObj = oldVersionObject[componentName];
                    const newObj = newVersionObject[componentName];

                    if (newObj === undefined) {
                        notInNewVersionData += componentName + ", " + JSON.stringify(oldObj) + "\n\n";
                    } else {
                        const newObjStr = JSON.stringify(newObj);
                        const oldObjStr = JSON.stringify(oldObj);

                        if (newObjStr !== oldObjStr) {
                            diffBindVariables += componentName + "\n" +
                                "   OLD BINDS: " + oldObjStr + "\n" +
                                "   NEW BINDS: " + newObjStr + "\n\n";
                        }
                    }
                });

                const backwardCompatibilityResults = bundlePaths.report.backwardCompatibilityResults;
                fs.mkdirs(backwardCompatibilityResults);
                saveResult(`${backwardCompatibilityResults}/${newVersion}/componentsFrom${oldVersion}NotInNewVersion.data`, "Following components from previous version were not found in new version", notInNewVersionData);
                saveResult(`${backwardCompatibilityResults}/${newVersion}/componentsBindingDiffWith${oldVersion}.data`, "Following components have diff bind variables between both versions", diffBindVariables);
            });
        });

    }
};
