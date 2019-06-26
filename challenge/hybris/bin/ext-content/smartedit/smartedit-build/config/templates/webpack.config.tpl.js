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
module.exports = function() {

    const path = require('path');
    const lodash = require('lodash');
    const webpackUtil = global.smartedit.taskUtil.webpack;

    const baseWebpackConfig = {
        output: {
            path: path.resolve("./jsTarget/"),
            filename: '[name].js',
            sourceMapFilename: '[file].map'
        },
        resolve: {
            /*
             * module resolution from sources
             */
            modules: [
                path.resolve(process.cwd(), './node_modules'),
                path.resolve(process.cwd(), './jsTarget/web/app'),
                path.resolve(process.cwd(), './jsTarget/web/features')
            ],
            extensions: ['.ts', '.js']
        },
        module: {
            rules: [{
                test: /\.ts$/,
                sideEffects: true,
                use: [{
                    loader: 'awesome-typescript-loader',
                    options: {
                        configFileName: null // property set in generateWebpackConfig.js
                    }
                }]
            }]
        },
        performance: {
            hints: false
        },
        stats: {
            colors: true,
            modules: true,
            reasons: true,
            errorDetails: true
        },
        plugins: [],
        bail: true,
        mode: 'development'
    };

    const baseExternal = {
        "angular": "angular",
        "lodash": "lodash",
        "angular-route": "angular-route",
        "angular-translate": "angular-translate",
        "crypto-js": "CryptoJS",
        "Reflect": "Reflect",
        "moment": "moment",
        /*
         * module resolution of functions from d.ts in downstream extensions:
         * it is assumed they are found under the smarteditcommons namespace
         */
        "smarteditcommons": "smarteditcommons"
    };

    const devExternal = lodash.defaultsDeep({
        "jasmine": "jasmine",
        "testutils": "testutils",
        "angular-mocks": "angular-mocks"
    }, baseExternal);

    const prodWebpackConfig = Object.assign({
        devtool: 'none',
        externals: baseExternal
    }, baseWebpackConfig);

    const devWebpackConfig = Object.assign({
        devtool: "source-map",
        externals: devExternal
    }, baseWebpackConfig);

    webpackUtil.addPlugin(devWebpackConfig, webpackUtil.karmaErrorsPlugin);
    webpackUtil.addPlugin(devWebpackConfig, webpackUtil.ignoreNotFoundExportPlugin);

    return {
        // if you change this object, please update the webpack.js in the bundle config
        devWebpackConfig,
        prodWebpackConfig
    };
}();
