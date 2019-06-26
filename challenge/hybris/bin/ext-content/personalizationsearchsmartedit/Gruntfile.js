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

    // -------------------------------------------------------------------------------------------------
    // Linting
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('linting', ['jshint', 'tslint']);

    // -------------------------------------------------------------------------------------------------
    // Compilation
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('compile_only', ['sanitize', 'linting', 'multipleCopySources', 'multipleNGTemplates', 'checkNoFocus', 'checkNoForbiddenNameSpaces', 'styling_only']);
    grunt.registerTask('compile', ['clean:target', 'compile_only']);

    grunt.registerTask('concatAndPushDev', ['instrumentSeInjectable', 'webpack:devSmartedit', 'webpack:devSmarteditContainer', 'requirePushExtensions', 'copy:dev']);

    // -------------------------------------------------------------------------------------------------
    // Unit Tests
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('test_only', ['generate', 'multiKarma']);
    grunt.registerTask('test', ['compile', 'test_only']);

    // -------------------------------------------------------------------------------------------------
    // Dev - For development code
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('dev_only', ['concatAndPushDev']);
    grunt.registerTask('dev', ['compile', 'dev_only', 'test_only']);

    // -------------------------------------------------------------------------------------------------
    // Packaging - For production ready code
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('concatAndPushProd', ['instrumentSeInjectable', 'webpack:prodSmartedit', 'webpack:prodSmarteditContainer', 'requirePushExtensions', 'copy:dev']);

    grunt.registerTask('package_only', ['concatAndPushProd', 'ngdocs']);
    grunt.registerTask('package', ['compile', 'package_only', 'test_only']);

    grunt.registerTask('packageSkipTests', ['generate', 'compile_only', 'package_only']);

    // -------------------------------------------------------------------------------------------------
    // E2E Tests
    // -------------------------------------------------------------------------------------------------
    grunt.registerTask('setupE2e', ['generateSmarteditIndexHtml:e2eSetup', 'generateStorefrontIndexHtml', 'connect:test']);
    grunt.registerTask('e2e', ['setupE2e', 'multiProtractor']); //any change to the e2e should be adapted to e2e_max task
    grunt.registerTask('e2e_max', ['setupE2e', 'multiProtractorMax']);
    grunt.registerTask('verify_only', ['e2e']);

    // Full PROD build
    grunt.registerTask('verify', ['generate', 'package', 'verify_only']); //any change to the verify tash should be adapted to verify_max task
    grunt.registerTask('verify_max', ['generate', 'package', 'e2e_max']);

    grunt.registerTask('styling_only', ['less']);
    grunt.registerTask('styling', ['clean', 'styling_only']);

};
