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
            'smartEdit',
            'smartEditContainer',
            'build'
        ],
        config: function(data, conf) {
            var paths = require('../paths');

            return {
                options: {
                    dest: 'jsTarget/docs',
                    title: "SmartEdit API",
                    startPage: '/smartEdit',
                },
                smartEdit: {
                    api: true,
                    src: paths.ngdocs.smartedit,
                    title: 'SmartEdit'
                },
                smartEditContainer: {
                    api: true,
                    src: paths.ngdocs.smarteditcontainer,
                    title: 'SmartEdit Container'
                },
                build: {
                    title: 'Smartedit Builder',
                    src: [
                        'smartedit-build/config/**/*.+(js|ts|json)'
                    ]
                }
            };
        }
    };
};
