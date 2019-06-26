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
module.exports = function(grunt) {
    return {
        targets: [],
        config: function(data, conf) {
            
            const lodash = require('lodash');
            const paths = require('../paths');

            let optionSpecs = {
                options: {
                    args: {
                        specs: global.smartedit.taskUtil.protractor.getSpecs(paths.tests.allE2e)
                    }
                }
            };

            lodash.defaultsDeep(conf.run, optionSpecs);
            lodash.defaultsDeep(conf.maxrun, optionSpecs);

            return conf;
        }
    };
};
