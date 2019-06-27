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
angular.module('alertsBoxModule', []).directive('alertsBox', function() {
    return {
        templateUrl: 'alertsTemplate.html',
        restrict: 'E', // it kicks in on <alerts-box> elements
        transclude: true,
        replace: false,
        scope: {
            alerts: '='
        },
        link: function(scope) {
            scope.dismissAlert = function(index) {
                scope.alerts.splice(index, 1);
            };
        }
    };
});
