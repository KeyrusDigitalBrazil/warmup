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
            const lodash = require('lodash');

            const personalizationsmarteditPaths = {
                "personalizationcommons": ["web/features/personalizationcommons"],
                "personalizationcommons/*": ["web/features/personalizationcommons/*"],
                "personalizationsmartedit": ["web/features/personalizationsmartedit"],
                "personalizationsmartedit/*": ["web/features/personalizationsmartedit/*"]
            };

            const personalizationsmarteditContainerPaths = {
                "personalizationcommons": ["web/features/personalizationcommons"],
                "personalizationcommons/*": ["web/features/personalizationcommons/*"],
                "personalizationsmarteditcontainer": ["web/features/personalizationsmarteditcontainer"],
                "personalizationsmarteditcontainer/*": ["web/features/personalizationsmarteditcontainer/*"]
            };

            function addPersonalizationSmarteditPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(personalizationsmarteditPaths));
            }

            function addPersonalizationSmarteditContainerPaths(conf) {
                lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(personalizationsmarteditContainerPaths));
            }

            addPersonalizationSmarteditPaths(conf.generateProdSmarteditTsConfig.data);
            addPersonalizationSmarteditContainerPaths(conf.generateProdSmarteditContainerTsConfig.data);
            addPersonalizationSmarteditPaths(conf.generateDevSmarteditTsConfig.data);
            addPersonalizationSmarteditContainerPaths(conf.generateDevSmarteditContainerTsConfig.data);
            addPersonalizationSmarteditPaths(conf.generateKarmaSmarteditTsConfig.data);
            addPersonalizationSmarteditContainerPaths(conf.generateKarmaSmarteditContainerTsConfig.data);
            addPersonalizationSmarteditPaths(conf.generateIDETsConfig.data);
            addPersonalizationSmarteditContainerPaths(conf.generateIDETsConfig.data);

            return conf;
        }
    };
};
