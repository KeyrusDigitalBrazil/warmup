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
describe("GenericEditor - Localized Elements ", function() {

    var page = require("./../../componentObjects/genericEditorPageObject");
    var genericEditor = require("./../../componentObjects/genericEditorComponentObject");

    // Constants
    var RICH_TEXT_FIELD = page.constants.CONTENT_FIELD;

    var ENGLISH_TAB = 'en';
    var ITALIAN_TAB = 'it';
    var FRENCH_TAB = 'fr';
    var POLISH_TAB = 'pl';
    var HINDI_TAB = 'hi';

    describe('GIVEN user has read access only to some languages', function() {

        beforeEach(function(done) {
            page.actions.configureTest({
                readableLanguages: ['en', 'it'],
                multipleTabs: false
            });
            page.actions.bootstrap(__dirname, done);
        });

        it('WHEN a localized component is displayed THEN it only displays the languages the user has access to', function() {

            // WHEN / THEN
            genericEditor.assertions.tabLanguageIsDisplayed(RICH_TEXT_FIELD, ENGLISH_TAB);
            genericEditor.assertions.tabLanguageIsDisplayed(RICH_TEXT_FIELD, ITALIAN_TAB);

            genericEditor.assertions.tabLanguageIsNotDisplayed(RICH_TEXT_FIELD, FRENCH_TAB);
            genericEditor.assertions.tabLanguageIsNotDisplayed(RICH_TEXT_FIELD, POLISH_TAB);
            genericEditor.assertions.tabLanguageIsNotDisplayed(RICH_TEXT_FIELD, HINDI_TAB);

            genericEditor.assertions.richTextFieldIsEnabled(RICH_TEXT_FIELD, ENGLISH_TAB);
            genericEditor.assertions.richTextFieldIsEnabled(RICH_TEXT_FIELD, ITALIAN_TAB);
        });
    });

    describe('GIVEN user has write access only to some languages', function() {

        beforeEach(function(done) {
            page.actions.configureTest({
                writeableLanguages: ['en'],
                multipleTabs: false
            });
            page.actions.bootstrap(__dirname, done);
        });

        it('WHEN a localized component is displayed THEN it displays all languages AND only the ones the user can write are enabled', function() {

            // WHEN / THEN
            genericEditor.assertions.tabLanguageIsDisplayed(RICH_TEXT_FIELD, ENGLISH_TAB);
            genericEditor.assertions.tabLanguageIsDisplayed(RICH_TEXT_FIELD, ITALIAN_TAB);
            genericEditor.assertions.tabLanguageIsDisplayed(RICH_TEXT_FIELD, FRENCH_TAB);
            genericEditor.assertions.tabLanguageIsDisplayed(RICH_TEXT_FIELD, POLISH_TAB);
            genericEditor.assertions.tabLanguageIsDisplayed(RICH_TEXT_FIELD, HINDI_TAB);

            genericEditor.assertions.richTextFieldIsEnabled(RICH_TEXT_FIELD, ENGLISH_TAB);
            genericEditor.assertions.richTextFieldIsDisabled(RICH_TEXT_FIELD, ITALIAN_TAB);
            genericEditor.assertions.richTextFieldIsDisabled(RICH_TEXT_FIELD, ITALIAN_TAB);
            genericEditor.assertions.richTextFieldIsDisabled(RICH_TEXT_FIELD, FRENCH_TAB);
            genericEditor.assertions.richTextFieldIsDisabled(RICH_TEXT_FIELD, POLISH_TAB);
            genericEditor.assertions.richTextFieldIsDisabled(RICH_TEXT_FIELD, HINDI_TAB);
        });

    });

});
