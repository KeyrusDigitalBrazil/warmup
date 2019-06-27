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
            'tslintRules',
            'tsInjectables',
            'tsInstrument'
        ],
        config: function(data, conf) {
            var paths = require('../paths');
            return {
                options: {},
                tslintRules: {
                    src: global.smartedit.bundlePaths.bundleRoot + "/config/tslint/rules/*.ts",
                    dest: global.smartedit.bundlePaths.bundleRoot + "/config/tslint/rules/generated/"
                },
                tsInjectables: paths.tools.seInjectableInstrumenter,
                tsInstrument: paths.tools.seCommonsInstrumenter
            };
        }
    };

};
