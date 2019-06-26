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
module.exports = {
    // Elements
    listItemByText: function(text) {
        browser.wait(EC.presenceOf(element(by.cssContainingText('.page-type-step-template__item', text))), 5000,
            'List item with text ' + text + ' was not found after 5000ms');
        return element(by.cssContainingText('.page-type-step-template__item', text));
    },
    field: function(fieldId) {
        return element(by.id(fieldId));
    },
    localizedField: function(fieldId, language) {
        return element(by.css('[data-tab-id=' + language + '] #' + fieldId));
    },

    // Actions
    selectItem: function(itemTitle) {
        return browser.click(this.listItem(itemTitle));
    },
    selectItemByIndex: function(itemIndex) {
        return browser.click(this.listItemByIndex(itemIndex));
    },
    selectItemByText: function(itemText) {
        return browser.click(this.listItemByText(itemText));
    },
    enterFieldData: function(fieldId, value) {
        return browser.sendKeys(this.field(fieldId), value);
    },
    enterLocalizedFieldData: function(fieldId, language, value) {
        return browser.sendKeys(this.localizedField(fieldId, language), value);
    }
};
