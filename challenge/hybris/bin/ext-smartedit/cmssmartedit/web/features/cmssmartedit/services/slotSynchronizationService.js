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
angular.module('slotSynchronizationServiceModule', [])
    .constant("SYNCHRONIZATION_SLOTS_SELECT_ALL_COMPONENTS_LABEL", "se.cms.synchronization.slots.select.all.components")
    .service('slotSynchronizationService', function(SYNCHRONIZATION_SLOTS_SELECT_ALL_COMPONENTS_LABEL, syncPollingService) {

        this.getSyncStatus = function(pageUUID, slotId) {
            return syncPollingService.getSyncStatus(pageUUID).then(function(syncStatus) {
                var slotSyncStatus = (syncStatus.selectedDependencies || []).concat(syncStatus.sharedDependencies || []).find(function(slot) {
                    return slot.name === slotId;
                }) || {};
                slotSyncStatus.selectAll = SYNCHRONIZATION_SLOTS_SELECT_ALL_COMPONENTS_LABEL;
                return slotSyncStatus;
            });
        };

        this.performSync = function(array, uriContext) {
            return syncPollingService.performSync(array, uriContext);
        };
    });
