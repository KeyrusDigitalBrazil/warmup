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
module.exports = (function() {

    var co = {
        SYNC_PANEL_HEADER: 'page-synchronization-header .se-sync-panel-header__text span',
        SYNC_PANEL_LAST_SYNC: 'page-synchronization-header .se-sync-panel-header__timestamp',
        SYNC_PANEL_HEADER_HELPER: 'page-synchronization-header y-help ',
        SYNC_ITEMS_SELECTOR: 'synchronization-panel .se-sync-panel__sync-info__row',
        SYNC_ITEMS_CHECKBOX: 'synchronization-panel .se-sync-panel__sync-info__row div input',
        SYNC_ITEMS_CHECKBOX_SELECTOR: 'synchronization-panel .se-sync-panel__sync-info__row div label',
        ignoreSynchronization: function() {
            browser.waitForAngularEnabled(false);
        },
        syncButtonElement: function() {
            return element(by.cssContainingText('synchronization-panel button', 'Sync'));
        },
        setupTest: function() {
            this.ignoreSynchronization();
            browser.manage().window().setSize(1700, 1000);
        },
        getSyncPanelHeaderText: function() {
            return element(by.css(this.SYNC_PANEL_HEADER)).getText();
        },
        getSyncPanelLastSyncTime: function() {
            return element(by.css(this.SYNC_PANEL_LAST_SYNC)).getText();
        },
        getPopoverAnchor: function() {
            var selector = 'synchronization-panel .se-sync-panel__sync-info__row [data-y-popover]';
            return by.css(selector);
        },
        hoverStatus: function(index) {
            return browser.actions().mouseMove(element.all(this.getPopoverAnchor()).get(index)).perform();
        },

        hoverHelp: function() {
            return browser.actions().mouseMove(element(by.css(this.SYNC_PANEL_HEADER_HELPER))).perform();
        },
        getSyncItemDependenciesContent: function() {
            browser.waitUntil(function() {
                return element.all(by.css('.popover .popover-content')).then(function(popovers) {
                    return popovers.length === 1;
                });
            }, 'no popovers are available');

            return this.getPopoverBodyText();
        },
        getSyncItems: function() {
            return element.all(by.css(this.SYNC_ITEMS_SELECTOR + ' label')).map(function(element) {
                return element.getText();
            });
        },
        getSyncItemsStatus: function() {
            return element.all(by.css(this.SYNC_ITEMS_SELECTOR + ' span.hyicon__se-sync-panel__sync-info')).map(function(element) {
                return element.getAttribute('data-status');
            });
        },
        getSyncItemDependenciesAvailable: function() {
            return this.getSyncItemsStatus().then(function(allStatus) {
                return allStatus.map(function(status) {
                    return status !== 'IN_SYNC';
                });
            });
        },
        checkItem: function(item) {
            return browser.click(by.cssContainingText(this.SYNC_ITEMS_CHECKBOX_SELECTOR, item));
        },
        getItemsCheckedStatus: function() {
            return element.all(by.css(this.SYNC_ITEMS_CHECKBOX)).map(function(element) {
                return element.isSelected();
            });
        },
        getItemsCheckboxEnabled: function() {
            return element.all(by.css(this.SYNC_ITEMS_CHECKBOX)).map(function(element) {
                return element.isEnabled();
            });
        },
        isSyncButtonEnabled: function() {
            return this.syncButtonElement().isEnabled();
        },
        assertPanelIsready: function() {
            return browser.waitForPresence(by.cssContainingText('synchronization-panel button', 'Sync'), "expected sync panel to show");
        },
        clickSync: function() {
            return browser.click(by.cssContainingText('synchronization-panel button', 'Sync')).then(function() {
                return this.ignoreSynchronization();
            }.bind(this));
        },
        switchToIFrame: function() {
            return browser.waitForContainerToBeReady().then(function() {
                return browser.switchToIFrame(true).then(function() {
                    return this.ignoreSynchronization();
                }.bind(this));
            }.bind(this));
        },
        getPopoverBodyText: function() {
            return co.utils.getPopoverElement().getText().then(function(text) {
                return text.replace(/\n|\r/g, " ");
            });
        },
        getItemList: function() {
            return element(by.css('.se-sync-panel__sync-info'));
        }
    };

    var SYNC_STATUS = {
        IN_SYNC: 'IN_SYNC',
        SYNC_FAILED: 'SYNC_FAILED',
        NOT_SYNC: 'NOT_SYNC'
    };

    co.elements = {
        syncStatusIconBySlotName: function(slotName, status) {
            // People say this is the most obvious xpath ever created
            var xp = "//*[contains(@class, 'se-sync-panel__sync-info__row')]//*[contains(text(), '" +
                slotName +
                "')]//ancestor::*[contains(@class, 'se-sync-panel__sync-info__row')]//*[@data-status='" +
                status +
                "']";
            return element(by.xpath(xp));
        },
        syncCheckboxBySlotName: function(slotName) {
            return element(by.xpath("//*[*[contains(text(), '" + slotName + "')]]//input"));
        },
        syncCheckboxLabelBySlotName: function(slotName) {
            return element(by.cssContainingText("synchronization-panel .se-sync-panel__sync-info__row div label", slotName));
        },
        firstPpover: function() {
            return element.all(by.css('.popover .popover-content')).get(0);
        }
    };

    co.assertions = {
        assertSlotNotSynced: function(slotName) {
            return browser.waitForPresence(co.elements.syncStatusIconBySlotName(slotName, SYNC_STATUS.NOT_SYNC));
        },
        assertSlotSynced: function(slotName) {
            return browser.waitForPresence(co.elements.syncStatusIconBySlotName(slotName, SYNC_STATUS.IN_SYNC));
        },
        assertSlotSyncFailed: function(slotName) {
            return browser.waitForPresence(co.elements.syncStatusIconBySlotName(slotName, SYNC_STATUS.SYNC_FAILED));
        },
        assertSyncCheckboxDisabledForSlot: function(slotName) {
            return expect(co.elements.syncCheckboxBySlotName(slotName).isEnabled()).toBe(false);
        },
        assertSyncCheckboxCheckedForSlot: function(slotName) {
            return expect(co.elements.syncCheckboxBySlotName(slotName).isSelected()).toBe(true);
        },
        assertItemListIsHidden: function() {
            return expect(browser.isAbsent(co.getItemList())).toBe(true);
        },
        assertItemListIsVisible: function() {
            return expect(browser.isPresent(co.getItemList())).toBe(true);
        },
        assertSyncButtonIsEnabled: function() {
            return expect(co.isSyncButtonEnabled()).toBe(true);
        },
        assertSyncButtonIsDisabled: function() {
            return expect(co.isSyncButtonEnabled()).toBe(false);
        }
    };


    co.actions = {
        checkSyncCheckbox: function(slotName) {
            var checkbox = co.elements.syncCheckboxBySlotName(slotName);
            return checkbox.isSelected().then(function(selected) {
                if (!selected) {
                    return browser.click(co.elements.syncCheckboxLabelBySlotName(slotName));
                }
                return true;
            });
        },
        closeAllPopovers: function() {
            // arbitrarily move to the synch button so we can move the mouse somewhere (coordinates don't seem to be working)
            return browser.actions().mouseMove(co.syncButtonElement()).click().perform().then(function() {
                return browser.waitForAbsence(co.elements.firstPpover());
            });
        }

    };

    co.utils = {
        getPopoverElement: function() {
            return browser.findElement(by.css('.popover .popover-content'));
        }
    };


    return co;

}());
