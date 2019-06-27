/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ('Confidential Information'). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
module.exports = function(grunt) {

	require('time-grunt')(grunt);
	const webpackConfig = require('./webpack.config');

	grunt.initConfig({
		webpack: {
			options: {
				stats: !process.env.NODE_ENV || process.env.NODE_ENV === 'development'
			},
			prod: webpackConfig,
			dev: Object.assign({ watch: true }, webpackConfig)
		},
		ts: {
			default : {
				tsconfig: './acceleratoraddon/web/features/tsconfig.json',
				files: [
					{
						src: [
							'./acceleratoraddon/web/features/carouselinitialiser/*.ts'
						],
						dest: './jsTarget'
					}
				]
			}
		},
		uglify: {
			options: {
				mangle: false
			},
			default: {
				files: {
					'./jsTarget/dest/merchandisingaddon.js': './jsTarget/dest/bundle.js'
				}
			}
		},
		copy: {
			default: {
				files: {
					'./acceleratoraddon/web/webroot/_ui/responsive/common/js/merchandisingaddon.js': './jsTarget/dest/merchandisingaddon.js'
				}
			}
		},
		karma: {
			unit: {
				configFile: 'karma.conf.js'
			},
			debug: {
				configFile: 'karma.conf.js',
				browsers: ['Chrome'],
				plugins: [
					'karma-webpack',
					'karma-jasmine',
					'karma-chrome-launcher',
					'karma-junit-reporter',
					'karma-spec-reporter'
				]
			}
		}
	});

	grunt.loadNpmTasks('grunt-webpack');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-karma');

	grunt.registerTask('default', ['webpack:prod', 'uglify', 'copy']);
	grunt.registerTask('test_only', ['karma:unit']);
	grunt.registerTask('test_local', ['karma:debug']); // needs to rebuild local node_modules (PhantomJS gyp build is platform dependant)
};
