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
 * @name pageListServiceModule
 * @description
 * # The pageListServiceModule
 *
 * The Page List Service module provides a service that fetches pages for a specified catalog
 *
 */
angular.module('pageListServiceModule', ['pagesRestServiceModule'])

    /**
     * @ngdoc service
     * @name pageListServiceModule.service:pageListService
     * @deprecated since 6.6
     *
     * @description
     * The Page List Service fetches pages for a specified catalog using REST calls to the cmswebservices pages API.
     * Deprecated since 6.6, please use {@link cmsitemsRestServiceModule.cmsitemsRestService cmsitemsRestService} instead.
     */
    .service('pageListService', function(pagesRestService) {

        /**
         * @ngdoc method
         * @name pageListServiceModule.service:pageListService#getPageListForCatalog
         * @methodOf pageListServiceModule.service:pageListService
         *
         * @description
         * Fetches a list of pages for the catalog that corresponds to the specified site UID, catalogId and catalogVersion. The pages are
         * retrieved using REST calls to the cmswebservices pages API.
         *
         * @param {Object} uriParams A {@link resourceLocationsModule.object:UriContext UriContext}
         *
         * @returns {Array} An array of pages descriptors. Each descriptor provides the following pages properties:
         * creationtime, modifiedtime, pk, template, title, typeCode, uid.
         */
        this.getPageListForCatalog = function(uriParams) {
            return pagesRestService.get(uriParams);
        };

        /**
         * @ngdoc method
         * @name pageListServiceModule.service:pageListService#getPageById
         * @methodOf pageListServiceModule.service:pageListService
         *
         * @description
         * Fetches the page that matches the provided page UID
         *
         * @param {String} pageUID The UID of the page to be fetched.
         *
         * @returns {Object} A page information object. Contains the following properties:
         * creationtime, modifiedtime, pk, template, title, typeCode, uid
         */
        this.getPageById = function(pageUID) {
            return pagesRestService.getById(pageUID);
        };

    });
