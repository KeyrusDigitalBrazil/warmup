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
            'toDummystorefront',
            'sources',
            'dev',
            'ckeditor',
            'thirdPartySourceMaps'
        ],
        config: function(data, conf) {
            var paths = require('../paths');

            return {

                ckeditor: {
                    files: [{
                        cwd: paths.thirdparties.dir + '/ckeditor',
                        expand: true,
                        flatten: false,
                        src: [
                            '**',
                            '!**/samples/**',
                            '!package.json'
                        ],
                        dest: 'web/webroot/static-resources/thirdparties/ckeditor'
                    }]
                },

                images: {
                    files: [{
                        cwd: paths.thirdparties.dir + '/select2',
                        expand: true,
                        flatten: false,
                        src: [
                            '**/*.+(png)'
                        ],
                        dest: 'web/webroot/static-resources/thirdparties/select2'
                    }]
                },
                toDummystorefront: {
                    files: [{
                        expand: true,
                        flatten: true,
                        src: [
                            paths.thirdparties.dir + '/angular/angular.min.js',// needed for fakeAngularEmptyPage.html
                            paths.thirdparties.dir + '/lodash/lodash.min.js',
                            paths.thirdparties.dir + '/jquery/dist/jquery.min.js',
                            paths.thirdparties.dir + '/scriptjs/dist/script.min.js',
                            'web/webroot/static-resources/thirdparties/polyfills/*.js', // for IE
                            'web/webroot/static-resources/webApplicationInjector.js',
                            'node_modules/bootstrap/dist/css/bootstrap.css'
                        ],
                        dest: paths.copyToDummystorefront
                    }, {
                        expand: true, // TODO remove this, we shouldn't have to give out the font in this hackish way
                        flatten: false,
                        cwd: 'web/webroot/static-resources/dist/smartedit',
                        src: [
                            'fonts/**/*'
                        ],
                        dest: global.smartedit.bundlePaths.bundleRoot + '/test/e2e/dummystorefront/imports'
                    }]
                },
                sources: {
                    files: [
                        // includes files within path
                        {
                            expand: true,
                            flatten: false,
                            src: [
                                'web/webApplicationInjector.js',
                                paths.common.allJs, 'web/app/common/**/*.ts',
                                'web/app/smarteditloader/**/*.js', 'web/app/smarteditloader/**/*.ts',
                                paths.web.smarteditcontainer.allJs, 'web/app/smarteditcontainer/**/*.ts',
                                paths.web.smartEdit.allJs, 'web/app/smartedit/**/*.ts'
                            ],
                            dest: 'jsTarget/'
                        }
                    ]
                },
                dev: {
                    files: [
                        // includes files within path
                        {
                            expand: true,
                            flatten: true,
                            src: ['jsTarget/smarteditloader.js*(.map)'],
                            dest: paths.web.webroot.staticResources.dir + '/smarteditloader/js'
                        }, {
                            expand: true,
                            flatten: true,
                            src: ['jsTarget/smarteditcontainer.js*(.map)'],
                            dest: paths.web.webroot.staticResources.dir + '/smarteditcontainer/js'
                        }, {
                            expand: true,
                            flatten: true,
                            src: ['jsTarget/smartedit.js*(.map)'],
                            dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/js'
                        }, {
                            expand: true,
                            flatten: true,
                            src: ['jsTarget/smarteditbootstrap.js*(.map)'],
                            dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/js'
                        }, {
                            expand: true,
                            flatten: true,
                            src: [paths.techne.allFonts],
                            dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/fonts'
                        }
                    ]
                },
                thirdPartySourceMaps: {
                    /**
                     * This copying is only to remove console errors from some browsers
                     * CMSX-6695, CMSX-6695, CMSX-4969
                     */
                    files: [{
                            expand: true,
                            flatten: true,
                            src: [
                                'node_modules/ui-select/dist/select.min.css.map',
                                'node_modules/popper.js/dist/umd/popper.min.js.map'
                            ],
                            dest: 'web/webroot/static-resources/dist/smartedit/css/'
                        }, {
                            expand: true,
                            flatten: true,
                            src: [
                                'node_modules/ui-select/dist/select.min.css.map',
                                'node_modules/popper.js/dist/umd/popper.min.js.map'
                            ],
                            dest: 'web/webroot/static-resources/smarteditcontainer/css/'
                        }
                    ]
                }
            };
        }
    };
};
