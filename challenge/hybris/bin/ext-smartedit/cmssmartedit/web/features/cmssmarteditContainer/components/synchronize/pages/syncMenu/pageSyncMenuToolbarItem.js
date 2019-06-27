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
angular.module('pageSyncMenuToolbarItemModule', ['pageSynchronizationPanelModule', 'pageSynchronizationPanelModule'])
    .controller('PageSyncMenuToolbarItemController', function(
        crossFrameEventService,
        catalogService,
        assetsService,
        systemEventService,
        iframeClickDetectionService,
        pageInfoService,
        pageSynchronizationService,
        SYNCHRONIZATION_STATUSES,
        SYNCHRONIZATION_POLLING,
        $translate,
        $q,
        PAGE_SYNC_STATUS_READY,
        pageService
    ) {
        this.isReady = false;

        this.fetchSyncStatus = function() {
            pageInfoService.getPageUUID().then(function(pageUUID) {
                pageSynchronizationService.getSyncStatus(pageUUID).then(function(syncStatus) {
                    this.isNotInSync = syncStatus.status !== SYNCHRONIZATION_STATUSES.IN_SYNC;
                }.bind(this));
            }.bind(this));
        }.bind(this);

        this.$onInit = function() {
            this.isContentCatalogVersionNonActive = false;

            $q.all([
                pageService.getCurrentPageInfo(),
                catalogService.retrieveUriContext(),
                catalogService.isContentCatalogVersionNonActive()
            ]).then(function(resolves) {
                this.cmsPage = resolves[0];
                this.uriContext = resolves[1];
                var isActive = resolves[2];

                if (!isActive) {
                    return;
                }

                this.icons = {
                    open: assetsService.getAssetsRoot() + "/images/icon_info_white.png",
                    closed: assetsService.getAssetsRoot() + "/images/icon_info_blue.png"
                };

                this.menuIcon = this.icons.closed;
                this.isNotInSync = false;
                this.isContentCatalogVersionNonActive = true;

                this.unRegisterSyncPolling = crossFrameEventService.subscribe(SYNCHRONIZATION_POLLING.FAST_FETCH, this.fetchSyncStatus);
                this.fetchSyncStatus();

                this.isReady = true;
            }.bind(this));

            this.unRegisterSyncPageConditions = systemEventService.subscribe(PAGE_SYNC_STATUS_READY, function(event, data) {
                this.syncPageConditions = data;

                this.helpTemplate = $translate.instant('se.cms.synchronization.page.header') +
                    (!this.syncPageConditions.pageHasNoDepOrNoSyncStatus ? ' ' + $translate.instant('se.cms.synchronization.page.header.help') : '');
            }.bind(this));

        }.bind(this);

        this.$onDestroy = function() {
            if (this.unRegisterSyncPolling) {
                this.unRegisterSyncPolling();
            }
            if (this.unRegisterSyncPageConditions) {
                this.unRegisterSyncPageConditions();
            }
        }.bind(this);

    })
    .component('pageSyncMenuToolbarItem', {
        templateUrl: 'pageSyncMenuToolbarItemTemplate.html',
        controller: 'PageSyncMenuToolbarItemController',
        controllerAs: '$ctrl',
        bindings: {
            toolbarItem: '<item'
        }
    });
