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
    var wizard = require('../componentObjects/WizardComponentObject');
    var wizardStep = require('./WizardStepPageObject');
    var displayCondition = require('../componentObjects/newPageDisplayConditionComponentObject');
    var pageRestrictionsEditor = require('./PageRestrictionsEditorPageObject');

    var pageObject = {};

    pageObject.elements = {
        getAddNewPageButton: function() {
            return element(by.css('.se-page-list__add-btn-wrapper button'));
        },
        getContentPageTypeListItem: function() {
            return wizardStep.listItemByText('Content Page');
        },
        getProductPageTypeListItem: function() {
            return wizardStep.listItemByText('Product Page');
        },
        getCategoryPageTypeListItem: function() {
            return wizardStep.listItemByText('Category Page');
        },
        getPageTemplate1ListItem: function() {
            return wizardStep.listItemByText('Page Template 1');
        },
        getPageTemplate2ListItem: function() {
            return wizardStep.listItemByText('Page Template 2');
        },
        getConditionDropdownToggle: function() {
            return displayCondition.elements.getConditionDropdownToggle();
        },
        getPrimaryConditionOption: function() {
            return displayCondition.elements.getPrimaryConditionOption();
        },
        isPrimaryConditionOptionDisplayed: function() {
            return browser.waitToBeDisplayed(this.getPrimaryConditionOption());
        },
        isPrimaryConditionOptionNotDisplayed: function() {
            return browser.waitNotToBeDisplayed(this.getPrimaryConditionOption());
        },
        getVariationConditionOption: function() {
            return displayCondition.elements.getVariationConditionOption();
        },
        getPrimaryPageDropdownToggle: function() {
            return element(by.css('#page-condition-primary-selector-id .ui-select-toggle'));
        },
        getPage1PrimaryPageOption: function() {
            return by.xpath("//label[contains(., 'Primary page associated to the variation')]/following-sibling::div[contains(., 'page1TitleSuffix')]");
        },
        isPage1PrimaryPageOptionDisplayed: function() {
            return browser.waitToBeDisplayed(this.getPage1PrimaryPageOption());
        },
        getProductPage1PrimaryPageOption: function() {
            return by.xpath("//div[@id='page-condition-primary-selector-id']//div[contains(., 'productPage1')]");
        },
        isProductPage1PrimaryPageOptionDisplayed: function() {
            return browser.waitToBeDisplayed(this.getProductPage1PrimaryPageOption());
        },
        getNameInput: function() {
            return element(by.css('#name-shortstring'));
        },
        getLabelInput: function() {
            return element(by.css('#label-shortstring'));
        },
        getTitleInput: function() {
            return element(by.css('[data-tab-id="en"] #title-shortstring'));
        },
        getIDInput: function() {
            return element(by.css('#uid-shortstring'));
        },
        getErrorMessage: function() {
            return element(by.css('.se-help-block--has-error'));
        },
        getNextButton: function() {
            return wizard.elements.nextButton();
        },
        getPageRestrictionsEditor: function() {
            return pageRestrictionsEditor.elements.getPageRestrictionsEditor();
        },
        getLabelInputText: function() {
            return pageObject.elements.getLabelInput().getAttribute('value').then(function(value) {
                return (value || '').trim();
            });
        },
        getLabelInputEnabled: function() {
            return pageObject.elements.getLabelInput().getAttribute('disabled').then(function(disabled) {
                return !disabled;
            });
        },
        getDoneButton: function() {
            return wizard.elements.submitButton();
        },
        isDoneButtonEnabled: function() {
            return pageObject.elements.getDoneButton().getAttribute('disabled').then(function(disabled) {
                return !disabled;
            });
        },
        getErrorMessageText: function() {
            return pageObject.elements.getErrorMessage().getText().then(function(text) {
                return (text || '').trim();
            });
        },
        isWindowOpen: function() {
            return wizard.actions.isWindowsOpen();
        },
        assertWindowIsOpen: function() {
            wizard.actions.assertWindowIsOpen();
        },
        assertWindowIsClosed: function() {
            wizard.actions.assertWindowIsClosed();
        }
    };

    pageObject.actions = {
        openApplication: function(params) {
            if (params.sendOnlyPrimaryDisplayCondition) {
                pageObject.utils.makeMockSendOnlyPrimaryDisplayCondition();
            }
            browser.waitForAngularEnabled(false);
            return browser.bootstrap().then(function() {
                return browser.waitForContainerToBeReady();
            });
        },

        selectContentPageType: function() {
            return browser.click(pageObject.elements.getContentPageTypeListItem());
        },
        selectProductPageType: function() {
            return browser.click(pageObject.elements.getProductPageTypeListItem());
        },
        selectCategoryPageType: function() {
            return browser.click(pageObject.elements.getCategoryPageTypeListItem());
        },
        selectPageTemplate1: function() {
            return browser.click(pageObject.elements.getPageTemplate1ListItem());
        },
        clickNext: function() {
            return wizard.actions.moveNext();
        },
        clickSubmit: function() {
            return wizard.actions.submit();
        },
        openAddPageWizard: function() {
            browser.click(pageObject.elements.getAddNewPageButton());
            return browser.waitUntilModalAppears();
        },
        openConditionDropdown: function() {
            return displayCondition.actions.openConditionDropdown();
        },
        selectPrimaryCondition: function() {
            return displayCondition.actions.selectPrimaryCondition();
        },
        selectVariationCondition: function() {
            return displayCondition.actions.selectVariationCondition();
        },
        selectPage1AsPrimaryPage: function() {
            return pageObject.actions.togglePrimaryPageDropdown().then((function() {
                return browser.click(pageObject.elements.getPage1PrimaryPageOption());
            }).bind(pageObject));
        },
        togglePrimaryPageDropdown: function() {
            return browser.click(pageObject.elements.getPrimaryPageDropdownToggle());
        },
        enterSomeValidPageInfo: function() {
            wizardStep.enterFieldData('name-shortstring', 'someName');
            wizardStep.enterFieldData('label-shortstring', 'someLabel');
            wizardStep.enterLocalizedFieldData('title-shortstring', 'en', 'someTitle');
            return wizardStep.enterFieldData('uid-shortstring', 'someId');
        },
        enterSomeValidPageInfoForVariationPage: function() {
            wizardStep.enterFieldData('name-shortstring', 'someName');
            wizardStep.enterLocalizedFieldData('title-shortstring', 'en', 'someTitle');
            return wizardStep.enterFieldData('uid-shortstring', 'someId');
        },
        enterPageName: function(name) {
            return wizardStep.enterFieldData('name-shortstring', name);
        },
        enterInvalidUid: function() {
            return wizardStep.enterFieldData('uid-shortstring', 'trump');
        },
        enterLabel: function(label) {
            return wizardStep.enterFieldData('label-shortstring', label);
        },
        enterPageTitle: function(title) {
            return wizardStep.enterLocalizedFieldData('title-shortstring', 'en', title);
        },
        getErrorMessageText: function() {
            return this.elements.getErrorMessage().getText().then(function(text) {
                return (text || '').trim();
            });
        },
        clearSearchFilter: function() {
            return browser.click(element(by.css('.glyphicon-remove-sign')));
        },
        addRestriction: function() {
            return pageRestrictionsEditor.actions.clickAddNew().then(function() {

            }).then(function() {
                return pageRestrictionsEditor.actions.openRestrictionTypesSelect();
            }).then(function() {
                return pageRestrictionsEditor.actions.selectFirstRestrictionTypeFromSelect();
            }).then(function() {
                return pageRestrictionsEditor.actions.enterSearchText('t');
            }).then(function() {
                return pageRestrictionsEditor.actions.clickSearchResultWithText('yet another');
            });
        }
    };

    pageObject.assertions = {
        currentStepTextIs: function(expectedLabel) {
            wizard.assertions.currentStepTextIs(expectedLabel);
        },
        variationConditionOptionIsDisplayed: function() {
            return displayCondition.assertions.variationConditionOptionIsDisplayed();
        },
        variationConditionOptionIsNotDisplayed: function() {
            return displayCondition.assertions.variationConditionOptionIsNotDisplayed();
        },
        cannotClickNext: function() {
            expect(wizard.elements.nextButton().isEnabled()).toBe(false);
        }
    };

    pageObject.utils = {
        makeMockSendOnlyPrimaryDisplayCondition: function() {
            browser.executeScript('window.sessionStorage.setItem("sendOnlyPrimaryDisplayCondition", arguments[0])', true);
        },
        makeMockSendAllDisplayCondition: function() {
            browser.executeScript('window.sessionStorage.setItem("sendOnlyPrimaryDisplayCondition", arguments[0])', false);
        }
    };

    return pageObject;
})();
