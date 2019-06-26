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
        targets: ['cmssmartedit', 'cmssmarteditContainer'],
        config: function(data, conf) {
            var paths = require('../../jsTests/paths');

            return {
                options: {
                    dest: 'jsTarget/docs',
                    title: "SmartEdit CMS API",
                    startPage: '/cmssmartedit'
                },
                cmssmartedit: {
                    api: true,
                    src: [
                        paths.sources.commonsFiles,
                        paths.sources.cmssmarteditFiles,
                        paths.sources.commonsTSFiles,
                        paths.sources.cmssmarteditTSFiles
                    ],
                    title: 'CMS SmartEdit'
                },
                cmssmarteditContainer: {
                    api: true,
                    src: [
                        paths.sources.commonsFiles,
                        paths.sources.cmssmarteditContainerFiles,
                        paths.sources.commonsTSFiles,
                        paths.sources.cmssmarteditContainerTSFiles
                    ],
                    title: 'CMS SmartEdit Container'
                }
            };
        }
    };
};
