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

    var componentObject = {
        PREVIEW_PERSPECTIVE: "PREVIEW",
        VERSIONING_PERSPECTIVE: "VERSIONING",
        ADVANCED_CMS_PERSPECTIVE: "Advanced CMS",
        BASIC_CMS_PERSPECTIVE: "Basic CMS",
        OVERLAY_SELECTOR: "#smarteditoverlay",

        select: function(perspectiveName) {
            return browser.waitUntilNoModal().then(function() {
                return browser.switchToParent().then(function() {
                    return browser.findElement(by.css('.ySEPerspectiveSelector > a'), true).getText().then(function(perspectiveSelected) {
                        if (perspectiveSelected.toUpperCase() !== perspectiveName.toUpperCase()) {
                            browser.waitForWholeAppToBeReady();
                            return browser.click(browser.findElement(by.css('.ySEPerspectiveSelector')), true).then(function() {
                                return browser.click(browser.findElement(by.cssContainingText('.ySEPerspectiveSelector ul li ', perspectiveName), true), "perspective " + perspectiveName + " is not clickable").then(function() {
                                    return browser.waitForContainerToBeReady().then(function() {
                                        return browser.switchToIFrame().then(function() {
                                            return perspectiveName === this.PREVIEW_PERSPECTIVE ? true : browser.waitForVisibility(this.OVERLAY_SELECTOR);
                                        }.bind(this));
                                    }.bind(this));
                                }.bind(this));
                            }.bind(this));
                        } else {
                            browser.waitForWholeAppToBeReady();
                            return browser.switchToIFrame().then(function() {
                                return perspectiveName === this.PREVIEW_PERSPECTIVE ? true : browser.waitForVisibility(this.OVERLAY_SELECTOR);
                            }.bind(this));
                        }
                    }.bind(this));
                }.bind(this)).then(function() {
                    return browser.switchToParent();
                });
            }.bind(this));
        },

        selectPreviewPerspective: function() {
            return this.select(this.PREVIEW_PERSPECTIVE);
        },

        selectVersioningPerspective: function() {
            return this.select(this.VERSIONING_PERSPECTIVE);
        },

        selectBasicPerspective: function() {
            return this.select(this.BASIC_CMS_PERSPECTIVE);
        },

        selectAdvancedPerspective: function() {
            return this.select(this.ADVANCED_CMS_PERSPECTIVE);
        },

        actions: {
            openPerspectiveList: function() {
                return browser.click(browser.findElement(by.css('.ySEPerspectiveSelector')));
            }
        },

        elements: {
            getSelectedModeName: function() {
                return browser.findElement(by.css('.ySEPerspectiveSelector > a')).getText();
            },
            getPerspectiveModeByName: function(perspectiveName) {
                return element(by.cssContainingText('.ySEPerspectiveSelector ul li a', perspectiveName));
            }
        },

        assertions: {
            expectedModeIsSelected: function(perspectiveName) {
                expect(componentObject.elements.getSelectedModeName()).toBe(perspectiveName,
                    'Expected current mode (perspective) name to be ' + perspectiveName);
            },
            expectVersioningModeIsAbsent: function() {
                expect(componentObject.elements.getPerspectiveModeByName(componentObject.VERSIONING_PERSPECTIVE)).toBeAbsent();
            },
            expectVersioningModeIsPresent: function() {
                expect(componentObject.elements.getPerspectiveModeByName(componentObject.VERSIONING_PERSPECTIVE)).toBeDisplayed();
            }
        }
    };

    return componentObject;
}();
