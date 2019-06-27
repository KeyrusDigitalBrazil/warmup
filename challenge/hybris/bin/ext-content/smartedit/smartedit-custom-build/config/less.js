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
            'dev'
        ],
        config: function(data, conf) {
            return {
                dev: {
                    files: [{
                        expand: true,
                        cwd: 'web/app/smartedit/styling',
                        src: 'inner-styling.less',
                        dest: 'web/webroot/static-resources/dist/smartedit/css/',
                        ext: '.css'
                    }, {
                        expand: true,
                        cwd: 'web/app/smartedit/styling',
                        src: 'outer-vendor.less',
                        dest: 'web/webroot/static-resources/dist/smartedit/css/temp',
                        ext: '.css'
                    }, {
                        expand: true,
                        cwd: 'web/app/smartedit/styling',
                        src: 'outer-styling.less',
                        dest: 'web/webroot/static-resources/dist/smartedit/css/temp',
                        ext: '.css'
                    }]
                }
            };
        }
    };
};
