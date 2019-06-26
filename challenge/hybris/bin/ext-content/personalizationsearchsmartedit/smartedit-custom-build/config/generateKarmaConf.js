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
        targets: [
            'generateSmarteditKarmaConf',
            'generateSmarteditContainerKarmaConf',
            'generateSmarteditCommonsKarmaConf'
        ],
        config: function(data, conf) {

            const lodash = require('lodash');
            const path = require('path');

            const pathsInBundle = global.smartedit.bundlePaths;
            const karmaSmartedit = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmarteditContainer));

            const personalizationsearchsmartedit = {
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
                    }]
                },

                junitReporter: {
                    outputDir: 'jsTarget/test/personalizationsearchsmartedit/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'web/features/personalizationsearchsmarteditcommons/**/*.+(js|ts)',
                        'web/features/personalizationsearchsmartedit/**/*.+(js|ts)',
                        'jsTarget/web/features/personalizationsearchsmartedit/templates.js',
                        'jsTests/personalizationsearchsmartedit/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: [
                    'web/features/personalizationsearchsmartedit/personalizationsearchsmartedit.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ],

                webpack: karmaSmartedit
            };

            const personalizationsearchsmarteditContainer = {
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
                    }]
                },

                junitReporter: {
                    outputDir: 'jsTarget/test/personalizationsearchsmarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditContainerUnitTestFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'web/features/personalizationsearchsmarteditcommons/**/*.+(js|ts)',
                        'web/features/personalizationsearchsmarteditContainer/**/*.+(js|ts)',
                        'jsTarget/web/features/personalizationsearchsmarteditContainer/templates.js',
                        'jsTests/personalizationsearchsmarteditContainer/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: [
                    'web/features/personalizationsearchsmarteditContainer/personalizationsearchsmarteditcontainer.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ],
                webpack: karmaSmarteditContainer
            };

            const personalizationsearchsmarteditcommons = {
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
                    }]
                },

                junitReporter: {
                    outputDir: 'jsTarget/test/personalizationsearchsmarteditcommons/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'web/features/personalizationsearchsmarteditcommons/**/*.+(js|ts)',
                        'jsTarget/web/features/personalizationsearchsmarteditcommons/templates.js',
                        'jsTests/personalizationsearchsmarteditcommons/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: [
                    '**/*.d.ts',
                    '*.d.ts'
                ],

                webpack: karmaSmarteditContainer
            };


            conf.generateSmarteditKarmaConf.data = lodash.merge(personalizationsearchsmartedit, conf.generateSmarteditKarmaConf.data);
            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(personalizationsearchsmarteditContainer, conf.generateSmarteditContainerKarmaConf.data);

            // Commons is not available in bundle, lets take a copy of the container config to use for the commons
            conf.generateSmarteditCommonsKarmaConf = {
                dest: pathsInBundle.external.generated.karma.smarteditCommons,
                data: lodash.merge(lodash.cloneDeep(conf.generateSmarteditContainerKarmaConf.data), personalizationsearchsmarteditcommons)
            };
            conf.generateSmarteditCommonsKarmaConf.data.files = personalizationsearchsmarteditcommons.files;


            return conf;
        }
    };
};
