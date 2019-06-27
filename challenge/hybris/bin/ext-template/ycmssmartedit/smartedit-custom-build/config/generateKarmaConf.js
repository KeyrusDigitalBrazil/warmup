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
module.exports = function() {

    return {
        targets: ['generateSmarteditKarmaConf', 'generateSmarteditContainerKarmaConf'],
        config: function(data, conf) {

            const lodash = require('lodash');
            const path = require('path');

            const paths = require('../paths');

            const pathsInBundle = global.smartedit.bundlePaths;
            const karmaSmartedit = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmarteditContainer));

            const cmssmartedit = {
                singleRun: true,

                coverageReporter: {
                    // specify a common output directory
                    dir: 'jsTarget/test/ycmssmartedit/coverage/',
                    reporters: [{
                        type: 'html',
                        subdir: 'report-html'
                    }, {
                        type: 'cobertura',
                        subdir: '.',
                        file: 'cobertura.xml'
                    }]
                },

                junitReporter: {
                    outputDir: 'jsTarget/test/ycmssmartedit/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'jsTests/cmssmartedit/**/*Test.js',
                        {
                            // Images
                            pattern: 'web/webroot/images/**/*',
                            watched: false,
                            included: false,
                            served: true
                        }
                    ]
                ),

                proxies: {
                    '/ycmssmartedit/images/': '/base/images/'
                },

                // list of files to exclude
                exclude: [
                    '**/index.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ],

                webpack: karmaSmartedit
            };


            const cmssmarteditContainer = {
                singleRun: true,

                coverageReporter: {
                    // specify a common output directory
                    dir: 'jsTests/coverage/',
                    reporters: [{
                        type: 'html',
                        subdir: 'report-html'
                    }, {
                        type: 'cobertura',
                        subdir: '.',
                        file: 'cobertura.xml'
                    }, ]
                },

                junitReporter: {
                    outputDir: 'jsTarget/test/ycmssmarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditContainerUnitTestFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'jsTests/cmssmarteditContainer/**/*Test.js',
                        {
                            // Images
                            pattern: 'web/webroot/images/**/*',
                            watched: false,
                            included: false,
                            served: true
                        }
                    ]
                ),

                proxies: {
                    '/ycmssmartedit/images/': '/base/images/'
                },

                // list of files to exclude
                exclude: [
                    '**/index.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ],

                webpack: karmaSmarteditContainer
            };


            conf.generateSmarteditKarmaConf.data = lodash.merge(cmssmartedit, conf.generateSmarteditKarmaConf.data);

            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(cmssmarteditContainer, conf.generateSmarteditContainerKarmaConf.data);


            return conf;
        }
    };
};
