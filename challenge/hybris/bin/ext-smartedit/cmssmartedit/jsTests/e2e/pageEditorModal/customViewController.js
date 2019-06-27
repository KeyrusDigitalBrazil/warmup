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
/* jshint unused:false, undef:false */
angular.module('customViewModule', ['templateCacheDecoratorModule', 'cmssmarteditContainerTemplates', 'ngMockE2E', 'smarteditServicesModule', 'renderServiceModule', 'genericEditorModule', 'resourceLocationsModule', 'backendMocksUtilsModule'])
    .constant('PATH_TO_CUSTOM_VIEW', 'pageEditorModal/customView.html')
    .run(function($httpBackend, sharedDataService, restServiceFactory, backendMocksUtils) {
        function prepareBackendMocksForPageInfo() {

            backendMocksUtils.getBackendMock('componentPUTMock').respond(function(method, url, data, headers, params) {
                var parsedData = JSON.parse(data);

                if (parsedData.uid === 'variationContentPage' && !parsedData.name) {
                    return [400, {
                        "errors": [{
                            "message": "This field is required.",
                            "reason": "invalid",
                            "subject": "name",
                            "subjectType": "parameter",
                            "type": "ValidationError"
                        }]
                    }];
                } else {
                    return [200, PAGE_BY_UID[parsedData.uid]];
                }
            });
        }

        function prepareBackendMocksForPrimaryContentPage() {
            // Content primary page has no fallbacks
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/primaryContentPage\/fallbacks/)
                .respond({
                    uids: []
                });

            // Content primary page has multiple variations
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/primaryContentPage\/variations$/)
                .respond({
                    uids: ['someVariationContentPageUid', 'someOtherVariationContentPageUid']
                });

            // Each variation has a name and creation time
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\?uids=someVariationContentPageUid&uids=someOtherVariationContentPageUid/)
                .respond({
                    pages: [{
                        uid: 'someVariationContentPageUid',
                        creationtime: "2016-07-07T14:33:37+0000",
                        name: "Some Variation Content Page"
                    }, {
                        uid: 'someOtherVariationContentPageUid',
                        creationtime: "2016-07-07T14:33:37+0000",
                        name: "Some Other Variation Content Page"
                    }]
                });

            // Each variation has a number of restrictions
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagesrestrictions\?pageId=someVariationContentPageUid/)
                .respond({
                    pageRestrictionList: [{
                        pageId: "someOtherVariationContentPageUid",
                        restrictionId: "restrictionUid1"
                    }, {
                        pageId: "someOtherVariationContentPageUid",
                        restrictionId: "restrictionUid2"
                    }]
                });

            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagesrestrictions\?pageId=someOtherVariationContentPageUid/)
                .respond({
                    pageRestrictionList: [{
                        pageId: "someOtherVariationContentPageUid",
                        restrictionId: "restrictionUid3"
                    }]
                });
        }

        function prepareBackendMocksForVariationContentPage() {
            // Content variation page has a fallback
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/variationContentPage\/fallbacks/)
                .respond({
                    uids: ['primaryContentPage']
                });
        }

        function prepareBackendMocksForPrimaryCategoryPage() {
            // Category primary page has no fallbacks
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/primaryCategoryPage\/fallbacks/)
                .respond({
                    uids: []
                });

            // Category primary page has multiple variations
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/primaryCategoryPage\/variations$/)
                .respond({
                    uids: []
                });
        }

        function prepareBackendMocksForVariationCategoryPage() {
            // Category variation page has a fallback
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/variationCategoryPage\/fallbacks/)
                .respond({
                    uids: ['primaryCategoryPage']
                });
        }

        function prepareBackendMocksForPrimaryProductPage() {
            // Product primary page has no fallbacks
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/primaryProductPage\/fallbacks/)
                .respond({
                    uids: []
                });

            // Product primary page has multiple variations
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/primaryProductPage\/variations$/)
                .respond({
                    uids: ['someVariationProductPageUid', 'someOtherVariationProductPageUid']
                });

            // Each variation has a name and creation time
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\?uids=someVariationProductPageUid&uids=someOtherVariationProductPageUid/)
                .respond({
                    pages: [{
                        creationtime: "2016-07-07T14:33:37+0000",
                        name: "Some Variation Product Page",
                        uid: 'someVariationProductPageUid'
                    }, {
                        creationtime: "2016-07-07T14:33:37+0000",
                        name: "Some Other Variation Product Page",
                        uid: 'someOtherVariationProductPageUid'
                    }]
                });

            // Each variation has a number of restrictions
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagesrestrictions\?pageId=someVariationProductPageUid/)
                .respond({
                    pageRestrictionList: [{
                        pageId: "someOtherVariationProductPageUid",
                        restrictionId: "restrictionUid1"
                    }, {
                        pageId: "someOtherVariationProductPageUid",
                        restrictionId: "restrictionUid2"
                    }]
                });

            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagesrestrictions\?pageId=someOtherVariationProductPageUid/)
                .respond({
                    pageRestrictionList: [{
                        pageId: "someOtherVariationProductPageUid",
                        restrictionId: "restrictionUid3"
                    }]
                });
        }

        function prepareBackendMocksForVariationProductPage() {
            // Product variation page has a fallback
            $httpBackend
                .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/variationProductPage\/fallbacks/)
                .respond({
                    uids: ['primaryProductPage']
                });
        }

        prepareBackendMocksForPageInfo();
        prepareBackendMocksForPrimaryContentPage();
        prepareBackendMocksForVariationContentPage();
        prepareBackendMocksForPrimaryCategoryPage();
        prepareBackendMocksForVariationCategoryPage();
        prepareBackendMocksForPrimaryProductPage();
        prepareBackendMocksForVariationProductPage();
    })
    .controller('customViewController', function(LANGUAGE_RESOURCE_URI, sharedDataService, restServiceFactory, $httpBackend, pageEditorModalService, languageService, I18N_RESOURCE_URI) {
        this.openModal = function(uid) {

            var page = {
                uid: uid,
                uuid: uid,
                typeCode: 'ContentPage',
                template: 'gridAccountPageTemplate',
                name: "Brands Category Page",
                numberOfRestrictions: 1,
                onlyOneRestrictionMustApply: true,
                uriContext: {
                    CURRENT_CONTEXT_CATALOG: "apparel-ukContentCatalog",
                    CURRENT_CONTEXT_CATALOG_VERSION: "Staged",
                    CURRENT_CONTEXT_SITE_ID: "apparel-uk"
                }
            };
            pageEditorModalService.open(page);
        };
    });
try {
    angular.module('smarteditcontainer').requires.push('customViewModule');
} catch (e) {}
