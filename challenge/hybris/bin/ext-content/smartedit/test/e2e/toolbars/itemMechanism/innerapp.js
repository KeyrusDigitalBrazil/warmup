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
angular.module('innerapp', ['ui.bootstrap', 'toolbarModule'])
    .run(function(gatewayFactory) {
        gatewayFactory.initListener();
    })
    .controller('defaultController', function($scope, $q, toolbarServiceFactory) {
        var toolbarService = toolbarServiceFactory.getToolbarService('toolbar');
        $scope.sendActions = function() {
            toolbarService.addItems([{
                key: 'toolbar.action.action3',
                type: 'ACTION',
                nameI18nKey: 'toolbar.action.action3',
                callback: function() {
                    $scope.message = 'Action 3 called';
                },
                icons: ['icon3.png']
            }, {
                key: 'toolbar.action.action4',
                type: 'ACTION',
                nameI18nKey: 'toolbar.action.action4',
                callback: function() {
                    $scope.message = 'Action 4 called';
                },
                icons: ['icon4.png']
            }]);
        };

        $scope.removeAction = function() {
            toolbarService.removeItemByKey('toolbar.action.action4');
        };
    });
