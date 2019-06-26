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
(function() {
    var synchronizationPanel = e2e.componentObjects.synchronizationPanel;

    var SLOTS = {
        HEADER: 'bottomHeaderSlot',
        FOOTER: 'footerSlot'
    };

    describe('Synchronization Panel', function() {

        beforeEach(function(done) {
            browser.bootstrap(__dirname);
            browser.waitForAngularEnabled(false);
            synchronizationPanel.assertPanelIsready().then(function() {
                done();
            });
        });

        it('WHEN panel is loaded THEN list of items are populated and sync button is enabled by default', function() {

            //header info

            expect(synchronizationPanel.getSyncPanelHeaderText()).toBe('Synchronize page information, associations and content slots, except shared content slots');
            expect(synchronizationPanel.getSyncPanelLastSyncTime()).toBe('11/10/16 1:10 PM');
            synchronizationPanel.hoverHelp().then(function() {
                expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('Shared slots should be synchronized from the slot on the Advanced Edit mode.');
            });

            //sync items
            expect(synchronizationPanel.getSyncItems()).toEqual(['All Slots and Page Information', 'topHeaderSlot', 'bottomHeaderSlot', 'footerSlot']);

            //sync button should be disabled when there are no checked item
            expect(synchronizationPanel.isSyncButtonEnabled()).toBe(false);
        });

        it('WHEN panel is loaded THEN the items have the right status and out of sync dependencies for out of sync items', function() {

            expect(synchronizationPanel.getSyncItemsStatus()).toEqual(['NOT_SYNC', 'NOT_SYNC', 'NOT_SYNC', 'NOT_SYNC']);

            expect(synchronizationPanel.getSyncItemDependenciesAvailable()).toEqual([true, true, true, true]);

            synchronizationPanel.actions.closeAllPopovers().then(function() {
                return synchronizationPanel.hoverStatus(0).then(function() {
                    expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('MetaData Restrictions Slot Component Navigation Customization');
                });
            });

            synchronizationPanel.actions.closeAllPopovers().then(function() {
                return synchronizationPanel.hoverStatus(1).then(function() {
                    expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('Component 1');
                });
            });

            synchronizationPanel.actions.closeAllPopovers().then(function() {
                return synchronizationPanel.hoverStatus(2).then(function() {
                    expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('Component 4');
                });
            });

            synchronizationPanel.actions.closeAllPopovers().then(function() {
                return synchronizationPanel.hoverStatus(3).then(function() {
                    expect(synchronizationPanel.getSyncItemDependenciesContent()).toBe('Restrictions');
                });
            });

        });

        it('WHEN panel is loaded and the first item its checked THEN all other dependencies are checked and disabled', function() {

            expect(synchronizationPanel.getItemsCheckedStatus()).toEqual([false, false, false, false]);

            expect(synchronizationPanel.getItemsCheckboxEnabled()).toEqual([true, true, true, true]);

            synchronizationPanel.checkItem('All Slots and Page Information').then(function() {
                expect(synchronizationPanel.getItemsCheckedStatus()).toEqual([true, true, true, true]);
                expect(synchronizationPanel.getItemsCheckboxEnabled()).toEqual([true, false, false, false]);
            }).then(function() {
                //uncheck the 1st item then it will leave all the check-boxes selected and enabled
                synchronizationPanel.checkItem('All Slots and Page Information').then(function() {
                    expect(synchronizationPanel.getItemsCheckedStatus()).toEqual([false, true, true, true]);
                    expect(synchronizationPanel.getItemsCheckboxEnabled()).toEqual([true, true, true, true]);
                });
            });

        });

        it('WHEN third and fourth items are checked and sync button is clicked' +
            ' THEN third one synchronizes successfully and fourth fails ',
            function() {

                synchronizationPanel.assertions.assertSlotNotSynced(SLOTS.HEADER);
                synchronizationPanel.assertions.assertSlotNotSynced(SLOTS.FOOTER);

                synchronizationPanel.actions.checkSyncCheckbox(SLOTS.HEADER);
                synchronizationPanel.actions.checkSyncCheckbox(SLOTS.FOOTER);

                synchronizationPanel.clickSync().then(function() {

                    browser.waitUntilNoModal(); // progress spinner

                    synchronizationPanel.assertions.assertSlotSynced(SLOTS.HEADER);
                    synchronizationPanel.assertions.assertSlotSyncFailed(SLOTS.FOOTER);

                    synchronizationPanel.assertions.assertSyncCheckboxDisabledForSlot(SLOTS.HEADER);
                    synchronizationPanel.assertions.assertSyncCheckboxCheckedForSlot(SLOTS.FOOTER);

                });

            });

    });
})();
