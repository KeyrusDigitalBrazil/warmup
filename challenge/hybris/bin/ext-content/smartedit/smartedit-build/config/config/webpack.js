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
            'prodSmartedit',
            'prodSmarteditContainer',
            'devSmartedit',
            'devSmarteditContainer',
            'smarteditForTests'
        ],
        config: function(data, conf) {
            const paths = global.smartedit.bundlePaths;
            const path = require('path');

            return {
                prodSmartedit: require(path.resolve(paths.external.generated.webpack.prodSmartedit)),
                prodSmarteditContainer: require(path.resolve(paths.external.generated.webpack.prodSmarteditContainer)),
                devSmartedit: require(path.resolve(paths.external.generated.webpack.devSmartedit)),
                devSmarteditContainer: require(path.resolve(paths.external.generated.webpack.devSmarteditContainer)),
                smarteditForTests: require(path.resolve(paths.external.generated.webpack.smarteditForTests))
            };
        }
    };

};
