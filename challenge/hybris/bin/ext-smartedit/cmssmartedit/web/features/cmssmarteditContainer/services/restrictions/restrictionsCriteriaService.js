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
angular.module('restrictionsCriteriaServiceModule', [])
    .service('restrictionsCriteriaService', function() {

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

        this.getRestrictionCriteriaOptions = function() {
            return restrictionCriteriaOptions;
        };

    });
