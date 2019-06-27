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
/**
 * @ngdoc overview
 * @name cmsItemsRestServiceModule
 * 
 * @description
 * The cmsitemsRestServiceModule provides a service to CRUD operations on CMS Items API.
 */
angular.module('cmsitemsRestServiceModule', [
        'functionsModule',
        'resourceLocationsModule',
        'smarteditServicesModule',
        'yLoDashModule'
    ])

    .provider('cmsitemsUri', function(CONTEXT_SITE_ID) {

        this.uri = '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/cmsitems';

        this.$get = function() {
            return this.uri;
        }.bind(this);

    })

    .provider('cmsitemsUuidsUri', function(CONTEXT_SITE_ID) {

        this.uri = '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/cmsitems/uuids';

        this.$get = function() {
            return this.uri;
        }.bind(this);

    })
    /**
     * @ngdoc service
     * @name cmsitemsRestServiceModule.cmsitemsRestService
     * 
     * @description
     * Service to deal with CMS Items related CRUD operations.
     */
    .service('cmsitemsRestService', function(lodash, restServiceFactory, cmsitemsUri, cmsitemsUuidsUri, catalogService, operationContextService, OPERATION_CONTEXT) {

        var resource = restServiceFactory.get(cmsitemsUri);

        var versionedResource = restServiceFactory.get(cmsitemsUri + "/:itemUuid");

        var uuidsResource = restServiceFactory.get(cmsitemsUuidsUri);

        operationContextService.register(cmsitemsUri.replace(/CONTEXT_SITE_ID/, ':CONTEXT_SITE_ID'), OPERATION_CONTEXT.CMS);

        /**
         * @ngdoc method
         * @name cmsitemsRestServiceModule.service:cmsitemsRestService#create
         * @methodOf cmsitemsRestServiceModule.cmsitemsRestService
         * 
         * @description
         * Create a new CMS Item.
         * 
         * @param {Object} cmsitem The object representing the CMS Item to create
         * 
         * @returns {Promise} If request is successful, it returns a promise that resolves with the CMS Item object. If the
         * request fails, it resolves with errors from the backend.
         */
        this.create = function(cmsitem) {
            return catalogService.getCatalogVersionUUid().then(function(catalogVersionUUid) {
                cmsitem.catalogVersion = cmsitem.catalogVersion || catalogVersionUUid;
                if (cmsitem.onlyOneRestrictionMustApply === undefined) {
                    cmsitem.onlyOneRestrictionMustApply = false;
                }
                return resource.save(cmsitem);
            });
        };

        /**
         * @ngdoc method
         * @name cmsitemsRestServiceModule.service:cmsitemsRestService#getById
         * @methodOf cmsitemsRestServiceModule.cmsitemsRestService
         * 
         * @description
         * Get the CMS Item that matches the given item uuid (Universally Unique Identifier).
         * 
         * @param {Number} cmsitemUuid The CMS Item uuid
         * 
         * @returns {Promise} If request is successful, it returns a promise that resolves with the CMS Item object. If the
         * request fails, it resolves with errors from the backend.
         */
        this.getById = function(cmsitemUuid) {
            return resource.getById(cmsitemUuid);
        };

        /**
         * @ngdoc method
         * @name cmsitemsRestServiceModule.service:cmsitemsRestService#getByIdAndVersion
         * @methodOf cmsitemsRestServiceModule.cmsitemsRestService
         * 
         * @description
         * Get the CMS Item that matches the given item uuid (Universally Unique Identifier) for a given version.
         * 
         * @param {String} cmsitemUuid The CMS Item uuid
         * @param {String} versionId The uid of the version to be retrieved.
         * 
         * @returns {Promise} If request is successful, it returns a promise that resolves with the CMS Item object. If the
         * request fails, it resolves with errors from the backend.
         */
        this.getByIdAndVersion = function(cmsitemUuid, versionId) {
            return versionedResource.get({
                itemUuid: cmsitemUuid,
                versionId: versionId
            });
        };

        /**
         * @ngdoc method
         * @name cmsitemsRestServiceModule.service:cmsitemsRestService#get
         * @methodOf cmsitemsRestServiceModule.cmsitemsRestService
         * 
         * @description
         * Fetch CMS Items search result by making a REST call to the CMS Items API.
         * A search can be performed by a typeCode (optionnaly in combination of a mask parameter), or by providing a list of cms items uuid.
         *
         * @param {Object} queryParams The object representing the query params
         * @param {String} queryParams.pageSize number of items in the page
         * @param {String} queryParams.currentPage current page number
         * @param {String =} queryParams.typeCode for filtering on the cms item typeCode
         * @param {String =} queryParams.mask for filtering the search
         * @param {String =} queryParams.itemSearchParams search on additional fields using a comma separated list of field name and value
         * pairs which are separated by a colon. Exact matches only.
         * @param {Array =} queryParams.uuids list of cms items uuids. Deprecated since 6.6.
         * @param {String =} queryParams.catalogId the catalog to search items in. If empty, the current context catalog will be used.
         * @param {String =} queryParams.catalogVersion the catalog version to search items in. If empty, the current context catalog version will be used.
         *
         * @returns {Promise} If request is successful, it returns a promise that resolves with the paged search result. If the
         * request fails, it resolves with errors from the backend.
         */
        this.get = function(queryParams) {
            return catalogService.retrieveUriContext().then(function(uriContext) {

                var catalogDetailsParams = {
                    catalogId: queryParams.catalogId || uriContext.CURRENT_CONTEXT_CATALOG,
                    catalogVersion: queryParams.catalogVersion || uriContext.CURRENT_CONTEXT_CATALOG_VERSION
                };

                queryParams = lodash.merge(catalogDetailsParams, queryParams);

                return resource.get(queryParams);
            });
        };


        /**
         * @ngdoc method
         * @name cmsitemsRestServiceModule.service:cmsitemsRestService#getByIds
         * @methodOf cmsitemsRestServiceModule.cmsitemsRestService
         * 
         * @description
         * Fetch CMS Items by uuids, making a POST call to the CMS Items API.
         * A search can be performed by providing a list of cms items uuid.
         *   
         * @param {Array =} uuids list of cms item uuids
         *
         * @returns {Promise} If request is successful, it returns a promise that resolves to the result. If the
         * request fails, it resolves with errors from the backend. Be mindful that the response payload size could 
         * increase dramatically depending on the number of uuids that you send on the request. 
         */
        this.getByIds = function(uuids) {
            return catalogService.getCatalogVersionUUid().then(function(catalogVersionUUid) {
                return uuidsResource.save({
                    "catalogVersion": catalogVersionUUid,
                    "uuids": uuids
                });
            });
        };

        /**
         * @ngdoc method
         * @name cmsitemsRestServiceModule.service:cmsitemsRestService#update
         * @methodOf cmsitemsRestServiceModule.cmsitemsRestService
         * 
         * @description
         * Update a CMS Item.
         * 
         * @param {Object} cmsitem The object representing the CMS Item to update
         * @param {String} cmsitem.identifier The cms item identifier (uuid)
         * 
         * @returns {Promise} If request is successful, it returns a promise that resolves with the updated CMS Item object. If the
         * request fails, it resolves with errors from the backend.
         */
        this.update = function(cmsitem) {
            return resource.update(cmsitem);
        };

        /**
         * @ngdoc method
         * @name cmsitemsRestServiceModule.service:cmsitemsRestService#delete
         * @methodOf cmsitemsRestServiceModule.cmsitemsRestService
         * 
         * @description
         * Remove a CMS Item.
         * 
         * @param {Number} cmsitemUuid The CMS Item uuid
         */
        this.delete = function(cmsitemUuid) {
            return resource.remove({
                identifier: cmsitemUuid
            });
        };


        this._getByUIdAndType = function(uid, typeCode) {
            return this.get({
                pageSize: 100,
                currentPage: 0,
                mask: uid,
                typeCode: typeCode
            }).then(function(response) {
                return response.response.find(function(element) {
                    return element.uid === uid;
                });
            });
        };


    });
