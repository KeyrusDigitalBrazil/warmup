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
module.exports = function(config) {

	config.set({
		// base path that will be used to resolve all patterns (eg. files, exclude)
		basePath: process.cwd(),

		// frameworks to use
		// available frameworks: https://npmjs.org/browse/keyword/karma-adapter
		frameworks: ['jasmine'],

		decorators: [
			'karma-phantomjs-launcher',
			'karma-jasmine'
		],

		preprocessors: {
			'./jsTarget/**/*.spec.js': ['webpack']
		},

		// DEFINE THESE IN EXTENSION CONFIG
		// ================================
		//
		// coverageReporter: {
		// },
		//
		// junitReporter: {
		// },

		// // list of files / patterns to load in the browser
		files: [
			'./node_modules/jquery/dist/jquery.js',
			'./node_modules/jasmine-core/lib/jasmine-core/jasmine.js',
			'./jsTarget/test.js',
			'./jsTarget/**/*.spec.js'
		],
		//
		// // list of files to exclude
		// exclude: [
		// ],


		// test results reporter to use
		// possible values: 'dots', 'progress'
		// available reporters: https://npmjs.org/browse/keyword/karma-reporter
		// coverage reporter generates the coverage
		reporters: ['spec', 'junit'], // 'coverage' interferes with gatewayProxy and proxies empty methods when it should not

		specReporter: {
			suppressPassed: true
		},

		// web server port
		port: 9876,


		// enable / disable colors in the output (reporters and logs)
		colors: true,

		// level of logging
		// possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
		// logLevel: config.LOG_INFO,

		// enable / disable watching file and executing tests whenever any file changes
		autoWatch: false,
		autoWatchBatchDelay: 1000,

		// start these browsers
		// available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
		browsers: ['PhantomJS'],

		// Continuous Integration mode
		// if true, Karma captures browsers, runs the tests and exits
		singleRun: true,

		proxies: {
			'/static-resources/images/': '/base/static-resources/images/'
		},

		plugins: [
			'karma-webpack',
			'karma-jasmine',
			'karma-phantomjs-launcher',
			'karma-junit-reporter',
			'karma-spec-reporter'
		]
	});
};
