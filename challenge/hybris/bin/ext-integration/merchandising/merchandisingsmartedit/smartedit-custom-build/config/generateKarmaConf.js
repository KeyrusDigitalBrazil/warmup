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
module.exports = function(grunt) {

    const lodash = require('lodash');
    const path = require('path');
    const paths = require('../paths');

    return {
        targets: [
            'generateSmarteditKarmaConf',
            'generateSmarteditContainerKarmaConf',
            'generateSmarteditCommonsKarmaConf'
        ],
        config: function(data, conf) {

            const pathsInBundle = global.smartedit.bundlePaths;
            const karmaCoverageConfig = global.smartedit.taskUtil.karma.coverageConfig;

            const karmaSmartedit = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(pathsInBundle.external.generated.webpack.karmaSmarteditContainer));

            const merchandisingsmartedit = {
                singleRun: true,

                junitReporter: {
                    outputDir: 'jsTarget/tests/merchandisingsmartedit/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'web/features/merchandisingsmarteditcommons/**/*.+(js|ts)',
                        'web/features/merchandisingsmartedit/**/*.+(js|ts)',
                        'jsTarget/web/features/merchandisingsmartedit/templates.js',
                        'jsTests/merchandisingsmartedit/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: lodash.union(conf.generateSmarteditKarmaConf.data.exclude, [
                    'web/features/merchandisingsmartedit/merchandisingsmartedit.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ]),

                webpack: karmaSmartedit,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.smarteditDirName)
            };

            const merchandisingsmarteditContainer = {
                singleRun: true,

                junitReporter: {
                    outputDir: 'jsTarget/tests/merchandisingsmarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditContainerUnitTestFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'web/features/merchandisingsmarteditcommons/**/*.+(js|ts)',
                        'web/features/merchandisingsmarteditContainer/**/*.+(js|ts)',
                        'jsTarget/web/features/merchandisingsmarteditContainer/templates.js',
                        'jsTests/merchandisingsmarteditContainer/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: lodash.union(conf.generateSmarteditContainerKarmaConf.data.exclude, [
                    'jsTarget/web/features/merchandisingsmarteditContainer/merchandisingsmarteditcontainer.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ]),
                webpack: karmaSmarteditContainer,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.smarteditcontainerDirName)
            };

            const merchandisingsmarteditcommons = {
                singleRun: true,

                junitReporter: {
                    outputDir: 'jsTarget/tests/merchandisingsmarteditcommons/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'web/features/merchandisingsmarteditcommons/**/*.+(js|ts)',
                        'jsTarget/web/features/merchandisingsmarteditcommons/templates.js',
                        'jsTests/merchandisingsmarteditcommons/unit/features/**/*.+(js|ts)'
                    ]
                ),

                // list of files to exclude
                exclude: lodash.union(conf.generateSmarteditContainerKarmaConf.data.exclude, [
                    '**/*.d.ts',
                    '*.d.ts'
                ]),

                webpack: karmaSmarteditContainer,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.smarteditcommonsDirName)
            };


            conf.generateSmarteditKarmaConf.data = lodash.merge(merchandisingsmartedit, conf.generateSmarteditKarmaConf.data);
            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(merchandisingsmarteditContainer, conf.generateSmarteditContainerKarmaConf.data);

            // Commons is not available in bundle, lets take a copy of the container config to use for the commons
            conf.generateSmarteditCommonsKarmaConf = {
                dest: pathsInBundle.external.generated.karma.smarteditCommons,
                data: lodash.merge(lodash.cloneDeep(conf.generateSmarteditContainerKarmaConf.data), merchandisingsmarteditcommons)
            };
            conf.generateSmarteditCommonsKarmaConf.data.files = merchandisingsmarteditcommons.files;

            return conf;
        }
    };
};
