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
angular.module('filterByFieldFilterModule', [])
    /**
     * @ngdoc filter
     * @name filterByFieldFilterModule.filter:filterByField
     * @description
     * A filter for an array of objects, that will search all the first level fields of an object,
     * or optionally allows you to specify which fields to include in the search. Only fields that correspond to string
     * values will be considered in the filtering. The filter implements the AND strategy, thus the filter will return search results
     * regardless of the search string order. IE search string "Add Mobile" will return strings such "Mobile Address" and "Address Mobile"
     *
     * @param {Array} items An array of objects
     * @param {String} query The search string in which the values will be filtered by. If no search string is given
     * the original array of objects is be returned.
     * @param {Array} keys (OPTIONAL) An array of object fields which determines which key values that the filter will parse through.
     * If no array is specified the filter will check each field value in the array of objects.
     * @param {Function} callbackFcn (OPTIONAL) A function that will be executed after each iteration of the filter.
     *
     */
    .filter('filterByField', function() {
        return function(items, query, keys, callbackFcn) {
            var filterResult = [];
            callbackFcn = callbackFcn || angular.noop;

            if (!query) {
                callbackFcn(items);
                return items;
            }

            query = query.toLowerCase();
            var queryList = query.split(" ");

            (items || []).forEach(function(item) {
                keys = keys || Object.keys(item);

                var terms = keys.map(function(key) {
                    return item[key];
                }).filter(function(value) {
                    return typeof value === 'string' || value instanceof String;
                }).map(function(value) {
                    return value.toLowerCase();
                });

                var matchList = queryList.map(function(queryItem) {
                    return (terms.find(function(term) {
                        return term.indexOf(queryItem) >= 0;
                    })) ? true : false;
                }).filter(function(exists) {
                    return !exists;
                });

                if (matchList.length === 0) {
                    filterResult.push(item);
                }
            });

            callbackFcn(filterResult);
            return filterResult;
        };
    });
