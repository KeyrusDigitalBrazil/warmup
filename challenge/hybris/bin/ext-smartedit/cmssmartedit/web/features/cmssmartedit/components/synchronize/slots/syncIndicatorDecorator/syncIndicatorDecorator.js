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
angular.module('syncIndicatorDecoratorModule', ['slotSynchronizationServiceModule'])
    .controller('syncIndicatorController', function($q, catalogService, slotSynchronizationService, crossFrameEventService, componentHandlerService, SYNCHRONIZATION_STATUSES, SYNCHRONIZATION_POLLING) {

        this.isVersionNonActive = false;
        this.unRegisterSyncPolling = angular.noop;

        this.$onInit = function() {
            // initial sync status is set to unavailable until the first fetch
            this.syncStatus = {
                status: SYNCHRONIZATION_STATUSES.UNAVAILABLE
            };

            componentHandlerService.getPageUUID().then(function(pageUUID) {
                this.pageUUID = pageUUID;
                this.unRegisterSyncPolling = crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, this.fetchSyncStatus.bind(this));

                catalogService.isContentCatalogVersionNonActive().then(function(isNonActive) {
                    this.isVersionNonActive = isNonActive;
                    if (this.isVersionNonActive) {
                        this.fetchSyncStatus();
                    }
                }.bind(this));
            }.bind(this));
        };

        this.$onDestroy = function() {
            this.unRegisterSyncPolling();
        };

        this.fetchSyncStatus = function() {
            return this.isVersionNonActive ? slotSynchronizationService.getSyncStatus(this.pageUUID, this.componentAttributes.smarteditComponentId).then(function(response) {
                this.syncStatus = response;
            }.bind(this), function() {
                this.syncStatus.status = SYNCHRONIZATION_STATUSES.UNAVAILABLE;
            }.bind(this)) : $q.when();
        }.bind(this);

    })
    .directive('syncIndicator', [function() {
        return {
            templateUrl: 'syncIndicatorDecoratorTemplate.html',
            restrict: 'C',
            transclude: true,
            replace: false,
            controller: 'syncIndicatorController',
            controllerAs: 'ctrl',
            bindToController: {
                active: '=',
                componentAttributes: '<'
            }
        };
    }]);
