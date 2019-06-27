/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
/* jshint unused:false, undef:false */
var fs = require('fs');
var path = require('path');
var PATHS = require('./paths.js');
module.exports = function() {

    function loadDir(directory, suffix) {

        var dir = path.resolve(directory);
        var collection = fs.readdirSync(dir).reduce(function(collection, filename) {
            // standards protection
            if (!filename.endsWith(suffix)) {
                throw "Invalid filename: " + dir + '/' + filename;
            }
            var objectKey = filename.substring(0, filename.length - suffix.length);
            var module = require(dir + '/' + filename);
            collection[objectKey] = module;
            return collection;
        }, {});
        return collection;
    }

    function checkFileNames(directory, suffix) {
        var dir = path.resolve(directory);
        fs.readdirSync(dir).forEach(function(filename) {
            // standards protection
            if (!filename.endsWith(suffix)) {
                throw "Invalid filename: " + dir + '/' + filename;
            }
        });
    }

    function getFeatures(done) {
        return browser.get('jsTests/tests/personalizationpromotionssmarteditContainer/e2e/features/app.html').then(function() {
            if (done) {
                done();
            }
        });
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

    function getE2EConfigs() {
        var configDirs = getConfigDirs(PATHS.tests.personalizationpromotionssmarteditContainere2eTestsRoot);
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
                    var path = configDir.substr(PATHS.tests.personalizationpromotionssmarteditContainere2eTestsRoot.length);
                    configs.push({
                        key: path + '/' + testFile,
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

    function getTestsListPageHtml(e2eConfigs) {
        var tplFileContent = fs.readFileSync(PATHS.tests.personalizationpromotionssmarteditContainere2eTestsRoot + '/features/list.html.tpl', 'utf8');
        return tplFileContent.replace(/{{items}}/g, JSON.stringify(e2eConfigs));
    }

    function createTestsListPage() {
        var e2eConfigs = getE2EConfigs();
        var htmlContent = getTestsListPageHtml(e2eConfigs);
        var fileName = PATHS.tests.personalizationpromotionssmarteditContainere2eTestsRoot + '/features/list.html';
        var stream = fs.createWriteStream(fileName);
        stream.once('open', function() {
            stream.end(htmlContent);
        });
    }

    return {
        loadE2eDependencies: function(browser, dependencyContainer) {

            createTestsListPage();

            var fakeAngularPage = '/smartedit-build/test/e2e/dummystorefront/fakeAngularEmptyPage.html';

            dependencyContainer.componentObjects = loadDir(PATHS.testObjects.componentObjectsRoot, 'ComponentObject.js');
            dependencyContainer.pageObjects = loadDir(PATHS.testObjects.pageObjectsRoot, 'PageObject.js');

            browser.setStorefrontDelayConfigInSessionStorage = function() {
                browser.get(fakeAngularPage);
                //     browser.executeScript('window.sessionStorage.setItem("STOREFRONT_DELAY_STRATEGY", arguments[0])', delayConfig);
            };

            /* save in local storage files that will be bootstrapped into main app by 
             * /personalizationpromotionssmartedit/jsTests/tests/personalizationpromotionssmarteditContainer/e2e/features/util/commonMockedModule/configurationMocks.js
             */
            browser.bootstrap = function(specDir, done) {

                var config = null;
                if (specDir) {
                    try {
                        config = require(specDir + '/config.json');
                    } catch (e) {
                        console.error(e);
                    }
                }

                browser.executeScript('window.sessionStorage.removeItem("additionalTestJSFiles")');

                if (config) {
                    return browser.get(fakeAngularPage).then(function() {
                        if (config.jsFiles) {
                            browser.executeScript('window.sessionStorage.setItem("additionalTestJSFiles", arguments[0])', JSON.stringify(config.jsFiles));
                        }
                        return getFeatures(done);
                    });
                } else {
                    return getFeatures(done);
                }
            };
        },

        checkUnitDependencyFileNames: function() {
            //            checkFileNames(PATHS.mocks.dataRoot, 'MockData.js');
            //            checkFileNames(PATHS.mocks.daoRoot, 'MockDao.js');
            //            checkFileNames(PATHS.mocks.serviceRoot, 'MockService.js');
        }

    };
}();
