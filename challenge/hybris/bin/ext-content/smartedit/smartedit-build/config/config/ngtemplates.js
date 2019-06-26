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
            'run'
        ],
        config: function(data, conf) {
            conf = conf || {};
            conf.options = {
                standalone: true, //to declare a module as opposed to binding to an existing one
                module: 'coretemplates'
            };
            conf.run = {
                src: [
                    'web/app/**/*.html',
                    '!**/generated/**/*'
                ],
                dest: 'jsTarget/templates.js'
            };
            return conf;
        }
    };
};
