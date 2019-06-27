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
module.exports = (function() {
    var linkToggle = {};

    linkToggle.assertions = {
        externalRadioIsSelected: function() {
            expect(linkToggle.elements.getExternalRadio().isSelected()).toBe(true,
                "Expect external radio to be selected");
        },
        externalRadioNotSelected: function() {
            expect(linkToggle.elements.getExternalRadio().isSelected()).toBe(false,
                "Expect external radio to be not selected");
        },
        internalRadioIsSelected: function() {
            expect(linkToggle.elements.getInternalRadio().isSelected()).toBe(true,
                "Expect external radio to be selected");
        },
        internalRadioNotSelected: function() {
            expect(linkToggle.elements.getInternalRadio().isSelected()).toBe(false,
                "Expect external radio to be not selected");
        },
        urlLinkEqualTo: function(possibleValue) {
            expect(linkToggle.elements.getUrlLinkField().getAttribute('value')).toEqual(possibleValue,
                "Expect urlLink to be equal to " + possibleValue);
        },
        urlLinkIsEmpty: function() {
            linkToggle.assertions.urlLinkEqualTo('');
        }
    };

    linkToggle.actions = {
        clearUrlLink: function() {
            return linkToggle.elements.getUrlLinkField().sendKeys('');
        },
        clickOnExternalRadio: function() {
            return browser.click(linkToggle.elements.getClickableExternalRadio());
        },
        clickOnInternalRadio: function() {
            return browser.click(linkToggle.elements.getInternalRadio());
        }
    };

    linkToggle.elements = {
        getUrlLinkField: function() {
            return element(by.id('urlLink'));
        },
        getExternalRadio: function() {
            return element(by.id('external-link'));
        },
        getInternalRadio: function() {
            return element(by.id('internal-link'));
        },
        getClickableExternalRadio: function() {
            return linkToggle.utils.getParentElement(linkToggle.elements.getExternalRadio());
        },
        getClickableInternalRadio: function() {
            return linkToggle.utils.getParentElement(linkToggle.elements.getInternalRadio());
        }
    };

    linkToggle.utils = {
        getParentElement: function(elem) {
            return elem.element(by.xpath('..'));
        }
    };

    return linkToggle;
})();
