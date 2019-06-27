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

    var componentObject = {};

    componentObject.constants = {};
    componentObject.elements = {
        // Editor
        getEditorTabs: function() {
            // TODO: This selector is too dependent on the current layout. 
            // It'd be convenient to parametrize yTabset and yTabs to include an id to be 
            // easier to find. 
            return element.all(by.css('form > y-tabset > div > ul > li'));
        },
        getSubmitButton: function() {
            return by.css('.ySEBtnRow #submit');
        },
        getCancelButton: function() {
            return by.css('.ySEBtnRow #cancel');
        },
        getAnyValidationError: function() {
            return by.css("[id^='validation-error']");
        },

        // Tabs
        getTabSelector: function(tabname, inError) {
            return by.cssContainingText('.ySEGenericEditorTabs > div > ul.nav-tabs > li > a' + (inError === true ? '.sm-tab-error' : ''), tabname);
        },

        // Fields
        getFieldStructure: function(fieldName) {
            return element(by.css('.ySEGenericEditorFieldStructure[data-cms-field-qualifier="' + fieldName + '"]'));
        },
        getField: function(fieldName, language) {
            var fieldStructure = this.getFieldStructure(fieldName);
            if (language) {
                return fieldStructure.element(by.css('localized-element y-tab[data-tab-id="' + language + '"] generic-editor-field'));
            } else {
                return fieldStructure.element(by.css('generic-editor-field'));
            }
        },
        getLocalizedTabHeader: function(fieldName, language) {
            return this.getFieldStructure(fieldName).element(by.css('localized-element ul.nav-tabs li[data-tab-id="' + language + '"]'));
        },
        getValidationErrors: function(fieldName, language) {
            var fieldStructure = this.getFieldStructure(fieldName);
            if (language) {
                return fieldStructure.all(by.css('localized-element y-tab[data-tab-id="' + language + '"] se-generic-editor-field-messages .se-help-block--has-error'));
            } else {
                return fieldStructure.all(by.css('se-generic-editor-field-messages .se-help-block--has-error'));
            }
        },
        getValidationErrorInLanguage: function(fieldName, language) {
            return element(by.css('[data-tab-id="' + language + '"] [validation-id="' + fieldName + '"] se-generic-editor-field-messages'));
        },

        // Short/Long String
        getTextField: function(fieldName, language) {
            return this.getField(fieldName, language).element(by.css('input, textarea'));
        },

        // Rich/Text Editor
        getRichTextField: function(fieldName, language) {
            var fieldStructure = this.getFieldStructure(fieldName);
            if (language) {
                return fieldStructure.element(by.css('localized-element y-tab[data-tab-id="' + language + '"] .cke_contents iframe'));
            } else {
                return fieldStructure.element(by.css('.cke_contents iframe'));
            }
        },
        getRichTextBody: function() {
            return browser.driver.findElement(by.tagName('body'));
        }
    };

    componentObject.actions = {
        // Editor
        submitForm: function() {
            return browser.click(componentObject.elements.getSubmitButton());
        },
        cancelForm: function() {
            return browser.click(componentObject.elements.getCancelButton());
        },
        // Tabs
        selectTab: function(tabName) {
            return browser.click(componentObject.elements.getTabSelector(tabName));
        },
        // Fields
        selectLocalizedFieldLanguage: function(fieldName, language) {
            return browser.click(componentObject.elements.getLocalizedTabHeader(fieldName, language));
        },
        setTextFieldValue: function(fieldName, language, value) {
            return componentObject.elements.getTextField(fieldName, language).clear().sendKeys(value);
        },
        setValueInRichTextField: function(fieldName, language, value) {
            var richTextField = componentObject.elements.getRichTextField(fieldName, language);
            return browser.switchTo().frame(richTextField.getWebElement('')).then(function() {
                return componentObject.elements.getRichTextBody().sendKeys(value).then(function() {
                    return browser.switchToParent();
                });
            });
        }
    };

    // --------------------------------------------------------------------------------------------------
    // Assertions
    // --------------------------------------------------------------------------------------------------
    componentObject.assertions = {
        // Editor 
        editorTabsAreNotDisplayed: function() {
            expect(componentObject.elements.getEditorTabs().count()).toBe(0, 'Expected editor not to have any displayed tabs.');
        },
        cancelButtonIsDisplayed: function() {
            expect(element(componentObject.elements.getCancelButton())).toBeDisplayed();
        },
        submitButtonIsDisplayed: function() {
            expect(element(componentObject.elements.getSubmitButton())).toBeDisplayed();
        },
        cancelButtonIsNotDisplayed: function() {
            expect(componentObject.elements.getCancelButton()).toBeAbsent();
        },
        submitButtonIsNotDisplayed: function() {
            expect(componentObject.elements.getSubmitButton()).toBeAbsent();
        },
        formHasNoValidationErrors: function() {
            expect(componentObject.elements.getAnyValidationError()).toBeAbsent();
        },

        // Tabs
        tabIsInError: function(tabname) {
            browser.waitForVisibility(componentObject.elements.getTabSelector(tabname, true));
        },
        tabIsNotInError: function(tabname) {
            browser.waitForVisibility(componentObject.elements.getTabSelector(tabname, false));
        },

        // Localized Elements
        tabLanguageIsDisplayed: function(qualifier, fieldLanguage) {
            expect(componentObject.elements.getLocalizedTabHeader(qualifier, fieldLanguage)).toBeDisplayed();
        },
        tabLanguageIsNotDisplayed: function(qualifier, fieldLanguage) {
            expect(browser.waitForAbsence(componentObject.elements.getLocalizedTabHeader(qualifier, fieldLanguage))).toBeTruthy();
        },

        // Fields
        richTextFieldIsDisabled: function(fieldName, language) {
            // Note: Using the getTextField element instead of getRichTextField on purpose. If getRichTextField is used, it returns a 
            // tag that doesn't contain any information about the field being enabled or not; it will always return true. 
            // Instead, the rich text field has a text area that determines if it is enabled; this text area is retrieved with getTextField.
            expect(componentObject.elements.getTextField(fieldName, language).isEnabled()).toBe(false);
        },
        richTextFieldIsEnabled: function(fieldName, language) {
            // Note: Using the getTextField element instead of getRichTextField on purpose. If getRichTextField is used, it returns a 
            // tag that doesn't contain any information about the field being enabled or not; it will always return true. 
            // Instead, the rich text field has a text area that determines if it is enabled; this text area is retrieved with getTextField.
            expect(componentObject.elements.getTextField(fieldName, language).isEnabled()).toBeTruthy();
        },
        fieldHasValidationErrors: function(fieldName, language, expectedCount) {
            expect(componentObject.elements.getValidationErrors(fieldName, language).count())
                .toBe(expectedCount, 'Expected field ' + fieldName + ' to have ' + expectedCount + ' errors.');
        },
        fieldHasNoValidationErrors: function(fieldName) {
            var expectedCount = 0;
            expect(componentObject.elements.getValidationErrors(fieldName, null).count())
                .toBe(expectedCount, 'Expected field ' + fieldName + ' not to have errors.');
        },
        fieldHasValidationErrorInLanguage: function(fieldName, language, expectedErrorMsg) {
            var validationErrors = componentObject.elements.getValidationErrors(fieldName, language);
            expect(validationErrors.count()).toBe(1, 'Expected ' + fieldName + ' in ' + language + " to have only one error message.");
            expect(validationErrors.get(0).getText()).toEqual(expectedErrorMsg);
        },
        fieldHasNoValidationErrorsInLanguage: function(fieldName, language) {
            browser.waitForAbsence(componentObject.elements.getValidationErrors(fieldName, language));
        }

    };

    return componentObject;
})();
