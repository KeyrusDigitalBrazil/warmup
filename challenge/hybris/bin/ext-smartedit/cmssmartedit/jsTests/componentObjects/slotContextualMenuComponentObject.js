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
var storefront;
if (typeof require !== 'undefined') {
    storefront = require('./storefrontComponentObject');
}

module.exports = (function() {

    var componentObject = {};

    componentObject.constants = {
        HIDDEN_COMPONENT_1: 'hiddenComponent1',
        HIDDEN_COMPONENT_2: 'hiddenComponent2'
    };

    componentObject.elements = {
        // -- HIDDEN COMPONENTS (VISIBILITY BUTTON) --
        visibilityButtonBySlotId: function(slotId) {
            return element(by.id('slot-visibility-button-' + slotId));
        },
        visibilityDropdownBySlotId: function(slotId) {
            return element(by.id('slot-visibility-list-' + slotId));
        },
        visibilityListBySlotId: function(slotId) {
            return this.visibilityDropdownBySlotId(slotId).all(by.css('li'));
        },
        hiddenComponentInSlot: function(slotId, componentId) {
            return this.visibilityDropdownBySlotId(slotId)
                .element(by.css('slot-visibility-component[data-component-id="' + componentId + '"]'));
        },
        hiddenComponentMenuButton: function(slotId, componentId) {
            return this.hiddenComponentInSlot(slotId, componentId)
                .element(by.css('hidden-component-menu .se-hidden-component-menu__popup-anchor'));
        },
        hiddenComponentMenu: function() {
            return element(by.css('.se-hidden-component-menu'));
        },
        hiddenComponentMenuItem: function(menuItemLabel) {
            return this.hiddenComponentMenu().element(by.cssContainingText('.se-hidden-component-menu__item-link', menuItemLabel));
        },

        // -- SHARED SLOT BUTTON --
        sharedSlotButtonBySlotId: function(slotId) {
            return element(by.id('sharedSlotButton-' + slotId));
        },
        sharedSlotMessageButtonCloneLinksBySlotId: function() {
            return element(by.css(".shared-slot__link"));
        },
        sharedSlotButtonMessage: function() {
            browser.waitUntil(function() {
                return element.all(by.css('.shared-slot__dropdown')).then(function(popovers) {
                    return popovers.length === 1;
                });
            }, 'no popovers are available');
            return browser.findElement(by.css(".shared-slot__description")).getText().then(function(text) {
                return text;
            });
        },
        sharedSlotButtonMenu: function() {
            return browser.findElement(by.css('.shared-slot-button-template__menu'));
        },

        // -- UNSHARED SLOT BUTTON --
        unsharedSlotButtonBySlotId: function(slotId) {
            return element(by.id('slot-unshared-button-' + slotId));
        },
        unsharedSlotButtonMessage: function() {
            browser.waitUntil(function() {
                return element.all(by.css('.shared-slot__link-description__unshared')).then(function(popovers) {
                    return popovers.length === 1;
                });
            }, 'no popovers are available');

            return browser.findElement(by.css('.shared-slot__link-description__unshared')).getAttribute("innerHTML").then(function(innerHTML) {
                return innerHTML.replace(" class=\"ng-scope\"", "");
            });
        },

        // -- SYNC BUTTON --
        syncButtonBySlotId: function(slotId) {
            return element(by.id('slot-sync-button-' + slotId));
        },
        syncyDropdownBySlotId: function(slotId) {
            return element(by.id('slot-sync-list-' + slotId));
        },
        syncButtonStatusBySlotId: function(slotId) {
            browser.waitForPresence(by.css("div[data-smartedit-component-id='" + slotId + "'] slot-sync-button"), 'cannot find slot sync icon');
            return element(by.css("#slot-sync-button-" + slotId + " > span:nth-child(2)"));
        },

        // -- SYNC BUTTON POPOVER --
        popover: function() {
            return element(by.css('.popover-content'));
        }
    };

    componentObject.actions = {
        clickOnSharedSlotBySlotId: function(slotId) {
            return browser.click(componentObject.elements.sharedSlotButtonBySlotId(slotId));
        },
        hoverOverSlotSyncButtonBySlotId: function(slotId) {
            return browser.actions()
                .mouseMove(componentObject.elements.syncButtonBySlotId(slotId))
                .perform();
        },
        openSharedSlotButtonByButtonId: function(slotId) {
            return browser.click(componentObject.elements.sharedSlotButtonBySlotId(slotId));
        },
        // -- HIDDEN COMPONENTS (VISIBILITY BUTTON) --
        clickOnSlotVisibilityButton: function(slotId) {
            return browser.click(componentObject.elements.visibilityButtonBySlotId(slotId));
        },
        openHiddenComponentsList: function(slotId) {
            return storefront.actions.moveToComponent(slotId).then(function() {
                return this.clickOnSlotVisibilityButton(slotId);
            }.bind(this));
        },
        openHiddenComponentMenu: function(slotId, componentId) {
            return browser.click(componentObject.elements.hiddenComponentMenuButton(slotId, componentId));
        },
        openFirstHiddenComponentMenu: function(slotId) {
            return this.openHiddenComponentMenu(slotId, componentObject.constants.HIDDEN_COMPONENT_1);
        },
        clickHiddenComponentMenuItemByLabel: function(menuItemLabel) {
            componentObject.assertions.menuItemIsDisplayedByLabel(menuItemLabel);
            return browser.click(componentObject.elements.hiddenComponentMenuItem(menuItemLabel));
        },
        clickOnSlotUnsharedButtonBySlotId: function(slotId) {
            return browser.click(componentObject.elements.unsharedSlotButtonBySlotId(slotId));
        },
        hoverSlot: function(slotId) {
            // There was an issue with some tests being flaky in smaller screens; Protractor
            // complained that it could not click the element in that position. Apparently, 
            // this was caused by the slot being outside the viewport. To avoid this issue, 
            // instead of just moving the mouse, this method scrolls the component into view 
            // and then just moves the mouse a little to trigger the slot contextual menu. 
            return storefront.actions.scrollComponentIntoView(slotId).then(function() {
                return browser.actions().mouseMove({
                    x: 1,
                    y: 1
                }).perform();
            });
        }
    };

    componentObject.assertions = {
        menuItemIsDisplayedByLabel: function(menuItemLabel) {
            expect(componentObject.elements.hiddenComponentMenuItem(menuItemLabel), 'Expected menu item with label ' + menuItemLabel + " to be displayed");
        },
        disabledSyncButtonShowsPopover: function() {
            expect(componentObject.elements.popover().getText()).toBe("This slot can be synced from the page level");
        },
        syncButtonIsDisabled: function(slotId) {
            // In slotSyncButtonTemplate.html we actually don't disable the button but move the hover component over it.
            // That's why we can not check the disabled attribute.
            expect(componentObject.elements.syncButtonBySlotId(slotId).getAttribute('class')).toContain('se-slot-ctx-menu__btn--disabled');
        },
        syncButtonIsAbsent: function(slotId) {
            expect(browser.isAbsent(componentObject.elements.syncButtonBySlotId(slotId))).toBe(true);
        },
        syncButtonIsPresent: function(slotId) {
            expect(browser.isPresent(componentObject.elements.syncButtonBySlotId(slotId))).toBe(true);
        },
        syncButtonStatusIsPresentBySlotId: function(slotId) {
            expect(componentObject.elements.syncButtonStatusBySlotId(slotId).isPresent()).toBe(true);
        },
        assertThatSlotShareButtonIsNotPresent: function(slotId) {
            componentObject.actions.hoverSlot(slotId);
            var button = componentObject.elements.sharedSlotButtonBySlotId(slotId);

            browser.waitForAbsence(button, 'Expected shared slot button not to be visible');
        },
        assertThatSlotUnsharedButtonIsPresent: function(slotId) {
            componentObject.actions.hoverSlot(slotId);
            var button = componentObject.elements.unsharedSlotButtonBySlotId(slotId);

            browser.waitForPresence(button, 'Expected unshared slot button to be present');
            expect(button.isDisplayed()).toBe(true, 'Expected unshared slot button to be displayed');
        },
        assertThatSlotUnsharedButtonIsNotPresent: function(slotId) {
            storefront.actions.moveToComponent(slotId);
            var button = componentObject.elements.unsharedSlotButtonBySlotId(slotId);
            expect(button).toBeAbsent();
        }
    };

    return componentObject;
})();
