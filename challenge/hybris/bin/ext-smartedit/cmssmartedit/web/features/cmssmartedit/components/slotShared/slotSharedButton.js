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
angular.module('slotSharedButtonModule', ['slotSharedServiceModule', 'translationServiceModule', 'smarteditServicesModule', 'seConstantsModule', 'hasOperationPermissionModule'])
    .controller('slotSharedButtonController', function(slotSharedService, $window, crossFrameEventService, EVENT_OUTER_FRAME_CLICKED) {
        this.slotSharedFlag = false;
        this.buttonName = 'slotSharedButton';
        this.isPopupOpened = false;
        this.isPopupOpenedPreviousValue = false;

        this.$onInit = function() {

            this.slotAttributes = {
                smarteditComponentId: this.componentAttributes.smarteditComponentId,
                contentSlotUuid: this.componentAttributes.smarteditComponentUuid,
                componentType: this.componentAttributes.smarteditComponentType,
                catalogVersionUuid: this.componentAttributes.smarteditCatalogVersionUuid
            };

            slotSharedService.isSlotShared(this.slotId).then(function(result) {
                this.slotSharedFlag = result;
            }.bind(this));

            this.unregFn = crossFrameEventService.subscribe(EVENT_OUTER_FRAME_CLICKED, function() {
                this.isPopupOpened = false;
            }.bind(this));
        };

        this.$doCheck = function() {
            if (this.isPopupOpenedPreviousValue !== this.isPopupOpened) {
                this.setRemainOpen({
                    button: this.buttonName,
                    remainOpen: this.isPopupOpened
                });
                this.isPopupOpenedPreviousValue = this.isPopupOpened;
            }
        };

        this.convertToNonSharedSlotAndCloneComponents = function() {
            slotSharedService.convertSharedSlotToNonSharedSlot(this.slotAttributes, true).then(function() {
                this.isPopupOpened = false;
                $window.location.reload();
            }.bind(this));
        };

        this.convertToNonSharedSlotAndRemoveComponents = function() {
            slotSharedService.convertSharedSlotToNonSharedSlot(this.slotAttributes, false).then(function() {
                this.isPopupOpened = false;
                $window.location.reload();
            }.bind(this));
        };

        this.$onDestroy = function() {
            this.unregFn();
        };
    })
    .component('slotSharedButton', {
        templateUrl: 'slotSharedButtonTemplate.html',
        controller: 'slotSharedButtonController',
        controllerAs: 'ctrl',
        bindings: {
            setRemainOpen: '&',
            slotId: '@',
            componentAttributes: '<'
        }
    });
