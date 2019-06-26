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
var path = require('path');

module.exports = (function() {

    var pageObject = {};

    pageObject.constants = {
        // Text Field
        HEADLINE_FIELD: 'headline',
        HEADLINE_INVALID_TEXT: 'I have changed to an invalid headline with two validation errors, % and lots of text',
        HEADLINE_UNKNOWN_TYPE: 'Checking unknown type',
        DESCRIPTION_FIELD: 'description',
        ID_FIELD: 'id',

        // Rich Text Field
        CONTENT_FIELD: 'content',
        CONTENT_FIELD_INVALID_TEXT: 'I have changed to an invalid content with one validation error',
        CONTENT_FIELD_INVALID_TEXT_IT: 'Ho cambiato ad un contenuto non valido con un errore di validazione',
        CONTENT_FIELD_ERROR_MSG: 'This field is required and must to be between 1 and 255 characters long.'
    };
    pageObject.elements = {};

    pageObject.actions = {
        configureTest: function(testConfig) {
            browser.executeScript('window.sessionStorage.setItem("TEST_CONFIGS", arguments[0])', JSON.stringify(testConfig));
        },
        bootstrap: function(testFolder, done) {
            try {
                var index = path.resolve('').length;
                var pathToTest = path.resolve(testFolder, 'genericEditorTest.html').substring(index);

                browser.get(pathToTest);
                done();
            } catch (exception) {
                console.error('genericEditorPageObject - Cannot load the test with folder ', testFolder);
            }
        }
    };


    return pageObject;
})();
