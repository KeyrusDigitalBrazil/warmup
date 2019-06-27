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
angular.module('pageMocks', ['ngMockE2E'])
    .run(
        function($httpBackend) {

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online$/).respond({
                "name": {
                    "en": "Apparel UK Content Catalog"
                },
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
                "uid": "apparel-ukContentCatalog",
                "version": "Online"
            });


            var pages = [{
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "page1TitleSuffix",
                label: 'page1TitleSuffix',
                typeCode: "ContentPage",
                uid: "auid1"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "page1TitleSuffix",
                typeCode: "ContentPage",
                uid: "auid1"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "ActionTemplate",
                name: "welcomePage",
                typeCode: "ActionPage",
                uid: "uid2"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "PageTemplate",
                name: "Advertise",
                typeCode: "MyCustomType",
                uid: "uid3"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "MyCustomPageTemplate",
                name: "page2TitleSuffix",
                typeCode: "HomePage",
                uid: "uid4"
            }, {
                creationtime: "2016-04-08T21:16:41+0000",
                modifiedtime: "2016-04-08T21:16:41+0000",
                pk: "8796387968048",
                template: "ZTemplate",
                name: "page3TitleSuffix",
                typeCode: "ProductPage",
                uid: "uid5"
            }, {
                type: 'contentPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Primary Content Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Primary Content Page'
                },
                typeCode: 'ContentPage',
                uid: 'primaryContentPage',
                label: 'primary-content-page'
            }, {
                type: 'contentPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Variation Content Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Variation Content Page'
                },
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                uid: 'variationContentPage',
                label: 'variation-content-page'
            }, {
                type: 'categoryPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Primary Category Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Primary Category Page'
                },
                typeCode: 'CategoryPage',
                uid: 'primaryCategoryPage',
                label: 'primary-category-page'
            }, {
                type: 'categoryPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Variation Category Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Variation Category Page'
                },
                typeCode: 'CategoryPage',
                uid: 'variationCategoryPage',
                label: 'variation-category-page'
            }, {
                type: 'productPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Primary Product Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Primary Product Page'
                },
                typeCode: 'ProductPage',
                uid: 'primaryProductPage',
                label: 'primary-product-page'
            }, {
                type: 'productPageData',
                creationtime: '2016-07-07T14:33:37+0000',
                defaultPage: true,
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'My Little Variation Product Page',
                onlyOneRestrictionMustApply: true,
                pk: '8796101182512',
                template: 'SomePageTemplate',
                title: {
                    en: 'Variation Product Page'
                },
                typeCode: 'ProductPage',
                uid: 'variationProductPage',
                label: 'variation-product-page'
            }, {
                type: 'contentPageData',
                creationtime: '2016-06-28T15:23:37+0000',
                defaultPage: false,
                modifiedtime: '2016-06-28T15:25:51+0000',
                name: 'Homepage',
                pk: "8796101182512",
                template: "AccountPageTemplate",
                title: {
                    de: "Mes lovens pendas",
                    en: "I love pandas"
                },
                typeCode: "ContentPage",
                uid: 'homepage',
                label: "i-love-pandas"
            }, {
                type: "contentPageData",
                creationtime: "2016-06-28T15:23:37+0000",
                defaultPage: true,
                modifiedtime: "2016-06-28T15:25:51+0000",
                name: "Some Other Page",
                pk: "8796101182512",
                template: "ProductPageTemplate",
                title: {
                    de: "Mes hatens pendas",
                    en: "I hate pandas"
                },
                typeCode: "ProductPage",
                uid: "secondpage",
                label: "i-hate-pandas"
            }, {
                type: "contentPageData",
                creationtime: "2018-06-28T15:23:37+0000",
                defaultPage: true,
                modifiedtime: "2018-06-28T15:25:51+0000",
                name: "Some Other Page",
                pk: "8796101182513",
                template: "ProductPageTemplate",
                title: {
                    'en': 'Third page'
                },
                typeCode: "ProductPage",
                uid: "thirdpage",
                label: "third-page"
            }];

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages$/).respond({
                pages: pages
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\?typeCode=.*/).respond(function() {
                return [200, {
                    pagination: {
                        count: 10,
                        page: 1,
                        totalCount: pages.length,
                        totalPages: 3
                    },
                    pages: pages
                }];
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages$/).respond({
                pages: pages
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/([a-zA-Z0-9_]*)$/, undefined, ['uid'])
                .respond(function(method, url, data, headers, params) {
                    return [200, pages.find(function(page) {
                        return params.uid === page.uid;
                    })];
                });


            $httpBackend.whenGET(/cmswebservices\/v1\/types\/CategoryPage/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.uid.name",
                    "localized": false,
                    "qualifier": "uid"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.name.name",
                    "localized": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.creationtime.name",
                    "localized": false,
                    "qualifier": "creationtime"
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.modifiedtime.name",
                    "localized": false,
                    "qualifier": "modifiedtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.title.name",
                    "localized": true,
                    "qualifier": "title",
                    "required": true
                }, {
                    "cmsStructureType": "PageRestrictionsEditor",
                    "i18nKey": "type.abstractpage.restrictions.name",
                    "qualifier": "restrictions"
                }],
                "category": "PAGE",
                "code": "MockPage",
                "i18nKey": "type.mockpage.name",
                "name": "Mock Page"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\/ContentPage/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.uid.name",
                    "localized": false,
                    "qualifier": "uid"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.name.name",
                    "localized": false,
                    "qualifier": "name",
                    "required": true
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.creationtime.name",
                    "localized": false,
                    "qualifier": "creationtime"
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.modifiedtime.name",
                    "localized": false,
                    "qualifier": "modifiedtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.title.name",
                    "localized": true,
                    "qualifier": "title",
                    "required": true
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.contentpage.label.name",
                    "localized": false,
                    "qualifier": "label",
                    "required": true
                }, {
                    "cmsStructureType": "PageRestrictionsEditor",
                    "i18nKey": "type.abstractpage.restrictions.name",
                    "qualifier": "restrictions"
                }],
                "category": "PAGE",
                "code": "MockPage",
                "i18nKey": "type.mockpage.name",
                "name": "Mock Page"
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/types\/ProductPage/).respond({
                "attributes": [{
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.uid.name",
                    "localized": false,
                    "qualifier": "uid"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.name.name",
                    "localized": false,
                    "qualifier": "name"
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.creationtime.name",
                    "localized": false,
                    "qualifier": "creationtime"
                }, {
                    "cmsStructureType": "DateTime",
                    "i18nKey": "type.abstractpage.modifiedtime.name",
                    "localized": false,
                    "qualifier": "modifiedtime"
                }, {
                    "cmsStructureType": "ShortString",
                    "i18nKey": "type.abstractpage.title.name",
                    "localized": true,
                    "qualifier": "title"
                }, {
                    "cmsStructureType": "PageRestrictionsEditor",
                    "i18nKey": "type.abstractpage.restrictions.name",
                    "qualifier": "restrictions"
                }],
                "category": "PAGE",
                "code": "MockPage",
                "i18nKey": "type.mockpage.name",
                "name": "Mock Page"
            });


            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages\?.*typeCode=ContentPage/).respond({
                pages: [{
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    pk: "8796387968048",
                    template: "PageTemplate",
                    name: "page1TitleSuffix",
                    label: 'page1TitleSuffix',
                    typeCode: "ContentPage",
                    uid: "auid1"
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages\?.*typeCode=CategoryPage/).respond({
                pages: [{
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    pk: "8796387968048",
                    template: "PageTemplate",
                    name: "page1TitleSuffix",
                    label: 'page1TitleSuffix',
                    typeCode: "ContentPage",
                    uid: "auid1"
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages\?.*typeCode=ProductPage/).respond({
                pages: [{
                    creationtime: "2016-04-08T21:16:41+0000",
                    modifiedtime: "2016-04-08T21:16:41+0000",
                    pk: "8796387968058",
                    template: "PageTemplate",
                    name: "productPage1",
                    label: 'productPage1',
                    typeCode: "CategoryPage",
                    uid: "auid2"
                }]
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pagetemplates*/).respond({
                "templates": [{
                    "frontEndName": "pageTemplate1",
                    "name": "Page Template 1",
                    "uid": "pageTemplate1",
                    "uuid": "pageTemplate1"
                }, {
                    "frontEndName": "pageTemplate2",
                    "name": "Page Template 2",
                    "uid": "pageTemplate2",
                    "uuid": "pageTemplate2"
                }]
            });

            $httpBackend.whenPOST(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Online\/pages$/)
                .respond(function(method, url, data, headers) {
                    var dataObject = angular.fromJson(data);
                    if (dataObject.uid === 'bla') {
                        return [400, {
                            "errors": [{
                                "message": "Some error msg.",
                                "reason": "invalid",
                                "subject": "uid",
                                "subjectType": "parameter",
                                "type": "ValidationError"
                            }]
                        }];
                    } else {
                        return [200, {
                            uid: 'valid'
                        }];
                    }
                });

            $httpBackend
                .whenGET("/cmswebservices\/v1\/pagetypes")
                .respond({
                    pageTypes: [{
                        code: 'ContentPage',
                        name: {
                            "en": 'Content Page',
                            "fr": 'Content Page in French'
                        },
                        description: {
                            "en": 'Description for content page',
                            "fr": 'Description for content page in French'
                        }
                    }, {
                        code: 'ProductPage',
                        name: {
                            "en": 'Product Page',
                            "fr": 'Product Page in French'
                        },
                        description: {
                            "en": 'Description for product page',
                            "fr": 'Description for product page in French'
                        }
                    }, {
                        code: 'CategoryPage',
                        name: {
                            "en": 'Category Page',
                            "fr": 'Category Page in French'
                        },
                        description: {
                            "en": 'Description for category page',
                            "fr": 'Description for category page in French'
                        }
                    }]
                });


            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pages\/homepage\/variations/).respond(function() {
                return [200, {
                    uids: ['MOCKED_VARIATION_PAGE_ID']
                }];
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pages\/variationCategoryPage\/variations/).respond(function() {
                return [200, {
                    uids: []
                }];
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pages\/primaryCategoryPage\/variations/).respond(function() {
                return [200, {
                    uids: []
                }];
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pages\/secondpage\/variations/).respond(function() {
                return [200, {
                    uids: []
                }];
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/catalogs\/.*\/versions\/.*\/pages\/thirdpage\/variations/).respond(function() {
                return [200, {
                    uids: []
                }];
            });


        });
try {
    angular.module('smarteditloader').requires.push('pageMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('pageMocks');
} catch (e) {}
