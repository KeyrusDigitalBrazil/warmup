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

    var lodash = require('lodash');

    return {
        config: function(data, _conf) {

            const conf = lodash.cloneDeep(_conf);

            conf.mappings.forEach((mapping) => {
                mapping.level = 'FATAL';
                mapping.patterns = mapping.patterns.map((pattern) => {
                    return pattern.replace("web/features", "web/app").replace("jsTests/", "tests/");
                });
            });
            return conf;
        }
    };
};
