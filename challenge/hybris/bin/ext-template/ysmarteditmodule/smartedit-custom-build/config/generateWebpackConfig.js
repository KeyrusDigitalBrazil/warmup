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

            const path = require("path");
            const lodash = require("lodash");

            function setSmarteditProperties(conf, addEntry) {
                if (addEntry) {
                    conf.entry = {
                        'ysmarteditmodule': './jsTarget/web/features/ysmarteditmodule/ysmarteditmoduleModule.ts'
                    };
                }
                //seems necessary on case sensitive OS to specify aliases in addition to paths in tsconfig
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "ysmarteditmodulecommons": path.resolve("./jsTarget/web/features/ysmarteditmodulecommons"),
                    "ysmarteditmodule": path.resolve("./jsTarget/web/features/ysmarteditmodule")
                });
            }

            function setSmarteditContainerProperties(conf, addEntry) {
                if (addEntry) { // don't add entries for any karma webpacks
                    conf.entry = {
                        'ysmarteditmoduleContainer': './jsTarget/web/features/ysmarteditmoduleContainer/ysmarteditmodulecontainerModule.ts'
                    };
                }
                //seems necessary on case sensitive OS to specify aliases in addition to paths in tsconfig
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "ysmarteditmodulecommons": path.resolve("./jsTarget/web/features/ysmarteditmodulecommons"),
                    "ysmarteditmodulecontainer": path.resolve("./jsTarget/web/features/ysmarteditmoduleContainer")
                });
            }

            // ======== PROD ========
            setSmarteditProperties(conf.generateProdSmarteditWebpackConfig.data, true);
            setSmarteditContainerProperties(conf.generateProdSmarteditContainerWebpackConfig.data, true);


            // ======== DEV ========
            setSmarteditProperties(conf.generateDevSmarteditWebpackConfig.data, true);
            setSmarteditContainerProperties(conf.generateDevSmarteditContainerWebpackConfig.data, true);

            // ======== KARMA ========
            setSmarteditProperties(conf.generateKarmaSmarteditWebpackConfig.data, false);
            setSmarteditContainerProperties(conf.generateKarmaSmarteditContainerWebpackConfig.data, false);

            return conf;
        }
    };

};
