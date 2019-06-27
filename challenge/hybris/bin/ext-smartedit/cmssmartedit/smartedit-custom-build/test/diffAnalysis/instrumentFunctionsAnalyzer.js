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
/* jshint esversion: 6 */
const _ = require('lodash');
const fs = require('fs-extra');
const diff = require('deep-diff').diff;
const saveResult = require('./utils').saveResult;
const getLines = require('./utils').getLines;
const squashArraysWithin = require('./utils').squashArraysWithin;
const removeDuplicatesAndMerge = require('./utils').removeDuplicatesAndMerge;
const bundlePaths = global.smartedit.bundlePaths;

/**
 * Returns a map of array of arguments/results from invocations identified by a unique composite key made of the properties passed to keyPropertyList
 * {
 *  someSignatureKey:[{arguments, result}, {arguments, result}]
 *  someotherSignatureKey:[{arguments, result}, {arguments, result}]
 * }
 * arrays in arguments are reduced to just one element being a merging of the array content
 * @param {*} fileName 
 * @param {*} keyPropertyList 
 * @param {*} callback 
 */
function prepareDataObject(fileName, keyPropertyList, callback) {
    const dataObject = {};

    getLines(fileName).forEach((line) => {
        if (line !== "") {
            let sanitizedLine;
            try {

                sanitizedLine = line.replace(/\{\{([\w]+)\}\}/, "$1");
                const obj = JSON.parse(sanitizedLine);

                if (obj.arguments.length) {
                    /*
                     * squashArraysWithin assuming homogeneous types throughout an array
                     * which is not the case for the arguments object
                     */
                    obj.arguments = obj.arguments.map((arg) => squashArraysWithin(arg));
                } else {
                    obj.arguments = [];
                }

                obj.result = obj.result ? squashArraysWithin(obj.result) : null;

                const key = keyPropertyList.map(function(propertyName) {
                    return obj[propertyName];
                }).join("-");

                if (dataObject[key] === undefined) {
                    dataObject[key] = {
                        list: []
                    };
                    keyPropertyList.forEach(function(propertyName) {
                        dataObject[key][propertyName] = obj[propertyName];
                    });
                }
                dataObject[key].list.push({
                    arguments: obj.arguments,
                    result: obj.result
                });
            } catch (e) {
                console.log('Error while reading data', e);
            }
        }
    });

    callback(dataObject);
}

/**
 * if any, return all structural differences between these 2 objects
 */
function getSignaturesDiffElements(oldArgumentsOrResult, newArgumentsOrResult) {


    if (JSON.stringify(oldArgumentsOrResult) === JSON.stringify(newArgumentsOrResult)) {
        return [];
    } else {
        return _.sortBy(diff(oldArgumentsOrResult, newArgumentsOrResult), (el) => el.kind);
    }
}

function formatSignaturesDiffElements(signaturesDiffElements) {
    return signaturesDiffElements.map((delta) => {
        return JSON.stringify(delta);
    }).join("\n");
}

/**
 * if any, return a string of arguments or return-value structural differences between an old and a new method signature
 */
function iterateAndCompareSignatures(oldSignatureObject, newSignatureObject, attributeWithSignatures) {

    const oldArgumentsOrResult = removeDuplicatesAndMerge(oldSignatureObject.list, attributeWithSignatures);

    const newArgumentsOrResult = removeDuplicatesAndMerge(newSignatureObject.list, attributeWithSignatures);

    const signaturesDiffElements = getSignaturesDiffElements(oldArgumentsOrResult, newArgumentsOrResult);

    if (signaturesDiffElements.length) {
        return oldSignatureObject.serviceName + ", " + oldSignatureObject.functionName + "\n" +
            (oldSignatureObject.arguments ? ("arguments: " + JSON.stringify(oldSignatureObject.arguments) + "\n") : "") +
            formatSignaturesDiffElements(signaturesDiffElements) + "\n\n";
    } else {
        return "";
    }
}

module.exports = {

    execute: function(oldVersion, newVersion) {

        const OLD_VERSION_FILE_NAME = bundlePaths.report.instrument_functions_file.replace("VERSION", oldVersion);
        const NEW_VERSION_FILE_NAME = bundlePaths.report.instrument_functions_file.replace("VERSION", newVersion);

        prepareDataObject(NEW_VERSION_FILE_NAME, ["serviceName", "functionName"], function(newVersionObject) {
            prepareDataObject(OLD_VERSION_FILE_NAME, ["serviceName", "functionName"], function(oldVersionObject) {

                let notInNewVersionData = "";
                let diffArgumentsData = "";

                Object.keys(oldVersionObject).forEach((key) => {
                    const oldSignatureObject = oldVersionObject[key];
                    const newSignatureObject = newVersionObject[key];
                    if (newSignatureObject === undefined) {
                        notInNewVersionData = notInNewVersionData + oldSignatureObject.serviceName + ", " + oldSignatureObject.functionName + "\n\n";
                    } else {

                        diffArgumentsData += iterateAndCompareSignatures(oldSignatureObject, newSignatureObject, "arguments");

                    }
                });

                const backwardCompatibilityResults = bundlePaths.report.backwardCompatibilityResults;
                fs.mkdirs(backwardCompatibilityResults);
                saveResult(`${backwardCompatibilityResults}/${newVersion}/functionsFrom${oldVersion}NotInNewVersion.data`, "Following requests from previous version were not found in new version", notInNewVersionData);
                saveResult(`${backwardCompatibilityResults}/${newVersion}/functionsArgumentsDiffWith${oldVersion}.data`, "Following requests have different arguments between both versions", diffArgumentsData);
            });
        });

        prepareDataObject(NEW_VERSION_FILE_NAME, ["serviceName", "functionName", "arguments"], function(newVersionObject) {
            prepareDataObject(OLD_VERSION_FILE_NAME, ["serviceName", "functionName", "arguments"], function(oldVersionObject) {

                let diffResultData = "";

                Object.keys(oldVersionObject).forEach((key) => {
                    const oldSignatureObject = oldVersionObject[key];
                    const newSignatureObject = newVersionObject[key];
                    if (newSignatureObject !== undefined) {
                        diffResultData += iterateAndCompareSignatures(oldSignatureObject, newSignatureObject, "result");
                    }
                });
                const backwardCompatibilityResults = bundlePaths.report.backwardCompatibilityResults;
                fs.mkdirs(backwardCompatibilityResults);
                saveResult(`${backwardCompatibilityResults}/${newVersion}/functionsReturnsDiffWith${oldVersion}.data`, "Following requests have different results from a version to another", diffResultData);
            });
        });
    }
};
