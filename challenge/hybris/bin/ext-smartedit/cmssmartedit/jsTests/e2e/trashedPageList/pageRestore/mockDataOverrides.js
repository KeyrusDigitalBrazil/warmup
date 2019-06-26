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
angular.module('mockDataOverridesModule', ['backendMocksUtilsModule', 'yLoDashModule'])
    .run(function(backendMocksUtils, $httpBackend, lodash) {

        var INVALID_NAME = "Page with duplicated name";
        var INVALID_NAME_2 = "Page with two errors";
        var DUPLICATED_LABEL = "some duplicated label";
        var MISSING_PRIMARY_LABEL = "some missing label";
        var DUPLICATED_PRIMARY_CATEGORY_ID = "duplicatedCategoryID";
        var CATEGORY_VARIATION_WITHOUT_PRIMARY_ID = "someCategoryWithoutPrimaryID";

        var CMS_ITEMS = {
            pages: [{
                catalogVersion: "apparel-ukContentCatalog/Staged",
                creationtime: '2016-07-07T14:33:37+0000',
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'Valid Page',
                template: 'SomePageTemplate',
                title: {
                    en: 'Valid Page'
                },
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                uid: 'validContentPage',
                uuid: "validContentPage",
                label: 'validContentPage',
                pageStatus: 'DELETED',
                restrictions: []
            }, {
                catalogVersion: "apparel-ukContentCatalog/Staged",
                creationtime: '2016-07-07T14:33:37+0000',
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: INVALID_NAME,
                template: 'SomePageTemplate',
                title: {
                    en: 'Page with duplicated name'
                },
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                uid: 'duplicatedPageName',
                uuid: "duplicatedPageName",
                label: 'some label',
                pageStatus: 'DELETED',
                restrictions: []
            }, {
                catalogVersion: "apparel-ukContentCatalog/Staged",
                creationtime: '2016-07-07T14:33:37+0000',
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'Duplicate Primary Content Page',
                template: 'SomePageTemplate',
                title: {
                    en: 'Page with duplicated name'
                },
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                uid: 'duplicatedPageName',
                uuid: "duplicatedPageName",
                label: DUPLICATED_LABEL,
                pageStatus: 'DELETED',
                restrictions: []
            }, {
                catalogVersion: "apparel-ukContentCatalog/Staged",
                creationtime: '2016-07-07T14:33:37+0000',
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'Page with two errors',
                template: 'SomePageTemplate',
                title: {
                    en: 'Page with duplicated name'
                },
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                uid: 'duplicatedPageName',
                uuid: "duplicatedPageName",
                label: DUPLICATED_LABEL,
                pageStatus: 'DELETED',
                restrictions: []
            }, {
                catalogVersion: "apparel-ukContentCatalog/Staged",
                creationtime: '2016-07-07T14:33:37+0000',
                modifiedtime: '2016-07-12T01:23:41+0000',
                name: 'Content Page without primary',
                template: 'SomePageTemplate',
                title: {
                    en: 'Page with duplicated name'
                },
                typeCode: 'ContentPage',
                itemtype: 'ContentPage',
                uid: 'duplicatedPageName',
                uuid: "duplicatedPageName",
                label: MISSING_PRIMARY_LABEL,
                pageStatus: 'DELETED',
                restrictions: []
            }, {
                catalogVersion: "apparel-ukContentCatalog/Staged",
                creationtime: '2016-07-07T14:33:37+0000',
                modifiedtime: '2017-09-26T15:22:37+0000',
                name: 'Duplicate Product page',
                template: 'SomePageTemplate',
                title: {
                    en: 'Variation Content Page'
                },
                typeCode: 'ProductPage',
                itemtype: 'ProductPage',
                uid: DUPLICATED_PRIMARY_CATEGORY_ID,
                uuid: DUPLICATED_PRIMARY_CATEGORY_ID,
                label: 'trashedProductPage',
                pageStatus: 'DELETED',
                restrictions: []
            }, {
                catalogVersion: "apparel-ukContentCatalog/Staged",
                creationtime: '2016-07-07T14:33:37+0000',
                modifiedtime: '2017-09-26T15:22:37+0000',
                name: 'Product Page with no primary',
                template: 'SomePageTemplate',
                title: {
                    en: 'Variation Content Page'
                },
                typeCode: 'ProductPage',
                itemtype: 'ProductPage',
                uid: CATEGORY_VARIATION_WITHOUT_PRIMARY_ID,
                uuid: CATEGORY_VARIATION_WITHOUT_PRIMARY_ID,
                label: 'trashedProductPage',
                pageStatus: 'DELETED',
                restrictions: []
            }],
            primaryActiveContentPages: [{
                uid: 'primaryContentPage',
                name: 'My Little Primary Content Page',
                defaultPage: true,
                template: "LandingPage2Template",
                typeCode: "ContentPage",
                label: "MyLittlePrimary"
            }, {
                uid: 'someOtherPrimaryPageContent',
                name: 'Some Other Primary Content Page',
                defaultPage: true,
                template: "LandingPage2Template",
                typeCode: "ContentPage",
                label: "SomeOtherPrimary"
            }]
        };

        function isActivePageRequested(url) {
            return url.indexOf('pageStatus:ACTIVE') > -1;
        }

        function isDefaultPageRequested(url) {
            return url.indexOf('defaultPage:true') > -1;
        }

        function isContentPageTypeRequested(url) {
            return url.indexOf('typeCode=ContentPage') > -1;
        }

        var componentsListGETMock = backendMocksUtils.getBackendMock('componentsListGETMock');
        componentsListGETMock.respond(function(method, url, data, headers) {
            if (isActivePageRequested(url) && isDefaultPageRequested(url) && isContentPageTypeRequested(url)) {
                return [200, {
                    pagination: {
                        count: 2,
                        page: 0,
                        totalCount: null,
                        totalPages: 1
                    },
                    response: CMS_ITEMS.primaryActiveContentPages
                }];
            } else {
                return [200, {
                    pagination: {
                        count: 10,
                        page: 0,
                        totalCount: null,
                        totalPages: 1
                    },
                    response: CMS_ITEMS.pages
                }];
            }
        });

        var errorTypes = {
            DUPLICATED_NAME: {
                "errorCode": "field.already.exist",
                "message": "The value provided is already in use.",
                "reason": "invalid",
                "subject": "name",
                "subjectType": "parameter",
                "type": "ValidationError"
            },
            DUPLICATE_CONTENT_PRIMARY_PAGE: {
                "errorCode": "default.page.label.already.exist",
                "message": "This label already exists.",
                "reason": "missing",
                "subject": "label",
                "subjectType": "parameter",
                "type": "ValidationError"
            },
            MISSING_CONTENT_PRIMARY_PAGE: {
                "errorCode": "default.page.does.not.exist",
                "message": "A Primary page does not exist for [label]: [cart2].",
                "reason": "missing",
                "subject": "label",
                "subjectType": "parameter",
                "type": "ValidationError"
            },
            DUPLICATE_NON_CONTENT_PRIMARY_PAGE: {
                "errorCode": "default.page.already.exist",
                "message": "A Primary page already exists for the selected page type.",
                "reason": "missing",
                "subject": "typeCode",
                "subjectType": "parameter",
                "type": "ValidationError"
            },
            MISSING_NON_CONTENT_PRIMARY_PAGE: {
                "errorCode": "default.page.does.not.exist",
                "message": "A Primary page does not exist for the type.",
                "reason": "missing",
                "subject": "typeCode",
                "subjectType": "parameter",
                "type": "ValidationError"
            }
        };

        var componentPUTMock = backendMocksUtils.getBackendMock('componentPUTMock');
        componentPUTMock.respond(function(method, url, data, headers) {
            var payload = JSON.parse(data);

            var errorsList = [];
            if (payload.name === INVALID_NAME || payload.name === INVALID_NAME_2) {
                errorsList.push(errorTypes.DUPLICATED_NAME);
            }
            if (payload.typeCode === 'ContentPage') {
                if (payload.label === DUPLICATED_LABEL && !payload.replace) {
                    errorsList.push(errorTypes.DUPLICATE_CONTENT_PRIMARY_PAGE);
                }
                if (payload.label === MISSING_PRIMARY_LABEL) {
                    errorsList.push(errorTypes.MISSING_CONTENT_PRIMARY_PAGE);
                }
            } else if (payload.typeCode === 'ProductPage') {
                if (payload.uuid === DUPLICATED_PRIMARY_CATEGORY_ID && !payload.replace) {
                    errorsList.push(errorTypes.DUPLICATE_NON_CONTENT_PRIMARY_PAGE);
                }
                if (payload.uuid === CATEGORY_VARIATION_WITHOUT_PRIMARY_ID) {
                    errorsList.push(errorTypes.MISSING_NON_CONTENT_PRIMARY_PAGE);
                }
            }

            if (errorsList.length > 0) {
                return [400, {
                    errors: errorsList
                }];
            } else {
                return [200];
            }
        });
    });

try {
    angular.module('smarteditloader').requires.push('mockDataOverridesModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('mockDataOverridesModule');
} catch (e) {}
