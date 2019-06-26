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
angular.module('pageSynchronizationPanelModule', ['functionsModule', 'synchronizationPanelModule', 'cmsSmarteditServicesModule', 'pageSynchronizationHeaderModule'])
    .constant("SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL", "se.cms.synchronization.page.select.all.slots")
    .constant('PAGE_SYNC_STATUS_READY', 'PAGE_SYNC_STATUS_READY')
    .controller('PageSynchronizationPanelController', function($attrs, PAGE_SYNC_STATUS_READY, SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL, isBlank, pageSynchronizationService, homepageService, $q, systemEventService) {

        this.pageSyncConditions = {
            canSyncHomepage: false,
            pageHasUnavailableDependencies: false,
            pageHasSyncStatus: false,
            pageHasNoDepOrNoSyncStatus: false
        };

        this.getSyncStatus = function() {
            return $q.all([
                homepageService.canSyncHomepage(this.cmsPage, this.uriContext),
                pageSynchronizationService.getSyncStatus(this.cmsPage.uuid, this.uriContext)
            ]).then(function(resolves) {
                this.pageSyncConditions.canSyncHomepage = resolves[0];
                this.syncStatus = resolves[1];

                this.syncStatus.selectAll = SYNCHRONIZATION_PAGE_SELECT_ALL_SLOTS_LABEL;
                return this.syncStatus;
            }.bind(this));
        }.bind(this);

        this.onSyncStatusReady = function($syncStatus) {
            this.pageSyncConditions.pageHasUnavailableDependencies = $syncStatus.unavailableDependencies.length > 0;
            this.pageSyncConditions.pageHasSyncStatus = !!($syncStatus.lastSyncStatus);
            this.pageSyncConditions.pageHasNoDepOrNoSyncStatus = this.pageSyncConditions.pageHasUnavailableDependencies || !this.pageSyncConditions.pageHasSyncStatus;

            if (this.pageSyncConditions.pageHasUnavailableDependencies) {
                this._disablePageSync();
            } else if (!this.pageSyncConditions.pageHasSyncStatus) {
                this._enablePageSync();
            } else {
                this._enableSlotsSync();
            }

            systemEventService.publish(PAGE_SYNC_STATUS_READY, this.pageSyncConditions);
        }.bind(this);

        this.performSync = function(array) {
            return pageSynchronizationService.performSync(array, this.uriContext);
        }.bind(this);

        this.$postLink = function() {
            this.showSyncButton = isBlank($attrs.syncItems);
        };

        this.getApi = function($api) {
            this.synchronizationPanelApi = $api;

            this.synchronizationPanelApi.disableItem = function(item) {
                return !this.pageSyncConditions.canSyncHomepage && item === this.syncStatus;
            }.bind(this);
        }.bind(this);

        // disbale page sync
        this._disablePageSync = function() {
            this.synchronizationPanelApi.displayItemList(false);
        };

        // enable page sync only
        this._enablePageSync = function() {
            this.synchronizationPanelApi.selectAll();
            this.synchronizationPanelApi.displayItemList(false);
        };

        // enable slot/page sync
        this._enableSlotsSync = function() {
            this.synchronizationPanelApi.displayItemList(true);
        };
    })
    .component('pageSynchronizationPanel', {
        templateUrl: 'pageSynchronizationPanelTemplate.html',
        controller: 'PageSynchronizationPanelController',
        bindings: {
            syncItems: '=?',
            uriContext: '<?',
            onSelectedItemsUpdate: '<?',
            cmsPage: '<?',
            showDetailedInfo: '<?'
        }
    });
