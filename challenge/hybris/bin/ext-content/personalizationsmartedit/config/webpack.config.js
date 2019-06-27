/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
var path = require('path');

var baseWebpackConfig = {
    output: {
        path: path.resolve("./jsTarget/"), 
        filename: '[name].js',
        sourceMapFilename: '[file].map'
    }, 
    resolve: {
        modules: [
            path.resolve('./jsTarget/web')
        ],
        extensions: ['.ts', '.js']
    }, 
    module: {
        rules: [{
            test: /\.ts$/,
            loader: 'awesome-typescript-loader', 
            options: {
                configFileName: './config/tsconfig.json'
            }
        }]
    },
    externals: {
        "angular": 'angular', 
        "angular-route": "angular-route", 
        "angular-translate": "angular-translate"
    },
    stats: {
        colors: true,
        modules: true,
        reasons: true,
        errorDetails: true
    }
};

// -----------------------------------------------------------------------------------------------------
// Production Webpack Config
// -----------------------------------------------------------------------------------------------------
var prodWebpackConfig = Object.assign({ devtool: 'none' }, baseWebpackConfig); 

// -----------------------------------------------------------------------------------------------------
// Development Webpack Config
// -----------------------------------------------------------------------------------------------------
var devWebpackConfig = Object.assign({ devtool: 'source-map' }, baseWebpackConfig);

// -----------------------------------------------------------------------------------------------------
// Test Webpack Config
// -----------------------------------------------------------------------------------------------------
var testWebpackConfig = Object.assign({}, baseWebpackConfig);

// TSConfig
testWebpackConfig.module = {
    rules: [{
        test: /\.ts$/,
        loader: 'awesome-typescript-loader', 
        options: {
            configFileName: './config/tsconfig-test.json'
        }
    }]
};

// Externals
testWebpackConfig.externals = {
        "angular": 'angular',
        "angular-route": "angular-route",
        "angular-translate": "angular-translate",
        "jasmine":"jasmine",
        "testutils":"testutils",
        "angular-mocks":"angular-mocks"
};

// -----------------------------------------------------------------------------------------------------
// Exports
// -----------------------------------------------------------------------------------------------------
module.exports = {
    devWebpackConfig: devWebpackConfig,
    testWebpackConfig: testWebpackConfig,
    prodWebpackConfig: prodWebpackConfig
}; 