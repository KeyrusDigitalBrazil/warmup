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
angular.module('nameFilterModule', [])
    .filter('nameFilter', function() {
        return function(components, criteria) {
            var filterResult = [];
            if (!criteria || criteria.length < 3) {
                return components;
            }

            criteria = criteria.toLowerCase();
            var criteriaList = criteria.split(" ");

            (components || []).forEach(function(component) {
                var match = true;
                var term = component.name.toLowerCase();

                criteriaList.forEach(function(item) {
                    if (term.indexOf(item) === -1) {
                        match = false;
                        return false;
                    }
                });

                if (match && filterResult.indexOf(component) === -1) {
                    filterResult.push(component);
                }
            });
            return filterResult;
        };
    });
