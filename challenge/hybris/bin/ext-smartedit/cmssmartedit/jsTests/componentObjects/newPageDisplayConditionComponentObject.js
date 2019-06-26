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

    componentObject.elements = {
        getPrimaryConditionOption: function() {
            return element(by.cssContainingText('.ui-select-choices-row', 'Primary'));
        },
        getVariationConditionOption: function() {
            return element(by.cssContainingText('.ui-select-choices-row', 'Variation'));
        },
        getSelectedConditionOption: function() {
            return componentObject.utils.getSelectElementById('page-condition-selector-id');
        },
        getConditionDropdownToggle: function() {
            return element(by.css('#page-condition-selector-id .ui-select-toggle'));
        },
        getAssociatedPrimaryPage: function() {
            return componentObject.utils.getSelectElementById('page-condition-primary-selector-id');
        }
    };

    componentObject.actions = {
        openConditionDropdown: function() {
            return browser.click(componentObject.elements.getConditionDropdownToggle());
        },
        selectVariationCondition: function() {
            return this.openConditionDropdown().then(function() {
                return browser.click(componentObject.elements.getVariationConditionOption());
            });
        },
        selectPrimaryCondition: function() {
            return componentObject.actions.openConditionDropdown().then(function() {
                return browser.click(componentObject.elements.getPrimaryConditionOption());
            });
        }
    };

    componentObject.assertions = {
        variationConditionOptionIsDisplayed: function() {
            var message = "Expected Variation Display Condition to be in the list.";
            expect(browser.isPresent(componentObject.elements.getVariationConditionOption())).toBe(true, message);
        },
        variationConditionOptionIsNotDisplayed: function() {
            var message = "Expected Variation Display Condition not to be in the list.";
            expect(browser.isAbsent(componentObject.elements.getVariationConditionOption())).toBe(true, message);
        },
        primaryConditionOptionIsDisplayed: function() {
            var message = "Expected Primary Display Condition to be in the list.";
            expect(browser.isPresent(componentObject.elements.getPrimaryConditionOption())).toBe(true, message);
        }
    };

    componentObject.utils = {
        getSelectElementById: function(id) {
            return element(by.xpath("//*[@id='" + id + "']//*[contains(@class, 'ySEPageRestr-picker--select__match')]"));
        }
    };

    return componentObject;
})();
