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
        'generateKarmaConf',
        'generateSmarteditIndexHtml:landingPage',
        'generateSmarteditIndexHtml:smarteditE2e',
        'generateStorefrontIndexHtml',
        'concat:unitUtilsForBundle',
        'copy:ckeditor',
        'copy:images',
        'compileTs'
    ]);

    // -------------------------------------------------------------------------------------------------
    // LINTING + SANITIZING
    grunt.registerTask('formatCode', [
        'jsbeautifier',
        'tsformatter',
        'jshint',
        'tslint',
        'checkNoForbiddenNameSpaces',
        'checkI18nKeysCompliancy',
        'checkNoFocus'
    ]);

    // -------------------------------------------------------------------------------------------------
    // PREPARE JSTARGET
    grunt.registerTask('prepareJsTarget_Base', [
        'clean:target',
        'copy:sources',
        'ngtemplates:run',
        'uglify:uglifyThirdparties',
        'concat:webApplicationInjector',
        'instrumentSmarteditCommons',
        'instrumentSeInjectable'
    ]);
    grunt.registerTask('prepareJsTarget_Dev', ['prepareJsTarget_Base', 'webpack:devSmartedit', 'webpack:devSmarteditContainer', 'webpack:smarteditForTests']);
    grunt.registerTask('prepareJsTarget_Prod', ['prepareJsTarget_Base', 'webpack:prodSmartedit', 'webpack:prodSmarteditContainer', 'webpack:smarteditForTests', 'ngdocs']);

    // -------------------------------------------------------------------------------------------------
    // TEST
    grunt.registerTask('unit', ['karma:unitSmartedit', 'karma:unitSmarteditContainer']);
    grunt.registerTask('test_only', ['generate', 'unit']); // Legacy - see yunit macro in buildcallbacks.xml
    grunt.registerTask('e2e', ['connect:dummystorefront', 'connect:test', 'protractorRun']);
    grunt.registerTask('e2e_max', ['connect:dummystorefront', 'connect:test', 'protractorMaxrun']);
    grunt.registerTask('e2e_dev', 'e2e local development mode', function() {
        grunt.option('keepalive_dummystorefront', true);
        // un-comment following line once smartedit e2e tests are aligned (/test folder renamed to /jsTests).
        // grunt.option('open_browser', 'http://localhost:7000/' + global.smartedit.bundlePaths.test.e2e.listDest);
        grunt.option('open_browser', 'http://localhost:7000/test/e2e/list.html');
        grunt.task.run(['generateE2eListHtml', 'connect:test', 'connect:dummystorefront']);
    });
    grunt.registerTask('e2e_debug', 'e2e local debug mode', function() {
        grunt.option('browser_debug', true);
        grunt.task.run('e2e');
    });

    // -------------------------------------------------------------------------------------------------
    // PREPARE WEBROOT
    grunt.registerTask('prepareWebroot_Base', [
        'clean:webroot',
        'copy:dev',
        'webfont',
        'less',
        'postcss',
        'concat:outerStyling',
        'clean:postConcat'
    ]);

    grunt.registerTask('prepareWebroot_Dev', ['prepareWebroot_Base', 'concat:containerThirdpartiesDev', 'concat:smarteditThirdpartiesDev']);
    grunt.registerTask('prepareWebroot_Prod', ['prepareWebroot_Base', 'concat:containerThirdparties', 'concat:smarteditThirdparties', 'uglify:webApplicationInjector']);

    // -------------------------------------------------------------------------------------------------
    // PREPARE BUNDLE
    // - Must prepare jstarget and webroot first
    //
    grunt.registerTask('declareTypes', ['concat:smarteditcommonsTypes', 'bundleTypes:smarteditcommons', 'concat:smarteditTypes', 'bundleTypes:smartedit', 'concat:smarteditcontainerTypes', 'bundleTypes:smarteditcontainer']);
    grunt.registerTask('prepareBundle', [
        'clean:bundleForNewSymlinks',
        'declareTypes',
        'symlink:appToBundle',
        'copy:thirdPartySourceMaps',
        'copy:toDummystorefront'
    ]);


    grunt.registerTask('prod', ['formatCode', 'prepareJsTarget_Prod', 'prepareWebroot_Prod']);
    grunt.registerTask('dev', ['formatCode', 'prepareJsTarget_Dev', 'prepareWebroot_Dev', 'declareTypes']);

    grunt.registerTask('packageSkipTests', ['generate', 'prod', 'prepareBundle']);

    grunt.registerTask('verify', ['generate', 'prod', 'prepareBundle', 'unit', 'e2e']);
    grunt.registerTask('verify_max', ['generate', 'prod', 'prepareBundle', 'unit', 'e2e_max']);

    // Please use prod or dev, these should only be used for watch: tasks
    // because people keep committing code that has not been run through 'formatCode' task
    grunt.registerTask('test', ['prepareJsTarget_Base', 'unit']);
    grunt.registerTask('package', ['prepareJsTarget_Prod', 'prepareWebroot_Prod']);
    grunt.registerTask('packageDev', ['prepareJsTarget_Dev', 'prepareWebroot_Dev']);

};
