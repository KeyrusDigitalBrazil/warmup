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
/* jshint unused:false */
function UiSelectPageObject(element) {
    this.getSelectToggle = function() {
        return element.find('.ui-select-toggle');
    };

    this.clickSelectToggle = function() {
        this.getSelectToggle().click();
    };

    this.getSelectedElement = function() {
        return element.find('.select2-chosen');
    };

    this.getSelectElement = function(index) {
        return window.smarteditJQuery(element.find('.ui-select-choices-row')[index]);
    };

    this.clickSelectElement = function(index) {
        this.getSelectElement(index).click();
    };

    // ---assertions

    this.assertNumberElements = function(expectedElementCount) {
        expect(element.find('.ui-select-choices-row').length).toBe(expectedElementCount);
    };

    this.assertElementTextEquals = function(index, text) {
        expect(this.getSelectElement(index).text().trim()).toBe(text);
    };

    this.assertElementInList = function(itemText) {
        expect(element.find('.ui-select-choices-row:contains("' + itemText + '")').length).toBe(1);
    };

    this.assertElementNotInList = function(itemText) {
        expect(element.find('.ui-select-choices-row:contains("' + itemText + '")').length).toBe(0);
    };


}
