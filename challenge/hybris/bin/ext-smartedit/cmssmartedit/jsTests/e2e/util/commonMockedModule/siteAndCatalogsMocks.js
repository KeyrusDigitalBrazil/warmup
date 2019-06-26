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
/* jshint unused:false */
angular.module('siteAndCatalogsMocks', ['ngMockE2E', 'functionsModule'])
    .constant('STOREFRONT_URI', 'http://127.0.0.1:9000/jsTests/e2e/storefront.html')
    .run(function($httpBackend, parseQuery, STOREFRONT_URI) {

        var apparelContentCatalog_global = {
            "catalogId": "apparelContentCatalog",
            "name": {
                "en": "Apparel Content Catalog",
            },
            "versions": [{
                "active": true,
                "pageDisplayConditions": [{
                    "options": [{
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "ProductPage"
                }, {
                    "options": [{
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "CategoryPage"
                }, {
                    "options": [{
                        "label": "page.displaycondition.primary",
                        "id": "PRIMARY"
                    }, {
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "ContentPage"
                }],
                "thumbnailUrl": "/medias/Homepage.png",
                "uuid": "apparelContentCatalog/Online",
                "version": "Online"
            }, {
                "active": false,
                "pageDisplayConditions": [{
                    "options": [{
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "ProductPage"
                }, {
                    "options": [{
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "CategoryPage"
                }, {
                    "options": [{
                        "label": "page.displaycondition.primary",
                        "id": "PRIMARY"
                    }, {
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "ContentPage"
                }],
                "thumbnailUrl": "/medias/Homepage.png",
                "uuid": "apparelContentCatalog/Staged",
                "version": "Staged"
            }]
        };

        var allSites = [{
            previewUrl: '/jsTests/e2e/storefront.html',
            name: {
                en: "Apparel - UK"
            },
            redirectUrl: 'redirecturlApparels',
            uid: 'apparel-uk',
            contentCatalogs: ['apparelContentCatalog', 'apparel-ukContentCatalog']
        }, {
            previewUrl: '/jsTests/e2e/storefront.html',
            name: {
                en: "Apparel - DE"
            },
            redirectUrl: 'redirecturlApparels',
            uid: 'apparel-de',
            contentCatalogs: ['apparelContentCatalog', 'apparel-deContentCatalog']
        }, {
            previewUrl: '/jsTests/e2e/storefront.html',
            name: {
                en: "Apparel UK and EU"
            },
            redirectUrl: 'redirecturlApparels',
            uid: 'apparel',
            contentCatalogs: ['apparelContentCatalog']
        }];

        $httpBackend.whenGET(/cmswebservices\/v1\/sites$/).respond({
            sites: allSites
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\?catalogIds=.*/).respond(function(method, url, data, headers) {
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

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel\/contentcatalogs/).respond({
            catalogs: [apparelContentCatalog_global]
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-uk\/contentcatalogs/).respond(function(method, url, data, headers) {
            var sendOnlyPrimaryDisplayCondition = JSON.parse(window.sessionStorage.getItem('sendOnlyPrimaryDisplayCondition'));

            var apparelUkContentCatalogOnlineVersionPayload = {
                "active": true,
                "pageDisplayConditions": [{
                    "options": [{
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "ProductPage"
                }, {
                    "options": [{
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "CategoryPage"
                }, {
                    "options": [{
                        "label": "page.displaycondition.primary",
                        "id": "PRIMARY"
                    }, {
                        "label": "page.displaycondition.variation",
                        "id": "VARIATION"
                    }],
                    "typecode": "ContentPage"
                }],
                "uuid": "apparel-ukContentCatalog/Online",
                "version": "Online"
            };

            if (sendOnlyPrimaryDisplayCondition) {
                apparelUkContentCatalogOnlineVersionPayload = {
                    "active": true,
                    "pageDisplayConditions": [{
                        "options": [],
                        "typecode": "ProductPage"
                    }, {
                        "options": [],
                        "typecode": "CategoryPage"
                    }, {
                        "options": [{
                            "label": "page.displaycondition.primary",
                            "id": "PRIMARY"
                        }],
                        "typecode": "ContentPage"
                    }],
                    "uuid": "apparel-ukContentCatalog/Online",
                    "version": "Online"
                };
            }


            var payload = {
                catalogs: [apparelContentCatalog_global, {
                    catalogId: "apparel-ukContentCatalog",
                    name: {
                        en: "Apparel UK Content Catalog"
                    },
                    versions: [{
                        "active": false,
                        "pageDisplayConditions": [{
                            "options": [{
                                "label": "page.displaycondition.variation",
                                "id": "VARIATION"
                            }],
                            "typecode": "ProductPage"
                        }, {
                            "options": [{
                                "label": "page.displaycondition.variation",
                                "id": "VARIATION"
                            }],
                            "typecode": "CategoryPage"
                        }, {
                            "options": [{
                                "label": "page.displaycondition.primary",
                                "id": "PRIMARY"
                            }, {
                                "label": "page.displaycondition.variation",
                                "id": "VARIATION"
                            }],
                            "typecode": "ContentPage"
                        }],
                        "uuid": "apparel-ukContentCatalog/Staged",
                        "version": "Staged",
                        "homepage": {
                            "current": {
                                "uid": "homepage",
                                "name": "Homepage",
                                "catalogVersionUuid": "apparel-ukContentCatalog/Staged"
                            },
                            "old": {
                                "uid": "thirdpage",
                                "name": "Some Other Page",
                                "catalogVersionUuid": "apparel-ukContentCatalog/Staged"
                            },
                        },
                    }, apparelUkContentCatalogOnlineVersionPayload]
                }]
            };

            return [200, payload];
        });

        $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-de\/contentcatalogs/).respond({
            catalogs: [apparelContentCatalog_global, {
                catalogId: "apparel-deContentCatalog",
                name: {
                    en: "Apparel DE Content Catalog"
                },
                versions: [{
                    "active": false,
                    "pageDisplayConditions": [{
                        "options": [{
                            "label": "page.displaycondition.variation",
                            "id": "VARIATION"
                        }],
                        "typecode": "ProductPage"
                    }, {
                        "options": [{
                            "label": "page.displaycondition.variation",
                            "id": "VARIATION"
                        }],
                        "typecode": "CategoryPage"
                    }, {
                        "options": [{
                            "label": "page.displaycondition.primary",
                            "id": "PRIMARY"
                        }, {
                            "label": "page.displaycondition.variation",
                            "id": "VARIATION"
                        }],
                        "typecode": "ContentPage"
                    }],
                    "uuid": "apparel-deContentCatalog/Staged",
                    "version": "Staged"
                }, {
                    "active": true,
                    "pageDisplayConditions": [{
                        "options": [{
                            "label": "page.displaycondition.variation",
                            "id": "VARIATION"
                        }],
                        "typecode": "ProductPage"
                    }, {
                        "options": [{
                            "label": "page.displaycondition.variation",
                            "id": "VARIATION"
                        }],
                        "typecode": "CategoryPage"
                    }, {
                        "options": [{
                            "label": "page.displaycondition.primary",
                            "id": "PRIMARY"
                        }, {
                            "label": "page.displaycondition.variation",
                            "id": "VARIATION"
                        }],
                        "typecode": "ContentPage"
                    }],
                    "uuid": "apparel-deContentCatalog/Online",
                    "version": "Online"
                }]
            }]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/targets\?mode=cloneableTo/).respond({
            "versions": [{
                "active": false,
                "name": {
                    "en": "Apparel UK Content Catalog - Staged"
                },
                "uuid": "apparel-ukContentCatalog/Staged",
                "version": "Staged"
            }]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/targets\?mode=cloneableTo/).respond({
            "versions": []
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel\/catalogs\/apparelContentCatalog\/versions\/Staged\/targets\?mode=cloneableTo/).respond({
            "versions": [{
                "active": false,
                "name": {
                    "en": "Apparel Content Catalog - Staged"
                },
                "uuid": "apparelContentCatalog/Staged",
                "version": "Staged"
            }]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel\/catalogs\/apparelContentCatalog\/versions\/Online\/targets\?mode=cloneableTo/).respond({
            "versions": [{
                "active": false,
                "name": {
                    "en": "Apparel Content Catalog - Staged"
                },
                "uuid": "apparelContentCatalog/Staged",
                "version": "Staged"
            }, {
                "active": false,
                "name": {
                    "en": "Apparel Content Catalog - Online"
                },
                "uuid": "apparelContentCatalog/Online",
                "version": "Online"
            }, {
                "active": false,
                "name": {
                    "en": "Apparel UK Content Catalog - Staged"
                },
                "uuid": "apparel-ukContentCatalog/Staged",
                "version": "Staged"
            }, {
                "active": false,
                "name": {
                    "en": "Apparel UK Content Catalog - Online"
                },
                "uuid": "apparel-ukContentCatalog/Online",
                "version": "Online"
            }, {
                "active": false,
                "name": {
                    "en": "Apparel DE Content Catalog - Staged"
                },
                "uuid": "apparel-deContentCatalog/Staged",
                "version": "Staged"
            }, {
                "active": false,
                "name": {
                    "en": "Apparel DE Content Catalog - Online"
                },
                "uuid": "apparel-deContentCatalog/Online",
                "version": "Online"
            }]
        });

        $httpBackend.whenPOST(/thepreviewTicketURI/).respond(function(method, url, data, headers) {
            var dataObject = angular.fromJson(data);
            return [200, {
                ticketId: 'dasdfasdfasdfa',
                resourcePath: STOREFRONT_URI,
                versionId: dataObject.versionId
            }];
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
            }]
        });


        $httpBackend.whenGET(/cmswebservices\/v1\/types\?code=PreviewData\&mode=PREVIEWVERSION/).respond({
            attributes: [{
                cmsStructureType: 'EditableDropdown',
                qualifier: 'previewCatalog',
                i18nKey: 'experience.selector.catalog',
                editable: false
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
            }]
        });
    });
try {
    angular.module('smarteditloader').requires.push('siteAndCatalogsMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('siteAndCatalogsMocks');
} catch (e) {}
