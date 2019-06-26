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
angular.module('slotUnsharedButtonModule', ['slotUnsharedServiceModule', 'translationServiceModule', 'confirmationModalServiceModule', 'smarteditServicesModule', 'seConstantsModule', 'hasOperationPermissionModule'])
    .controller('slotUnsharedButtonController', function($scope, $window, EVENT_OUTER_FRAME_CLICKED, slotUnsharedService, confirmationModalService, crossFrameEventService) {

        this.revertToSharedSlot = function() {

            var message = {
                title: 'se.cms.slot.unshared.revert.to.shared.title',
                templateUrl: 'revertToSharedSlotConfirmationTemplate.html'
            };

            confirmationModalService.confirm(message).then(function() {
                slotUnsharedService.revertToSharedSlot(this.slotUuid).then(function() {
                    this.isPopupOpened = false;
                    $window.location.reload();
                }.bind(this));
            }.bind(this));

        };

        this.$onInit = function() {

            this.slotUnsharedFlag = false;
            this.buttonName = 'slotUnsharedButton';
            this.isPopupOpened = false;
            this.isPopupOpenedOldValue = this.isPopupOpened;

            slotUnsharedService.isSlotUnshared(this.slotUid).then(function(result) {
                this.slotUnsharedFlag = result;
            }.bind(this));

            this.unregFn = crossFrameEventService.subscribe(EVENT_OUTER_FRAME_CLICKED, function() {
                this.isPopupOpened = false;
            }.bind(this));
        };

        this.$doCheck = function() {

            if (this.isPopupOpenedOldValue !== this.isPopupOpened) {
                this.isPopupOpenedOldValue = this.isPopupOpened;
                this.setRemainOpen({
                    button: this.buttonName,
                    remainOpen: this.isPopupOpened
                });
            }

        };

        this.$onDestroy = function() {
            this.unregFn();
        };

    })
    .component('slotUnsharedButton', {
        templateUrl: 'slotUnsharedButtonTemplate.html',
        controller: 'slotUnsharedButtonController',
        controllerAs: 'ctrl',
        bindings: {
            setRemainOpen: '&',
            slotUuid: '@',
            slotUid: '@'
        }
    });
