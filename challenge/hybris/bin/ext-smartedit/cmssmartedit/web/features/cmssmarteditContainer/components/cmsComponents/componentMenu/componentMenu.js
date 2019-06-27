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
angular.module('componentMenuModule', ['smarteditServicesModule', 'componentTypesTabModule', 'componentsTabModule', 'cmsDragAndDropServiceModule', 'componentMenuServiceModule'])
    .controller('componentMenuController', function($q, systemEventService, componentMenuService, DRAG_AND_DROP_EVENTS, EVENTS) {

        // --------------------------------------------------------------------------------------------------
        // Constants
        // --------------------------------------------------------------------------------------------------
        var OPEN_COMPONENT_EVENT = 'ySEComponentMenuOpen';
        var OVERLAY_DISABLED_EVENT = 'OVERLAY_DISABLED';
        var RESET_COMPONENT_MENU_EVENT = 'RESET_COMPONENT_MENU_EVENT';

        var TAB_IDS = {
            COMPONENT_TYPES_TAB_ID: 'componentTypesTab',
            COMPONENTS_TAB_ID: 'componentsTab'
        };

        // --------------------------------------------------------------------------------------------------
        // Methods
        // --------------------------------------------------------------------------------------------------
        this.$onInit = function() {
            this._recompileDom = function() {};
            this._initializeComponentMenu();
            this.removePageChangeEventHandler = systemEventService.subscribe(EVENTS.PAGE_CHANGE, this._initializeComponentMenu);
            this.removeOpenComponentEventHandler = systemEventService.subscribe(OPEN_COMPONENT_EVENT, this._resetComponentMenu);
            this.removeDropdownEvent = systemEventService.subscribe(DRAG_AND_DROP_EVENTS.DRAG_STARTED, this._closeMenu);
            this.removeOverlapEvent = systemEventService.subscribe(OVERLAY_DISABLED_EVENT, this._closeMenu);
        };

        this.$onDestroy = function() {
            this.removePageChangeEventHandler();
            this.removeOpenComponentEventHandler();
            this.removeDropdownEvent();
            this.removeOverlapEvent();
        };

        // --------------------------------------------------------------------------------------------------
        // Helper Methods
        // --------------------------------------------------------------------------------------------------
        this._initializeComponentMenu = function() {
            // This is to ensure that the component menu DOM is completely clean, even after a page change.
            this.tabsList = null;
            this._recompileDom();

            componentMenuService.hasMultipleContentCatalogs().then(function(hasMultipleContentCatalogs) {
                this.hasMultipleContentCatalogs = hasMultipleContentCatalogs;

                var componentTypesTab = {
                    id: TAB_IDS.COMPONENT_TYPES_TAB_ID,
                    title: 'se.cms.compomentmenu.tabs.componenttypes',
                    templateUrl: 'componentTypesTabWrapperTemplate.html'
                };
                var componentsTab = {
                    id: TAB_IDS.COMPONENTS_TAB_ID,
                    title: 'se.cms.compomentmenu.tabs.customizedcomp',
                    templateUrl: 'componentsTabWrapperTemplate.html',
                    hasMultipleContentCatalogs: hasMultipleContentCatalogs
                };
                this.tabsList = [componentTypesTab, componentsTab];

                // This variable is assigned in the tabsList to make sure there's no
                // tight coupling with the view (instead of relying on a position in the array). 
                this.tabsList.componentsTab = componentsTab;
                this._resetComponentMenu();
            }.bind(this));
        }.bind(this);

        this._resetComponentMenu = function() {
            if (this.tabsList) {
                this.tabsList.map(function(tab) {
                    tab.active = (tab.id === TAB_IDS.COMPONENT_TYPES_TAB_ID);
                    return tab.active;
                });

                // Reset tab contents. 
                systemEventService.publishAsync(RESET_COMPONENT_MENU_EVENT);
            }
        }.bind(this);

        this._closeMenu = function() {
            if (this.actionItem) {
                this.actionItem.isOpen = false;
            }
        }.bind(this);

    })
    .component('componentMenu', {
        templateUrl: 'componentMenuTemplate.html',
        controller: 'componentMenuController',
        bindings: {
            actionItem: '<'
        }
    });
