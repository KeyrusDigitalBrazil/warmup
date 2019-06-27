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
angular.module('innerapp', ['smarteditServicesModule', 'ui.bootstrap'])
    .run(function(gatewayFactory) {
        gatewayFactory.initListener();
    })
    .controller('defaultController', function($scope, $q, gatewayFactory, customTimeout) {

        var gateway1 = gatewayFactory.createGateway('Gateway1');
        var gateway2 = gatewayFactory.createGateway('Gateway2');

        $scope.notifyParent = function() {
            $scope.acknowledged = "";
            gateway1.publish("display2", {
                message: 'hello parent ! (from iframe)'
            }).then(function(returnValue) {
                $scope.acknowledged = "(parent acknowledged my message and sent back:" + returnValue + ")";
                customTimeout(function() {
                    delete $scope.acknowledged;
                }, 2000);
            }, function() {
                $scope.acknowledged = "(parent did not acknowledge my message)";
                customTimeout(function() {
                    delete $scope.acknowledged;
                }, 2000);
            });
        };

        //first listener
        gateway1.subscribe("display1", function(eventId, data) {
            var deferred = $q.defer();
            if ($scope.listener1WillSucceed) {
                $scope.message = data.message;
                customTimeout(function() {
                    delete $scope.message;
                }, 2000);
                deferred.resolve("hello to you parent from first listener on gateway1");
            } else {
                $scope.message = 'failure';
                customTimeout(function() {
                    delete $scope.message;
                }, 3000);
                deferred.reject();
            }
            return deferred.promise;
        });

        //second listener
        gateway1.subscribe("display1", function(eventId, data) {
            var deferred = $q.defer();
            if ($scope.listener2WillSucceed) {
                $scope.message2 = data.message;
                customTimeout(function() {
                    delete $scope.message2;
                }, 2000);
                deferred.resolve("hello to you parent from second listener on gateway1");
            } else {
                $scope.message2 = 'failure';
                customTimeout(function() {
                    delete $scope.message2;
                }, 3000);
                deferred.reject();
            }
            return deferred.promise;
        });

        //third listener, on second gateway
        gateway2.subscribe("display1", function(eventId, data) {
            var deferred = $q.defer();
            if ($scope.listener3WillSucceed) {
                $scope.message3 = data.message;
                customTimeout(function() {
                    delete $scope.message3;
                }, 2000);
                deferred.resolve("hello to you parent from unique listener on gateway2");
            } else {
                $scope.message3 = 'failure';
                customTimeout(function() {
                    delete $scope.message3;
                }, 3000);
                deferred.reject();
            }
            return deferred.promise;
        });
    });
