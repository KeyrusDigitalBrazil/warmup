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

    var OVERLAY_SELECTOR = "#smarteditoverlay";

    var perspectiveSelectorObject = {};

    perspectiveSelectorObject.constants = {

        DEFAULT_PERSPECTIVES: {
            ALL: 'se.perspective.all.name',
            NONE: 'se.perspective.none.name'
        }

    };

    perspectiveSelectorObject.elements = {

        getTooltipIcon: function() {
            return element(by.id('perspectiveTooltip'));
        },

        getPerspectiveDropdownToggle: function() {
            return element(by.css('.ySEPerspectiveSelector'));
        },

        getPerspectiveDropdownMenu: function() {
            return element(by.css('perspective-selector ul.dropdown-menu'));
        },

        getPerspectiveDropdownOption: function(perspectiveName) {
            return element(by.cssContainingText('.ySEPerspectiveSelector ul li ', perspectiveName));
        },

        getActivePerspectiveName: function() {
            return browser.switchToParent().then(function() {
                return element(by.css('.ySEPerspectiveSelector > a')).getText();
            });
        },

        deprecated_getElementInOverlay: function(componentID, componentType) {
            var selector =
                '#smarteditoverlay .smartEditComponentX[data-smartedit-component-id="' +
                componentID + '"]';

            if (componentType) {
                selector += '[data-smartedit-component-type="' + componentType + '"]';
            }

            return element(by.css(selector));
        }

    };

    perspectiveSelectorObject.actions = {

        openAndBeReady: function() {
            browser.get('test/e2e/perspectiveService/perspectiveTest.html');

            browser.waitForContainerToBeReady();
            browser.switchToIFrame();
        },

        refreshAndWaitForAngularEnabled: function() {
            browser.get('test/e2e/perspectiveService/perspectiveTest.html').then(function() {
                return browser.waitForAngularEnabled(false);
            });
        },

        openPerspectiveSelectorDropdown: function() {
            return browser.switchToParent().then(function() {
                return browser.click(perspectiveSelectorObject.elements.getPerspectiveDropdownToggle());
            });
        },

        selectPerspective: function(perspectiveName) {
            return browser.switchToParent().then(function() {
                return perspectiveSelectorObject.elements.getActivePerspectiveName().then(function(perspectiveSelected) {
                    if (perspectiveSelected.toUpperCase() !== perspectiveName.toUpperCase()) {
                        return perspectiveSelectorObject.actions.openPerspectiveSelectorDropdown().then(function() {
                            return browser.click(
                                perspectiveSelectorObject.elements.getPerspectiveDropdownOption(perspectiveName),
                                "perspective " + perspectiveName + " is not clickable").then(function() {
                                return browser.waitForContainerToBeReady().then(function() {
                                    return browser.switchToIFrame().then(function() {
                                        return perspectiveName === perspectiveSelectorObject.constants.DEFAULT_PERSPECTIVES.NONE ? true : browser.waitForVisibility(OVERLAY_SELECTOR);
                                    });
                                });
                            });
                        });
                    } else {
                        browser.waitForWholeAppToBeReady();
                        return browser.switchToIFrame().then(function() {
                            return perspectiveName === perspectiveSelectorObject.constants.DEFAULT_PERSPECTIVES.NONE ? true : browser.waitForVisibility(OVERLAY_SELECTOR);
                        }.bind(this));
                    }
                });
            }.bind(this)).then(function() {
                return browser.switchToParent();
            });
        }

    };

    perspectiveSelectorObject.assertions = {

        assertPerspectiveActive: function(perspectiveName) {
            expect(perspectiveSelectorObject.elements.getActivePerspectiveName())
                .toBe(perspectiveName.toUpperCase());
        },

        assertPerspectiveSelectorDropdownDisplayed: function(isDisplayed) {
            expect(perspectiveSelectorObject.elements.getPerspectiveDropdownMenu().isDisplayed())
                .toBe(isDisplayed);
        },

        assertSmarteditOverlayIsPresent: function() {
            expect(browser.waitToBeDisplayed(by.css(OVERLAY_SELECTOR))).toBe(true);
        },

        assertSmarteditOverlayIsAbsent: function() {
            expect(browser.isAbsent(by.id("perspectiveTooltip"))).toBe(true);
        }

    };

    return perspectiveSelectorObject;

})();
