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

        var removeSourceMapping = function(src, filepath) {
            var regEx = new RegExp(/^\/\/#\ssourceMappingURL=.+/, 'm');
            return src.replace(regEx, '');
        };
    
        return {
            targets: [
                'unitUtilsForBundle',
                'outerStyling',
                'webApplicationInjector',
                'smarteditThirdparties',
                'smarteditThirdpartiesDev',
                'containerThirdpartiesDev',
                'containerThirdparties',
                'commonTypes',
                'smarteditTypes',
                'smarteditcontainerTypes'
            ],
            config: function(data, baseConf) {
    
                var paths = require('../paths');
    
                baseConf.unitUtilsForBundle = {
                    src: [
                        'web/app/common/services/constants.js',
                        'web/app/common/services/vendors/yjQuery.js',
                        'web/app/common/services/rest/resourceLocations.js',
                        'web/app/common/services/vendors/ylodash.js',
                        'web/app/common/components/genericEditor/dropdownPopulators/dropdownPopulatorInterface.js',
                        'web/app/common/services/templateCacheDecorator.js'
                    ],
                    dest: global.smartedit.bundlePaths.bundleRoot + '/test/unit/generated/unitUtils.js'
                };
    
                baseConf.outerStyling = {
                    src: [
                        paths.web.webroot.staticResources.smartEdit.css.temp.outerVendor,
                        paths.web.webroot.staticResources.smartEdit.css.temp.outerStyling
                    ],
                    dest: paths.web.webroot.staticResources.smartEdit.css.outerStyling
                };
    
                baseConf.webApplicationInjector = {
                    src: [
                        paths.thirdparties.dir + '/scriptjs/dist/script.min.js',
                        'jsTarget/web/webApplicationInjector.js',
                    ],
                    dest: 'jsTarget/webApplicationInjector.js'
                };
        
                baseConf.smarteditThirdparties = {
                    src: paths.getSmarteditThirdpartiesFiles(),
                    options: {
                        process: removeSourceMapping
                    },
                    dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/js/prelibraries.js'
                };
    
                baseConf.smarteditThirdpartiesDev = {
                    src: paths.getSmarteditThirdpartiesDevFiles(),
                    dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/js/prelibraries.js'
                };
    
                baseConf.containerThirdpartiesDev = {
                    src: paths.getContainerThirdpartiesDevFiles(),

                    dest: 'web/webroot/static-resources/dist/smartedit/js/thirdparties.js'
                };
    
                baseConf.containerThirdparties = {
                    src: paths.containerThirdpartiesFiles(),
                    options: {
                        process: removeSourceMapping
                    },
                    dest: 'web/webroot/static-resources/dist/smartedit/js/thirdparties.js'
                };
    
                baseConf.smarteditcommonsTypes = {
                    flatten: true,
                    src: ['temp/types/common/**/*.d.ts'],
                    dest: global.smartedit.bundlePaths.bundleRoot + '/@types/smarteditcommons/index.d.ts'
                };
    
                baseConf.smarteditTypes = {
                    flatten: true,
                    src: ['temp/types/smartedit/**/*.d.ts'],
                    dest: global.smartedit.bundlePaths.bundleRoot + '/@types/smartedit/index.d.ts'
                };
    
                baseConf.smarteditcontainerTypes = {
                    flatten: true,
                    src: ['temp/types/smarteditcontainer/**/*.d.ts'],
                    dest: global.smartedit.bundlePaths.bundleRoot + '/@types/smarteditcontainer/index.d.ts'
                };
                return baseConf;
            }
        };
    
    };