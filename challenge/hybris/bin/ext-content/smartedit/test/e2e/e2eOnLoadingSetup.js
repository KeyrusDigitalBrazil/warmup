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
(function() {

    angular.module('e2eOnLoadingSetup', ['ngMockE2E', 'resourceLocationsModule'])
        .constant('STOREFRONT_URI', 'http://127.0.0.1:9000/test/utils/storefront.html')
        .run(function(experienceService, STOREFRONT_URI, $location, $timeout, $httpBackend, parseQuery) {

            $httpBackend.whenGET(/test\/e2e/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/electronics\/contentcatalogs/).respond({
                catalogs: [{
                    catalogId: "electronicsContentCatalog",
                    name: {
                        en: "Electronics Content Catalog"
                    },
                    versions: [{
                        version: "Online",
                        active: true,
                        uuid: "electronicsContentCatalog/Online",
                    }, {
                        version: "Staged",
                        active: false,
                        uuid: "electronicsContentCatalog/Staged"
                    }]
                }]
            });

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/contentcatalogs/).respond({
                catalogs: [{
                    catalogId: "apparel-ukContentCatalog",
                    name: {
                        en: "Apparel UK Content Catalog"
                    },
                    versions: [{
                        version: "Online",
                        active: true,
                        uuid: "apparel-ukContentCatalog/Online"
                    }, {
                        version: "Staged",
                        active: false,
                        uuid: "apparel-ukContentCatalog/Staged"
                    }]
                }]
            });

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/electronics\/productcatalogs/).respond({

                catalogs: [{
                    catalogId: "electronicsProductCatalog",
                    name: {
                        en: "Electronics Product Catalog",
                        de: "Produktkatalog Handys"
                    },
                    versions: [{
                        active: true,
                        uuid: "electronicsProductCatalog/Online",
                        version: "Online"
                    }, {
                        active: false,
                        uuid: "electronicsProductCatalog/Staged",
                        version: "Staged"
                    }]
                }]
            });

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/productcatalogs/).respond({

                catalogs: [{
                    catalogId: "apparel-ukProductCatalog-clothing",
                    name: {
                        en: "Clothing Product Catalog"
                    },
                    versions: [{
                        active: true,
                        uuid: "apparel-ukProductCatalog-clothing/Online",
                        version: "Online"
                    }, {
                        active: false,
                        uuid: "apparel-ukProductCatalog-clothing/Staged",
                        version: "Staged"
                    }, ]
                }, {
                    catalogId: "apparel-ukProductCatalog-shoes",
                    name: {
                        en: "Shoes Product Catalog"
                    },
                    versions: [{
                        active: true,
                        uuid: "apparel-ukProductCatalog-shoes/Online",
                        version: "Online"
                    }, {
                        active: false,
                        uuid: "apparel-ukProductCatalog-shoes/Staged-1",
                        version: "Staged-1"
                    }, {
                        active: false,
                        uuid: "apparel-ukProductCatalog-shoes/Staged-2",
                        version: "Staged-2"
                    }]
                }]
            });

            var allSites = [{
                previewUrl: '/smartedit-build/test/e2e/dummystorefront/dummystorefrontElectronics.html',
                name: {
                    en: "Electronics"
                },
                redirectUrl: 'redirecturlElectronics',
                uid: 'electronics',
                contentCatalogs: ['electronicsContentCatalog']
            }, {
                previewUrl: '/test/utils/storefront.html',
                name: {
                    en: "Apparels"
                },
                redirectUrl: 'redirecturlApparels',
                uid: 'apparel-uk',
                contentCatalogs: ['apparel-ukContentCatalog']
            }];

            $httpBackend.whenGET(/cmswebservices\/v1\/sites$/).respond({
                sites: allSites
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\?catalogIds=.*/).respond(function(method, url) {
                var params = parseQuery(url);
                var catalogIds = params.catalogIds && params.catalogIds.split(',');

                if (catalogIds) {
                    var filteredItems = allSites.filter(function(site) {
                        return catalogIds.indexOf(site.contentCatalogs[site.contentCatalogs.length - 1]) > -1;
                    });

                    return [200, {
                        sites: filteredItems
                    }];
                }

                return [200, {
                    sites: []
                }];

            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/electronics\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=PreviewData\&mode=DEFAULT/).respond({
                attributes: [{
                    cmsStructureType: 'EditableDropdown',
                    qualifier: 'previewCatalog',
                    i18nKey: 'experience.selector.catalog',
                }, {
                    cmsStructureType: 'EditableDropdown',
                    qualifier: 'language',
                    i18nKey: 'experience.selector.language',
                    dependsOn: 'previewCatalog'
                }, {
                    cmsStructureType: 'DateTime',
                    qualifier: 'time',
                    i18nKey: 'experience.selector.date.and.time'
                }, {
                    cmsStructureType: 'ProductCatalogVersionsSelector',
                    qualifier: 'productCatalogVersions',
                    i18nKey: 'experience.selector.catalogversions'
                }, {
                    cmsStructureType: 'ShortString',
                    qualifier: 'newField',
                    i18nKey: 'experience.selector.newfield'
                }]
            });

            $httpBackend.whenPOST(/thepreviewTicketURI/).respond(function(method, url, data) {

                var returnedPayload = angular.extend({}, data, {
                    ticketId: 'dasdfasdfasdfa',
                    resourcePath: STOREFRONT_URI
                });

                return [200, returnedPayload];
            });

            experienceService.loadExperience({
                siteId: "apparel-uk",
                catalogId: "apparel-ukContentCatalog",
                catalogVersion: "Staged"
            });
        });

    angular.module('smarteditcontainer').constant('isE2eTestingActive', true);
    angular.module('smarteditcontainer').requires.push('e2eOnLoadingSetup');

})();
