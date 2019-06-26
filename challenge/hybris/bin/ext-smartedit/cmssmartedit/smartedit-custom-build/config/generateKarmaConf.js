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
        targets: ['generateSmarteditKarmaConf', 'generateSmarteditContainerKarmaConf'],
        config: function(data, conf) {

            const paths = require('../../jsTests/paths');

            const karmaCoverageConfig = global.smartedit.taskUtil.karma.coverageConfig;

            const karmaSmartedit = require(path.resolve(global.smartedit.bundlePaths.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(global.smartedit.bundlePaths.external.generated.webpack.karmaSmarteditContainer));

            const cmssmartedit = {
                singleRun: true,
                junitReporter: {
                    outputDir: 'jsTarget/test/cmssmartedit/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                files: paths.cmssmarteditKarmaConfFiles,

                proxies: {
                    '/cmssmartedit/images/': '/base/images/'
                },

                exclude: lodash.union(conf.generateSmarteditKarmaConf.data.exclude, [
                    '**/requireLegacyJsFiles.js',
                    '**/cmssmarteditAppModule.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ]),

                webpack: karmaSmartedit,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.cmssmarteditDirName)
            };


            const cmssmarteditContainer = {
                singleRun: true,
                junitReporter: {
                    outputDir: 'jsTarget/test/cmssmarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: paths.cmssmarteditContainerKarmaConfFiles,

                proxies: {
                    '/cmssmartedit/images/': '/base/images/'
                },

                exclude: lodash.union(conf.generateSmarteditContainerKarmaConf.data.exclude, [
                    '**/requireLegacyJsFiles.js',
                    '**/cmssmarteditContainerAppModule.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ]),

                webpack: karmaSmarteditContainer,
                coverageIstanbulReporter: karmaCoverageConfig.config(paths.coverage.dir, paths.coverage.cmssmarteditcontainerDirName)
            };


            conf.generateSmarteditKarmaConf.data = lodash.merge(cmssmartedit, conf.generateSmarteditKarmaConf.data);

            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(cmssmarteditContainer, conf.generateSmarteditContainerKarmaConf.data);

            return conf;
        }
    };
};
