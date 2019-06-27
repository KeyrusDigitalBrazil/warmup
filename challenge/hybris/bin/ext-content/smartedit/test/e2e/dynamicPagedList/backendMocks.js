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
/* jshint unused:false, undef:false */
angular
    .module('e2eBackendMocks', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule', 'functionsModule'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .run(function($httpBackend, parseQuery) {

        var items = [];

        var makeItems = function() {
            for (i = 1; i <= 1000; i++) {
                items.push({
                    template: "itemTemplate-" + i,
                    name: "item-" + i,
                    typeCode: "itemTypeCode-" + i,
                    uid: "item-" + i
                });
            }
        };

        makeItems();

        $httpBackend.whenGET(/\/pagedItems/).respond(function(method, url, data, headers) {
            var params = parseQuery(url);
            var currentPage = params.currentPage;
            var pageSize = params.pageSize;
            var sort = params.sort;

            var filteredItems = items.filter(function(item) {
                return params.mask ? ((item.name && typeof item.name === 'string' && item.name.toUpperCase().indexOf(params.mask.toUpperCase()) > -1) || item.uid.toUpperCase().indexOf(params.mask.toUpperCase()) > -1) : true;
            });

            if (sort) {
                var sortColumn = sort.split(':')[0];
                var sortdirection = sort.split(':')[1];

                filteredItems = filteredItems.sort(function(a, b) {
                    var nameA = a.name.toUpperCase(); // ignore upper and lowercase
                    var nameB = b.name.toUpperCase(); // ignore upper and lowercase
                    if (nameA < nameB) {
                        return sortdirection === 'asc' ? -1 : 1;
                    }
                    if (nameA > nameB) {
                        return sortdirection === 'asc' ? 1 : -1;
                    }
                    return 0;
                });

            }

            var results = filteredItems.slice(currentPage * pageSize, currentPage * pageSize + parseInt(pageSize));

            var pagedResults = {
                pagination: {
                    count: pageSize,
                    page: currentPage,
                    totalCount: filteredItems.length
                },
                response: results
            };

            return [200, pagedResults];

        });
    });
