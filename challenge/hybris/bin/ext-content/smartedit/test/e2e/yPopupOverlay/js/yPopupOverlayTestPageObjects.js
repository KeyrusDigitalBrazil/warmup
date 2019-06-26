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

    var elements = {
        anchor: element(by.css('anchor')),
        popupTemplate: element(by.css('#popup-template')),
        popupTrigger: element(by.css('#popup-trigger')),
        popupHalign: element(by.css('#popup-halign')),
        popupValign: element(by.css('#popup-valign')),
        showCount: element(by.css('#show-count')),
        hideCount: element(by.css('#hide-count'))
    };

    var pageObjects = {

        actions: {
            clickAnchor: function() {
                return browser.click(elements.anchor);
            },
            setTriggerValue: function(newValue) {
                return elements.popupTrigger.clear().sendKeys(newValue);
            },
            setPopupLeftAlign: function() {
                return elements.popupHalign.clear().sendKeys('left');
            },
            setPopupRightAlign: function() {
                return elements.popupHalign.clear().sendKeys('right');
            },
            setPopupTopAlign: function() {
                return elements.popupValign.clear().sendKeys('top');
            },
            setPopupBottomAlign: function() {
                return elements.popupValign.clear().sendKeys('bottom');
            }
        },

        assertions: {
            assertPopupIsVisible: function() {
                expect(elements.popupTemplate.isDisplayed()).toBe(true);
            },
            assertPopupIsNotVisible: function() {
                browser.waitForAbsence(elements.popupTemplate);
            }
        },

        utils: {
            getAnchorLocation: function() {
                return elements.anchor.getLocation();
            },
            getAnchorSize: function() {
                return elements.anchor.getSize();
            },
            getPopupLocation: function() {
                return elements.popupTemplate.getLocation();
            },
            getPopupSize: function() {
                return elements.popupTemplate.getSize();
            },
            getShowCount: function() {
                return Number(elements.showCount.getAttribute('value'));
            },
            getHideCount: function() {
                return Number(elements.hideCount.getAttribute('value'));
            }
        }
    };

    return pageObjects;

})();
