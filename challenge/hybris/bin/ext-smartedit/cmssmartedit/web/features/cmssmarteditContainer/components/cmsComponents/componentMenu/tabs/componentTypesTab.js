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
angular.module('componentTypesTabModule', ['smarteditServicesModule', 'componentSearchModule', 'componentServiceModule', 'componentTypeModule', 'nameFilterModule', 'componentMenuServiceModule'])
    .controller('componentTypesTabController', function($log, systemEventService, ComponentService, componentMenuService) {

        // --------------------------------------------------------------------------------------------------
        // Constants
        // --------------------------------------------------------------------------------------------------

        // --------------------------------------------------------------------------------------------------
        // Lifecycle Methods
        // --------------------------------------------------------------------------------------------------
        this.$onInit = function() {
            this._retrieveComponentTypes();
        };

        // --------------------------------------------------------------------------------------------------
        // Event Handlers
        // --------------------------------------------------------------------------------------------------
        this.onSearchTermChanged = function(searchTerm) {
            this.searchTerm = searchTerm;
            componentMenuService.refreshDragAndDrop();
        }.bind(this);

        // --------------------------------------------------------------------------------------------------
        // Helper Methods
        // --------------------------------------------------------------------------------------------------
        /*
            This method is only called on init since the whole component menu is wrapped in the 'page-sensitive' directive.
            This means that if there's a page change, the directive will be recreated. 
        */
        this._retrieveComponentTypes = function() {
            ComponentService.getSupportedComponentTypesForCurrentPage().then(function(supportedTypes) {
                this.componentTypes = supportedTypes;
                componentMenuService.refreshDragAndDrop();
            }.bind(this)).catch(function(errData) {
                $log.error('ComponentMenuController.$onInit() - error loading types. ' + errData);
            });
        };
    })
    .component('componentTypesTab', {
        templateUrl: 'componentTypesTabTemplate.html',
        controller: 'componentTypesTabController',
        bindings: {}
    });
