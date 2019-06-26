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
        targets: ['personalizationpromotionssmartedit', 'personalizationpromotionssmarteditContainer'],
        config: function(data, conf) {
            var paths = require('../../jsTests/paths');

            return {
                options: {
                    dest: 'jsTarget/docs',
                    title: "Personalization Promotions API",
                    startPage: '/#/personalizationpromotionssmartedit'
                },
                personalizationpromotionssmartedit: {
                    api: true,
                    src: [
                        paths.sources.commonsFiles,
                        paths.sources.personalizationpromotionssmarteditFiles,
                        paths.sources.commonsTSFiles,
                        paths.sources.personalizationpromotionssmarteditTSFiles
                    ],
                    title: 'Personalization Promotions SmartEdit'
                },
                personalizationpromotionssmarteditContainer: {
                    api: true,
                    src: [
                        paths.sources.commonsFiles,
                        paths.sources.personalizationpromotionssmarteditContainerFiles,
                        paths.sources.commonsTSFiles,
                        paths.sources.personalizationpromotionssmarteditContainerTSFiles
                    ],
                    title: 'Personalization Promotions SmartEdit Container'
                }
            };
        }
    };
};
