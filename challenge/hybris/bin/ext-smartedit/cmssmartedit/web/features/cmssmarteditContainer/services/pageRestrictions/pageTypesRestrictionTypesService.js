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
 * @name pageTypesRestrictionTypesServiceModule
 * @requires pageTypesRestrictionTypesRestServiceModule
 * @description
 * This module defines the {@link pageTypesRestrictionTypesServiceModule.service:pageTypesRestrictionTypesService
 * pageTypesRestrictionTypesService} REST service used to consolidate business logic for SAP Hybris platform CMS
 * pageTypes-restrictionTypes relations.
 */
angular.module('pageTypesRestrictionTypesServiceModule', [
        'pageTypesRestrictionTypesRestServiceModule'
    ])

    /**
     * @ngdoc service
     * @name pageTypesRestrictionTypesServiceModule.service:pageTypesRestrictionTypesService
     * @requires pageTypesRestrictionTypesRestService
     * @description
     * Service that concerns business logic tasks related to CMS pageTypes-restrictionTypes relations.
     */
    .service('pageTypesRestrictionTypesService', function(
        pageTypesRestrictionTypesRestService
    ) {

        var cache = null;
        var self = this;

        /**
         * @ngdoc method
         * @name pageTypesRestrictionTypesServiceModule.service:pageTypesRestrictionTypesService#getRestrictionTypeCodesForPageType
         * @methodOf pageTypesRestrictionTypesServiceModule.service:pageTypesRestrictionTypesService
         * @param {String} pageType The page type for which the restriction type codes can be applied.
         * @returns {Array} An array of restriction type codes that can be applied to the given page type.
         */
        this.getRestrictionTypeCodesForPageType = function(pageType) {
            return self.getPageTypesRestrictionTypes().then(function(pageTypesRestrictionTypes) {
                pageTypesRestrictionTypes = pageTypesRestrictionTypes.filter(function(pageTypeRestrictionType) {
                    return pageTypeRestrictionType.pageType === pageType;
                });
                return pageTypesRestrictionTypes.map(function(pageTypeRestrictionType) {
                    return pageTypeRestrictionType.restrictionType;
                });
            });
        };

        /**
         * @ngdoc method
         * @name pageTypesRestrictionTypesServiceModule.service:pageTypesRestrictionTypesService#getPageTypesRestrictionTypes
         * @methodOf pageTypesRestrictionTypesServiceModule.service:pageTypesRestrictionTypesService
         * @returns {Array} An array of all of the pageType-restrictionType relations in the system.
         */
        this.getPageTypesRestrictionTypes = function() {
            if (cache && cache.$$state.status !== 2) { // if the get fails, allow it to be retried
                return cache;
            } else {
                cache = pageTypesRestrictionTypesRestService.getPageTypesRestrictionTypes().then(function(response) {
                    return response.pageTypeRestrictionTypeList;
                });
            }
            return cache;
        };
    });
