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
angular.module("itemPrinterModule", [])
    .component('itemPrinter', {
        transclude: false,
        replace: false,
        template: '<div class="se-item-printer" ng-include="printer.templateUrl"></div>',
        require: {
            ySelect: '^ySelect'
        },
        controller: ['$scope', function($scope) {
            $scope.selected = true;
            this.$onChanges = function() {
                /* needs to bind it scope and not controller in order for the templates required by API
                 * to be agnostic of whether they are invoked within ui-select-coices or ui-select-match of ui-select
                 */
                $scope.item = this.model;
                $scope.ySelect = this.ySelect;
            };
        }],
        controllerAs: 'printer',
        bindings: {
            templateUrl: "<",
            model: "<"
        }
    });
