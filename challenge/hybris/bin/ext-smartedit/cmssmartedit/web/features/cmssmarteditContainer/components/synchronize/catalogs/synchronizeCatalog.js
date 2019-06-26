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
 * @name synchronizeCatalogModule
 * @description
 *
 * The synchronization module contains the service and the directives necessary 
 * to perform catalog synchronization.
 *
 * The {@link synchronizationServiceModule.service:synchronizationService synchronizationService} 
 * calls backend API in order to get synchronization status or trigger a catalog synchronaization.
 *
 * The {@link synchronizationServiceModule.directive:synchronizeCatalog synchronizeCatalog} directive is used to display
 * the synchronization area in the landing page for each store.
 *
 */
angular.module('synchronizeCatalogModule', [
        'confirmationModalServiceModule',
        'synchronizationServiceModule',
        'l10nModule',
        'hasOperationPermissionModule',
        'seConstantsModule'
    ])

    .controller('synchronizeCatalogController', function($scope, synchronizationService, $q, confirmationModalService, systemEventService, l10nFilter, EVENT_CONTENT_CATALOG_UPDATE) {

        // Constants
        var JOB_STATUS = {
            RUNNING: 'RUNNING',
            ERROR: 'ERROR',
            FAILURE: 'FAILURE',
            FINISHED: 'FINISHED',
            UNKNOWN: 'UNKNOWN'
        };

        this.$onInit = function() {
            this.syncJobStatus = {
                syncStartTime: '',
                syncEndTime: '',
                status: '',
                source: '',
                target: ''
            };
            this.targetCatalogVersion = this.activeCatalogVersion.version;
            this.sourceCatalogVersion = (!this.catalogVersion.active) ? this.catalogVersion.version : null;
            this.syncCatalogPermission = [{
                names: ['se.sync.catalog'],
                context: {
                    catalogId: this.catalog.catalogId,
                    catalogVersion: this.sourceCatalogVersion,
                    targetCatalogVersion: this.targetCatalogVersion
                }
            }];

            // Catalog works as a DTO. Thus it needs the target and source catalog versions. 
            this.catalogDto = {
                catalogId: this.catalog.catalogId,
                targetCatalogVersion: this.targetCatalogVersion,
                sourceCatalogVersion: this.sourceCatalogVersion
            };

            // on init, start auto updating synchronization data
            synchronizationService.startAutoGetSyncData(this.catalogDto, this._updateSyncStatusData.bind(this));

            // call the update for the first time. 
            this._invokeGetSyncData();
        };

        this.$onDestroy = function() {
            synchronizationService.stopAutoGetSyncData(this.catalogDto);
        };

        // Catalog Syncing
        this.syncCatalog = function() {
            var catalogName = l10nFilter(this.catalog.name);
            confirmationModalService.confirm({
                template: '<div id="confirmationModalDescription">{{ "se.sync.confirm.msg" | translate: { catalogName: modalController.catalogName } }}</div>',
                title: 'se.sync.confirmation.title',
                scope: {
                    catalogName: catalogName
                }
            }).then(function() {
                synchronizationService.updateCatalogSync(this.catalogDto).then(function(response) {
                    this._updateSyncStatusData(response);
                    systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE, response);
                }.bind(this));
            }.bind(this));
        };

        // Auto Get
        this._invokeGetSyncData = function() {
            synchronizationService
                .getCatalogSyncStatus(this.catalogDto)
                .then(function(response) {
                    this._updateSyncStatusData(response);
                }.bind(this));
        };

        this._updateSyncStatusData = function(response) {
            this.syncJobStatus = {
                syncStartTime: response.creationDate,
                syncEndTime: response.endDate,
                status: response.syncStatus,
                source: (response.sourceCatalogVersion) ? response.sourceCatalogVersion : '',
                target: (response.targetCatalogVersion) ? response.targetCatalogVersion : ''
            };
        };

        // Status 
        this.isSyncJobFinished = function() {
            return this.syncJobStatus.status === JOB_STATUS.FINISHED;
        };

        this.isSyncJobInProgress = function() {
            return (this.syncJobStatus.status === "RUNNING" || this.syncJobStatus.status === "UNKNOWN");
        };

        this.isSyncJobFailed = function() {
            return this.syncJobStatus.status === JOB_STATUS.ERROR || this.syncJobStatus.status === JOB_STATUS.FAILURE;
        };

        this.isButtonEnabled = function() {
            return !this.isSyncJobInProgress();
        };

        this.getSyncFromLabels = function() {
            var returnValue = {
                sourceCatalogVersion: this.syncJobStatus.source
            };
            return returnValue;
        };

    })

    /**
     * @ngdoc directive
     * @name synchronizationServiceModule.directive:synchronizeCatalog
     * @restrict E
     * @element synchronize-catalog
     *
     * @description
     * 
     * The synchronize catalog directive is used to display catalog synchronization information in the landing page. 
     * 
     * For the active-catalog version, this directive displays the information of the last sync job affecting that catalog version.
     * 
     * For non-active catalog versions it provides information about the last sync job from that catalog version towards the catalog's active version. 
     * Also, it displays a button to trigger a new synchronization job, going from the current catalog version to the catalog's active version. 
     *
     * @param {Object} catalog An object that contains the catalog details. 
     * @param {Object} catalogVersion An object representing the current catalog version. 
     * @param {Object} activeCatalogVersion An object representing the active catalog version of the provided catalog. 
     * 
     */
    .directive('synchronizeCatalog', function() {
        return {
            templateUrl: 'synchronizeCatalogTemplate.html',
            restrict: 'E',
            controller: 'synchronizeCatalogController',
            controllerAs: 'ctrl',
            bindToController: {
                catalog: '<',
                catalogVersion: '<',
                activeCatalogVersion: '<'
            }
        };
    });
