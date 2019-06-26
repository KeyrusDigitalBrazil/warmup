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
angular.module('customViewModule', [
        'yjqueryModule', 'smarteditServicesModule', 'alertServiceModule', 'httpErrorInterceptorServiceModule', 'retryInterceptorModule'
    ])
    .constant('PATH_TO_CUSTOM_VIEW', '../../test/e2e/a_httpErrorInterceptorService/customView.html')
    .controller('customViewController', function($q, yjQuery, alertService, restServiceFactory, httpErrorInterceptorService, retryInterceptor, operationContextService) {
        this.triggerError404Json = function() {
            restServiceFactory.get("/error404_json").get().then(function() {});
        };
        this.triggerError400Json = function() {
            restServiceFactory.get("/error400_json").get().then(function() {});
        };
        this.triggerError404Html = function() {
            restServiceFactory.get("/error404_html").get().then(function() {});
        };
        this.triggerError501Json = function() {
            restServiceFactory.get("/error501_json").get().then(function() {});
        };
        this.triggerError503 = function() {
            restServiceFactory.get("/error503/a123/v1/foobar/").get().then(function() {});
        };
        this.triggerError502 = function() {
            restServiceFactory.get('/error502/retry').get().then(function() {
                yjQuery('#gd-status').html('PASSED');
            });
        };

        // custom interceptor
        httpErrorInterceptorService.addInterceptor({
            predicate: function(response) {
                return response.status === 501;
            },
            responseError: function(response) {
                alertService.showDanger({
                    message: response.data.errors[0].message
                });
                return $q.reject(response);
            }
        });

        /**
         * Graceful degradation - Retry Interceptor
         * 
         * Register a custom retry strategy with a custom operation context.
         * The e2e test assert that the user see a message when the custom retry strategy reached it's maximum number of retry.
         */
        (function setupCustomGracefulDegradation() {
            operationContextService.register('/error503/:var1/v1/:var2', 'CUSTOM_OPERATION_CONTEXT');

            var predicate503 = function(httpObj, operationContext) {
                return httpObj.status === 503 && operationContext === 'CUSTOM_OPERATION_CONTEXT';
            };
            var StrategyFactory = function() {};
            StrategyFactory.prototype.canRetry = function() {
                if (this.attemptCount > 1) {
                    yjQuery('#gd-status').html('FAILED');
                }
                return this.attemptCount <= 1;
            };
            StrategyFactory.prototype.calculateNextDelay = function() {
                return 0;
            };
            retryInterceptor.register(predicate503, StrategyFactory);

            // Strategy Factory for errors 502
            var predicate502 = function(httpObj) {
                return httpObj.status === 502;
            };
            var StrategyFactory502 = function() {};
            StrategyFactory502.prototype.canRetry = function() {
                return this.attemptCount <= 1;
            };
            StrategyFactory502.prototype.calculateNextDelay = function() {
                return 0;
            };
            retryInterceptor.register(predicate502, StrategyFactory502);
        })();
    });
