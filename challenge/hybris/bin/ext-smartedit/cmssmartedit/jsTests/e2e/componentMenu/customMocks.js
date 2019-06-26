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
angular.module('customMocksModule', ['backendMocksUtilsModule'])
    .run(function($httpBackend, parseQuery, backendMocksUtils) {

        var componentsListGETMock = backendMocksUtils.getBackendMock('componentsListGETMock');
        componentsListGETMock.respond(function(method, url, data, headers) {

            var params = parseQuery(url);
            var currentPage = params.currentPage;
            var mask = params.mask;
            var pageSize = params.pageSize;
            var typeCode = params.typeCode;
            var uuids = params.uuids && params.uuids.split(',');
            var additionalParams = params.params && params.params.split(',');

            var filteredItems = JSON.parse(sessionStorage.getItem('componentMocks')).componentItems;

            if (uuids) {
                filteredItems = items.componentItems.filter(function(item) {
                    return uuids.indexOf(item.uuid) > -1;
                });
                return [200, {
                    response: filteredItems
                }];
            }

            if (typeCode) {
                filteredItems = filteredItems.filter(function(item) {
                    return item.typeCode === typeCode;
                });
            }

            if (params.catalogId === "apparel-ukContentCatalog" && params.catalogVersion === "Staged") {
                filteredItems.splice(20);
            }

            filteredItems = filteredItems.filter(function(item) {
                return mask ? ((item.name && typeof item.name === 'string' && item.name.toUpperCase().indexOf(mask.toUpperCase()) > -1) || item.uid.toUpperCase().indexOf(mask.toUpperCase()) > -1) : true;
            });

            var results = filteredItems.slice(currentPage * 10, currentPage * 10 + 10);

            var pagedResults = {
                pagination: {
                    count: 10,
                    page: currentPage,
                    totalCount: filteredItems.length,
                    totalPages: 2
                },
                response: results
            };

            return [200, pagedResults];
        });
    });
try {
    angular.module('smarteditloader').requires.push('customMocksModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('customMocksModule');
} catch (e) {}
