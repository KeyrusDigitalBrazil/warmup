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
 * @name pageRestrictionsCriteriaModule
 * @description
 * This module defines the {@link pageRestrictionsCriteriaModule.service:pageRestrictionsCriteriaService pageRestrictionsCriteriaService} service used to consolidate business logic for restriction criteria.
 */
angular.module('pageRestrictionsCriteriaModule', [])

    /**
     * @ngdoc service
     * @name pageRestrictionsCriteriaModule.service:pageRestrictionsCriteriaService
     * @description
     * A service for working with restriction criteria.
     */
    .service('pageRestrictionsCriteriaService', function() {

        var ALL = {};
        var ANY = {};

        function setupCriteria(criteria, id, boolValue) {
            Object.defineProperty(criteria, 'id', {
                writable: false,
                value: id
            });
            Object.defineProperty(criteria, 'label', {
                writable: false,
                value: 'se.cms.restrictions.criteria.' + id
            });
            Object.defineProperty(criteria, 'editLabel', {
                writable: false,
                value: 'se.cms.restrictions.criteria.select.' + id
            });
            Object.defineProperty(criteria, 'value', {
                writable: false,
                value: boolValue
            });
        }
        setupCriteria(ALL, 'all', false);
        setupCriteria(ANY, 'any', true);

        var restrictionCriteriaOptions = [ALL, ANY];

        /**
         * @ngdoc method
         * @name pageRestrictionsCriteriaModule.service:pageRestrictionsCriteriaService#getMatchCriteriaLabel
         * @methodOf pageRestrictionsCriteriaModule.service:pageRestrictionsCriteriaService
         * @param {Boolean} onlyOneRestrictionMustApply A boolean to determine whether one restriction should be applied.
         * @return {String} The i18n key of the restriction criteria.
         */
        this.getMatchCriteriaLabel = function(onlyOneRestrictionMustApply) {
            if (onlyOneRestrictionMustApply) {
                return ANY.label;
            }
            return ALL.label;
        };

        /**
         * @ngdoc method
         * @name pageRestrictionsCriteriaModule.service:pageRestrictionsCriteriaService#getRestrictionCriteriaOptions
         * @methodOf pageRestrictionsCriteriaModule.service:pageRestrictionsCriteriaService
         * @return {Array} An array of criteria options.
         */
        this.getRestrictionCriteriaOptions = function() {
            return restrictionCriteriaOptions;
        };

        /**
         * @ngdoc method
         * @name pageRestrictionsCriteriaModule.service:pageRestrictionsCriteriaService#getRestrictionCriteriaOptionFromPage
         * @methodOf pageRestrictionsCriteriaModule.service:pageRestrictionsCriteriaService
         * @return {Object} An object of the restriction criteria for the given page.
         */
        this.getRestrictionCriteriaOptionFromPage = function(page) {
            if (page && typeof page.onlyOneRestrictionMustApply === 'boolean') {
                if (page.onlyOneRestrictionMustApply) {
                    return ANY;
                }
            }
            return ALL;
        };

    });
