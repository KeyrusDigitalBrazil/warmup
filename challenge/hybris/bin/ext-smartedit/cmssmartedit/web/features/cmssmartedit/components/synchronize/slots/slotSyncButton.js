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
angular.module('slotSyncButtonModule', ['slotSynchronizationPanelModule', 'slotSynchronizationServiceModule', 'seConstantsModule'])
    .controller('slotSyncButtonController', function($scope, $translate, SYNCHRONIZATION_POLLING, SYNCHRONIZATION_STATUSES, EVENT_OUTER_FRAME_CLICKED, catalogService, slotSynchronizationService, componentHandlerService, crossFrameEventService) {

        $scope.$watch('ctrl.isPopupOpened', function() {
            this.setRemainOpen({
                button: this.buttonName,
                remainOpen: this.isPopupOpened
            });
        }.bind(this));

        this.statusIsInSync = function(syncStatus) {
            return syncStatus.status && syncStatus.status === SYNCHRONIZATION_STATUSES.IN_SYNC;
        };

        this.getSyncStatus = function() {
            componentHandlerService.getPageUUID().then(function(pageUUID) {
                slotSynchronizationService.getSyncStatus(pageUUID, this.slotId).then(function(syncStatus) {
                    this.isSlotInSync = this.statusIsInSync(syncStatus);
                    this.newPageIsNotSynchronized = !syncStatus.lastSyncStatus;
                    this.ready = true;
                }.bind(this));
            }.bind(this));
        }.bind(this);

        this.updateStatus = function(evenId, syncStatus) {
            var slotSyncStatus = (syncStatus.selectedDependencies || []).concat(syncStatus.sharedDependencies || []).find(function(slot) {
                return slot.itemId === this.slotId;
            }.bind(this)) || {};
            this.isSlotInSync = this.statusIsInSync(slotSyncStatus);
        };

        var updateStatusCallback = this.getSyncStatus;

        this.$onInit = function() {

            this.buttonName = 'slotSyncButton';
            this.isPopupOpened = false;
            this.newPageIsNotSynchronized = false;
            this.ready = false;

            this.isSlotInSync = true;
            this.getSyncStatus();
            this.unRegisterSyncPolling = crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, updateStatusCallback);
            this.newPageIsNotSynchronizedTemplate = "<div class='se-ypopover--inner-content'>" + $translate.instant("se.cms.slot.sync.from.page.level") + "</div>";

            this.unregFn = crossFrameEventService.subscribe(EVENT_OUTER_FRAME_CLICKED, function() {
                this.isPopupOpened = false;
            }.bind(this));
        };

        this.$onDestroy = function() {
            this.unRegisterSyncPolling();
            this.unregFn();
        };

    })
    .component('slotSyncButton', {
        templateUrl: 'slotSyncButtonTemplate.html',
        controller: 'slotSyncButtonController',
        controllerAs: 'ctrl',
        bindings: {
            setRemainOpen: '&',
            slotId: '@'
        }
    });
