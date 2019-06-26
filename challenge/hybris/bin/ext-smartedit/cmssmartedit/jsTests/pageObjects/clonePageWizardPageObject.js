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
    var displayCondition = require('../componentObjects/newPageDisplayConditionComponentObject');
    var pageRestrictionsEditor = require('./PageRestrictionsEditorPageObject');

    function getTrimmedTextFromElement(element) {
        return element.getText().then(function(text) {
            return text.trim();
        });
    }

    var pageObject = {};

    pageObject.constants = {

    };

    pageObject.util = {
        isWindowOpen: function() {
            return wizard.actions.isWindowsOpen();
        }
    };

    pageObject.elements = {
        getClonePageToolbarButton: function() {
            return element(by.xpath('.//*[.="Clone" and contains(@class ,"toolbar-action--button")]'));
        },
        getConditionDropdownToggle: function() {
            return displayCondition.elements.getConditionDropdownToggle();
        },
        getComponentsCloneOptions: function() {
            return element.all(by.css("*[class*='components-cloned-option-id']"));
        },
        getComponentsCloneOptionAttribute: function(position, attribute) {
            return pageObject.elements.getComponentsCloneOptions().get(position).getAttribute(attribute);
        },
        getSelectedConditionOption: function() {
            return displayCondition.elements.getSelectedConditionOption();
        },
        getAssociatedPrimaryPage: function() {
            return displayCondition.elements.getAssociatedPrimaryPage();
        },
        getPageLabelField: function() {
            return element(by.id('label-shortstring'));
        },
        getClonePageOptionsUseExistingRadio: function() {
            return element(by.xpath("//component-clone-option-form//*[@value='reference']"));
        },
        getRestrictionListItem: function(index) {
            pageRestrictionsEditor.elements.getRestrictionListItem(index);
        },
        getRestrictionsLabelsList: function() {
            return element.all(by.css("*[class*='ySERestrictionsNameHeader']"));
        },
        getLabelField: function() {
            return element(by.id('label-shortstring'));
        },
        getLabelFieldText: function() {
            return this.getLabelField().getAttribute("value");
        },
        getLabelFieldWarning: function() {
            return browser.findElement(by.css('#label .se-help-block--has-warning'));
        },
        getLabelFieldWarningText: function() {
            return getTrimmedTextFromElement(this.getLabelFieldWarning());
        },
        getNameField: function() {
            return element(by.id('name-shortstring'));
        },
        getNameFieldText: function() {
            return this.getNameField().getAttribute("value");
        },
        getUidField: function() {
            return element(by.id('uid-shortstring'));
        },
        getUidFieldText: function() {
            return this.getUidField().getAttribute("value");
        },
        getUidErrorsElement: function() {
            return element(by.css('#uid se-generic-editor-field-messages .se-help-block--has-error'));
        },
        getUidErrorsText: function() {
            return getTrimmedTextFromElement(this.getUidErrorsElement());
        },
        getCloneSuccessAlert: function() {
            return element(by.css('system-alerts .alert-success'));
        },
        getCloneInfoAlert: function() {
            return element(by.css("system-alerts .alert-info"));
        },
        getTargetCatalogAndVersionToggle: function() {
            return browser.findElement(by.css('div.target-catalog-version-selector-wrapper y-select'));
        },
        getTargetCatalogAndVersionOption: function() {
            return this.getTargetCatalogAndVersionToggle().element(by.css('.y-select-item-printer'));
        },
        getTargetCatalogAndVersionWarningText: function() {
            return getTrimmedTextFromElement(browser.findElement(by.css('div.target-catalog-version-selector-wrapper .se-help-block--has-warning')));
        },
        getTargetCatalogAndVersionInDropdown: function(name) {
            return browser.findElement(by.css('div.target-catalog-version-selector-wrapper y-select')).all(by.cssContainingText('ul li span', name)).first();
        },
        getCloneComponentInfoForm: function() {
            return element(by.css('.component-clone-info-form'));
        },
        getFieldWarningsCount: function() {
            return pageObject.elements.getCloneComponentInfoForm().all(by.css('.se-help-block--has-warning')).count();
        }
    };

    pageObject.actions = {
        openClonePageWizard: function() {
            return browser.click(pageObject.elements.getClonePageToolbarButton());
        },
        openConditionDropdown: function() {
            return displayCondition.actions.openConditionDropdown();
        },
        selectVariationCondition: function() {
            return displayCondition.actions.selectVariationCondition();
        },
        selectPrimaryCondition: function() {
            return displayCondition.actions.selectPrimaryCondition();
        },
        clickNext: function() {
            return wizard.actions.moveNext();
        },
        submit: function() {
            return wizard.actions.submit();
        },
        enterTextInUidField: function(text) {
            return browser.clearAndSendKeys(pageObject.elements.getUidField(), text);
        },
        enterTextInLabelField: function() {
            return browser.clearAndSendKeys(pageObject.elements.getLabelField(), 'something else');
        },
        openTargetCatalogAndVersionToggle: function() {
            return browser.click(pageObject.elements.getTargetCatalogAndVersionToggle());
        },
        selectTargetCatalogAndVersion: function(name) {
            return this.openTargetCatalogAndVersionToggle().then(function() {
                return browser.click(pageObject.elements.getTargetCatalogAndVersionInDropdown(name));
            });
        },
        openAndBeReady: function(done) {
            var perspective = e2e.componentObjects.modeSelector;
            browser.waitForWholeAppToBeReady().then(function() {
                return perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
            }).then(function() {
                pageObject.actions.openClonePageWizard();
                done();
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
        primaryConditionOptionIsDisplayed: function() {
            return displayCondition.assertions.primaryConditionOptionIsDisplayed();
        },
        componentCloneOptionsUseExistingIsSelected: function() {
            return expect(pageObject.elements.getClonePageOptionsUseExistingRadio().getAttribute("selected")).toBeTruthy();
        },
        contentPageLabelFieldEnabledToBeFalse: function() {
            return expect(pageObject.elements.getPageLabelField().isEnabled()).toBeFalsy();
        },
        contentPageLabelFieldEnabledToBeTrue: function() {
            return expect(pageObject.elements.getPageLabelField().isEnabled()).toBeTruthy();
        },
        assertWindowIsOpen: function() {
            wizard.actions.assertWindowIsOpen().then(function(openWindow) {
                expect(openWindow).toBeTruthy();
            });
        },
        assertWindowIsClosed: function() {
            return browser.waitForAbsence(by.css('.modal-dialog'), "modal window is still showing");
        },
        assertCloneSuccessAlertIsDisplayed: function() {
            expect(browser.waitToBeDisplayed(pageObject.elements.getCloneSuccessAlert())).toBeTruthy();
        },
        assertCloneActionableAlertIsDisplayed: function() {
            expect(browser.waitToBeDisplayed(pageObject.elements.getCloneInfoAlert())).toBeTruthy();
        },
        assertCloneActionableAlertIsNotDisplayed: function() {
            expect(browser.waitNotToBeDisplayed(pageObject.elements.getCloneInfoAlert())).toBeTruthy();
        },
        cloneIconIsDisplayed: function() {
            expect(pageObject.elements.getClonePageToolbarButton().isPresent()).toBe(true);
        },
        cloneIconIsNotDisplayed: function() {
            browser.waitForAbsence(pageObject.elements.getClonePageToolbarButton());
        }
    };

    return pageObject;

})();
