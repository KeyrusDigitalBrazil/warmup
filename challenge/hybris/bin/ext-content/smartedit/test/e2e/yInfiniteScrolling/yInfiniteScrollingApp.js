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
angular.module('yInfiniteScrollingApp', ['coretemplates', 'templateCacheDecoratorModule', 'smarteditCommonsModule'])
    .controller('defaultController', function(restServiceFactory) {

        this.pageSize = 10;

        this.loadItems = function(mask, pageSize, currentPage) {
            return restServiceFactory.get('/loadItems').get({
                pageSize: pageSize,
                currentPage: currentPage,
                mask: mask,
            }).then(function(response) {
                return response;
            });
        };

    });
