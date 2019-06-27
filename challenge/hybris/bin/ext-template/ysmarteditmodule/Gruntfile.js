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
module.exports = function(grunt) {

    require('time-grunt')(grunt);
    require('./smartedit-build')(grunt).load();

    // -------------------------------------------------------------------------------------------------
    // FILE GENERATION
    grunt.registerTask('generate', [
        'generateWebpackConfig',
        'generateTsConfig',
        'generateKarmaConf'
    ]);

    // -------------------------------------------------------------------------------------------------
    // Beautify
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('sanitize', ['jsbeautifier', 'tsformatter']);


    // webpack
    grunt.registerTask('webpackDev', ['webpack:devSmartedit', 'webpack:devSmarteditContainer']);
    grunt.registerTask('webpackProd', ['webpack:prodSmartedit', 'webpack:prodSmarteditContainer']);

    // -------------------------------------------------------------------------------------------------
    // Linting
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('linting', ['jshint', 'tslint']);

    grunt.registerTask('sanitize', ['jsbeautifier', 'tsformatter']);

    grunt.registerTask('compile_only', ['sanitize', 'linting', 'copy:sources', 'multiNGTemplates', 'checkNoForbiddenNameSpaces', 'checkI18nKeysCompliancy', 'checkNoFocus']);
    grunt.registerTask('compile', ['clean:target', 'compile_only']);

    grunt.registerTask('test_only', ['generate', 'multiKarma']);
    grunt.registerTask('test', ['compile', 'test_only']);

    grunt.registerTask('concatAndPushDev', ['instrumentSeInjectable', 'webpackDev', 'copy:dev']);
    grunt.registerTask('concatAndPushProd', ['instrumentSeInjectable', 'webpackProd', 'copy:dev']);

    grunt.registerTask('dev_only', ['concatAndPushDev']);
    grunt.registerTask('dev', ['test', 'dev_only']);

    grunt.registerTask('package_only', ['concatAndPushProd', 'ngdocs']);
    grunt.registerTask('package', ['test', 'package_only']);
    grunt.registerTask('packageSkipTests', ['generate', 'compile_only', 'package_only']);

    grunt.registerTask('e2e', ['connect:dummystorefront', 'connect:test', 'multiProtractor']);
    grunt.registerTask('e2e_max', ['connect:dummystorefront', 'connect:test', 'multiProtractorMax']);
    grunt.registerTask('e2e_dev', 'e2e local development mode', function() {
        grunt.option('keepalive_dummystorefront', true);
        grunt.option('open_browser', true);
        grunt.task.run(['connect:test', 'connect:dummystorefront']);
    });
    grunt.registerTask('e2e_debug', 'e2e local debug mode', function() {
        grunt.option('browser_debug', true);
        grunt.task.run('e2e');
    });
    grunt.registerTask('verify_only', ['e2e']);
    grunt.registerTask('verify', ['generate', 'package', 'verify_only']);
    grunt.registerTask('verify_max', ['generate', 'package', 'e2e_max']);

};
