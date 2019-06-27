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
            'e2e',
            'test',
            'dev',
            'pack',
            'ngdocs'
        ],
        config: function(data, conf) {
            return {
                e2e: {
                    files: ['Gruntfile.js', 'web/features/**/*', 'jsTests'],
                    tasks: ['e2e'],
                    options: {
                        atBegin: true
                    }
                },
                test: {
                    files: ['Gruntfile.js', 'web/features/**/*', 'jsTests'],
                    tasks: ['test'],
                    options: {
                        atBegin: true
                    }
                },
                dev: {
                    files: ['Gruntfile.js', 'web/features/**/*', 'jsTests/**/*'],
                    tasks: ['dev'],
                    options: {
                        atBegin: true
                    }
                },
                pack: {
                    files: ['Gruntfile.js', 'web/features/**/*', 'jsTests'],
                    tasks: ['package'],
                    options: {
                        atBegin: true
                    }
                },
                ngdocs: {
                    files: ['web/features/**/*'],
                    tasks: ['ngdocs'],
                    options: {
                        atBegin: true
                    }
                }
            };
        }
    };
};
