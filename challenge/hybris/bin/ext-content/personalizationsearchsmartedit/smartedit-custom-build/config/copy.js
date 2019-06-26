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
            'sources',
            'dev'
        ],
        config: function(data, conf) {
            return {
                sources: {
                    files: [{
                        expand: true,
                        flatten: false,
                        src: [
                            'web/features/**/*.+(js|ts)'
                        ],
                        dest: 'jsTarget/'
                    }]
                },
                dev: {
                    expand: true,
                    flatten: true,
                    src: ['jsTarget/*.js'],
                    dest: 'web/webroot/personalizationsearchsmartedit/js'
                }
            };
        }
    };
};
