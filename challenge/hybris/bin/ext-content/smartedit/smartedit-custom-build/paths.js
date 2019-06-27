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
    var paths = {};

    // grunt
    paths.gruntTasks = {
        dir: 'gruntTasks'
    };
    paths.gruntTasks.allJs = paths.gruntTasks.dir + '/**/*.js';

    // common
    paths.common = {
        dir: 'web/app/common'
    };
    paths.common.allJs = paths.common.dir + '/**/*.js';
    paths.common.allTs = paths.common.dir + '/**/*.ts';
    paths.common.allHtml = paths.common.dir + '/**/*.html';

    // web
    paths.web = {
        dir: 'web'
    };
    paths.web.allHtml = paths.web.dir + '/**/*.html';
    paths.web.allJs = paths.web.dir + '/**/*.js';
    paths.web.allTs = paths.web.dir + '/**/*.ts';

    //SmartEdit
    paths.web.smartEdit = {
        dir: 'web/app/smartedit'
    };
    paths.web.smartEdit.allJs = paths.web.smartEdit.dir + '/**/*.js';
    paths.web.smartEdit.allTs = paths.web.smartEdit.dir + '/**/*.ts';
    paths.web.smartEdit.allHtml = paths.web.smartEdit.dir + '/**/*.html';
    paths.web.smartEdit.styling = paths.web.smartEdit.dir + '/styling';

    paths.web.webroot = {
        dir: paths.web.dir + '/webroot'
    };
    paths.web.webroot.all = paths.web.webroot.dir + '/**/*';
    paths.web.webroot.staticResources = {
        dir: paths.web.webroot.dir + '/static-resources'
    };
    paths.web.webroot.staticResources.smartEdit = {
        dir: paths.web.webroot.staticResources.dir + '/dist/smartedit'
    };
    paths.web.webroot.staticResources.smartEdit.css = {
        dir: paths.web.webroot.staticResources.smartEdit.dir + '/css'
    };
    paths.web.webroot.staticResources.smartEdit.css.all = paths.web.webroot.staticResources.smartEdit.css.dir + '/*.css';
    paths.web.webroot.staticResources.smartEdit.css.outerStyling = paths.web.webroot.staticResources.smartEdit.css.dir + '/outer-styling.css';
    paths.web.webroot.staticResources.smartEdit.css.innerStyling = paths.web.webroot.staticResources.smartEdit.css.dir + '/inner-styling.css';
    paths.web.webroot.staticResources.smartEdit.css.temp = {
        dir: paths.web.webroot.staticResources.smartEdit.css.dir + '/temp'
    };
    paths.web.webroot.staticResources.smartEdit.css.temp.outerStyling = paths.web.webroot.staticResources.smartEdit.css.temp.dir + '/outer-styling.css';
    paths.web.webroot.staticResources.smartEdit.css.temp.outerVendor = paths.web.webroot.staticResources.smartEdit.css.temp.dir + '/outer-vendor.css';

    //SmartEditContainer
    paths.web.smarteditcontainer = {
        dir: 'web/app/smarteditcontainer'
    };
    paths.web.smarteditcontainer.allJs = paths.web.smarteditcontainer.dir + '/**/*.js';
    paths.web.smarteditcontainer.allTs = paths.web.smarteditcontainer.dir + '/**/*.ts';
    paths.web.smarteditcontainer.allHtml = paths.web.smarteditcontainer.dir + '/**/*.html';
    paths.web.smarteditcontainer.components = {
        dir: paths.web.smarteditcontainer + '/components'
    };
    paths.web.smarteditcontainer.components.allJs = paths.web.smarteditcontainer.components.dir + '/**/*.js';
    paths.web.smarteditcontainer.dao = {
        dir: paths.web.smarteditcontainer + '/dao'
    };
    paths.web.smarteditcontainer.dao.allJs = paths.web.smarteditcontainer.dao.dir + '/**/*.js';

    paths.web.smarteditcontainer.services = {
        dir: paths.web.smarteditcontainer + '/services'
    };
    paths.web.smarteditcontainer.services.allJs = paths.web.smarteditcontainer.services.dir + '/**/*.js';

    // techne
    paths.techne = {
        dir: 'node_modules/techne/'
    };
    paths.techne.allFonts = [paths.techne.dir + 'dist/bootstrap/fonts/*'];

    // localization
    paths.smartEditLocalesProperties = 'resources/localization/smartedit-locales_en.properties';

    // ################## TESTS ##################
    paths.tests = {};
    paths.tests.allUnit = 'test/unit/**/*';
    paths.tests.allE2e = ['test/e2e/**/*Test.js'];

    paths.thirdparties = {
        dir: 'node_modules'
    };

    // TODO: eliminate duplication in functions below
    paths.getSmarteditThirdpartiesFiles = function() {
        return [
			'web/webroot/static-resources/thirdparties/blockumd/blockumd.js',
            'node_modules/lodash/lodash.min.js',
            'node_modules/jquery/dist/jquery.min.js',
            'web/app/noConflict.js',
            'node_modules/angular/angular.min.js',
            'node_modules/angular-resource/angular-resource.min.js',
            'node_modules/angular-cookies/angular-cookies.min.js',
            'node_modules/angular-mocks/angular-mocks.min.js',
            'node_modules/angular-mocks-async/dist/angular-mocks-async.min.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.min.js',
            'node_modules/angular-translate/dist/angular-translate.min.js',
            'node_modules/angular-sanitize/angular-sanitize.min.js',
            'node_modules/ui-select/dist/select.min.js',
            'web/webroot/static-resources/thirdparties/polyfills/**/*.js',
            'node_modules/intersection-observer/intersection-observer.min.js',
            'node_modules/moment/min/moment-with-locales.min.js',
            'node_modules/element-resize-detector/dist/element-resize-detector.min.js',
            'node_modules/popper.js/dist/umd/popper.min.js',
            'node_modules/ng-infinite-scroll/build/ng-infinite-scroll.min.js',
            'node_modules/crypto-js/crypto-js.min.js',
            'node_modules/reflect-metadata/Reflect.min.js',
            'web/webroot/static-resources/thirdparties/blockumd/unblockumd.js'
        ];
    };
    paths.getSmarteditThirdpartiesDevFiles = function() {
        return [
            'web/webroot/static-resources/thirdparties/blockumd/blockumd.js',
            'node_modules/lodash/lodash.js',
            'node_modules/jquery/dist/jquery.js',
            'web/app/noConflict.js',
            'node_modules/angular/angular.js',
            'node_modules/angular-resource/angular-resource.js',
            'node_modules/angular-cookies/angular-cookies.js',
            'node_modules/angular-mocks/angular-mocks.js',
            'node_modules/angular-mocks-async/dist/angular-mocks-async.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
            'node_modules/angular-translate/dist/angular-translate.js',
            'node_modules/angular-sanitize/angular-sanitize.js',
            'node_modules/ui-select/dist/select.js',
            'web/webroot/static-resources/thirdparties/polyfills/**/*.js',
            'node_modules/intersection-observer/intersection-observer.js',
            'node_modules/moment/min/moment-with-locales.js',
            'node_modules/element-resize-detector/dist/element-resize-detector.js',
            'node_modules/popper.js/dist/umd/popper.js',
            'node_modules/ng-infinite-scroll/build/ng-infinite-scroll.js',
            'node_modules/crypto-js/crypto-js.js',
            'node_modules/reflect-metadata/Reflect.js',
            'web/webroot/static-resources/thirdparties/blockumd/unblockumd.js'
        ];
    };

    paths.getContainerThirdpartiesDevFiles = function() {
        return [
            'node_modules/lodash/lodash.js',
            'node_modules/jquery/dist/jquery.js',
            'web/app/noConflict.js',
            'node_modules/angular/angular.js',
            'node_modules/angular-animate/angular-animate.js',
            'node_modules/angular-route/angular-route.js',
            'node_modules/angular-resource/angular-resource.js',
            'node_modules/angular-cookies/angular-cookies.js',
            'node_modules/angular-mocks/angular-mocks.js',
            'node_modules/angular-mocks-async/dist/angular-mocks-async.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
            'node_modules/angular-translate/dist/angular-translate.js',
            'node_modules/angular-sanitize/angular-sanitize.js',
            'node_modules/ui-select/dist/select.js',
            'web/webroot/static-resources/thirdparties/polyfills/**/*.js',
            'node_modules/intersection-observer/intersection-observer.js',
            'node_modules/scriptjs/dist/script.js',
            'node_modules/moment/min/moment-with-locales.js',
            'node_modules/element-resize-detector/dist/element-resize-detector.js',
            'node_modules/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js',
            'node_modules/angular-ui-tree/dist/angular-ui-tree.js',
            'node_modules/popper.js/dist/umd/popper.js',
            'node_modules/ng-infinite-scroll/build/ng-infinite-scroll.js',
            'node_modules/crypto-js/crypto-js.js',
            'node_modules/reflect-metadata/Reflect.js'
        ];
    };
    paths.containerThirdpartiesFiles = function() {
        return [
            'node_modules/lodash/lodash.min.js',
            'node_modules/jquery/dist/jquery.min.js',
            'web/app/noConflict.js',
            'node_modules/angular/angular.min.js',
            'node_modules/angular-animate/angular-animate.min.js',
            'node_modules/angular-route/angular-route.min.js',
            'node_modules/angular-resource/angular-resource.min.js',
            'node_modules/angular-cookies/angular-cookies.min.js',
            'node_modules/angular-mocks/angular-mocks.min.js',
            'node_modules/angular-mocks-async/dist/angular-mocks-async.min.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.min.js',
            'node_modules/angular-translate/dist/angular-translate.min.js',
            'node_modules/angular-sanitize/angular-sanitize.min.js',
            'node_modules/ui-select/dist/select.min.js',
            'web/webroot/static-resources/thirdparties/polyfills/**/*.js',
            'node_modules/intersection-observer/intersection-observer.min.js',
            'node_modules/scriptjs/dist/script.min.js',
            'node_modules/moment/min/moment-with-locales.min.js',
            'node_modules/element-resize-detector/dist/element-resize-detector.min.js',
            'node_modules/eonasdan-bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js',
            'node_modules/angular-ui-tree/dist/angular-ui-tree.min.js',
            'node_modules/popper.js/dist/umd/popper.min.js',
            'node_modules/ng-infinite-scroll/build/ng-infinite-scroll.min.js',
            'node_modules/crypto-js/crypto-js.min.js',
            'node_modules/reflect-metadata/Reflect.min.js'
        ];
    };

    paths.watchFiles = ['web/app/common/**/*', 'web/app/smarteditloader/**/*', 'web/app/smartedit/**/*', 'web/app/smarteditcontainer/**/*'];

    /**
     * Task: COPY: toDummystorefront
     */
    paths.copyToDummystorefront = global.smartedit.bundlePaths.bundleRoot + '/test/e2e/dummystorefront/imports/generated/';
    paths.sharedSmarteditForTests = global.smartedit.bundlePaths.bundleRoot + '/test/unit/generated/';


    /**
     * Note: config includes Gruntfile.js
     * (+ any other config @ the root)
     *
     *
     * Task: jsbeautifier
     * Ext: js, html  ????css
     * Type: source, config, allTest
     */
    paths.jsbeautifier = [
        'web/app/+(common|smartedit)*/**/*.+(js|html)',
        'Gruntfile.js',
        'test/**/*.+(js|html)',
        'smartedit-build/**/*.+(js|html)',
        '!**/generated/**/*',
        '!' + global.smartedit.bundlePaths.bundleRoot + '/webroot/**/*'
    ];

    /**
     * Task: jshint
     * Ext: js
     * Type: source, config, allTest
     */
    paths.jshint = [
        'web/app/+(common|smartedit)*/**/*.js',
        'web/webApplicationInjector.js',
        'test/**/*.js',
        '!**/generated/**/*'
    ];

    /**
     * Task: tsformatter
     * Ext: ts
     * Type: source, config, allTest
     */
    paths.tsformatter = [
        'web/app/**/*.ts',
        'test/**/*.ts',
        'smartedit-build/**/*.ts',
        '!' + global.smartedit.bundlePaths.bundleRoot + '/**/*',
        '!**/generated/**/*'
    ];

    /**
     * Task: tslint
     * Ext: ts, js
     * Type: source, config, allTest
     */
    paths.tslint = [
        'web/app/+(common|smartedit)*/**/*.+(ts|js)',
        'test/**/*.ts',
        '!' + global.smartedit.bundlePaths.bundleRoot + '/**/*',
        '!**/generated/**/*'
    ];

    /**
     * Task: ngDocs
     * Ext: js, ts
     * Type: smartedit+CommonSource, smarteditContainer+CommonSources
     */
    paths.ngdocs = {};
    paths.ngdocs.smartedit = [
        'web/app/+(common|smartedit)/**/*.+(ts|js)',
        '!**/generated/**/*'
    ];
    paths.ngdocs.smarteditcontainer = [
        'web/app/+(common|smarteditcontainer)/**/*.+(ts|js)',
        '!**/generated/**/*'
    ];

    /**
     * Task: checkI18nKeysCompliancy
     * Ext: js, ts, html
     * Type: sources
     */
    paths.checkI18nKeysCompliancy = [
        'web/app/+(common|smartedit)*/**/*.+(ts|js|html)',
    ];

    /**
     * Task: checkNoFocus
     * Ext: js, ts
     * Type: test
     */
    paths.checkNoFocus = [
        'test/**/*.+(js|ts)',
    ];

    //TODO
    // /**
    //  * Task: watch
    //  * Ext: js, ts
    //  * Type: test
    //  */
    // paths.watch = {};
    // paths.watch. = [
    //     'test/**/*.+(js|ts)',
    // ];


    /**
     * Task: uglify, concat, copy, clean, webpack
     * -- Leave as is for now, revist later, too much cherry-picking
     *
     *
     *
     *
     *
     */

    /**
     * Entrypoints
     */
    paths.entrypoints = {
        sharedSmarteditForTests: './smartedit-build/test/unit/sharedSmarteditForTests.ts',   
        smartedit: './jsTarget/web/app/smartedit/index.ts',    
        smarteditbootstrap: './jsTarget/web/app/smartedit/smarteditbootstrap.ts',
        smarteditloader: './jsTarget/web/app/smarteditloader/smarteditloader.ts',
        smarteditcontainer: './jsTarget/web/app/smarteditcontainer/index.ts'
    };

    /**
     * SmarteditProperties
     */
    paths.smarteditproperties = {
        smarteditcommons: './jsTarget/web/app/common',
        smartedit: './jsTarget/web/app/smartedit'
    };

    /**
     * SmarteditContainerProperties
     */
    paths.smarteditcontainerproperties = {
        smarteditcontainer: './jsTarget/web/app/smarteditcontainer'
    };

    /**
     * Code coverage
     */
    paths.coverage = {
        dir: './jsTarget/test/coverage',
        smarteditDirName: 'smartedit',
        smarteditcontainerDirName: 'smarteditcontainer'
    };

    // app
    paths.webAppTargetTs = 'jsTarget/web/app/**/*.ts';

    /**
     * TypeScript instrumentation
     */
    paths.tools = {
        seCommonsInstrumenter: {
            src: './smartedit-custom-build/tools/tsInstrument/*.ts',
            dest: './smartedit-custom-build/tools/tsInstrument/generated/',
            js: './smartedit-custom-build/tools/tsInstrument/generated/secommonsInstrumenter.js'
        },
        seInjectableInstrumenter: {
            src: global.smartedit.bundlePaths.bundleRoot+'/config/tools/tsInstrument/*.ts',
            dest: global.smartedit.bundlePaths.bundleRoot+'/config/tools/tsInstrument/generated/'
        }
    };

    /**
     * Webfont
     */
    paths.webfont = {
        src: 'web/app/smartedit/styling/icons/*.svg',
        dest: 'web/webroot/static-resources/dist/smartedit/fonts/',
        destLess: 'web/app/smartedit/styling/shared/',
        relativeFontPath: '../fonts/'
    };

    return paths;

}();
