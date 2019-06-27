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
angular
    .module('permissionsMocks', ['ngMockE2E'])
    .run(
        function($httpBackend) {

            $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/global\?.*/).respond({
                "id": "global",
                "permissions": {
                    "entry": [{
                        "key": "smartedit.configurationcenter.read",
                        "value": "true"
                    }]
                }
            });

            $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/catalogs\?catalogId=apparel-ukContentCatalog&catalogVersion=Staged/).respond(function() {
                var defaultSyncPermissions = {
                    "canSynchronize": true,
                    "targetCatalogVersion": "Online"
                };
                var syncPermissions = JSON.parse(sessionStorage.getItem("syncPermissions")) || defaultSyncPermissions;

                return [200, {
                    "permissionsList": [{
                        "catalogId": "some.catalog.id",
                        "catalogVersion": "Staged",
                        "permissions": [{
                            "key": "read",
                            "value": "true"
                        }, {
                            "key": "write",
                            "value": "true"
                        }],
                        "syncPermissions": [syncPermissions]
                    }]
                }];
            });

            $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/catalogs\?catalogId=apparel-ukContentCatalog&catalogVersion=Online/).respond(function() {
                return [200, {
                    "permissionsList": [{
                        "catalogId": "some.catalog.id",
                        "catalogVersion": "Online",
                        "permissions": [{
                            "key": "read",
                            "value": "true"
                        }, {
                            "key": "write",
                            "value": "true"
                        }],
                        "syncPermissions": [{}]
                    }]
                }];
            });
        });
try {
    angular.module('smarteditloader').requires.push('permissionsMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('permissionsMocks');
} catch (e) {}
