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

var genericEditorCommons;
if (typeof require !== 'undefined') {
    genericEditorCommons = require('./commonGenericEditorComponentObject');
}

module.exports = (function() {

    var componentObject = {};

    componentObject.constants = {};

    componentObject.elements = {

        getTextField: function(fieldQualifier) {
            var field = genericEditorCommons.elements.getFieldByQualifier(fieldQualifier);
            browser.waitForPresence(field);
            var input = field.element(by.css('input[type=text]'));
            browser.waitForPresence(input);
            return input;
        },
        getBooleanField: function(fieldQualifier) {
            var field = genericEditorCommons.elements.getFieldByQualifier(fieldQualifier);
            browser.waitForPresence(field);
            var input = field.element(by.css('input'));
            browser.waitForPresence(input);
            return input;
        }
    };

    componentObject.actions = {
        // -- Text Widget --
        setTextFieldValue: function(fieldQualifier, value) {
            return componentObject.elements.getTextField(fieldQualifier).clear().sendKeys(value);
        }
    };

    componentObject.assertions = {
        // -- Text Widget --
        textFieldHasRightValue: function(fieldQualifier, expectedText) {
            expect(componentObject.elements.getTextField(fieldQualifier).getAttribute('value')).toBe(expectedText,
                'Expected field ' + fieldQualifier + ' to have the following text: ' + expectedText);
        },

        // -- Boolean Widget -- 
        booleanFieldHasRightValue: function(fieldQualifier, expectedValue) {
            expect(componentObject.elements.getBooleanField(fieldQualifier).isSelected()).toBe(expectedValue,
                'Expected field ' + fieldQualifier + ' to be ' + expectedValue);
        }
    };

    componentObject.utils = {};

    return componentObject;

}());
