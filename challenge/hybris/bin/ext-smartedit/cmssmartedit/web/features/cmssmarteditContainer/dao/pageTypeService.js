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
 * @name pageTypeServiceModule
 *
 * @description
 * The Page Type Service module provides a service that retrieves all supported page types
 *
 */
angular.module('pageTypeServiceModule', [])

    /**
     * @ngdoc service
     * @name pageTypeServiceModule.pageTypeService
     *
     * @description
     * Service that concerns business logic tasks related to CMS page types in the SAP Hybris platform.
     * This service retrieves all supported page types configured on the platform, and caches them for the duration of the session.
     */
    .service('pageTypeService', function(restServiceFactory, PAGE_TYPES_URI) {
        var pageTypeRestService = restServiceFactory.get(PAGE_TYPES_URI);
        var pageTypes;

        /**
         * @ngdoc method
         * @name pageTypeServiceModule.pageTypeService.getPageTypes
         * @methodOf pageTypeServiceModule.pageTypeService
         *
         * @description
         * Returns a list of page type descriptor objects. The page type descriptor object
         * returned is structured as follows:
         *
         * ```js
         *  {
         *      code {String} The unique identifier for the page type.
         *      name {Object} A map between language ISO code and localized name of the page type.
         *      description {Object} A map between language ISO code and localized description of the page type.
         *  };
         * ```
         *
         * @returns {Array} An array of page type objects
         */
        this.getPageTypes = function() {
            pageTypes = pageTypes || pageTypeRestService.get();
            return pageTypes.then(function(pageTypesResponse) {
                return pageTypesResponse.pageTypes;
            });
        };
    });
