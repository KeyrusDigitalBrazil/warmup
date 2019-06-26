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

    /**
     * @ngdoc overview
     * @name generateWebpackConfig(C)
     * @description
     * # generateWebpackConfig Configuration
     * The default generateWebpackConfig configuration provides the following targets:
     * - generateProdSmarteditWebpackConfig
     * - generateProdSmarteditContainerWebpackConfig
     * - generateDevSmarteditWebpackConfig
     * - generateDevSmarteditContainerWebpackConfig
     * - generateKarmaSmarteditWebpackConfig
     * - generateKarmaSmarteditContainerWebpackConfig
     * - generateSharedSmarteditForTestsWebpackConfig
     *
     * The webpack targets correspond the {@link generateTsConfig(C) tsConfig targets}
     *
     * For the default output file locations, see bundlePaths.external.generated.webpack
     *
     */

    return {
        targets: [
            'generateProdSmarteditWebpackConfig',
            'generateProdSmarteditContainerWebpackConfig',
            'generateDevSmarteditWebpackConfig',
            'generateDevSmarteditContainerWebpackConfig',
            'generateKarmaSmarteditWebpackConfig',
            'generateKarmaSmarteditContainerWebpackConfig',
            'generateSharedSmarteditForTestsWebpackConfig'
        ],
        config: function(data, conf) {

            const paths = global.smartedit.bundlePaths;
            const lodash = require('lodash');
            const path = require('path');

            const webpackConfigTemplates = require('../templates').webpackConfigTemplates;
            const webpackUtil = global.smartedit.taskUtil.webpack;

            // ======== PROD ========
            conf.generateProdSmarteditWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.prodSmartedit,
                dest: paths.external.generated.webpack.prodSmartedit,
                data: lodash.cloneDeep(webpackConfigTemplates.prodWebpackConfig)
            };
            conf.generateProdSmarteditContainerWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.prodSmarteditContainer,
                dest: paths.external.generated.webpack.prodSmarteditContainer,
                data: lodash.cloneDeep(webpackConfigTemplates.prodWebpackConfig)
            };

            webpackUtil.addMinimizer(conf.generateProdSmarteditWebpackConfig.data, webpackUtil.uglifyJsPlugin);
            webpackUtil.addMinimizer(conf.generateProdSmarteditContainerWebpackConfig.data, webpackUtil.uglifyJsPlugin);
            webpackUtil.addPlugin(conf.generateProdSmarteditWebpackConfig.data, webpackUtil.ngAnnotatePlugin);
            webpackUtil.addPlugin(conf.generateProdSmarteditContainerWebpackConfig.data, webpackUtil.ngAnnotatePlugin);

            // ======== DEV ========
            conf.generateDevSmarteditWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.devSmartedit,
                dest: paths.external.generated.webpack.devSmartedit,
                data: lodash.cloneDeep(webpackConfigTemplates.devWebpackConfig)
            };
            conf.generateDevSmarteditContainerWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.devSmarteditContainer,
                dest: paths.external.generated.webpack.devSmarteditContainer,
                data: lodash.cloneDeep(webpackConfigTemplates.devWebpackConfig)
            };
            webpackUtil.addPlugin(conf.generateDevSmarteditWebpackConfig.data, webpackUtil.ngAnnotatePlugin);
            webpackUtil.addPlugin(conf.generateDevSmarteditContainerWebpackConfig.data, webpackUtil.ngAnnotatePlugin);

            let testCommons = lodash.cloneDeep(webpackConfigTemplates.devWebpackConfig);
            testCommons.resolve.modules.push(paths.test.unit.root);
            testCommons.resolve.alias = testCommons.resolve.alias || {};
            testCommons.resolve.alias.testhelpers = paths.test.unit.root;

            // ======== SHARED SMARTEDIT FOR TESTS ========
            conf.generateSharedSmarteditForTestsWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.smarteditForTests,
                dest: paths.external.generated.webpack.smarteditForTests,
                data: lodash.cloneDeep(webpackConfigTemplates.devWebpackConfig)
            };
            conf.generateSharedSmarteditForTestsWebpackConfig.data.resolve.alias = conf.generateSharedSmarteditForTestsWebpackConfig.data.resolve.alias || {};
            conf.generateSharedSmarteditForTestsWebpackConfig.data.resolve.alias.testhelpers = paths.test.unit.root;

            // ======== KARMA ========
            conf.generateKarmaSmarteditWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.karmaSmartedit,
                dest: paths.external.generated.webpack.karmaSmartedit,
                data: lodash.cloneDeep(testCommons)
            };
            conf.generateKarmaSmarteditContainerWebpackConfig = {
                awesomeTsConfigFile: paths.external.generated.tsconfig.karmaSmarteditContainer,
                dest: paths.external.generated.webpack.karmaSmarteditContainer,
                data: lodash.cloneDeep(testCommons)
            };
            webpackUtil.addLoader(conf.generateKarmaSmarteditWebpackConfig.data, webpackUtil.istanbulInstrumenterLoader);
            webpackUtil.addLoader(conf.generateKarmaSmarteditContainerWebpackConfig.data, webpackUtil.istanbulInstrumenterLoader);
            webpackUtil.addPlugin(conf.generateKarmaSmarteditWebpackConfig.data, webpackUtil.ngAnnotatePlugin);
            webpackUtil.addPlugin(conf.generateKarmaSmarteditContainerWebpackConfig.data, webpackUtil.ngAnnotatePlugin);

            return conf;
        }
    };
};
