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
var page = require("../../utils/components/Page.js");

module.exports = (function() {

    var dropdownObject = {};

    dropdownObject.constants = {
        PAGE_URL: 'test/e2e/genericEditor/componentWithDropdown/genericEditorTest.html'
    };

    dropdownObject.elements = {

        getDropdownSelector: function(dropdown) {
            return element(by.id(dropdown + '-selector'));
        },

        getDropdowOptionsSelector: function(dropdownId) {
            return '#' + dropdownId + ' .ui-select-choices-row-inner';
        },

        getDropdownValues: function(dropdowns) {

            return protractor.promise.all(dropdowns.map(function(dropdown) {
                return dropdownObject.elements.getDropdownSelector(dropdown).getText();
            }));
        },

        getMultiDropdownSelector: function(dropdown) {
            return element(by.css("#" + dropdown + '-selector .select2-search-field .ui-select-search'));
        },

        getMultiDropdownValue: function(dropdown) {
            return element.all(by.xpath("//div[@id='" + dropdown + "-selector']/ul/span/li")).map(function(element) {
                return element.getText().then(function(text) {
                    return text;
                });
            });
        },

        getListOfOptions: function(dropdownId) {
            return element.all(by.css(dropdownObject.elements.getDropdowOptionsSelector(dropdownId))).map(function(element) {
                browser.waitForPresence(element);
                return element.getText();
            });
        },

        getItemPrinter: function(dropdown) {
            return element(by.xpath("//item-printer[@id='" + dropdown + "-selected']/div/span"));
        }

    };

    dropdownObject.actions = {

        clickDropdown: function(dropdown) {
            return browser.click(element(by.cssContainingText('label', 'Dropdown A'))).then(function() {
                return dropdownObject.actions.selectDropdown(dropdown);
            });
        },

        selectOption: function(dropdownId, optionLabel) {
            return browser.click(by.cssContainingText(dropdownObject.elements.getDropdowOptionsSelector(dropdownId), optionLabel));
        },

        selectDropdown: function(dropdown) {
            return browser.click(dropdownObject.elements.getDropdownSelector(dropdown));
        },

        openAndBeReady: function() {
            return page.actions.get(dropdownObject.constants.PAGE_URL).then(function() {
                dropdownObject._helper.isReady();
            });
        },

        clickMultiSelectDropdown: function(dropdown) {
            return browser.click(dropdownObject.elements.getMultiDropdownSelector(dropdown));
        }
    };

    dropdownObject.assertions = {

        assertListOfOptions: function(dropdownId, expectedOptions) {
            var dropdownOptionCssSelector = '#' + dropdownId + ' .ui-select-choices-row-inner';
            browser.wait(function() {
                return element.all(by.css(dropdownOptionCssSelector)).map(function(element) {
                    return element.getText().then(function(text) {
                        return text;
                    }, function() {
                        return '';
                    });
                }).then(function(actualOptions) {
                    return actualOptions.join(',') === expectedOptions.join(',');
                });
            }, 5000, 'Expected dropdown options for ' + dropdownId + ' to be ' + expectedOptions);
        },

        searchAndAssertInDropdown: function(dropdownId, searchTerm, expectedOptions) {
            element(by.css('#' + dropdownId + ' input')).clear().sendKeys(searchTerm);
            dropdownObject.assertions.assertListOfOptions(dropdownId, expectedOptions);
        },

    };

    dropdownObject._helper = {

        isReady: function() {
            return browser.wait(EC.visibilityOf(dropdownObject.elements.getItemPrinter('dropdownA')), 5000, 'cannot select dropdown');
        }
    };

    return dropdownObject;

})();
