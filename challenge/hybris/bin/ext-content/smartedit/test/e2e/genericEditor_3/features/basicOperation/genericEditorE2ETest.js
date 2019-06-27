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
/* jshint undef:false */
describe("GenericEditor -", function() {

    var page = require("./../../componentObjects/genericEditorPageObject");
    var genericEditor = require("./../../componentObjects/genericEditorComponentObject");

    // Constants
    var TEXT_FIELD = page.constants.HEADLINE_FIELD;
    var TEXT_FIELD_INVALID_VALUE = page.constants.HEADLINE_INVALID_TEXT;
    var TEXT_FIELD_UNKNOWN_TYPE = page.constants.HEADLINE_UNKNOWN_TYPE;

    var RICH_TEXT_FIELD = page.constants.CONTENT_FIELD;
    var RICH_TEXT_FIELD_INVALID_VALUE = page.constants.CONTENT_FIELD_INVALID_TEXT;
    var RICH_TEXT_FIELD_INVALID_VALUE_IT = page.constants.CONTENT_FIELD_INVALID_TEXT_IT;
    var RICH_TEXT_FIELD_ERROR_MSG = page.constants.CONTENT_FIELD_ERROR_MSG;

    var NOT_LOCALIZED = null;
    var ENGLISH_TAB = 'en';
    var ITALIAN_TAB = 'it';
    var FRENCH_TAB = 'fr';
    var POLISH_TAB = 'pl';
    var HINDI_TAB = 'hi';

    beforeEach(function(done) {
        page.actions.configureTest({
            multipleTabs: false
        });
        page.actions.bootstrap(__dirname, done);
    });

    describe('basic', function() {
        it('GIVEN no tabs have been configured for the editor WHEN the editor opens THEN all fields are in only one tab', function() {
            // THEN 
            genericEditor.assertions.editorTabsAreNotDisplayed();
        });
    });

    describe('form save', function() {
        it('WHEN the editor is opened THEN it will display cancel button AND not display submit button', function() {
            // THEN 
            genericEditor.assertions.cancelButtonIsNotDisplayed();
            genericEditor.assertions.submitButtonIsNotDisplayed();
        });

        it('WHEN the editor is opened THEN it will display cancel and submit buttons when an attribute is modified', function() {
            // WHEN 
            genericEditor.actions.setTextFieldValue(TEXT_FIELD, NOT_LOCALIZED, 'some value');

            // THEN 
            genericEditor.assertions.cancelButtonIsDisplayed();
            genericEditor.assertions.submitButtonIsDisplayed();
        });

        it('GIVEN field has invalid information WHEN the form is saved THEN it will display validation errors', function() {
            // GIVEN 

            // WHEN 
            genericEditor.actions.setTextFieldValue(TEXT_FIELD, NOT_LOCALIZED, TEXT_FIELD_INVALID_VALUE);
            genericEditor.actions.submitForm();

            // THEN 
            genericEditor.assertions.fieldHasValidationErrors(TEXT_FIELD, NOT_LOCALIZED, 2);
        });

        it('GIVEN invalid information has been pushed to two languages in a localized field WHEN the form is saved THEN it will display validation errors only on those two languages', function() {
            // GIVEN 
            genericEditor.actions.selectLocalizedFieldLanguage(RICH_TEXT_FIELD, ENGLISH_TAB);
            genericEditor.actions.setValueInRichTextField(RICH_TEXT_FIELD, ENGLISH_TAB, RICH_TEXT_FIELD_INVALID_VALUE);

            genericEditor.actions.selectLocalizedFieldLanguage(RICH_TEXT_FIELD, ITALIAN_TAB);
            genericEditor.actions.setValueInRichTextField(RICH_TEXT_FIELD, ITALIAN_TAB, RICH_TEXT_FIELD_INVALID_VALUE_IT);

            // WHEN 
            genericEditor.actions.submitForm();

            // THEN 
            genericEditor.assertions.fieldHasValidationErrorInLanguage(RICH_TEXT_FIELD, ITALIAN_TAB, RICH_TEXT_FIELD_ERROR_MSG);
            genericEditor.actions.selectLocalizedFieldLanguage(RICH_TEXT_FIELD, ENGLISH_TAB);
            genericEditor.assertions.fieldHasValidationErrorInLanguage(RICH_TEXT_FIELD, ENGLISH_TAB, RICH_TEXT_FIELD_ERROR_MSG);
            genericEditor.assertions.fieldHasNoValidationErrorsInLanguage(RICH_TEXT_FIELD, FRENCH_TAB);
            genericEditor.assertions.fieldHasNoValidationErrorsInLanguage(RICH_TEXT_FIELD, POLISH_TAB);
            genericEditor.assertions.fieldHasNoValidationErrorsInLanguage(RICH_TEXT_FIELD, HINDI_TAB);
        });

        it('GIVEN form has validation errors WHEN reset is clicked THEN validation errors are removed', function() {
            // GIVEN 
            genericEditor.actions.setTextFieldValue(TEXT_FIELD, NOT_LOCALIZED, TEXT_FIELD_INVALID_VALUE);
            genericEditor.actions.submitForm();
            genericEditor.assertions.fieldHasValidationErrors(TEXT_FIELD, NOT_LOCALIZED, 2);

            // WHEN 
            genericEditor.actions.cancelForm();

            // THEN 
            genericEditor.assertions.fieldHasNoValidationErrors(TEXT_FIELD);
        });

        it('WHEN form is submitted AND API returns a field that does not exist THEN the editor will display no validation errors', function() {
            // GIVEN 
            genericEditor.actions.setTextFieldValue(TEXT_FIELD, NOT_LOCALIZED, TEXT_FIELD_UNKNOWN_TYPE);

            // WHEN 
            genericEditor.actions.submitForm();

            // THEN 
            genericEditor.assertions.formHasNoValidationErrors();
        });
    });
});
