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
angular.module('customMocksModule', ['backendMocksUtilsModule'])
    .run(function($httpBackend, backendMocksUtils) {

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged/).respond({
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
                    "label": "page.displaycondition.primary",
                    "id": "PRIMARY"
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

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel\/catalogs\/apparelContentCatalog\/versions\/Online/).respond({
            "name": {
                "en": "Apparel Content Catalog"
            },
            "pageDisplayConditions": [{
                "options": [{
                    "label": "page.displaycondition.variation",
                    "id": "VARIATION"
                }],
                "typecode": "ProductPage"
            }, {
                "options": [{
                    "label": "page.displaycondition.primary",
                    "id": "PRIMARY"
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
            "uid": "apparelContentCatalog",
            "version": "Online"
        });

        backendMocksUtils.getBackendMock('componentPOSTMock').respond(function(method, url, data) {

            var dataObject = angular.fromJson(data);
            if (dataObject.uid === 'targetCloneUid') {
                return [200, {
                    uid: 'valid',
                    catalogVersion: "apparel-ukContentCatalog/Online"
                }];
            } else if (dataObject.restrictions[0].uuid === "timeRestrictionIdA" && dataObject.restrictions[1].uuid === "timeRestrictionIdB") {

                if (dataObject.uid === 'trump') {
                    return [400, {
                        "errors": [{
                            "message": "No Trump jokes plz.",
                            "reason": "invalid",
                            "subject": "uid",
                            "subjectType": "parameter",
                            "type": "ValidationError"
                        }]
                    }];
                }

                return [200, {
                    uid: 'valid',
                    catalogVersion: "apparel-ukContentCatalog/Staged"
                }];

            } else if (dataObject.restrictions.length < 2 || dataObject.restrictions.length > 2) {

                return [400, {
                    "errors": [{
                        "message": "There were 2 restrictions onscreen that didn't make it into the REST payload"
                    }]
                }];
            } else {
                return [400, {
                    "errors": [{
                        "message": "Unknown error"
                    }]
                }];
            }
        });

        $httpBackend
            .whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pagesrestrictions\?pageId=homepage/)
            .respond({
                pageRestrictionList: [{
                    pageId: "homepage",
                    restrictionId: "timeRestrictionIdA"
                }, {
                    pageId: "homepage",
                    restrictionId: "timeRestrictionIdB"
                }]
            });
    });
try {
    angular.module('smarteditloader').requires.push('customMocksModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('customMocksModule');
} catch (e) {}
