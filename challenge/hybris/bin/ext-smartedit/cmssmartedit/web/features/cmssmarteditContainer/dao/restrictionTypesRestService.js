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
 * @name restrictionTypesRestServiceModule
 * @requires smarteditServicesModule
 * @description
 * This module contains the {@link restrictionTypesRestServiceModule.service:restrictionTypesRestService restrictionTypesRestService} REST service for restrictionTypes API.
 */
angular.module('restrictionTypesRestServiceModule', [])


    /**
     * @ngdoc service
     * @name restrictionTypesRestServiceModule.service:restrictionTypesRestService
     * @requires languageService
     * @requires RESTRICTION_TYPES_URI
     * @requires restServiceFactory
     * @description
     * Service that handles REST requests for the restrictionTypes CMS API endpoint.
     */
    .service('restrictionTypesRestService', function(
        languageService,
        RESTRICTION_TYPES_URI,
        restServiceFactory
    ) {

        var restrictionTypesRestService = restServiceFactory.get(RESTRICTION_TYPES_URI);

        /**
         * @ngdoc method
         * @name restrictionTypesRestServiceModule.service:restrictionTypesRestService#getRestrictionTypes
         * @methodOf restrictionTypesRestServiceModule.service:restrictionTypesRestService
         * @returns {Array} An array of all restriction types in the system.
         */
        this.getRestrictionTypes = function() {
            return restrictionTypesRestService.get().then(function(restrictionTypesResponse) {
                return restrictionTypesResponse;
            });
        };

    });
