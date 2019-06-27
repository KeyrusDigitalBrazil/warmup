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
// TODO: move this in it's own grunt task (e2e_dev)

var fs = require('fs');
var path = require('path');
module.exports = function(bundlePaths) {

    // TODO: parametrize I/O
    const e2ePath = bundlePaths.test.e2e.root;

    function getE2EConfigs() {
        var configDirs = getConfigDirs(e2ePath);
        var configs = [];
        configDirs.forEach(function(configDir) {
            var files = fs.readdirSync(configDir);
            var testFile = files.find(function(file) {
                return file.endsWith('Test.js');
            });
            if (testFile) {
                testFile = testFile.replace(/.js/g, '');
                var contents = fs.readFileSync(configDir + '/config.json');
                try {
                    var testName = configDir.substr(e2ePath.length);
                    configs.push({
                        key: testName + '/' + testFile,
                        data: JSON.parse(contents)
                    });
                } catch (e) {
                    return console.error('config.json is invalid for:', testFile);
                }
            } else {
                throw 'Error: no *Test.js file in ' + configDir;
            }
        });
        return configs;
    }

    function getConfigDirs(dir, filelist) {
        var files = fs.readdirSync(dir);
        filelist = filelist || [];
        files.forEach(function(file) {
            if (fs.statSync(path.join(dir, file)).isDirectory()) {
                filelist = getConfigDirs(path.join(dir, file), filelist);
            } else {
                if (file === 'config.json') {
                    filelist.push(dir);
                }
            }
        });
        return filelist;
    }

    function getTestsListPageHtml(e2eConfigs) {
        var tplFileContent = fs.readFileSync(bundlePaths.test.e2e.listTpl, 'utf8');
        return tplFileContent.replace(/{{items}}/g, JSON.stringify(e2eConfigs));
    }

    function createTestsListPage() {
        var e2eConfigs = getE2EConfigs();
        var htmlContent = getTestsListPageHtml(e2eConfigs);
        var fileName = e2ePath + '/list.html';
        var stream = fs.createWriteStream(fileName);
        stream.once('open', function() {
            stream.end(htmlContent);
        });
    }

    if (fs.existsSync(e2ePath)) {
        createTestsListPage();
    } else {
        console.warn('WARNING: listGenerator: invalid path:', e2ePath);
    }

};
