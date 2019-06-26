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
    var ySelect = function(id) {

        this.id = id;
        this.elem = element(by.id(id));

        // --------------------------------------------------------------------------------------------------
        // Constant
        // --------------------------------------------------------------------------------------------------
        ySelect.constants = {
            VALIDATION_MESSAGE_TYPE: {
                VALIDATION_ERROR: 'has-error',
                WARNING: 'has-warning'
            }
        };

        // --------------------------------------------------------------------------------------------------
        // Locators
        // --------------------------------------------------------------------------------------------------
        this.locators = {
            selectorContainer: function() {
                return by.id(this.id + "-selector");
            }.bind(this)
        };

        // --------------------------------------------------------------------------------------------------
        // Elements
        // --------------------------------------------------------------------------------------------------
        this.elements = {
            getSelectorContainer: function() {
                return this.elem
                    .element(this.locators.selectorContainer());
            }.bind(this),
            getSimpleDropdownToggle: function() {
                return this.elem
                    .element(by.css(".select2-choice"));
            }.bind(this),
            getMultiDropdownToggle: function() {
                return this.elem
                    .element(by.css('.ui-select-search'));
            }.bind(this),
            getAllOptions: function() {
                return this.elem
                    .all(by.css(".ui-select-choices-row"));
            }.bind(this),
            getOptionByText: function(text) {
                return this.elem
                    .element(by.xpath("//*[contains(@class, 'ui-select-choices-row')]//span[normalize-space(text())='" + text + "']"));
            }.bind(this),
            getAllMultiSelections: function() {
                return this.elem
                    .all(by.css('.select2-search-choice > span'));
            }.bind(this)
        };

        // --------------------------------------------------------------------------------------------------
        // Actions
        // --------------------------------------------------------------------------------------------------
        this.actions = {
            toggleSimpleSelector: function() {
                return browser.click(this.elements.getSimpleDropdownToggle());
            }.bind(this),
            toggleMultiSelector: function() {
                return browser.click(this.elements.getMultiDropdownToggle());
            }.bind(this),
            selectOptionByText: function(text) {
                return browser.click(this.elements.getOptionByText(text));
            }.bind(this)
        };

        // --------------------------------------------------------------------------------------------------
        // Assertions
        // --------------------------------------------------------------------------------------------------
        this.assertions = {
            assertSelectorHasValidationType: function(validationMessageType) {
                expect(this.locators.selectorContainer()).toContainClass(validationMessageType);
            }.bind(this),
            assertSelectorHasNoValidationType: function() {
                this.elements.getSelectorContainer().getAttribute('class').then(function(classNames) {
                    expect(!classNames.match(new RegExp('(' +
                        ySelect.constants.VALIDATION_MESSAGE_TYPE.VALIDATION_ERROR + '|' +
                        ySelect.constants.VALIDATION_MESSAGE_TYPE.WARNING + ')', 'g')), 'Selector has a validation state').toBeTruthy();
                });
            }.bind(this),
            assertSelectorHasOptionsEqualTo: function(expectedOptions) {
                this.elements.getAllOptions().then(function(elements) {
                    assertWebElementsHasSameOptions(elements, expectedOptions);
                });
            }.bind(this),
            assertMultiSelectorHasSelectedOptionsEqualTo: function(expectedOptions) {
                this.elements.getAllMultiSelections().then(function(elements) {
                    assertWebElementsHasSameOptions(elements, expectedOptions);
                });
            }.bind(this)
        };

        function assertWebElementsHasSameOptions(elements, expectedOptions) {
            var options$ = elements.map(function(element) {
                return element.getText();
            });
            protractor.promise.all(options$).then(function(options) {
                expect(options).toEqual(expectedOptions);
            });
        }

    };

    return ySelect;
})();
