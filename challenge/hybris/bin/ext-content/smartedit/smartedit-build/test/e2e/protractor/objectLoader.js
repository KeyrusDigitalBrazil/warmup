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
var fs = require('fs');
var path = require('path');
module.exports = function(bundlePaths, ns) {

    function loadDir(directory, suffix) {
        try {
            directory = path.resolve(directory);
            var collection = fs.readdirSync(directory).reduce(function(collection, filename) {
                if (!filename.endsWith(suffix)) {
                    throw "Invalid filename: " + directory + '/' + filename;
                }
                var objectKey = filename.substring(0, filename.length - suffix.length);
                collection[objectKey] = require(directory + '/' + filename);
                return collection;
            }, {});
            return collection;
        } catch (e) {
            console.warn('WARNING: objectLoader unable to load from:', directory);
        }
    }

    // extension specific component/page objects
    ns.componentObjects = loadDir('./jsTests/componentObjects', 'ComponentObject.js');
    ns.pageObjects = loadDir('./jsTests/pageObjects', 'PageObject.js');

    // smartedit bundle component/page objects
    ns.se.componentObjects = loadDir(bundlePaths.test.e2e.componentObjects, 'ComponentObject.js');
    ns.se.pageObjects = loadDir(bundlePaths.test.e2e.pageObjects, 'PageObject.js');
};
