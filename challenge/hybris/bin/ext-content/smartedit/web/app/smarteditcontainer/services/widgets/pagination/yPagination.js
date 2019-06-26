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
/**
 * @ngdoc overview
 * @name yPaginationModule
 *
 * @description
 * The yPaginationModule contains the yPagination which is a wrapper around the bootstrap uib-pagination
 * directive that is focused on providing pagination.
 */
angular.module('yPaginationModule', [])
    .controller('yPaginationController', function() {

        this.$onInit = function() {
            //in order to propagate down changes to ngModel from the parent controller
            this.exposedModel.$viewChangeListeners.push(this.onPageChange);
            this.exposedModel.$render = this.onPageChange;
        };

        this.onPageChange = function() {
            this.currentPage = this.exposedModel.$modelValue;
        }.bind(this);

        this.pageChanged = function() {
            this.exposedModel.$setViewValue(this.currentPage);
            this.onChange({
                $currentPage: this.currentPage
            });
        };

    })
    /**
     * @ngdoc directive
     * @name ySelectModule.directive:yPagination
     * @scope
     * @restrict E
     * @element y-pagination
     *
     * @description
     * The SmartEdit component that provides pagination by providing a visual pagination bar and buttons/numbers to navigate between pages.
     *
     * You need to bind the current page value to the ng-model property of the component.
     *
     * @param {<Number} totalItems The total number of items.
     * @param {<Number} itemsPerPage The total number of items per page.
     * @param {<String=} boundaryLinks Whether to display First / Last buttons. Defaults to false.
     * @param {<Number} ngModel The current page number.
     * @param {&Function} onCurrentPageChange The function that is called with the current page number when either a button or page number is clicked. The invoker
     * can bind this to a custom function to act and fetch results based on new page number.
     */
    .component('yPagination', {
        templateUrl: 'yPaginationTemplate.html',
        controller: 'yPaginationController',
        require: {
            exposedModel: 'ngModel'
        },
        bindings: {
            totalItems: '<',
            itemsPerPage: '<',
            boundaryLinks: '<?',
            onChange: '&'
        }
    });
