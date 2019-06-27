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
 * @name restrictionTypesServiceModule
 * @requires pageTypesRestrictionTypesServiceModule
 * @requires restrictionTypesRestServiceModule
 * @description
 * This module defines the {@link restrictionTypesServiceModule.service:restrictionTypesService restrictionTypesService} REST service used to consolidate business logic for SAP Hybris platform CMS restriction types for
 * both pages and components.
 */
angular.module('restrictionTypesServiceModule', [
        'pageTypesRestrictionTypesServiceModule',
        'restrictionTypesRestServiceModule'
    ])

    /**
     * @ngdoc service
     * @name restrictionTypesServiceModule.service:restrictionTypesService
     * @requires $q
     * @requires languageService
     * @requires pageTypesRestrictionTypesService
     * @requires restrictionTypesRestService
     * @description
     * Service that concerns business logic tasks related to CMS page and component restriction types in the SAP Hybris platform.
     * This service fetches all restriction types configured on the platform, and caches them for the duration of the session.
     */
    .service('restrictionTypesService', function(
        $q,
        languageService,
        pageTypesRestrictionTypesService,
        restrictionTypesRestService) {

        var cache = null;
        var self = this;

        /**
         * @ngdoc method
         * @name restrictionTypesServiceModule.service:restrictionTypesService#getRestrictionTypesByPageType
         * @methodOf restrictionTypesServiceModule.service:restrictionTypesService
         * @param {String} pageType The page type for which the restrictions types can be applied
         * @returns {Array} All types of restriction that can be applied to the given page type.
         */
        this.getRestrictionTypesByPageType = function(pageType) {
            return self.getRestrictionTypes().then(function(restrictionTypes) {
                return pageTypesRestrictionTypesService.getRestrictionTypeCodesForPageType(pageType).then(function(restrictionTypeCodes) {
                    return restrictionTypes.filter(function(restrictionType) {
                        return restrictionTypeCodes.indexOf(restrictionType.code) >= 0;
                    });
                });
            });
        };

        /**
         * @ngdoc method
         * @name restrictionTypesServiceModule.service:restrictionTypesService#getRestrictionTypes
         * @methodOf restrictionTypesServiceModule.service:restrictionTypesService
         * @returns {Array} All restriction types in the system
         */
        this.getRestrictionTypes = function() {
            if (cache && cache.$$state.status !== 2) { // if the get fails, allow it to be retried
                return cache;
            } else {
                cache = restrictionTypesRestService.getRestrictionTypes().then(function(response) {
                    return response.restrictionTypes;
                });
            }
            return cache;
        };

        /**
         * @ngdoc method
         * @name restrictionTypesServiceModule.service:restrictionTypesService#getRestrictionTypeForTypeCode
         * @methodOf restrictionTypesServiceModule.service:restrictionTypesService
         * @param {String} The typeCode of a restriction
         * @returns {Object} The restriction type object, who's code property matches the given restriction typeCode property
         */
        this.getRestrictionTypeForTypeCode = function(typeCode) {
            return self.getRestrictionTypes().then(function(restrictionTypes) {
                return restrictionTypes.find(function(restrictionType) {
                    return restrictionType.code === typeCode;
                });
            });
        };

    });
