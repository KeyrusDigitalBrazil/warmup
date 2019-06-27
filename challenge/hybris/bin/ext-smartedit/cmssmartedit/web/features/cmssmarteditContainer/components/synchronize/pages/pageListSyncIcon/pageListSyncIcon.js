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
angular.module('pageListSyncIconModule', ['cmsSmarteditServicesModule'])

    .controller('pageListSyncIconController', function(pageSynchronizationService, catalogService, SYNCHRONIZATION_STATUSES, SYNCHRONIZATION_POLLING, crossFrameEventService) {

        this.unRegisterSyncPolling = angular.noop;
        this.classes = {};
        this.classes[SYNCHRONIZATION_STATUSES.UNAVAILABLE] = "";
        this.classes[SYNCHRONIZATION_STATUSES.IN_SYNC] = "hyicon-done se-sync-button__sync__done";
        this.classes[SYNCHRONIZATION_STATUSES.NOT_SYNC] = "hyicon-sync se-sync-button__sync__sync-not";
        this.classes[SYNCHRONIZATION_STATUSES.IN_PROGRESS] = "hyicon-sync se-sync-button__sync__sync-not";
        this.classes[SYNCHRONIZATION_STATUSES.SYNC_FAILED] = "hyicon-sync se-sync-button__sync__sync-not";

        this.fetchSyncStatus = function() {
            return pageSynchronizationService.getSyncStatus(this.pageId, this.uriContext).then(function(response) {
                this.syncStatus = response;
            }.bind(this), function() {
                this.syncStatus.status = SYNCHRONIZATION_STATUSES.UNAVAILABLE;
            }.bind(this));
        }.bind(this);


        this.triggerFetch = function(eventId, eventData) {
            if (eventData.itemId === this.pageId) {
                this.fetchSyncStatus();
            }
        };

        this.$onInit = function() {
            catalogService.isContentCatalogVersionNonActive(this.uriContext).then(function(isNonActive) {

                if (isNonActive) {
                    // set initial sync status to unavailable
                    this.syncStatus = {
                        status: SYNCHRONIZATION_STATUSES.UNAVAILABLE
                    };

                    this.unRegisterSyncPolling = crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, this.triggerFetch.bind(this));

                    // the first sync fetch is done manually
                    this.fetchSyncStatus();
                }
            }.bind(this));
        };

        this.$onDestroy = function() {
            this.unRegisterSyncPolling();
        };

    })

    /**
     * @ngdoc directive
     * @name pageListSyncIconModule.directive:pageListSyncIcon
     * @restrict E
     * @element sync-icon
     *
     * @description
     * The Page Synchronization Icon component is used to display the icon that describes the synchronization status of a page.
     *
     * @param {string} pageId The identifier of the page for which the synchronzation status must be displayed.
     * 
     */
    .component('pageListSyncIcon', {
        templateUrl: 'pageListSyncIconTemplate.html',
        controller: 'pageListSyncIconController',
        controllerAs: '$ctrl',
        bindings: {
            pageId: '<',
            uriContext: '<'
        }
    });
