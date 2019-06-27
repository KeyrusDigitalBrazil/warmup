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
            'webApplicationInjector',
            'uglifyThirdparties'
        ],
        config: function(data, conf) {
            var paths = require('../paths');

            return {
                //Since uglify properly terminates statements with semi-colon, it thereby sanitizes the not so clean $script js
                webApplicationInjector: {
                    files: {
                        'web/webroot/static-resources/webApplicationInjector.js': ['jsTarget/webApplicationInjector.js'],
                    },
                    options: {
                        mangle: true,

                        output: {
                            /**
                             * This is because CI adds license header, we don't want to keep generating this file with no
                             * header and seeing local file changes in git diff.
                             *
                             * We might consider creating a custom function for this in the future to look specifically for
                             * our license header.
                             * https://github.com/gruntjs/grunt-contrib-uglify
                             */
                            comments: 'all'
                        }
                    }
                },

                uglifyThirdparties: {
                    files: {
                        'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.min.js': ['node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js'],
                        'node_modules/crypto-js/crypto-js.min.js': ['node_modules/crypto-js/crypto-js.js'],
                        'node_modules/reflect-metadata/Reflect.min.js': ['node_modules/reflect-metadata/Reflect.js'],
                        'node_modules/angular-mocks/angular-mocks.min.js': ['node_modules/angular-mocks/angular-mocks.js'],
                        'node_modules/intersection-observer/intersection-observer.min.js': ['node_modules/intersection-observer/intersection-observer.js'],
                    },
                    options: {
                        mangle: true
                    }

                }
            };
        }
    };
};
