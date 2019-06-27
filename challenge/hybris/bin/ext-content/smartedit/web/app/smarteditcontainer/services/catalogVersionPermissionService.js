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
angular.module('catalogVersionPermissionModule', [
        'catalogVersionPermissionRestServiceModule',
        'smarteditServicesModule',
        'resourceLocationsModule',
        'functionsModule',
        'catalogVersionPermissionServiceInterfaceModule'
    ])
    .constant('PERMISSION_TYPES', {
        READ: 'read',
        WRITE: 'write'
    })
    .service('catalogVersionPermissionService', function($q, catalogVersionPermissionRestService, catalogService, gatewayProxy, extend, CatalogVersionPermissionServiceInterface, CONTEXT_CATALOG, CONTEXT_CATALOG_VERSION, CONTEXT_SITE_ID, PERMISSION_TYPES) {

        var CatalogVersionPermissionService = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this, ["hasWritePermission", "hasReadPermission", "hasWritePermissionOnCurrent", "hasReadPermissionOnCurrent"]);
        };

        CatalogVersionPermissionService = extend(CatalogVersionPermissionServiceInterface, CatalogVersionPermissionService);

        var hasPermission = function(accessType, catalogId, catalogVersion, siteId) {

            return shouldIgnoreCatalogPermissions(accessType, catalogId, catalogVersion, siteId).then(function(shouldOverride) {
                return catalogVersionPermissionRestService.getCatalogVersionPermissions(
                    catalogId, catalogVersion
                ).then(function(response) {
                    if (response.permissions) {
                        var permission = response.permissions.find(function(permission) {
                            return permission.key === accessType;
                        });
                        var value = (permission ? permission.value : 'false');
                        return $q.when(value === 'true' || shouldOverride);
                    } else {
                        return $q.when(false);
                    }
                });
            });
        };

        /**
         * if in the context of an experience AND the catalogVersion is the active one, then permissions should be ignored in read mode
         */
        var shouldIgnoreCatalogPermissions = function(accessType, catalogId, catalogVersion, siteId) {
            var promise = (siteId && accessType === PERMISSION_TYPES.READ) ? catalogService.getActiveContentCatalogVersionByCatalogId(catalogId) : $q.when();
            return promise.then(function(versionCheckedAgainst) {
                return versionCheckedAgainst === catalogVersion;
            });
        };
        /**
         * Verifies whether current user has write or read permission for current catalog version.
         * @param {String} accessType
         */
        var hasCurrentCatalogPermission = function(accessType) {
            return catalogService.retrieveUriContext().then(function(data) {
                return hasPermission(accessType, data[CONTEXT_CATALOG], data[CONTEXT_CATALOG_VERSION], data[CONTEXT_SITE_ID]);
            });
        };

        CatalogVersionPermissionService.prototype.hasWritePermission = function(catalogId, catalogVersion) {
            return hasPermission(PERMISSION_TYPES.WRITE, catalogId, catalogVersion);
        };

        CatalogVersionPermissionService.prototype.hasReadPermission = function(catalogId, catalogVersion) {
            return hasPermission(PERMISSION_TYPES.READ, catalogId, catalogVersion);
        };

        CatalogVersionPermissionService.prototype.hasWritePermissionOnCurrent = function() {
            return hasCurrentCatalogPermission(PERMISSION_TYPES.WRITE);
        };

        CatalogVersionPermissionService.prototype.hasReadPermissionOnCurrent = function() {
            return hasCurrentCatalogPermission(PERMISSION_TYPES.READ);
        };

        CatalogVersionPermissionService.prototype.hasSyncPermission = function(catalogId, sourceCatalogVersion, targetCatalogVersion) {
            return catalogVersionPermissionRestService.getCatalogVersionPermissions(
                catalogId, sourceCatalogVersion
            ).then(function(response) {
                if (response.syncPermissions && response.syncPermissions.length > 0) {
                    var permission = response.syncPermissions.some(function(syncPermission) {
                        return syncPermission ? (syncPermission.canSynchronize === true && syncPermission.targetCatalogVersion === targetCatalogVersion) : false;
                    });
                    return $q.when(permission);
                } else {
                    return $q.when(false);
                }
            });
        };

        CatalogVersionPermissionService.prototype.hasSyncPermissionFromCurrentToActiveCatalogVersion = function() {
            return catalogService.retrieveUriContext().then(function(data) {
                return this.hasSyncPermissionToActiveCatalogVersion(data[CONTEXT_CATALOG], data[CONTEXT_CATALOG_VERSION]);
            }.bind(this));
        };

        CatalogVersionPermissionService.prototype.hasSyncPermissionToActiveCatalogVersion = function(catalogId, catalogVersion) {
            return catalogService.getActiveContentCatalogVersionByCatalogId(catalogId).then(function(targetCatalogVersion) {
                return this.hasSyncPermission(catalogId, catalogVersion, targetCatalogVersion);
            }.bind(this));
        };

        return new CatalogVersionPermissionService("CatalogVersionPermissionServiceId");
    });
