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
 * @name pageRestrictionsModule
 * @requires pageRestrictionsCriteriaModule
 * @requires pageRestrictionsServiceModule
 * @requires restrictionTypesServiceModule
 * @description
 * This module defines the {@link pageRestrictionsModule.factory:pageRestrictionsFacade pageRestrictionsFacade} facade module for page restrictions.
 */
angular.module('pageRestrictionsModule', [
        'pageRestrictionsCriteriaModule',
        'pageRestrictionsServiceModule',
        'restrictionTypesServiceModule'
    ])

    /**
     * @ngdoc service
     * @name pageRestrictionsModule.factory:pageRestrictionsFacade
     * @requires pageRestrictionsCriteriaService
     * @requires pageRestrictionsService
     * @requires restrictionTypesService
     * @description
     * A facade that exposes only the business logic necessary for features that need to work with page restrictions.
     */
    .factory('pageRestrictionsFacade', function(
        pageRestrictionsCriteriaService,
        pageRestrictionsService,
        restrictionTypesService
    ) {

        return {

            // pageRestrictionsCriteriaService
            getRestrictionCriteriaOptions: pageRestrictionsCriteriaService.getRestrictionCriteriaOptions,
            getRestrictionCriteriaOptionFromPage: pageRestrictionsCriteriaService.getRestrictionCriteriaOptionFromPage,

            // restrictionTypesService
            getRestrictionTypesByPageType: restrictionTypesService.getRestrictionTypesByPageType,

            // pageRestrictionsService
            getRestrictionsByPageUID: pageRestrictionsService.getRestrictionsByPageUID, //@deprecated since 6.4
            isRestrictionTypeSupported: pageRestrictionsService.isRestrictionTypeSupported,
            updateRestrictionsByPageUID: pageRestrictionsService.updateRestrictionsByPageUID, //@deprecated since 6.5

            //new API's
            getRestrictionsByPageUUID: pageRestrictionsService.getRestrictionsByPageUUID

        };

    });
