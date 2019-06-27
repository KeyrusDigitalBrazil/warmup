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
module.exports = function() {

    return {
        config: function(data, conf) {
            const lodash = require('lodash');

            const merchandisingsmarteditPaths = {
                "merchandisingsmartedit/*": ["web/features/merchandisingsmartedit/*"],
                "merchandisingsmarteditcommons": ["web/features/merchandisingsmarteditcommons"],
                "merchandisingsmarteditcommons*": ["web/features/merchandisingsmarteditcommons*"]
            };

            const yssmarteditmoduleContainerPaths = {
                "merchandisingsmarteditcontainer/*": ["web/features/merchandisingsmarteditContainer/*"],
                "merchandisingsmarteditcommons": ["web/features/merchandisingsmarteditcommons"],
                "merchandisingsmarteditcommons*": ["web/features/merchandisingsmarteditcommons*"]
            };

            function addYsmarteditmodulePaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(merchandisingsmarteditPaths));
            }

            function addYsmarteditmoduleContainerPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(yssmarteditmoduleContainerPaths));
            }

            // PROD
            addYsmarteditmodulePaths(conf.generateProdSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);

            // DEV
            addYsmarteditmodulePaths(conf.generateDevSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);

            // KARMA
            addYsmarteditmodulePaths(conf.generateKarmaSmarteditTsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);

            // IDE
            addYsmarteditmodulePaths(conf.generateIDETsConfig.data);
            addYsmarteditmoduleContainerPaths(conf.generateIDETsConfig.data);

            return conf;
        }
    };

};
