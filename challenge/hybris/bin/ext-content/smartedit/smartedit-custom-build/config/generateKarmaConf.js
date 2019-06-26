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
module.exports = function() {

    const lodash = require('lodash');
    const path = require('path');

    return {
        targets: [], // only in bundle
        config: function(data, conf) {

            const paths = require('../paths');

            const pathsInBundle = global.smartedit.bundlePaths;
            const karmaCoverageConfig = global.smartedit.taskUtil.karma.coverageConfig;

            const karmaSmartedit = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmarteditContainer));
            
            const unitSmarteditConf = {
                junitReporter: {
                    outputDir: 'jsTarget/test/smartedit/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml', // if included, results will be saved as $outputDir/$browserName/$outputFile
                    suite: '' // suite will become the package name attribute in xml testsuite element
                },

                // list of files / patterns to load in the browser
                // each file acts as entry point for the webpack configuration
                files: lodash.concat(
                    'node_modules/ckeditor/ckeditor.js',
                    paths.getSmarteditThirdpartiesDevFiles(),
                    global.smartedit.bundlePaths.test.unit.commonUtilModules,
                    'jsTarget/templates.js',
                    'jsTarget/web/app/common/**/*.js',
                    'jsTarget/web/app/smartedit/directives/**/*.js',
                    'jsTarget/web/app/smartedit/services/**/*.js',
                    'jsTarget/web/app/smartedit/modules/**/*.js',
                    'test/unit/smartedit/specBundle.ts',
                    {
                        pattern: 'web/webroot/static-resources/images/**/*',
                        watched: false,
                        included: false,
                        served: true
                    },
					{
						pattern: 'web/webApplicationInjector.js',
						type: 'js',
						watched: false,
						included: false,
						served: true
					}
                ),

                exclude: [
                    'jsTarget/web/app/smartedit/smarteditModule.ts',
                    'jsTarget/web/app/smartedit/partialBackendMocks.js',
                    'jsTarget/web/app/smartedit/smarteditbootstrap.ts',
                    '**/index.ts',
                    '**/*.d.ts'
                ],

                webpack: karmaSmartedit,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.smarteditDirName)
            };

            const unitSmarteditContainer = {
                junitReporter: {
                    outputDir: 'jsTarget/test/smarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml', // if included, results will be saved as $outputDir/$browserName/$outputFile
                    suite: '' // suite will become the package name attribute in xml testsuite element
                },

                // list of files / patterns to load in the browser
                // each file acts as entry point for the webpack configuration
                files: lodash.concat(
                    'node_modules/ckeditor/ckeditor.js',
                    paths.getContainerThirdpartiesDevFiles(),
                    global.smartedit.bundlePaths.test.unit.commonUtilModules,
                    'jsTarget/templates.js',
                    'jsTarget/web/app/common/**/*.js',
                    'jsTarget/web/app/smarteditcontainer/**/*.js',
                    'test/unit/smarteditcontainer/specBundle.ts',
                    {
                        pattern: 'web/webroot/static-resources/images/**/*',
                        watched: false,
                        included: false,
                        served: true
                    }

                ),

                exclude: [
                    '**/index.ts',
                    '**/*.d.ts'
                ],

                webpack: karmaSmarteditContainer,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.smarteditcontainerDirName)
            };
            
            conf.generateSmarteditKarmaConf.data = lodash.merge(unitSmarteditConf, conf.generateSmarteditKarmaConf.data);
            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(unitSmarteditContainer, conf.generateSmarteditContainerKarmaConf.data);
            
            return conf;
        }
    };
};