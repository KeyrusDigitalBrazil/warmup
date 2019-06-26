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
angular.module('PermissionMockModule', ['ngMockE2E', 'resourceLocationsModule', 'functionsModule'])
    .run(function($httpBackend, resourceLocationToRegex, USER_GLOBAL_PERMISSIONS_RESOURCE_URI) {

        $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/catalogs\?catalogId=toysContentCatalog&catalogVersion=Online/).respond({
            "permissionsList": [{
                "catalogId": "toysContentCatalog",
                "catalogVersion": "Online",
                "permissions": [{
                    "key": "read",
                    "value": "true"
                }, {
                    "key": "write",
                    "value": "false"
                }],
                "syncPermissions": [{}]
            }]
        });

        $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/catalogs\?catalogId=actionFiguresContentCatalog&catalogVersion=Online/).respond({
            "permissionsList": [{
                "catalogId": "actionFiguresContentCatalog",
                "catalogVersion": "Online",
                "permissions": [{
                    "key": "read",
                    "value": "true"
                }, {
                    "key": "write",
                    "value": "false"
                }],
                "syncPermissions": [{}]
            }]
        });

        $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/catalogs\?catalogId=electronicsContentCatalog&catalogVersion=Online/).respond({
            "permissionsList": [{
                "catalogId": "electronicsContentCatalog",
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
        });

        $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/catalogs\?catalogId=electronicsContentCatalog&catalogVersion=Staged/).respond({
            "permissionsList": [{
                "catalogId": "electronicsContentCatalog",
                "catalogVersion": "Staged",
                "permissions": [{
                    "key": "read",
                    "value": "true"
                }, {
                    "key": "write",
                    "value": "true"
                }],
                "syncPermissions": [{
                    "canSynchronize": true,
                    "targetCatalogVersion": "Online"
                }]
            }]
        });

        $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/catalogs\?catalogId=apparel-ukContentCatalog&catalogVersion=Online/).respond({
            "permissionsList": [{
                "catalogId": "apparel-ukContentCatalog",
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
        });

        $httpBackend.whenGET(/permissionswebservices\/v1\/permissions\/principals\/.+\/catalogs\?catalogId=apparel-ukContentCatalog&catalogVersion=Staged/).respond({
            "permissionsList": [{
                "catalogId": "apparel-ukContentCatalog",
                "catalogVersion": "Staged",
                "permissions": [{
                    "key": "read",
                    "value": "true"
                }, {
                    "key": "write",
                    "value": "true"
                }],
                "syncPermissions": [{
                    "canSynchronize": true,
                    "targetCatalogVersion": "Online"
                }]
            }]
        });

        $httpBackend.whenGET(resourceLocationToRegex(USER_GLOBAL_PERMISSIONS_RESOURCE_URI)).respond(function(method, url) {
            var user = getUserFromUrl(url);
            if (user === 'admin') {
                return [200, {
                    "id": "global",
                    "permissions": [{
                        "key": "smartedit.configurationcenter.read",
                        "value": "true"
                    }]
                }];
            } else {
                return [200, {
                    "id": "global",
                    "permissions": [{
                        "key": "smartedit.configurationcenter.read",
                        "value": "false"
                    }]
                }];
            }
        });

        function getUserFromUrl(url) {
            return /principals\/(.+)\/.*/.exec(url)[1];
        }
    });

try {
    angular.module('smarteditloader').requires.push('PermissionMockModule');
    angular.module('smarteditcontainer').requires.push('PermissionMockModule');
} catch (ex) {}
