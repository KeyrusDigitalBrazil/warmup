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
module.exports = function(grunt) {

    /**
     * @ngdoc overview
     * @name generateE2eListHtml(T)
     * @description
     *
     * # generateE2eListHtml Task
     * generateE2eListHtml is a task that creates the list.html file used for e2e local testing. It contains links to each e2e test available in the extension.
     *
     * # Configuration
     * ```js
     *  root: string // path to the e2e root folder
     *  tpl: string // path to the list.html.tpl template file
     *  dest: string // the output path/filename to write the list html file
     * ```
     *
     */

    var fs = require('fs');
    var path = require('path');

    const taskName = 'generateE2eListHtml';
    const taskDescription = "Generates a list.html file for e2e local testing";

    const CONFIG_JSON_FILENAME = 'config.json';

    function getE2EConfigs(root) {
        var configDirs = getConfigDirs(root);
        var configs = [];
        configDirs.forEach(function(configDir) {
            var files = fs.readdirSync(configDir);
            var testFile = files.find(function(file) {
                return file.endsWith('Test.js');
            });
            if (testFile) {
                testFile = testFile.replace(/.js/g, '');
                var contents = fs.readFileSync(configDir + '/' + CONFIG_JSON_FILENAME);
                try {
                    var testName = configDir.substr(root.length);
                    configs.push({
                        key: testName + '/' + testFile,
                        data: JSON.parse(contents)
                    });
                } catch (e) {
                    return grunt.fail.warn(CONFIG_JSON_FILENAME + ' is invalid for:', testFile);
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
                if (file === CONFIG_JSON_FILENAME) {
                    filelist.push(dir);
                }
            }
        });
        return filelist;
    }

    function getTestsListPageHtml(tpl, e2eConfigs) {
        var tplFileContent = fs.readFileSync(tpl, 'utf8');
        return tplFileContent.replace(/{{items}}/g, JSON.stringify(e2eConfigs));
    }

    function createTestsListPage(tpl, root, dest) {
        var e2eConfigs = getE2EConfigs(root);
        var htmlContent = getTestsListPageHtml(tpl, e2eConfigs);
        var stream = fs.createWriteStream(dest);
        stream.once('open', function() {
            stream.end(htmlContent);
        });
    }

    grunt.registerTask(taskName, taskDescription, function() {
        var gruntConfig = grunt.config.get(taskName);
        if (fs.existsSync(gruntConfig.root)) {
            createTestsListPage(gruntConfig.tpl, gruntConfig.root, gruntConfig.dest);
            grunt.log.ok();
        } else {
            grunt.fail.warn(`${taskName} grunt task: invalid path: ${gruntConfig.root}`);
        }
    });
};
