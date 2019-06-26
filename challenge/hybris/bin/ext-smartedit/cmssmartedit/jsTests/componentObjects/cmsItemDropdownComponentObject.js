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
var genericEditorCommons;
if (typeof require !== 'undefined') {
    genericEditorCommons = require('./commonGenericEditorComponentObject');
}

module.exports = (function() {

    var componentObject = {};

    componentObject.constants = {
        DROPDOWN_TYPE: {
            SINGLE_SELECT: 'single select',
            MULTI_SELECT: 'multi select'
        }
    };

    componentObject.elements = {
        // -- Common -- 
        getDropdownByQualifier: function(fieldQualifier) {
            return genericEditorCommons.elements.getFieldByQualifier(fieldQualifier).element(by.css('cms-item-dropdown'));
        },

        // -- Single Select Dropdown -- 
        getDropdownEmptyLabel: function(fieldQualifier) {
            return this.getDropdownByQualifier(fieldQualifier).element(by.css('.select2-default'));
        },
        getSingleSelectOpenDropdownButton: function(fieldQualifier) {
            return this.getDropdownByQualifier(fieldQualifier).element(by.css('.ui-select-toggle'));
        },
        getDropdownOptionById: function(fieldQualifier, itemId) {
            return this.getDropdownByQualifier(fieldQualifier).element(by.css('.ui-select-choices-group nested-component [data-component-id="' + itemId + '"]'));
        },
        getSingleSelectSelectedItemById: function(fieldQualifier, itemId) {
            return this.getDropdownByQualifier(fieldQualifier).element(by.css('.ui-select-match .y-select-item-printer nested-component [data-component-id="' + itemId + '"]'));
        },
        getSingleSelectSelectedItemImageById: function(fieldQualifier, itemId) {
            return this.getSingleSelectSelectedItemById(fieldQualifier, itemId).element(by.css('img'));
        },
        getSearchBox: function(fieldQualifier, selectType) {
            if (selectType === componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT) {
                return this.getDropdownByQualifier(fieldQualifier).element(by.css('.select2-search input'));
            } else {
                return this.getDropdownByQualifier(fieldQualifier).element(by.css('.select2-search-field input'));
            }
        },
        getCreateNewComponentButton: function(fieldQualifier) {
            return this.getDropdownByQualifier(fieldQualifier).element(by.css('.se-actionable-search-item__create-btn'));
        },
        getComponentSubType: function(componentType) {
            return element(by.cssContainingText('sub-type-selector button.cms-sub-type-selector_button', componentType));
        },

        // -- Multiple Select Dropdown -- 
        getDropdownLabel: function(fieldQualifier) {
            return this.getDropdownByQualifier(fieldQualifier).element(by.css('.se-generic-editor-dropdown'));
        },
        getSelectedItemsList: function(fieldQualifier) {
            return this.getDropdownByQualifier(fieldQualifier).all(by.css('.select2-choices li.ui-select-match-item nested-component'));
        },
        getMultiSelectSelectedItem: function(fieldQualifier, componentId) {
            return this.getDropdownByQualifier(fieldQualifier).element(by.css('.select2-choices li.ui-select-match-item nested-component [data-component-id="' + componentId + '"]'));
        },
        getSelectedItemRemoveButton: function(fieldQualifier, componentId) {
            // Note: The xPath expression is complicated because it's necessary to check which link has a 'nested-component' with the right id. 
            var xPathExpression = '//li[contains(@class, "ui-select-match-item")][//nested-component/*[@data-component-id="' + componentId + '"]]/a';
            return this.getDropdownByQualifier(fieldQualifier).element(by.xpath(xPathExpression));
        },
    };

    componentObject.actions = {
        // -- Common -- 

        // -- Single Select Dropdown -- 


        openDropdown: function(fieldQualifier, selectType) {
            if (selectType === componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT) {
                return browser.click(componentObject.elements.getSingleSelectOpenDropdownButton(fieldQualifier));
            } else if (selectType === componentObject.constants.DROPDOWN_TYPE.MULTI_SELECT) {
                return browser.click(componentObject.elements.getDropdownLabel(fieldQualifier));
            }
        },
        searchComponentInDropdown: function(fieldQualifier, selectType, componentNameToSearch) {
            return this.openDropdown(fieldQualifier, selectType).then(function() {
                return componentObject.elements.getSearchBox(fieldQualifier, selectType).clear().sendKeys(componentNameToSearch);
            });
        },
        clickDropdownOption: function(fieldQualifier, itemId) {
            return browser.click(componentObject.elements.getDropdownOptionById(fieldQualifier, itemId));
        },
        selectItemInDropdown: function(fieldQualifier, selectType, itemId) {
            return this.openDropdown(fieldQualifier, selectType).then(function() {
                return this.clickDropdownOption(fieldQualifier, itemId);
            }.bind(this));
        },
        clickSelectedItem: function(fieldQualifier, selectType, itemId) {
            if (selectType === componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT) {
                return browser.click(componentObject.elements.getSingleSelectSelectedItemImageById(fieldQualifier, itemId));
            } else {
                return browser.click(componentObject.elements.getMultiSelectSelectedItem(fieldQualifier, itemId));
            }
        },
        clickCreateNewComponentButton: function(fieldQualifier) {
            return browser.click(componentObject.elements.getCreateNewComponentButton(fieldQualifier));
        },
        selectComponentType: function(componentType) {
            return browser.click(componentObject.elements.getComponentSubType(componentType));
        },
        openNewNestedComponentOfTypeFromDropdown: function(fieldQualifier, newComponentName, componentType) {
            var selectType = componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT;
            return this.searchComponentInDropdown(fieldQualifier, selectType, newComponentName).then(function() {
                return this.clickCreateNewComponentButton(fieldQualifier).then(function() {
                    return this.selectComponentType(componentType);
                }.bind(this));
            }.bind(this));
        },
        openNewNestedComponentFromDropdown: function(fieldQualifier, newComponentName) {
            var selectType = componentObject.constants.DROPDOWN_TYPE.MULTI_SELECT;
            return this.searchComponentInDropdown(fieldQualifier, selectType, newComponentName).then(function() {
                return this.clickCreateNewComponentButton(fieldQualifier);
            }.bind(this));
        },

        // -- Multiple Select Dropdown -- 
        removeSelectedItemInDropdown: function(fieldQualifier, componentId) {
            return browser.click(componentObject.elements.getSelectedItemRemoveButton(fieldQualifier, componentId));
        }
    };

    componentObject.assertions = {
        // -- Common -- 
        isEmpty: function(fieldQualifier, selectType) {
            selectType = (selectType) ? selectType : componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT;

            if (selectType === componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT) {
                expect(browser.isPresent(componentObject.elements.getDropdownEmptyLabel(fieldQualifier)))
                    .toBe(true, 'Expected dropdown ' + fieldQualifier + ' to be empty');
            } else {
                expect(componentObject.elements.getSelectedItemsList(fieldQualifier).count())
                    .toBe(0, 'Expected dropdown ' + fieldQualifier + ' to be empty');
            }
        },
        isNotEmpty: function(fieldQualifier, selectType) {
            selectType = (selectType) ? selectType : componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT;

            if (selectType === componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT) {
                expect(browser.isAbsent(componentObject.elements.getDropdownEmptyLabel(fieldQualifier)))
                    .toBe(true, 'Expected dropdown ' + fieldQualifier + ' not to be empty');
            } else {
                expect(componentObject.elements.getSelectedItemsList(fieldQualifier).count())
                    .not.toBe(0, 'Expected dropdown ' + fieldQualifier + ' not to be empty');
            }
        },
        itemIsSelected: function(fieldQualifier, selectType, itemId) {
            if (selectType === componentObject.constants.DROPDOWN_TYPE.SINGLE_SELECT) {
                expect(componentObject.elements.getSingleSelectSelectedItemById(fieldQualifier, itemId).isPresent()).toBe(true,
                    'Expected item ' + itemId + ' to be selected');
            }
        },

        // -- Single Select Dropdown -- 

        // -- Multiple Select Dropdown -- 

    };

    componentObject.utils = {};

    return componentObject;

}());
