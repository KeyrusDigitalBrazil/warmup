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
module.exports = {

    PREVIEW_PERSPECTIVE: "PREVIEW",
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

    selectBasicPerspective: function() {
        return this.select(this.BASIC_CMS_PERSPECTIVE);
    },

    selectAdvancedPerspective: function() {
        return this.select(this.ADVANCED_CMS_PERSPECTIVE);
    }
};
