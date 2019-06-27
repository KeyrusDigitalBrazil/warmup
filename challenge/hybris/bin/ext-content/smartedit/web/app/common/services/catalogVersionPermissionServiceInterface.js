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
angular.module('catalogVersionPermissionServiceInterfaceModule', [])
    /**
     * @ngdoc service
     * @name catalogVersionPermissionModule.service:catalogVersionPermissionService
     * @description
     * # The catalogVersionPermissionService
     *
     * The catalog version permission service provides a logic that allows to verify
     * read and write permissions for a particular catalog version.
     */
    .service('CatalogVersionPermissionServiceInterface', function() {

        var CatalogVersionPermissionServiceInterface = function() {};

        /**
         * @ngdoc method
         * @name catalogVersionPermissionModule.service:catalogVersionPermissionService#hasWritePermission
         * @methodOf catalogVersionPermissionModule.service:catalogVersionPermissionService
         *
         * @description
         * Verifies whether current user has write permission for provided catalogId and catalogVersion.
         *
         * @param {String} catalogId catalog id
         * @param {String} catalogVersion catalog version
         * @returns {Promise<Boolean>} A promise resolving to a boolean `true` if current user has write permission, else `false`
         */
        CatalogVersionPermissionServiceInterface.prototype.hasWritePermission = function() {};

        /**
         * @ngdoc method
         * @name catalogVersionPermissionModule.service:catalogVersionPermissionService#hasReadPermission
         * @methodOf catalogVersionPermissionModule.service:catalogVersionPermissionService
         *
         * @description
         * Verifies whether current user has read permission for provided catalogId and catalogVersion.
         *
         * @param {String} catalogId catalog id
         * @param {String} catalogVersion catalog version
         * @returns {Promise<Boolean>} A promise resolving to a boolean `true` if current user has read permission, else `false`
         */
        CatalogVersionPermissionServiceInterface.prototype.hasReadPermission = function() {};

        /**
         * @ngdoc method
         * @name catalogVersionPermissionModule.service:catalogVersionPermissionService#hasWritePermissionOnCurrent
         * @methodOf catalogVersionPermissionModule.service:catalogVersionPermissionService
         *
         * @description
         * Verifies whether current user has write permission for current catalog version.
         *
         * @returns {Promise<Boolean>} A promise resolving to a boolean `true` if current user has write permission, else `false`
         */
        CatalogVersionPermissionServiceInterface.prototype.hasWritePermissionOnCurrent = function() {};

        /**
         * @ngdoc method
         * @name catalogVersionPermissionModule.service:catalogVersionPermissionService#hasReadPermissionOnCurrent
         * @methodOf catalogVersionPermissionModule.service:catalogVersionPermissionService
         *
         * @description
         * Verifies whether current user has read permission for current catalog version.
         *
         * @returns {Promise<Boolean>} A promise resolving to a boolean `true` if current user has read permission, else `false`
         */
        CatalogVersionPermissionServiceInterface.prototype.hasReadPermissionOnCurrent = function() {};

        /**
         * @ngdoc method
         * @name catalogVersionPermissionModule.service:catalogVersionPermissionService#hasSyncPermission
         * @methodOf catalogVersionPermissionModule.service:catalogVersionPermissionService
         *
         * @description
         * Verifies whether current user has sync permission for provided catalogId, source and target catalog versions.
         *
         * @returns {Promise<Boolean>} A promise resolving to a boolean `true` if current user has sync permission, else `false`
         */
        CatalogVersionPermissionServiceInterface.prototype.hasSyncPermission = function() {};

        /**
         * @ngdoc method
         * @name catalogVersionPermissionModule.service:catalogVersionPermissionService#hasSyncPermissionFromCurrentToActiveCatalogVersion
         * @methodOf catalogVersionPermissionModule.service:catalogVersionPermissionService
         *
         * @description
         * Verifies whether current user has sync permission for current catalog version.
         *
         * @returns {Promise<Boolean>} A promise resolving to a boolean `true` if current user has sync permission, else `false`
         */
        CatalogVersionPermissionServiceInterface.prototype.hasSyncPermissionFromCurrentToActiveCatalogVersion = function() {};

        /**
         * @ngdoc method
         * @name catalogVersionPermissionModule.service:catalogVersionPermissionService#hasSyncPermissionToActiveCatalogVersion
         * @methodOf catalogVersionPermissionModule.service:catalogVersionPermissionService
         *
         * @description
         * Verifies whether current user has sync permission for provided catalogId and catalog version.
         *
         * @returns {Promise<Boolean>} A promise resolving to a boolean `true` if current user has sync permission, else `false`
         */
        CatalogVersionPermissionServiceInterface.prototype.hasSyncPermissionToActiveCatalogVersion = function() {};

        return CatalogVersionPermissionServiceInterface;
    });
