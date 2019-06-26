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
describe('catalogVersionPermissionRestService', function() {

    var catalogId = "apparel-deContentCatalog",
        catalogVersion = "Staged";

    var SELECTD_CATALOG_PERMISSIONS = {
        catalogId: "apparel-deContentCatalog",
        catalogVersion: "Staged",
        permissions: [{
            "key": "read",
            "value": "true"
        }, {
            "key": "write",
            "value": "false"
        }],
        syncPermissions: [{
            "canSynchronize": true,
            "targetCatalogVersion": "Online"
        }]
    };

    var PREMISSIONS_API_RESPONSE = {
        permissionsList: [SELECTD_CATALOG_PERMISSIONS]
    };

    var PRINCIPAL_IDENTIFIER = "cmsmanager";

    var catalogVersionPermissionRestService, CATALOG_VERSION_PERMISSIONS_RESOURCE_URI, restService, restServiceFactory, sessionService, $q, $rootScope;

    /*
     * In this section, a mock REST service is created to make it possible to spy on its post
     * method and return a mocked response.
     *
     * Then, the REST service factory is mocked to return the REST service previously described.
     *
     * The session service is mocked to return a user identifier, which is required to make a call
     * to the catalog version permission API.
     */
    beforeEach(angular.mock.module('smarteditServicesModule', function($provide) {
        restService = jasmine.createSpyObj('restService', ['get']);
        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(restService);
        $provide.value('restServiceFactory', restServiceFactory);

        sessionService = jasmine.createSpyObj('sessionService', ['getCurrentUsername']);
        $provide.value('sessionService', sessionService);
    }));

    beforeEach(inject(function(_catalogVersionPermissionRestService_, _CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_, _$q_, _$rootScope_) {
        catalogVersionPermissionRestService = _catalogVersionPermissionRestService_;
        CATALOG_VERSION_PERMISSIONS_RESOURCE_URI = _CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    describe('CatalogVersionPermissionService.getCatalogVersionPermissions ', function() {

        it('THROWS exception if parameter catalogId does not exist', function() {

            // Given/When/Then
            expect(function() {
                catalogVersionPermissionRestService.getCatalogVersionPermissions(null);
            }).toThrowError("catalog.version.permission.service.catalogid.is.required");
        });

        it('THROWS exception if parameter catalogVersion does not exist', function() {

            // Given/When/Then
            expect(function() {
                catalogVersionPermissionRestService.getCatalogVersionPermissions(catalogId);
            }).toThrowError("catalog.version.permission.service.catalogversion.is.required");
        });

        it('GIVEN the REST call suceeds WHEN getCatalogVersionPermissions is called THEN REST factory and the REST endpoint are called with the right parameters', function() {

            // Given
            sessionService.getCurrentUsername.and.returnValue($q.when(PRINCIPAL_IDENTIFIER));
            restService.get.and.returnValue($q.when(PREMISSIONS_API_RESPONSE));

            // When
            catalogVersionPermissionRestService.getCatalogVersionPermissions(catalogId, catalogVersion);
            $rootScope.$digest();

            // Then
            var updatedResourceUri = CATALOG_VERSION_PERMISSIONS_RESOURCE_URI.replace(':principal', PRINCIPAL_IDENTIFIER);
            expect(restServiceFactory.get).toHaveBeenCalledWith(updatedResourceUri);
            expect(restService.get).toHaveBeenCalledWith({
                catalogId: catalogId,
                catalogVersion: catalogVersion
            });
        });

        it('GIVEN the REST call suceeds WHEN getCatalogVersionPermissions is called THEN the promise is resolved to a object containing list of permissions', function() {

            // Given
            sessionService.getCurrentUsername.and.returnValue($q.when(PRINCIPAL_IDENTIFIER));
            restService.get.and.returnValue($q.when(PREMISSIONS_API_RESPONSE));

            // When
            var permissionPromise = catalogVersionPermissionRestService.getCatalogVersionPermissions(catalogId, catalogVersion);
            $rootScope.$apply();

            // Then
            expect(permissionPromise).toBeResolvedWithData(SELECTD_CATALOG_PERMISSIONS);
        });


        it('GIVEN the REST call fails WHEN getCatalogVersionPermissions is aclled THEN the promise is rejected', function() {

            // Given
            sessionService.getCurrentUsername.and.returnValue($q.when(PRINCIPAL_IDENTIFIER));
            restService.get.and.returnValue($q.reject());

            // When
            var permissionPromise = catalogVersionPermissionRestService.getCatalogVersionPermissions(catalogId, catalogVersion);
            $rootScope.$apply();

            // Then
            expect(permissionPromise).toBeRejected();
        });
    });
});
