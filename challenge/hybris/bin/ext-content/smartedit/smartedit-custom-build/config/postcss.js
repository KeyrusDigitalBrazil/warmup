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
        targets: [
            'dist'
        ],
        config: function(data, conf) {
            var paths = require('../paths');

            return {
                options: {
                    processors: [
                        require('autoprefixer')({
                            browsers: ["Safari >= 8", "last 2 versions", "ie >= 9"]
                        })
                    ]
                },
                dist: {
                    src: paths.web.webroot.staticResources.smartEdit.css.temp.outerStyling
                }
            };
        }
    };
};
