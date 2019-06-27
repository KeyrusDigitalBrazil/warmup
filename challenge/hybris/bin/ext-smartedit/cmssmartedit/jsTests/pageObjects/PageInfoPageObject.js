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
var confirmationModal;
if (typeof require !== 'undefined') {
    confirmationModal = require('../componentObjects/confirmationModalComponentObject');
} else {
    confirmationModal = {
        actions: {}
    };
}

module.exports = function() {

    var pageObject = {};

    var selectors = {
        getPageInfoMenuToolbarItemCssSelector: function() {
            return '[data-item-key="se.cms.pageInfoMenu"]';
        },
        getPageInfoMenuButtonCssSelector: function() {
            return selectors.getPageInfoMenuToolbarItemCssSelector() + ' button.toolbar-action--button';
        },
        getPageInfoMenuButtonSelector: function() {
            return by.css(selectors.getPageInfoMenuButtonCssSelector());
        },
        getPageInfoMenuSelector: function() {
            return by.css('.ySEPageInfoMenu');
        },
        getEditButtonSelector: function() {
            return by.css('.ySEPageInfo__edit-btn');
        },
        getRestrictionsTab: function() {
            return by.css('li[data-tab-id="restrictions"] a');
        }
    };

    pageObject.elements = {
        getPageInfoToolbarButton: function() {
            return browser.element(selectors.getPageInfoMenuButtonSelector());
        },
        // Container
        getPageInfoMenuContainer: function() {
            return browser.element(selectors.getPageInfoMenuSelector());
        },
        getEditButton: function() {
            return this.getPageInfoMenuContainer().element(selectors.getEditButtonSelector());
        },

        // Fields
        getField: function(fieldQualifier) {
            var field = this.getPageInfoMenuContainer().element(by.css(".ySEGenericEditorFieldStructure[data-cms-field-qualifier='" + fieldQualifier + "']"));
            browser.waitForPresence(field);

            return field;
        },
        getTimeFieldValue: function(qualifier) {
            return this.getField(qualifier).element(by.css('input[disabled]')).getAttribute('value');
        },
        getFieldValue: function(qualifier) {
            return this.getField(qualifier).element(by.css('input')).getAttribute('value');
        },

        // Editor Modal
        getPageEditorModal: function() {
            var modal = browser.element(by.css(".modal-dialog"));
            browser.waitForPresence(modal);

            return modal;
        },
        getEditorCancelButton: function() {
            return this.getPageEditorModal().element(by.css("#cancel"));
        },

        // Restrictions
        getRestrictionsTab: function() {
            return browser.element(selectors.getRestrictionsTab());
        },
        getRestrictionsList: function() {
            return this.getPageInfoMenuContainer().element(by.css('restrictions-list'));
        },
        getRestrictionByName: function(restrictionName) {
            return this.getRestrictionsList().element(by.cssContainingText('.restrictions-list__item', restrictionName));
        },
        getCurrentHomepageIcon: function() {
            return this.getPageInfoToolbarButton().element(by.css('.hyicon-home.hyicon-home--current'));
        },
        getOldHomepageIcon: function() {
            return this.getPageInfoToolbarButton().element(by.css('.hyicon-home.hyicon-home--old'));
        }
    };

    pageObject.actions = {

        openPageInfoMenu: function() {
            browser.switchToParent();
            return browser.click(selectors.getPageInfoMenuButtonSelector());
        },
        clickEditButton: function() {
            return browser.click(pageObject.elements.getEditButton());
        },
        dismissEditor: function() {
            return browser.click(pageObject.elements.getEditorCancelButton()).then(function() {
                confirmationModal.actions.confirmConfirmationModal();
            });
        },
        clickRestrictionsTab: function() {
            return browser.click(pageObject.elements.getRestrictionsTab());
        }
    };

    pageObject.assertions = {
        hasNoRestrictionsTab: function() {
            browser.isAbsent(selectors.getRestrictionsTab());
        },
        restrictionHasRightName: function(restrictionElement, restrictionName) {
            expect(restrictionElement.element(by.css('.restrictions-list__item-name')).getText()).toBe(restrictionName);
        },
        restrictionHasRightDescription: function(restrictionElement, restrictionName) {
            expect(restrictionElement.element(by.css('.restrictions-list__item-description')).getText()).toBe(restrictionName);
        },
        hasRestrictionWithRightData: function(restrictionInfo) {
            var restrictionElement = pageObject.elements.getRestrictionByName(restrictionInfo.name);
            this.restrictionHasRightName(restrictionElement, restrictionInfo.name);
            this.restrictionHasRightDescription(restrictionElement, restrictionInfo.description);
        },
        uidIs: function(expectedUid) {
            browser.switchToParent();
            return browser.wait(function() {
                return pageObject.elements.getPageUidField().getAttribute('value').then(function(actualUid) {
                    return actualUid === expectedUid;
                });
            }, browser.explicitWait, "Expected uid to be " + expectedUid);
        },
        hasCurrentHomepageIcon: function() {
            browser.waitForPresence(pageObject.elements.getCurrentHomepageIcon(), 'Expected current homepage icon to be present');
        },
        hasOldHomepageIcon: function() {
            browser.waitForPresence(pageObject.elements.getOldHomepageIcon(), 'Expected current homepage icon to be present');
        },
        hasNoEditButton: function() {
            browser.isAbsent(pageObject.elements.getEditButton());
        }
    };

    return pageObject;

}();
