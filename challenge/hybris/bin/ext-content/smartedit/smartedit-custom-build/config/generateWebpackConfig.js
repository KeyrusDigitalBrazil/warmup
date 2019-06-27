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
        targets: [
            //only in bundle
        ],
        config: function(data, conf) {

            const lodash = require('lodash');
            const path = require("path");
            const paths = require('../paths');

            const excludedPatterns = ["smarteditcommons", "smartedit", "smarteditcontainer"];

            const smarteditEntry = {
                'smartedit': paths.entrypoints.smartedit,
                'smarteditbootstrap': paths.entrypoints.smarteditbootstrap
            };

            const smarteditContainerEntry = {
                'smarteditloader': paths.entrypoints.smarteditloader,
                'smarteditcontainer': paths.entrypoints.smarteditcontainer
            };

            function excludeFromExternal(conf) {
            
                Object.keys(conf.externals).forEach((key) => {
                    if (excludedPatterns.find((pattern) => key.indexOf(pattern) > -1)) {
                        delete conf.externals[key];
                    }
                });
            }

            function setSmarteditProperties(conf, addEntries) {
                if (addEntries !== false){
                    conf.entry = smarteditEntry;
                }

                //seems necessary on case sensitive OS to specify aliases in addition to paths in tsconfig
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "smarteditcommons": path.resolve(paths.smarteditproperties.smarteditcommons),
                    "smartedit": path.resolve(paths.smarteditproperties.smartedit)
                });

                excludeFromExternal(conf);
            }

            function setSmarteditContainerProperties(conf, addEntries) {
                if (addEntries !== false){
                    conf.entry = smarteditContainerEntry;
                }
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "smarteditcommons": path.resolve(paths.smarteditproperties.smarteditcommons),
                    "smarteditcontainer": path.resolve(paths.smarteditcontainerproperties.smarteditcontainer)
                });

                excludeFromExternal(conf);
            }

            function setSmarteditForTestsProperties(conf) {
                conf.entry = {
                    'sharedSmarteditForTests': paths.entrypoints.sharedSmarteditForTests
                };
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "smarteditcommons": path.resolve(paths.smarteditproperties.smarteditcommons)
                });
                conf.output = conf.output || {};
                conf.output.path = paths.sharedSmarteditForTests;
                excludeFromExternal(conf);
            }

            // ======== PROD ========
            setSmarteditProperties(conf.generateProdSmarteditWebpackConfig.data);
            setSmarteditContainerProperties(conf.generateProdSmarteditContainerWebpackConfig.data);
            
            // ======== DEV ========
            setSmarteditProperties(conf.generateDevSmarteditWebpackConfig.data);
            setSmarteditContainerProperties(conf.generateDevSmarteditContainerWebpackConfig.data);

            // ======== SHARED SMARTEDIT FOR TESTS ========
            setSmarteditForTestsProperties(conf.generateSharedSmarteditForTestsWebpackConfig.data);
            
            // ======== KARMA ========
            setSmarteditProperties(conf.generateKarmaSmarteditWebpackConfig.data, false);
            setSmarteditContainerProperties(conf.generateKarmaSmarteditContainerWebpackConfig.data, false);

            return conf;
        }
    };
};
