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

    return {
        config: function(data, conf) {
            const lodash = require('lodash');

            const declaration = {
                "declaration": true,
                "declarationDir": "../../temp/types",
                "stripInternal": true
            };

            const smarteditPaths = {
                "smarteditcommons": ["web/app/common"],
                "smarteditcommons/*": ["web/app/common/*"],
                "smartedit": ["web/app/smartedit"],
                "smartedit/*": ["web/app/smartedit/*"]
            };

            const smarteditContainerPaths = {
                "smarteditcommons": ["web/app/common"],
                "smarteditcommons/*": ["web/app/common/*"],
                "smarteditcontainer": ["web/app/smarteditcontainer"],
                "smarteditcontainer/*": ["web/app/smarteditcontainer/*"]
            };

            const smarteditCommonsPaths = {
                "smarteditcommons": ["web/app/common"],
                "smarteditcommons/*": ["web/app/common/*"]
            };

            const excludedPatterns = ["smarteditcommons", "smartedit", "smarteditcontainer"];

            function addTypesDeclarationAndRemovePathsAndTypeRoots(conf, arrayOfPath, declareTypes) {
                conf.compilerOptions.paths = conf.compilerOptions.paths || {};

                if (declareTypes !== false){
                    lodash.merge(conf.compilerOptions, declaration);
                }
                
                arrayOfPath.forEach((paths) => {
                    Object.keys(conf.compilerOptions.paths || []).forEach((key) => {
                        if (excludedPatterns.find((pattern) => key.indexOf(pattern) > -1)) {
                            delete conf.compilerOptions.paths[key];
                        }
                    });
                });
                
                conf.compilerOptions.typeRoots = conf.compilerOptions.typeRoots.filter((key) => {
                    return key.indexOf(global.smartedit.bundlePaths.bundleDirName + "/@types") === -1;
                });

                arrayOfPath.forEach((paths) => {
                    lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(paths));
                });
            }

            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateProdSmarteditTsConfig.data, [smarteditPaths]);
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateProdSmarteditContainerTsConfig.data, [smarteditContainerPaths]);
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateDevSmarteditTsConfig.data, [smarteditPaths]);
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateDevSmarteditContainerTsConfig.data, [smarteditContainerPaths]);
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateKarmaSmarteditTsConfig.data, [smarteditPaths], false);
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateKarmaSmarteditContainerTsConfig.data, [smarteditContainerPaths], false);
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateIDETsConfig.data, [smarteditPaths, smarteditContainerPaths], false);
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateSharedSmarteditForTestsTsConfig.data, [smarteditCommonsPaths], false);
             
            return conf;
        }
    };

};
