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
module.exports = function() {

    var componentObjects = {

        actions: {
            clickOnDeletePageAnchor: function() {
                return browser.click(componentObjects.elements.getDeletePageItemAnchor());
            },
            confirmPageSoftDeletion: function() {
                return browser.click(componentObjects.elements.getPageSoftDeletionConfirmationButton());
            },
            hoverDeletePageItemAnchor: function() {
                return browser.hoverElement(componentObjects.elements.getDeletePageItemPopoverAnchor());
            },
            openMoreActionsToolbarItem: function() {
                return browser.click(componentObjects.elements.getMoreActionsItemButton());
            }
        },

        assertions: {
            deletePageItemIsActive: function() {
                expect(componentObjects.elements.getDeletePageItemAnchor()).not.toContainClass('se-dropdown-item__disabled');
            },
            deletePageItemIsInactive: function() {
                expect(componentObjects.elements.getDeletePageItemAnchor()).toContainClass('se-dropdown-item__disabled');
            },
            deletePageItemPopoverAnchorIsNotPresent: function() {
                expect(componentObjects.elements.getDeletePageItemPopoverAnchor()).toBeAbsent();
            }
        },

        constants: {},

        elements: {
            getDeletePageItemAnchor: function() {
                // there can only be one delete-page-item displayed
                browser.waitForAngularEnabled(true);
                return element.all(by.css('delete-page-item a')).filter(function(element) {
                    browser.waitForPresence(element);
                    return element.isDisplayed().then(function(isElementDisplayed) {
                        return isElementDisplayed;
                    });
                }).first();
            },
            getDeletePageItemPopoverAnchor: function() {
                return componentObjects.elements.getDeletePageItemAnchor().element(by.css('[data-y-popover]'));
            },
            getMoreActionsItemButton: function() {
                return browser.findElement(by.css('div[data-item-key="moreActionsMenu"] button'));
            },
            getPageSoftDeletionConfirmationButton: function() {
                return browser.findElement(by.css("#confirmOk"));
            }
        }

    };

    return componentObjects;

}();
