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
/* jshint unused:false, undef:false */
describe("slot synchronization service - ", function() {

    var harness, slotSynchronizationService, mockSyncPollingService;
    var uriContext = "uriContext";

    var synchronizationMockData = unit.mockData.synchronization;
    var pageSyncStatus = new synchronizationMockData().PAGE_ID1_SYNC_STATUS;
    var slot1_syncStatus = new synchronizationMockData().SLOT1_SYNC_STATUS;
    var slot3_syncStatus = new synchronizationMockData().SLOT3_SYNC_STATUS;

    beforeEach(function() {

        harness = AngularUnitTestHelper.prepareModule('slotSynchronizationServiceModule')
            .mock('syncPollingService', 'registerSyncPollingEvents')
            .mock('syncPollingService', 'getSyncStatus')
            .mock('syncPollingService', 'performSync')
            .service('slotSynchronizationService');

        $q = harness.injected.$q;

        slotSynchronizationService = harness.service;
        mockSyncPollingService = harness.mocks.syncPollingService;
    });

    it('GIVEN sync polling service returns a successful promise ' +
        'WHEN getSyncStatus is called ' +
        'THEN will fetch the status for the page in which the slot is present and then will retrieve the status of the slot if present in selectedDependencies',
        function() {

            //GIVEN
            mockSyncPollingService.getSyncStatus.and.returnValue($q.when(pageSyncStatus));

            //WHEN
            var promise = slotSynchronizationService.getSyncStatus('pageId1', 'slot1');

            //THEN
            expect(promise).toBeResolvedWithData(window.smarteditJQuery.extend(slot1_syncStatus, {
                selectAll: 'se.cms.synchronization.slots.select.all.components'
            }));

        });

    it('GIVEN sync polling service returns a successful promise ' +
        'WHEN getSyncStatus is called ' +
        'THEN will fetch the status for the page in which the slot is present and then will retrieve the status of the slot if present in sharedDependencies',
        function() {

            //GIVEN
            mockSyncPollingService.getSyncStatus.and.returnValue($q.when(pageSyncStatus));

            //WHEN
            var promise = slotSynchronizationService.getSyncStatus('pageId1', 'slot3');

            //THEN
            expect(promise).toBeResolvedWithData(window.smarteditJQuery.extend(slot3_syncStatus, {
                selectAll: 'se.cms.synchronization.slots.select.all.components'
            }));

        });

    it('GIVEN sync polling service returns a rejected promise ' +
        'WHEN getSyncStatus is called ' +
        'THEN the result is rejected',
        function() {

            //GIVEN
            mockSyncPollingService.getSyncStatus.and.returnValue($q.reject());

            //WHEN
            var promise = slotSynchronizationService.getSyncStatus('pageId1', 'slot3');

            //THEN
            expect(promise).toBeRejected();

        });

    it('GIVEN getPageSynchronizationPostRestService returns a successful promise ' +
        'WHEN performSync is called ' +
        'THEN the result is resolved',
        function() {

            //GIVEN
            mockSyncPollingService.performSync.and.returnValue($q.when({}));

            //WHEN
            slotSynchronizationService.performSync(pageSyncStatus, uriContext);

            //THEN
            expect(mockSyncPollingService.performSync).toHaveBeenCalledWith(pageSyncStatus, uriContext);

        });
});
