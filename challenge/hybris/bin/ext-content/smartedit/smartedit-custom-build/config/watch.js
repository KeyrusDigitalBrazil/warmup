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
            'options',
            'test',
            'test_only',
            'packageDev',
            'package',
            'buildDocs'
        ],
        config: function(data, conf) {
            var paths = require('../paths');

            return {
                options: {
                    atBegin: true
                },
                test: {
                    files: paths.watchFiles.concat([
                        'web/app/**/*',
                        paths.tests.allUnit
                    ]),
                    tasks: ['test']
                },
                test_only: {
                    files: paths.watchFiles.concat([
                        paths.tests.allUnit
                    ]),
                    tasks: ['unit']
                },
                packageDev: {
                    files: paths.watchFiles.concat([
                        'web/app/**/*'
                    ]),
                    tasks: ['packageDev']
                },
                package: {
                    files: paths.watchFiles.concat([
                        'web/app/**/*'
                    ]),
                    tasks: ['package']
                },
                docs: {
                    files: paths.watchFiles,
                    tasks: ['ngdocs'],
                    options: {
                        atBegin: true
                    }
                },
                buildDocs: {
                    files: [
                        'smartedit-build/**/*',
                        'smartedit-custom-build/**/*'
                    ],
                    tasks: ['ngdocs:build'],
                    options: {
                        atBegin: true
                    }
                }
            };
        }
    };
};
