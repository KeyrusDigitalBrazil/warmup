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
module.exports = function() {

    return {
        targets: ['generateSmarteditKarmaConf', 'generateSmarteditContainerKarmaConf'],
        config: function(data, conf) {

            const lodash = require('lodash');
            const path = require('path');

            const paths = require('../../jsTests/paths');

            const karmaSmartedit = require(path.resolve(global.smartedit.bundlePaths.external.generated.webpack.karmaSmartedit));
            const karmaSmarteditContainer = require(path.resolve(global.smartedit.bundlePaths.external.generated.webpack.karmaSmarteditContainer));

            const personalizationpromotionssmartedit = {
                singleRun: true,

                coverageReporter: {
                    // specify a common output directory
                    dir: 'jsTarget/test/personalizationpromotionssmartedit/coverage/',
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
                    outputDir: 'jsTarget/test/personalizationpromotionssmartedit/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                files: paths.getPersonalizationpromotionssmarteditKarmaConfFiles(),

                proxies: {
                    '/personalizationpromotionssmartedit/images/': '/base/images/'
                },

                // list of files to exclude
                exclude: lodash.union(conf.generateSmarteditKarmaConf.data.exclude, [
                    '**/requireLegacyJsFiles.js',
                    '**/PersonalizationpromotionssmarteditApp.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ]),

                webpack: karmaSmartedit
            };


            const personalizationpromotionssmarteditContainer = {
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
                    outputDir: 'jsTarget/test/personalizationpromotionssmarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
                    outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
                },

                // list of files / patterns to load in the browser
                files: paths.getPersonalizationpromotionssmarteditContainerKarmaConfFiles(),

                proxies: {
                    '/personalizationpromotionssmartedit/images/': '/base/images/'
                },

                // list of files to exclude
                exclude: lodash.union(conf.generateSmarteditKarmaConf.data.exclude, [
                    '**/requireLegacyJsFiles.js',
                    '**/PersonalizationpromotionssmarteditContainerApp.ts',
                    '**/*.d.ts',
                    '*.d.ts'
                ]),

                webpack: karmaSmarteditContainer
            };


            conf.generateSmarteditKarmaConf.data = lodash.merge(personalizationpromotionssmartedit, conf.generateSmarteditKarmaConf.data);

            conf.generateSmarteditContainerKarmaConf.data = lodash.merge(personalizationpromotionssmarteditContainer, conf.generateSmarteditContainerKarmaConf.data);


            return conf;
        }
    };
};
