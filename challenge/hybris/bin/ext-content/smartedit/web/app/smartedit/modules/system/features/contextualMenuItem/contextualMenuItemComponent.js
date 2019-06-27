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
angular.module('contextualMenuItemModule', [
        'yPopupOverlayModule',
        'smarteditServicesModule'
    ])

    .controller('contextualMenuItemController', function($element) {
        var modes = ['small', 'compact'];
        this._validateInput = function() {
            if (typeof this.mode !== 'string' || modes.indexOf(this.mode) === -1) {
                throw "Error initializing contextualMenuItem - unknown mode";
            }
        };
        this.isHybrisIcon = function(icon) {
            return icon && icon.indexOf("hyicon") >= 0;
        };

        this.$onInit = function() {

            this.classes = 'cmsx-ctx__icon-more--small ' + this.itemConfig.displayClass;

            this._validateInput();

            if (this.itemConfig.action && this.itemConfig.action.callbacks) {
                var compAttrs = this.componentAttributes;
                var slotAttrs = this.slotAttributes;
                angular.forEach(this.itemConfig.action.callbacks, function(value, key) {
                    $element.on(key, value.bind(undefined, {
                        componentType: compAttrs.smarteditComponentType,
                        componentId: compAttrs.smarteditComponentId,
                        componentUuid: compAttrs.smarteditComponentUuid,
                        containerType: compAttrs.smarteditContainerType,
                        containerId: compAttrs.smarteditContainerId,
                        componentAttributes: compAttrs,
                        slotId: slotAttrs.smarteditSlotId,
                        slotUuid: slotAttrs.smarteditSlotUuid
                    }));
                });
            }
        };

        this.$onDestroy = function() {
            $element.off();
        };

        this.setHoverState = function(isHovered) {
            this.isHovered = isHovered;
        }.bind(this);

        this.getIconState = function() {
            if (this.isHovered || this.itemConfig.isOpen) {
                return this.itemConfig.iconNonIdle;
            }
            return this.itemConfig.iconIdle;
        }.bind(this);

    })

    .component('contextualMenuItem', {
        controller: 'contextualMenuItemController',
        templateUrl: 'contextualMenuItemComponentTemplate.html',
        bindings: {
            mode: '@',
            index: '<',
            componentAttributes: '<',
            slotAttributes: '<',
            itemConfig: '<'
        }
    });
