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
var genericEditorCommons, genericEditorWidgets;
if (typeof require !== 'undefined') {
    genericEditorCommons = require('./commonGenericEditorComponentObject');
    genericEditorWidgets = require('./genericEditorWidgetsComponentObject');
} else {
    genericEditorWidgets = {
        assertions: {},
        actions: {}
    };
}

module.exports = (function() {

    var componentObject = {};

    componentObject.constants = {
        COMPARATORS_BY_FIELD_TYPE: {
            "shortString": genericEditorWidgets.assertions.textFieldHasRightValue,
            "boolean": genericEditorWidgets.assertions.booleanFieldHasRightValue
        },
        SETTERS_BY_FIELD_TYPE: {
            "shortString": genericEditorWidgets.actions.setTextFieldValue,
            "boolean": genericEditorWidgets.actions.toggleBooleanField
        }
    };

    componentObject.elements = {
        // -- Dialog Elements --
        getSaveButton: function() {
            return genericEditorCommons.elements.getTopEditorModal().element(by.id('save'));
        },
        getCancelButton: function() {
            return genericEditorCommons.elements.getTopEditorModal().element(by.id('cancel'));
        },
        getCloseButton: function() {
            return genericEditorCommons.elements.getTopEditorModal().element(by.css('button.close'));
        },

        // -- Generic Editor Elements --
        getField: function(fieldId) {
            return genericEditorCommons.elements.getFieldByQualifier(fieldId);
        },
        getLocalizedFieldLanguageButton: function(fieldId, language) {
            return componentObject.elements.getField(fieldId)
                .element(by.cssContainingText('localized-element ul li a', language));
        },
        getLocalizedFieldInputs: function(fieldId) {
            return componentObject.elements.getField(fieldId)
                .all(by.css('localized-element y-tab input'));
        },
        getLocalizedFieldInputForLanguage: function(fieldId, language) {
            return componentObject.elements.getField(fieldId)
                .element(by.css('localized-element y-tab[data-tab-id="' + language.toLowerCase() + '"] input'));
        },
        getValidationErrors: function(fieldName, language) {
            var fieldStructure = this.getField(fieldName);
            if (language) {
                return fieldStructure.all(by.css('localized-element y-tab[data-tab-id="' + language + '"] se-generic-editor-field-messages .se-help-block--has-error'));
            } else {
                return fieldStructure.all(by.css('se-generic-editor-field-messages .se-help-block--has-error'));
            }
        },
        modalWithName: function(componentName) {
            return by.xpath('//*[@id="smartedit-modal-title-type.' + componentName + '.name"]');
        },
        modalWithTitle: function(i18nKey) {
            return by.xpath('//*[@id="smartedit-modal-title-' + i18nKey + '"]');
        },
        contentTabWithError: function() {
            return by.css('.sm-tab-error');
        }
    };

    componentObject.actions = {

        waitForEditorModalWithComponentNameToBeOpen: function(componentName) {
            browser.waitForPresence(componentObject.elements.modalWithName(componentName));
        },
        waitForEditorModalWithComponentNameToBeClosed: function(componentName) {
            browser.waitForAbsence(componentObject.elements.modalWithName(componentName));
        },
        waitForEditorModalWithTitleToBeOpen: function(titleI18nKey) {
            browser.waitForPresence(componentObject.elements.modalWithTitle(titleI18nKey));
        },
        waitForEditorModalWithTitleToBeClosed: function(titleI18nKey) {
            browser.waitForAbsence(componentObject.elements.modalWithTitle(titleI18nKey));
        },

        waitForNumberOfEditorModals: function(numberOfExpectedModals) {
            browser.wait(function() {
                    return genericEditorCommons.elements.getOpenedEditorModals().count().then(function(count) {
                        return count === numberOfExpectedModals;
                    });
                },
                5000,
                'Timeout waiting for number of editor modals to be ' + numberOfExpectedModals + '}.');
        },

        // -- Dialog Actions --
        save: function() {
            return browser.click(componentObject.elements.getSaveButton());
        },
        cancel: function() {
            return browser.click(componentObject.elements.getCancelButton());
        },
        closeEditor: function() {
            return browser.click(componentObject.elements.getCloseButton());
        },

        // -- Generic Editor actions --
        clickLocalizedFieldLanguageButton: function(fieldId, language) {
            return browser.click(componentObject.elements.getLocalizedFieldLanguageButton(fieldId, language));
        },
        setValueForLanguage: function(fieldId, language, name) {
            return componentObject.actions.clickLocalizedFieldLanguageButton(fieldId, language).then(function() {
                var input = componentObject.elements.getLocalizedFieldInputForLanguage(fieldId, language);
                return browser.click(input).then(function() {
                    return browser.sendKeys(input, name);
                });
            });
        },
        setTextValueInLocalizedField: function(fieldId, valuesByLanguage) {
            var promisesToResolve = [];

            Object.keys(valuesByLanguage).forEach(function(language) {
                promisesToResolve.push(
                    componentObject.actions.setValueForLanguage(fieldId, language, valuesByLanguage[language]));
            });

            return protractor.promise.all(promisesToResolve);
        },

        setEditorData: function(newData) {
            var promisesToResolve = [];

            newData.forEach(function(fieldInfo) {
                var setterMethod = componentObject.utils.getSetterMethodForFieldType(fieldInfo.type);
                promisesToResolve.push(setterMethod(fieldInfo.qualifier, fieldInfo.value));
            });

            return protractor.promise.all(promisesToResolve);
        }
    };

    componentObject.assertions = {
        // -- Dialog Assertions --
        saveIsDisabled: function() {
            componentObject.elements.getSaveButton().getAttribute('disabled').then(function(isButtonDisabled) {
                expect(isButtonDisabled).toBe('true', 'Expected save button to be disabled.');
            });
        },

        // -- Generic Editor Assertions --
        localizedFieldHasExpectedValueForLanguage: function(fieldId, language, expectedValue) {
            return componentObject.actions.clickLocalizedFieldLanguageButton(fieldId, language).then(function() {
                return componentObject.elements.getLocalizedFieldInputForLanguage(fieldId, language).getAttribute('value').then(function(currentValue) {
                    expect(currentValue).toEqual(expectedValue, 'Invalid message in ' + language + ' for localized value ' + fieldId);
                });
            });
        },
        localizedFieldHasExpectedValues: function(fieldId, expectedValues) {
            Object.keys(expectedValues).forEach(function(language) {
                componentObject.assertions.localizedFieldHasExpectedValueForLanguage(fieldId, language, expectedValues[language]);
            });
        },
        localizedFieldIsEmpty: function(fieldId) {
            var localizedFieldInputs = componentObject.elements.getLocalizedFieldInputs(fieldId);
            for (var i = 0; i < localizedFieldInputs.length; i++) {
                var input = localizedFieldInputs.get(i);
                expect(input.getText()).toBe('', 'Expected localized field ' + fieldId + ' to be empty');
            }
        },

        topEditorIsNested: function(expectedLevelOfNesting) {
            expect(genericEditorCommons.elements.getOpenedEditorModals().count()).toBe(expectedLevelOfNesting,
                "Expected editor to be nested " + expectedLevelOfNesting + " levels.");
        },
        topEditorIsNotNested: function() {
            expect(genericEditorCommons.elements.getOpenedEditorModals().count()).toBe(1,
                "Expected top editor not to be nested.");
        },
        openEditorHasRightData: function(expectedData) {
            expectedData.forEach(function(fieldInfo) {
                var comparatorMethod = componentObject.utils.getComparatorMethodForFieldType(fieldInfo.type);
                comparatorMethod(fieldInfo.qualifier, fieldInfo.value);
            });
        },
        fieldHasValidationErrors: function(fieldName, language, expectedCount) {
            expect(componentObject.elements.getValidationErrors(fieldName, language).count())
                .toBe(expectedCount, 'Expected field ' + fieldName + ' to have ' + expectedCount + ' errors.');
        },
        fieldHasValidationErrorsInTab: function(fieldName, language, expectedCount) {
            browser.waitForPresence(componentObject.elements.contentTabWithError());
            expect(componentObject.elements.getValidationErrors(fieldName, language).count())
                .toBe(expectedCount, 'Expected field ' + fieldName + ' to have ' + expectedCount + ' errors.');
        },
        fieldHasNoValidationErrors: function(fieldName) {
            var expectedCount = 0;
            expect(componentObject.elements.getValidationErrors(fieldName, null).count())
                .toBe(expectedCount, 'Expected field ' + fieldName + ' not to have errors.');
        },
    };

    componentObject.utils = {
        getComparatorMethodForFieldType: function(fieldType) {
            var comparator = componentObject.constants.COMPARATORS_BY_FIELD_TYPE[fieldType];
            if (!comparator) {
                throw Error("Can't find comparator for field " + fieldType);
            }

            return comparator;
        },
        getSetterMethodForFieldType: function(fieldType) {
            var setter = componentObject.constants.SETTERS_BY_FIELD_TYPE[fieldType];
            if (!setter) {
                throw Error("Can't find setter for field " + fieldType);
            }

            return setter;
        }
    };

    return componentObject;

}());
