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
angular.module('outerapp', ['smarteditServicesModule', 'templateCacheDecoratorModule', 'ui.bootstrap'])
    .run(function(gatewayFactory) {
        gatewayFactory.initListener();
    })
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .controller('defaultController', function($rootScope, $scope, $q, gatewayFactory, systemEventService, customTimeout) {

        var gateway1 = gatewayFactory.createGateway('Gateway1');
        var gateway2 = gatewayFactory.createGateway('Gateway2');

        $scope.notifyIframeOnGateway1 = function() {
            this.notifyIframe(gateway1);
        };

        $scope.notifyIframeOnGateway2 = function() {
            this.notifyIframe(gateway2);
        };

        $scope.notifyIframe = function(gateway) {
            gateway.publish("display1", {
                message: 'hello Iframe ! (from parent)'
            }).then(function(returnValue) {
                $scope.acknowledged = "(iframe acknowledged my message and sent back:" + returnValue + ")";
                customTimeout(function() {
                    delete $scope.acknowledged;
                }, 3000);
            }, function() {
                $scope.acknowledged = "(iframe did not acknowledge my message)";
                customTimeout(function() {
                    delete $scope.acknowledged;
                }, 3000);
            });
        };

        gateway1.subscribe("display2", function(eventId, data) {
            var deferred = $q.defer();
            $scope.message = data.message;
            customTimeout(function() {
                delete $scope.message;
            }, 3000);
            deferred.resolve("hello to you iframe");
            return deferred.promise;
        });

    });
