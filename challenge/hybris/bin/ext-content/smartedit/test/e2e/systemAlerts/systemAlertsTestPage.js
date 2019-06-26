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
angular.module('systemAlertsTestPageModule', [
        'alertServiceModule'
    ])

    .controller('systemAlertsTestController', function($scope, $timeout, alertService) {


        // default for manual testing
        // automation starts each test with reset()
        this.alert = {
            message: '',
            type: 'info',
            closeable: true,
            timeout: 5000,
            template: '',
            templateUrl: ''
        };

        this.types = ['info', 'warning', 'danger', 'success'];

        this.count = 1;

        this.addAlert = function() {
            if (this.alert.messagePlaceholders) {
                this.alert.messagePlaceholders = $scope.$eval(this.alert.messagePlaceholders);
            }
            for (var i = 0; i < this.count; i++) {
                alertService.showAlert(this.alert);
            }
        };

        this.reset = function() {
            this.alert = {};
        };

    })

    .component('systemAlertsTest', {
        controller: 'systemAlertsTestController',
        templateUrl: '/test/e2e/systemAlerts/systemAlertsTestPageTemplate.html'
    });

angular.module('smarteditcontainer').requires.push('systemAlertsTestPageModule');
