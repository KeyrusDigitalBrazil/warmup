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
     * @name generateTsConfig(C)
     * @description
     * # generateTsConfig Configuration
     * The default generateTsConfig configuration provides the following targets:
     * - generateProdSmarteditTsConfig
     * - generateProdSmarteditContainerTsConfig
     * - generateDevSmarteditTsConfig
     * - generateDevSmarteditContainerTsConfig
     * - generateKarmaSmarteditTsConfig
     * - generateKarmaSmarteditContainerTsConfig
     * - generateIDETsConfig
     * - generateSharedSmarteditForTestsTsConfig
     *
     * The dev types are a super set of the prod types, and include typing such as jasmine, or other test related types.
     * The smartedit and smarteditContainer also have isolated types, but share the common types.
     * The IDE target has all the types.
     *
     * For the default output file locations, see bundlePaths.external.generated.tsconfig
     *
     */

    return {
        targets: [
            'generateProdSmarteditTsConfig',
            'generateProdSmarteditContainerTsConfig',
            'generateDevSmarteditTsConfig',
            'generateDevSmarteditContainerTsConfig',
            'generateKarmaSmarteditTsConfig',
            'generateKarmaSmarteditContainerTsConfig',
            'generateIDETsConfig',
            'generateSharedSmarteditForTestsTsConfig'
        ],
        config: function(data, conf) {
            const paths = global.smartedit.bundlePaths;
            const lodash = require('lodash');
            const tsConfigTemplates = require('../templates').tsConfigTemplates;

            const smarteditPaths = {
                "smarteditcommons": [paths.bundleRoot + "/@types/smarteditcommons"],
                "smartedit": [paths.bundleRoot + "/@types/smartedit"]
            };

            const smarteditContainerPaths = {
                "smarteditcommons": [paths.bundleRoot + "/@types/smarteditcommons"],
                "smarteditcontainer": [paths.bundleRoot + "/@types/smarteditcontainer"]
            };

            const testpaths = {
                "testhelpers": [paths.test.unit.root],
                "testhelpers/*": [paths.test.unit.root + "/*"]
            };

            function addSmartEditPaths(conf) {
                conf.compilerOptions.paths = lodash.cloneDeep(smarteditPaths);
                conf.compilerOptions.typeRoots = lodash.union(conf.compilerOptions.typeRoots, [
                    paths.bundleRoot + "/@types",
                    "!" + paths.bundleRoot + "/@types/smarteditcontainer"
                ]);
            }

            function addSmartEditContainerPaths(conf) {
                conf.compilerOptions.paths = lodash.cloneDeep(smarteditContainerPaths);
                conf.compilerOptions.typeRoots = lodash.union(conf.compilerOptions.typeRoots, [
                    paths.bundleRoot + "/@types",
                    "!" + paths.bundleRoot + "/@types/smartedit"
                ]);
            }

            function addTestPaths(conf) {
                conf.compilerOptions.typeRoots = lodash.union(conf.compilerOptions.typeRoots, [
                    paths.bundleRoot + "/test/@types"
                ]);

                conf.compilerOptions.paths = lodash.merge(conf.compilerOptions.paths, testpaths);
            }

            function addAllPaths(conf) {
                conf.compilerOptions.paths = lodash.merge(lodash.cloneDeep(smarteditPaths), lodash.cloneDeep(smarteditContainerPaths));

                conf.compilerOptions.typeRoots = lodash.union(conf.compilerOptions.typeRoots, [
                    paths.bundleRoot + "/@types",
                    paths.bundleRoot + "/test/@types"
                ]);

                conf.compilerOptions.paths = lodash.merge(conf.compilerOptions.paths, testpaths);

            }

            function getIDETsConfig() {
                var conf = {
                    dest: paths.external.generated.tsconfig.ide,
                    data: lodash.cloneDeep(tsConfigTemplates.ide)
                };

                conf.data.compilerOptions.baseUrl = '../../';
                conf.data.compilerOptions.typeRoots = ['node_modules/@types'];

                addAllPaths(conf.data);

                return conf;
            }

            // ====== PROD ======
            conf.generateProdSmarteditTsConfig = {
                dest: paths.external.generated.tsconfig.prodSmartedit,
                data: lodash.cloneDeep(tsConfigTemplates.prodSmartedit)
            };

            conf.generateProdSmarteditContainerTsConfig = {
                dest: paths.external.generated.tsconfig.prodSmarteditContainer,
                data: lodash.cloneDeep(tsConfigTemplates.prodSmarteditContainer)
            };

            // ====== DEV ======
            conf.generateDevSmarteditTsConfig = {
                dest: paths.external.generated.tsconfig.devSmartedit,
                data: lodash.cloneDeep(tsConfigTemplates.devSmartedit)
            };
            conf.generateDevSmarteditContainerTsConfig = {
                dest: paths.external.generated.tsconfig.devSmarteditContainer,
                data: lodash.cloneDeep(tsConfigTemplates.devSmarteditContainer)
            };

            // ====== Karma ======
            conf.generateKarmaSmarteditTsConfig = {
                dest: paths.external.generated.tsconfig.karmaSmartedit,
                data: lodash.cloneDeep(tsConfigTemplates.karmaSmartedit)
            };

            conf.generateKarmaSmarteditContainerTsConfig = {
                dest: paths.external.generated.tsconfig.karmaSmarteditContainer,
                data: lodash.cloneDeep(tsConfigTemplates.karmaSmarteditContainer)
            };

            // ====== IDE ======
            conf.generateIDETsConfig = getIDETsConfig(conf.generateIDETsConfig);

            // ======== SHARED SMARTEDIT FOR TESTS ========
            conf.generateSharedSmarteditForTestsTsConfig = {
                dest: paths.external.generated.tsconfig.smarteditForTests,
                data: lodash.cloneDeep(tsConfigTemplates.devSmarteditContainer)
            };
            conf.generateSharedSmarteditForTestsTsConfig.data.compilerOptions.paths = lodash.merge(conf.generateSharedSmarteditForTestsTsConfig.data.compilerOptions.paths, testpaths);

            // se
            addSmartEditPaths(conf.generateProdSmarteditTsConfig.data);
            addSmartEditContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);
            addSmartEditPaths(conf.generateDevSmarteditTsConfig.data);
            addSmartEditContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);

            // karma
            addSmartEditContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);
            addTestPaths(conf.generateKarmaSmarteditContainerTsConfig.data);
            addSmartEditPaths(conf.generateKarmaSmarteditTsConfig.data);
            addTestPaths(conf.generateKarmaSmarteditTsConfig.data);

            return conf;
        }
    };
};
