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
        config: function(data, conf) {

            const path = require("path");
            const lodash = require("lodash");

            function setSmarteditProperties(conf, addEntry) {
                if (addEntry) {
                    conf.entry = {
                        'personalizationsmartedit': './jsTarget/web/features/personalizationsmartedit/PersonalizationsmarteditApp.ts'
                    };
                }
                //seems necessary on case sensitive OS to specify aliases in addition to paths in tsconfig
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "personalizationcommons": path.resolve("./jsTarget/web/features/personalizationcommons"),
                    "personalizationsmartedit": path.resolve("./jsTarget/web/features/personalizationsmartedit")
                });
            }

            function setSmarteditContainerProperties(conf, addEntry) {
                if (addEntry) {
                    conf.entry = {
                        'personalizationsmarteditcontainer': './jsTarget/web/features/personalizationsmarteditcontainer/PersonalizationsmarteditcontainerApp.ts'
                    };
                }
                conf.resolve.alias = conf.resolve.alias || {};
                conf.resolve.alias = lodash.merge(conf.resolve.alias, {
                    "personalizationcommons": path.resolve("./jsTarget/web/features/personalizationcommons"),
                    "personalizationsmarteditcontainer": path.resolve("./jsTarget/web/features/personalizationsmarteditcontainer")
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
