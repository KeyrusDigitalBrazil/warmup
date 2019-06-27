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
angular
    .module('OuterMocks', ['ngMockE2E', 'smarteditServicesModule', 'resourceLocationsModule'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .run(
        function($httpBackend, languageService, I18N_RESOURCE_URI, parseQuery) {

            $httpBackend.whenGET(/test\/e2e/).passThrough();
            $httpBackend.whenGET(/static-resources/).passThrough();

            var map = [{
                "value": "[\"*\"]",
                "key": "whiteListedStorefronts"
            }, {
                "value": "\"thepreviewTicketURI\"",
                "key": "previewTicketURI"
            }, {
                "value": "\"/cmswebservices/v1/i18n/languages\"",
                "key": "i18nAPIRoot"
            }, {
                "value": "{\"smartEditLocation\":\"/test/e2e/landingPage/innerMocks.js\"}",
                "key": "applications.InnerMocks"
            }];

            $httpBackend
                .whenGET(document.location.origin + "/cmswebservices/i18n/languages/" + languageService.getBrowserLocale())
                .respond({
                    'se.landingpage.title': 'Your Touchpoints',
                    'cataloginfo.pagelist': 'PAGE LIST',
                    'cataloginfo.lastsynced': 'LAST SYNCED',
                    'cataloginfo.button.sync': 'SYNC',
                });

            $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
                'sync.confirm.msg': 'this {{catalogName}}is a test'
            });

            $httpBackend.whenGET(/configuration/).respond(
                function() {
                    return [200, map];
                });

            $httpBackend.whenPUT(/configuration/).respond(404);


            $httpBackend.whenPOST(/thepreviewTicketURI/)
                .respond({
                    ticketId: 'dasdfasdfasdfa',
                    resourcePath: document.location.origin + '/test/utils/storefront.html'
                });

            $httpBackend.whenGET(/fragments/).passThrough();


            $httpBackend.whenGET(/cmswebservices\/v1\/languages/).respond({
                languages: [{
                    language: 'en',
                    required: true
                }]
            });

            $httpBackend
                .whenGET("/cmswebservices/v1/i18n/languages/" + languageService.getBrowserLocale())
                .respond({});

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/toys\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'Polish',
                    isocode: 'pl',
                    required: true
                }, {
                    nativeName: 'Italian',
                    isocode: 'it'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/action\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'Polish',
                    isocode: 'pl',
                    required: true
                }, {
                    nativeName: 'Italian',
                    isocode: 'it'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/electronics\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'Polish',
                    isocode: 'pl',
                    required: true
                }, {
                    nativeName: 'Italian',
                    isocode: 'it'
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'French',
                    isocode: 'fr'
                }]
            });


            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/synchronization\/versions\/Staged\/Online/).
            respond({
                date: '2016-01-29T16:25:28+0000',
                status: 'RUNNING'
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-deContentCatalog\/synchronization\/versions\/Staged\/Online/).
            respond({
                date: "2015-01-29T16:25:44+0000",
                status: "ABORTED"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/electronicsContentCatalog\/synchronization\/versions\/Staged\/Online/).
            respond({
                date: "2014-01-28T17:05:29+0000",
                status: "FINISHED"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/actionFiguresContentCatalog\/synchronization\/versions\/Staged\/Online/).
            respond({
                date: "2013-01-28T17:05:29+0000",
                status: "FINISHED"
            });

            $httpBackend.whenPUT(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/synchronization\/versions\/Staged\/Online/).
            respond({
                date: '2016-01-29T16:25:28+0000',
                status: 'RUNNING'
            });

            $httpBackend.whenPUT(/cmswebservices\/v1\/catalogs\/apparel-deContentCatalog\/synchronization\/versions\/Staged\/Online/).
            respond({
                date: "2016-01-29T16:25:44+0000",
                status: "ABORTED"
            });

            $httpBackend.whenPUT(/cmswebservices\/v1\/catalogs\/electronicsContentCatalog\/synchronization\/versions\/Staged\/Online/).
            respond({
                date: "2014-01-28T17:05:29+0000",
                status: "FINISHED"
            });

            $httpBackend.whenPUT(/cmswebservices\/v1\/catalogs\/actionFiguresContentCatalog\/synchronization\/versions\/Staged\/Online/).
            respond({
                date: "2013-01-28T17:05:29+0000",
                status: "FINISHED"
            });

            var allSites = [{
                previewUrl: '/test/utils/storefront.html',
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
            }, {
                previewUrl: '/test/utils/storefront.html',
                name: {
                    en: "Apparels"
                },
                redirectUrl: 'redirecturlApparels',
                uid: 'apparel-de',
                contentCatalogs: ['apparel-deContentCatalog']
            }, {
                previewUrl: '/test/utils/storefront.html',
                name: {
                    en: "Toys"
                },
                redirectUrl: 'redirectSomeOtherSite',
                uid: 'toys',
                contentCatalogs: ['toysContentCatalog']
            }, {
                previewUrl: '/test/utils/storefront.html',
                name: {
                    en: "Action Figures"
                },
                redirectUrl: 'redirectSomeOtherSite',
                uid: 'action',
                contentCatalogs: ['toysContentCatalog', 'actionFiguresContentCatalog']
            }];

            $httpBackend.whenGET(/cmswebservices\/v1\/sites$/).respond({
                sites: allSites
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites/).respond(function(method, url) {
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

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/electronics\/contentcatalogs/).respond({
                catalogs: [{
                    catalogId: "electronicsContentCatalog",
                    name: {
                        en: "Electronics Content Catalog"
                    },
                    versions: [{
                        version: "Online",
                        active: true,
                        uuid: "electronicsContentCatalog/Online"
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

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/apparel-de\/contentcatalogs/).respond({
                catalogs: [{
                    catalogId: "apparel-deContentCatalog",
                    name: {
                        en: "Apparel DE Content Catalog"
                    },
                    versions: [{
                        version: "Online",
                        active: true,
                        uuid: "apparel-deContentCatalog/Online"
                    }, {
                        version: "Staged",
                        active: false,
                        uuid: "apparel-deContentCatalog/Online"
                    }]
                }]
            });

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/toys\/contentcatalogs/).respond({
                catalogs: [{
                    catalogId: "toysContentCatalog",
                    name: {
                        en: "Toys Content Catalog"
                    },
                    versions: [{
                        version: "Online",
                        active: true
                    }, {
                        version: "Staged",
                        active: false
                    }]
                }]
            });

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/action\/contentcatalogs/).respond({
                catalogs: [{
                    catalogId: "toysContentCatalog",
                    name: {
                        en: "Toys Content Catalog"
                    },
                    versions: [{
                        version: "Online",
                        active: true
                    }, {
                        version: "Staged",
                        active: false
                    }]
                }, {
                    catalogId: "actionFiguresContentCatalog",
                    name: {
                        en: "Action Figures Content Catalog"
                    },
                    versions: [{
                        version: "Online",
                        active: true
                    }, {
                        version: "Staged",
                        active: false
                    }]
                }]
            });

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/toys\/productcatalogs/).respond({

                catalogs: [{
                    catalogId: "toysProductCatalog",
                    name: {
                        en: "Toys Product Catalog",
                    },
                    versions: [{
                        active: true,
                        uuid: "toysProductCatalog/Online",
                        version: "Online"
                    }, {
                        active: false,
                        uuid: "toysProductCatalog/Staged",
                        version: "Staged"
                    }]
                }]
            });

            $httpBackend.whenGET(/cmssmarteditwebservices\/v1\/sites\/action\/productcatalogs/).respond({

                catalogs: [{
                    catalogId: "actionProductCatalog",
                    name: {
                        en: "Action Product Catalog",
                    },
                    versions: [{
                        active: true,
                        uuid: "actionProductCatalog/Online",
                        version: "Online"
                    }, {
                        active: false,
                        uuid: "actionProductCatalog/Staged",
                        version: "Staged"
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

        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
