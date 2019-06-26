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
        targets: ['files'],
        config: function(data, conf) {
            conf.files = [
                'web/features/**/*.js',
                'web/features/**/*.html',
                'jsTests/**/*.js',
                'smartedit-custom-build/**/*.+(js|html)'
            ];

            return conf;
        }
    };
};
