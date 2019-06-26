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
angular.module('abAnalyticsServiceModule', [])
    .service('abAnalyticsService', function($q) {
        /**
         * Returns the AB analytics for a specific component by ID. Asynchronous and
         * promise based to mimic a REST transaction.
         * @returns {Promise} A promise that resolves to the AB analytics for the component
         */
        this.getABAnalyticsForComponent = function() {
            return $q.when({
                aValue: 30,
                bValue: 70
            });
        };
    });
