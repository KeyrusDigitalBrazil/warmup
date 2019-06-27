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
 * @name hiddenComponentMenuModule
 * @description
 *
 * This module contains the component used to display a menu for hidden components. 
 */
angular.module('hiddenComponentMenuModule', ['yPopupOverlayModule', 'yLoDashModule'])
    /**
     * @ngdoc object
     * @name hiddenComponentMenuModule.HIDDEN_COMPONENT_OPENED_EVENT
     *
     * @description
     * The name of the event triggered whenever a menu is opened on a hidden component. 
     */
    .constant('HIDDEN_COMPONENT_OPENED_EVENT', 'HIDDEN_COMPONENT_OPENED_EVENT')
    .controller('hiddenComponentMenuController', function(lodash, hiddenComponentMenuService, systemEventService, componentHandlerService, HIDDEN_COMPONENT_OPENED_EVENT, CONTENT_SLOT_TYPE) {

        // --------------------------------------------------------------------------------------
        // Lifecycle methods
        // --------------------------------------------------------------------------------------
        this.$onInit = function() {

            var slot = componentHandlerService.getOriginalComponent(this.slotId, CONTENT_SLOT_TYPE);
            this.slotUuid = componentHandlerService.getUuid(slot);

            this.isMenuOpen = false;

            hiddenComponentMenuService.getItemsForHiddenComponent(this.component, this.slotId).then(function(hiddenComponentMenu) {
                this.configuration = lodash.cloneDeep(hiddenComponentMenu.configuration);
                this.configuration.slotUuid = this.slotUuid;
                this.menuItems = lodash.cloneDeep(hiddenComponentMenu.buttons);
            }.bind(this));

            this.unRegRemoveComponentOpenedEvent = systemEventService.subscribe(HIDDEN_COMPONENT_OPENED_EVENT, this.onOtherMenuOpening);
        };

        this.$onDestroy = function() {
            this.unRegRemoveComponentOpenedEvent();
        };

        this.popupConfig = {
            templateUrl: 'hiddenComponentMenuItemsTemplate.html',
            halign: 'left',
            valign: 'bottom'
        };

        // --------------------------------------------------------------------------------------
        // Event Handlers
        // --------------------------------------------------------------------------------------
        this.onButtonClick = function($event) {
            $event.stopPropagation();

            this.isMenuOpen = !this.isMenuOpen;
            if (this.isMenuOpen) {
                systemEventService.publishAsync(HIDDEN_COMPONENT_OPENED_EVENT, {
                    componentId: this.component.uid,
                    slotId: this.slotId
                });
            }
        }.bind(this);

        this.onMenuHide = function() {
            this.isMenuOpen = false;
        }.bind(this);

        this.onOtherMenuOpening = function(eventId, eventData) {
            var isSameComponent = this.component.uid === eventData.componentId;
            var isSameSlot = this.slotId === eventData.slotId;

            if (!isSameComponent || !isSameSlot) {
                this.isMenuOpen = false;
            }
        }.bind(this);

        this.executeItemCallback = function(item, $event) {
            if (item.action) {
                item.action.callback(this.configuration, $event);
                this.isMenuOpen = false;
            }
        };
    })
    /**
     * @ngdoc directive
     * @name hiddenComponentMenuModule.directive:hiddenComponentMenu
     *
     * @description
     * The hidden component menu is a component used by the slot contextual menu to display a menu on hidden components.
     */
    .component('hiddenComponentMenu', {
        templateUrl: 'hiddenComponentMenuTemplate.html',
        transclude: false,
        controller: 'hiddenComponentMenuController',
        controllerAs: 'ctrl',
        bindings: {
            component: '<',
            slotId: '<'
        }
    });
