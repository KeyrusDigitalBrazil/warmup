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
 * @name pageTypesRestrictionTypesRestServiceModule
 * @requires smarteditServicesModule
 * @description
 * This module defines the {@link pageRestrictionsRestServiceModule.service:pageRestrictionsRestService pageRestrictionsRestService} REST service for pageTypes restrictionTypes API.
 */
angular.module('pageTypesRestrictionTypesRestServiceModule', [])


    /**
     * @ngdoc service
     * @name pageTypesRestrictionTypesRestServiceModule.service:pageTypesRestrictionTypesRestService
     * @requires languageService
     * @requires PAGE_TYPES_RESTRICTION_TYPES_URI
     * @requires restServiceFactory
     * @description
     * Service that handles REST requests for the pageTypes restrictionTypes CMS API endpoint.
     */
    .service('pageTypesRestrictionTypesRestService', function(
        languageService,
        PAGE_TYPES_RESTRICTION_TYPES_URI,
        restServiceFactory
    ) {

        var rest = restServiceFactory.get(PAGE_TYPES_RESTRICTION_TYPES_URI);

        /**
         * @ngdoc method
         * @name pageTypesRestrictionTypesRestServiceModule.service:pageTypesRestrictionTypesRestService#getPageTypesRestrictionTypes
         * @methodOf pageTypesRestrictionTypesRestServiceModule.service:pageTypesRestrictionTypesRestService
         * 
         * @return {Array} An array of all pageType-restrictionType in the system.
         */
        this.getPageTypesRestrictionTypes = function() {
            return rest.get().then(function(pageTypesRestrictionTypesArray) {
                return pageTypesRestrictionTypesArray;
            });
        };

    });
