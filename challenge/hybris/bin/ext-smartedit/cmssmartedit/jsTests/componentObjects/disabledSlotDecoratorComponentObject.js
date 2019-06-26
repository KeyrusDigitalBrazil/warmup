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

    var componentObjects = {

        assertions: {
            disabledSlotContainsSpecificHTMLAttribute: function(slotId) {
                expect(componentObjects.elements.slotDisabledDecorator(slotId).getAttribute("data-popover-content"))
                    .toBe(componentObjects.constants.disabledSharedSlotPopoverContent);
            },
            disabledSlotWrapperIsNotPresent: function() {
                expect(element(by.css(componentObjects.constants.disabledSharedSlotWrapper())).isPresent())
                    .toBe(false, "Disabled slot wrapper should not be present");
            },
            assertDisabledDecoratorIsNotDisplayedOnSlot: function(slotId) {
                var message = "Expected " + slotId + " not to have a disabled decorator.";
                expect(browser.isAbsent(by.css(componentObjects.constants.disabledSharedSlotCSSPath(slotId)))).toBe(true, message);
            },
            assertDisabledDecoratorIsDisplayedOnSlot: function(slotId) {
                var message = "Expected " + slotId + " to have a disabled decorator.";
                expect(browser.isPresent(by.css(componentObjects.constants.disabledSharedSlotCSSPath(slotId)))).toBe(true, message);
            },
            assertExternalDisabledDecoratorIsDisplayedOnSlot: function(slotId) {
                var message = "Expected " + slotId + " to have an external slot disabled decorator.";
                expect(browser.isPresent(by.css(componentObjects.constants.disabledExternalSharedSlotCSSPath(slotId)))).toBe(true, message);
            }
        },

        constants: {
            disabledSharedSlotCSSPath: function(slotId) {
                return "[data-smartedit-component-id='" + slotId + "'] .disabled-shared-slot";
            },
            disabledExternalSharedSlotCSSPath: function(slotId) {
                return "[data-smartedit-component-id='" + slotId + "'] .external-shared-slot";
            },
            disabledSharedSlotPopoverContent: "Shared Slot: Edit in Advanced Mode",
            disabledSharedSlotWrapper: function() {
                return ".disabled-shared-slot-tooltip__wrapper";
            }
        },

        elements: {
            slotDisabledDecorator: function(slotId) {
                return browser.findElement(by.css(componentObjects.constants.disabledSharedSlotCSSPath(slotId)), true);
            },
            slotExternalDisabledDecorator: function(slotId) {
                return browser.findElement(by.css(componentObjects.constants.disabledExternalSharedSlotCSSPath(slotId)), true);
            }
        }

    };

    return componentObjects;

})();
