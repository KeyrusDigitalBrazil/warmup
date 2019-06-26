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

            const ysmarteditmodule = {
                singleRun: true,

                junitReporter: {
                    outputDir: 'jsTarget/tests/ysmarteditmodule/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'jsTarget/web/features/ysmarteditmodulecommons/**/*.js',
                        'jsTarget/web/features/ysmarteditmodule/**/*.js',
                        'jsTarget/web/features/ysmarteditmodule/templates.js',
                        'jsTests/tests/ysmarteditmodule/unit/specBundle.ts'
                    ]
                ),

                // list of files to exclude
                exclude: lodash.union(conf.generateSmarteditKarmaConf.data.exclude, [
                    'jsTarget/web/features/ysmarteditmodule/ysmarteditmoduleModule.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ]),

                webpack: karmaSmartedit,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.smarteditDirName)
            };

            const ysmarteditmoduleContainer = {
                singleRun: true,

                junitReporter: {
                    outputDir: 'jsTarget/tests/ysmarteditmoduleContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditContainerUnitTestFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'jsTarget/web/features/ysmarteditmodulecommons/**/*.js',
                        'jsTarget/web/features/ysmarteditmoduleContainer/**/*.js',
                        'jsTarget/web/features/ysmarteditmoduleContainer/templates.js',
                        'jsTests/tests/ysmarteditmoduleContainer/unit/specBundle.ts'
                    ]
                ),

                // list of files to exclude
                exclude: lodash.union(conf.generateSmarteditContainerKarmaConf.data.exclude, [
                    'jsTarget/web/features/ysmarteditmoduleContainer/ysmarteditmodulecontainerModule.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ]),
                webpack: karmaSmarteditContainer,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.smarteditcontainerDirName)
            };

            const ysmarteditmodulecommons = {
                singleRun: true,

                junitReporter: {
                    outputDir: 'jsTarget/tests/ysmarteditmodulecommons/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: lodash.concat(
                    pathsInBundle.test.unit.smarteditThirdPartyJsFiles,
                    pathsInBundle.test.unit.commonUtilModules, [
                        'jsTarget/web/features/ysmarteditmodulecommons/**/*.js',
                        'jsTarget/web/features/ysmarteditmodulecommons/templates.js',
                        'jsTests/tests/ysmarteditmodulecommons/unit/specBundle.ts'
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


            conf.generateSmarteditKarmaConf.data = lodash.merge(ysmarteditmodule, conf.generateSmarteditKarmaConf.data);
            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(ysmarteditmoduleContainer, conf.generateSmarteditContainerKarmaConf.data);

            // Commons is not available in bundle, lets take a copy of the container config to use for the commons
            conf.generateSmarteditCommonsKarmaConf = {
                dest: pathsInBundle.external.generated.karma.smarteditCommons,
                data: lodash.merge(lodash.cloneDeep(conf.generateSmarteditContainerKarmaConf.data), ysmarteditmodulecommons)
            };
            conf.generateSmarteditCommonsKarmaConf.data.files = ysmarteditmodulecommons.files;

            return conf;
        }
    };
};
