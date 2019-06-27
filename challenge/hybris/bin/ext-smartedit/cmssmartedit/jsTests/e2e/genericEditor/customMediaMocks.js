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
angular.module('customMediaMocksModule', ['backendMocksUtilsModule'])
    .run(function(backendMocksUtils) {

        backendMocksUtils.getBackendMock('componentTypesPermissionsGET').respond({
            "permissionsList": [{
                "id": "MediaContainer",
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
                "id": "MediaFormat",
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
            }]
        });
    });
try {
    angular.module('smarteditloader').requires.push('customMediaMocksModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('customMediaMocksModule');
} catch (e) {}
