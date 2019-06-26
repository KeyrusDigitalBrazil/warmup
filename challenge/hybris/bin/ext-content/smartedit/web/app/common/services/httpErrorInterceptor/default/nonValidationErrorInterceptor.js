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
angular.module('nonvalidationErrorInterceptorModule', ['alertServiceModule'])
    /**
     * @ngdoc service
     * @name nonvalidationErrorInterceptorModule.service:nonValidationErrorInterceptor
     * @description
     * Used for HTTP error code 400. It removes all errors of type 'ValidationError' and displays alert messages for non-validation errors.
     */
    .factory('nonValidationErrorInterceptor', function($q, alertService) {
        return {
            predicate: function(response) {
                return response.status === 400;
            },
            responseError: function(response) {
                if (response.data && response.data.errors) {
                    response.data.errors.filter(function(error) {
                        return error.type !== 'ValidationError';
                    }).forEach(function(error) {
                        alertService.showDanger({
                            message: error.message || 'se.unknown.request.error',
                            timeout: 10000
                        });
                    });
                }
                return $q.reject(response);
            }
        };
    });
