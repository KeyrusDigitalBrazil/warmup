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
angular.module('customMocksModule', ['backendMocksUtilsModule'])
    .run(function(backendMocksUtils) {
        backendMocksUtils.getBackendMock('componentTypesPermissionsGET').respond(function() {

            var typePermissions = JSON.parse(sessionStorage.getItem('cmsVersionTypePermissions'));

            return [200, {
                "permissionsList": [{
                    "id": "ContentPage",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "CategoryPage",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "ProductPage",
                    "permissions": [{
                        "key": "read",
                        "value": "true"
                    }, {
                        "key": "change",
                        "value": "true"
                    }, {
                        "key": "create",
                        "value": "true"
                    }, {
                        "key": "remove",
                        "value": "true"
                    }]
                }, {
                    "id": "CMSVersion",
                    "permissions": typePermissions
                }]
            }];
        });

    });
try {
    angular.module('smarteditloader').requires.push('customMocksModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('customMocksModule');
} catch (e) {}
