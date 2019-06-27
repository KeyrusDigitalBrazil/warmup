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
describe("GenericEditor MultiTabs -", function() {

    var page = require("./../../componentObjects/genericEditorPageObject");
    var genericEditor = require("./../../componentObjects/genericEditorComponentObject");

    var DEFAULT_TAB_KEY = "GENERICEDITOR.TAB.DEFAULT.TITLE".toLowerCase();
    var ADMIN_TAB_KEY = "GENERICEDITOR.TAB.ADMINISTRATION.TITLE".toLowerCase();

    // Fields
    var HEADLINE_FIELD_NAME = page.constants.HEADLINE_FIELD;
    var DESCRIPTION_FIELD_NAME = page.constants.DESCRIPTION_FIELD;
    var ID_FIELD_NAME = page.constants.ID_FIELD;

    // Constants

    var TEXT_FIELD_INVALID_VALUE = page.constants.HEADLINE_INVALID_TEXT;
    //var TEXT_FIELD_UNKNOWN_TYPE = page.constants.HEADLINE_UNKNOWN_TYPE;

    // var RICH_TEXT_FIELD = page.constants.CONTENT_FIELD;
    // var RICH_TEXT_FIELD_INVALID_VALUE = page.constants.CONTENT_FIELD_INVALID_TEXT;
    // var RICH_TEXT_FIELD_INVALID_VALUE_IT = page.constants.CONTENT_FIELD_INVALID_TEXT_IT;
    // var RICH_TEXT_FIELD_ERROR_MSG = page.constants.CONTENT_FIELD_ERROR_MSG;

    var NOT_LOCALIZED = null;
    // var ENGLISH_TAB = 'en';
    // var ITALIAN_TAB = 'it';
    // var FRENCH_TAB = 'fr';
    // var POLISH_TAB = 'pl';
    // var HINDI_TAB = 'hi';

    beforeEach(function(done) {
        page.actions.configureTest({
            multipleTabs: true
        });
        page.actions.bootstrap(__dirname, done);
    });

    describe('basic', function() {
        it('GIVEN tabs have been configured for the editor WHEN the editor opens THEN tabs are displayed and sorted in the right order', function() {
            // THEN 
            // genericEditor.assertions.editorHasTabsDisplayed([
            //     DEFAULT_TAB_ID, 
            //     ADMIN_TAB_ID, 
            //     VISIBILITY_TAB_ID
            // ]);
        });

        it('GIVEN tabs have been configured for the editor WHEN the editor opens THEN fields are organized per tab as per the configuration', function() {
            // THEN 
            // genericEditor.assertions.fieldIsInTab(HEADLINE, DEFAULT_TAB_ID); 
            // genericEditor.assertions.fieldIsInTab(CONTENT, DEFAULT_TAB_ID); 

            // TODO: Add others
        });
    });

    describe('form save', function() {
        it('WHEN the editor is opened THEN it will display cancel button AND not display submit button', function() {
            // THEN 
            genericEditor.assertions.cancelButtonIsNotDisplayed();
            genericEditor.assertions.submitButtonIsNotDisplayed();
        });

        it('WHEN the editor is opened THEN it will display cancel and submit buttons when an attribute is modified in another tab', function() {
            // // GIVEN 
            // genericEditor.actions.selectEditorTab(ADMIN_TAB_ID); 

            // // WHEN 
            // genericEditor.actions.setTextFieldValue(TODO, NOT_LOCALIZED, 'some value');

            // // THEN 
            // genericEditor.assertions.cancelButtonIsDisplayed(); 
            // genericEditor.assertions.submitButtonIsDisplayed(); 
        });

        it('GIVEN field has invalid information in another tab WHEN the form is saved THEN it will display validation errors in the correct tab', function() {
            // GIVEN
            genericEditor.actions.selectTab(DEFAULT_TAB_KEY);
            genericEditor.actions.setTextFieldValue(HEADLINE_FIELD_NAME, NOT_LOCALIZED, TEXT_FIELD_INVALID_VALUE);

            // WHEN             
            genericEditor.actions.selectTab(ADMIN_TAB_KEY);
            genericEditor.actions.submitForm();

            // THEN 
            genericEditor.assertions.tabIsInError(DEFAULT_TAB_KEY);
            genericEditor.actions.selectTab(DEFAULT_TAB_KEY);
            genericEditor.assertions.fieldHasValidationErrors(HEADLINE_FIELD_NAME, NOT_LOCALIZED, 2);
        });

        it('GIVEN form has validation errors in multiple tabs WHEN reset is clicked THEN validation errors are removed in all tabs', function() {
            // GIVEN 
            genericEditor.actions.setTextFieldValue(HEADLINE_FIELD_NAME, NOT_LOCALIZED, TEXT_FIELD_INVALID_VALUE);
            genericEditor.actions.submitForm();
            genericEditor.assertions.fieldHasValidationErrors(HEADLINE_FIELD_NAME, NOT_LOCALIZED, 2);

            // WHEN 
            genericEditor.actions.cancelForm();

            // THEN 
            genericEditor.assertions.fieldHasNoValidationErrors(HEADLINE_FIELD_NAME);
        });

        it("when errors is about a field on another tab than the current one, the target tab lights up and the field is in error", function() {
            // GIVEN
            genericEditor.actions.selectTab(ADMIN_TAB_KEY);
            genericEditor.actions.setTextFieldValue(ID_FIELD_NAME, NOT_LOCALIZED, "some wrong content X");
            genericEditor.actions.selectTab(DEFAULT_TAB_KEY);

            // WHEN
            genericEditor.actions.submitForm();

            // THEN
            genericEditor.assertions.tabIsInError(ADMIN_TAB_KEY);
            genericEditor.actions.selectTab(ADMIN_TAB_KEY);
            var elements = genericEditor.elements.getValidationErrors('id');
            expect(elements.count()).toBe(1);
            expect(elements.get(0).getText()).toBe("This field cannot contain an X");
        });
    });

    describe('field and tab validation', function() {
        it("GIVEN non empty required pristine field AND no validation errors AND tab is not highlighted WHEN value is removed (field is empty and not pristine now) THEN field has validation error AND tab is highlighted", function() {
            // GIVEN
            genericEditor.assertions.fieldHasNoValidationErrors(DESCRIPTION_FIELD_NAME);
            genericEditor.assertions.tabIsNotInError(DEFAULT_TAB_KEY);

            // WHEN
            genericEditor.actions.setTextFieldValue(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, "");

            // THEN
            genericEditor.assertions.fieldHasValidationErrors(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, 1);
            genericEditor.assertions.tabIsInError(DEFAULT_TAB_KEY);
        });

        it("GIVEN required non pristine field is empty AND tab is highlighted AND validation error is displayed WHEN submit is clicked AND field error returned from backend AND required non pristine field is populated THEN tab is still highlighted", function() {
            // GIVEN
            genericEditor.actions.setTextFieldValue(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, "");
            genericEditor.assertions.fieldHasValidationErrors(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, 1);
            genericEditor.assertions.tabIsInError(DEFAULT_TAB_KEY);

            // WHEN   
            genericEditor.actions.setTextFieldValue(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, "error description");
            genericEditor.actions.submitForm();
            genericEditor.actions.setTextFieldValue(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, "some data");

            // THEN
            genericEditor.assertions.tabIsInError(DEFAULT_TAB_KEY);
        });

        it('GIVEN required non pristine fields are empty on default tab and admin tab THEN both tabs are highlighted AND both fields have validation errors', function() {
            // GIVEN
            genericEditor.actions.selectTab(ADMIN_TAB_KEY);
            genericEditor.actions.setTextFieldValue(ID_FIELD_NAME, NOT_LOCALIZED, "");
            genericEditor.actions.selectTab(DEFAULT_TAB_KEY);
            genericEditor.actions.setTextFieldValue(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, "");

            // THEN
            genericEditor.assertions.tabIsInError(DEFAULT_TAB_KEY);
            genericEditor.assertions.tabIsInError(ADMIN_TAB_KEY);
            genericEditor.actions.selectTab(DEFAULT_TAB_KEY);
            genericEditor.assertions.fieldHasValidationErrors(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, 1);
            genericEditor.actions.selectTab(ADMIN_TAB_KEY);
            genericEditor.assertions.fieldHasValidationErrors(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, 1);
        });

        it("GIVEN required non pristine field is empty AND validation error is displayed AND tab is highlighted WHEN field is populated THEN validation error is not displayed AND tab is not highlighted", function() {
            // GIVEN
            genericEditor.actions.selectTab(DEFAULT_TAB_KEY);
            genericEditor.actions.setTextFieldValue(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, "");
            genericEditor.assertions.tabIsInError(DEFAULT_TAB_KEY);
            genericEditor.assertions.fieldHasValidationErrors(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, 1);

            // WHEN
            genericEditor.actions.setTextFieldValue(DESCRIPTION_FIELD_NAME, NOT_LOCALIZED, "some data");

            // THEN
            genericEditor.assertions.tabIsNotInError(DEFAULT_TAB_KEY);
            genericEditor.assertions.fieldHasNoValidationErrors(DESCRIPTION_FIELD_NAME);
        });
    });
});
