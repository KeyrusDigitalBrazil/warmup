/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
// Karma configuration
// Generated on Sat Jul 05 2014 07:57:17 GMT-0400 (EDT)

module.exports = function(config) {
    config.set({

        // base path that will be used to resolve all patterns (eg. files, exclude)
        //basePath: '../../../main/webapp/',
        basePath: '../../',


        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],

        decorators: [
            'karma-phantomjs-launcher',
            //'karma-chrome-launcher',
            //'karma-firefox-launcher',
            'karma-jasmine',
            'karma-coverage',
            'karma-ng-html2js-preprocessor'
        ],

        preprocessors: {
            //'web/**/*.html': 'ng-html2js',
            //'web/decorators/**/*.js': ['coverage']
        },

        coverageReporter: {
            // specify a common output directory
            dir: 'jsTests/coverage/',
            reporters: [{
                type: 'html',
                subdir: 'report-html'
            }, {
                type: 'cobertura',
                subdir: '.',
                file: 'cobertura.xml'
            }]
        },

        junitReporter: {
            outputDir: 'jsTarget/test/personalizationsearchsmarteditContainer/junit/', // results will be saved as $outputDir/$browserName.xml
            outputFile: 'testReport.xml' // if included, results will be saved as $outputDir/$browserName/$outputFile
            //suite: '' // suite will become the package name attribute in xml testsuite element
        },

        ngHtml2JsPreprocessor: {},

        // list of files / patterns to load in the browser
        files: [
            'build/bundle/webroot/static-resources/dist/smartedit/js/thirdparties.js',
            'build/bundle/webroot/static-resources/thirdparties/ckeditor/ckeditor.js',
            'build/bundle/test/unitUtils.js',

            'web/features/personalizationsearchsmarteditcommons/**/*.js',
            'web/features/personalizationsearchsmarteditContainer/**/*.js',
            'jsTarget/web/features/personalizationsearchsmarteditContainer/templates.js',

            'jsTests/utils/**/*.js',
            'jsTests/personalizationsearchsmarteditContainer/unit/features/**/*.js'
        ],

        // list of files to exclude
        exclude: [],


        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        // coverage reporter generates the coverage
        reporters: ['progress', 'junit'], // 'coverage'


        // web server port
        port: 9876,


        // enable / disable colors in the output (reporters and logs)
        colors: true,


        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_INFO,


        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,


        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: ['PhantomJS'], //Chrome


        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: false
    });
};
