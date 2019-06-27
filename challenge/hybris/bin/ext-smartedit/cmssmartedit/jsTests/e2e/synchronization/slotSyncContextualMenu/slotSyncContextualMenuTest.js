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
describe('slotSyncContextualMenu - ', function() {

    var storefront = e2e.componentObjects.storefront;
    var synchronizationPanel = e2e.componentObjects.synchronizationPanel;
    var slotContextualMenu = e2e.componentObjects.slotContextualMenu;
    var syncMenu = e2e.componentObjects.syncMenu;

    var TOP_HEADER_SLOT_ID = storefront.constants.TOP_HEADER_SLOT_ID;
    var STATIC_SLOT_ID = storefront.constants.STATIC_SLOT_ID;
    var BOTTOM_HEADER_SLOT_ID = storefront.constants.BOTTOM_HEADER_SLOT_ID;
    var FOOTER_SLOT_ID = storefront.constants.FOOTER_SLOT_ID;
    var OTHER_SLOT_ID = storefront.constants.OTHER_SLOT_ID;

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    describe('slotSyncContextualMenu with sync permissions', function() {
        beforeEach(function(done) {
            syncMenu.actions.prepareApp(done, {
                "canSynchronize": true,
                "targetCatalogVersion": "Online"
            });
        });
        it('GIVEN on advanced edit mode WHEN we select sync menu icon for topHeaderSlot then it should be out of sync', function() {
            storefront.actions.moveToComponent(TOP_HEADER_SLOT_ID).then(function() {
                expect(slotContextualMenu.elements.syncButtonStatusBySlotId(TOP_HEADER_SLOT_ID).isPresent()).toBe(true);
            });
        });

        it('GIVEN on advanced edit mode WHEN we select sync menu icon should show a warning if the slot is not in sync', function() {

            storefront.actions.moveToComponent(TOP_HEADER_SLOT_ID).then(function() {
                expect(slotContextualMenu.elements.syncButtonStatusBySlotId(TOP_HEADER_SLOT_ID).isPresent()).toBe(true); // out of sync

                storefront.actions.moveToComponent(BOTTOM_HEADER_SLOT_ID).then(function() {
                    expect(slotContextualMenu.elements.syncButtonStatusBySlotId(BOTTOM_HEADER_SLOT_ID).isPresent()).toBe(true); // out of sync

                    storefront.actions.moveToComponent(FOOTER_SLOT_ID).then(function() {
                        browser.waitForAbsence(slotContextualMenu.elements.syncButtonBySlotId(FOOTER_SLOT_ID));

                        storefront.actions.moveToComponent(OTHER_SLOT_ID).then(function() {
                            browser.waitForAbsence(slotContextualMenu.elements.syncButtonStatusBySlotId(OTHER_SLOT_ID));
                        });
                    });
                });
            });

        });

        it('GIVEN I open sync panel of topHeaderSlot then open sync panel of page WHEN I sync topHeaderSlot from the page panel THEN the status of the slot panel must be automatically updated', function() {
            storefront.actions.moveToComponent(TOP_HEADER_SLOT_ID).then(function() {
                expect(slotContextualMenu.elements.syncButtonStatusBySlotId(TOP_HEADER_SLOT_ID).isPresent()).toBe(true); // out of sync
                browser.click(slotContextualMenu.elements.syncButtonBySlotId(TOP_HEADER_SLOT_ID)).then(function() {
                    syncMenu.actions.click();
                    synchronizationPanel.checkItem('All Slots and Page Information');
                    synchronizationPanel.clickSync().then(function() {
                        browser.waitForContainerToBeReady().then(function() {
                            syncMenu.actions.click();
                            synchronizationPanel.switchToIFrame().then(function() {
                                storefront.actions.moveToComponent(TOP_HEADER_SLOT_ID).then(function() {
                                    browser.waitForAbsence(slotContextualMenu.elements.syncButtonStatusBySlotId(TOP_HEADER_SLOT_ID));
                                });
                            });
                        });
                    });
                });
            });

        });

        it('GIVEN content slot that has not been synced with target catalog version WHEN I hover over it THEN the sync button is disabled and the message is shown', function() {
            // GIVEN
            storefront.actions.moveToComponent(STATIC_SLOT_ID);
            slotContextualMenu.assertions.syncButtonStatusIsPresentBySlotId(STATIC_SLOT_ID);

            // WHEN
            slotContextualMenu.actions.hoverOverSlotSyncButtonBySlotId(STATIC_SLOT_ID);

            // THEN
            slotContextualMenu.assertions.syncButtonIsDisabled(STATIC_SLOT_ID);
            slotContextualMenu.assertions.disabledSyncButtonShowsPopover();
        });

        it('GIVEN content slot which has sync permissions, THEN sync button should be displayed', function() {
            // GIVEN
            storefront.actions.moveToComponent(STATIC_SLOT_ID);

            // THEN
            slotContextualMenu.assertions.syncButtonIsPresent(STATIC_SLOT_ID);
        });
    });

    describe('slotSyncContextualMenu without sync permissions', function() {
        beforeEach(function(done) {
            syncMenu.actions.prepareApp(done, {});
        });

        it('GIVEN content slot which does not have sync permissions, THEN sync button should not be displayed', function() {
            // GIVEN
            storefront.actions.moveToComponent(STATIC_SLOT_ID);

            // THEN
            slotContextualMenu.assertions.syncButtonIsAbsent(STATIC_SLOT_ID);
        });
    });
});
