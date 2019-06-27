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
        targets: ['personalizationsmartedit', 'personalizationsmarteditContainer'],
        config: function(data, conf) {
            var paths = require('../../jsTests/paths');

            return {
                options: {
                    dest: 'jsTarget/docs',
                    title: "Personalization API",
                    startPage: '/#/personalizationsmartedit'
                },
                personalizationsmartedit: {
                    api: true,
                    src: [
                        paths.sources.commonsFiles,
                        paths.sources.personalizationsmarteditFiles,
                        paths.sources.commonsTSFiles,
                        paths.sources.personalizationsmarteditTSFiles
                    ],
                    title: 'Personalization SmartEdit'
                },
                personalizationsmarteditContainer: {
                    api: true,
                    src: [
                        paths.sources.commonsFiles,
                        paths.sources.personalizationsmarteditContainerFiles,
                        paths.sources.commonsTSFiles,
                        paths.sources.personalizationsmarteditContainerTSFiles
                    ],
                    title: 'Personalization SmartEdit Container'
                }
            };
        }
    };
};
