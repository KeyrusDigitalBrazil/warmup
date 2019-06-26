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
var navigationNodeEditor = function() {
    this.yDropDownComponent = require('../componentObjects/yDropDownComponentObject.js');
};

navigationNodeEditor.prototype = {
    getAllEntryNames: function() {
        return element.all(by.css('.nav-node-editor-entry-item .nav-node-editor-entry-item__name'));
    },
    clickMoreMenuOptionByIndex: function(entryPosition, text) {
        return this.yDropDownComponent.actions.selectLabel('.nav-node-editor-entry-item:nth-child(' + (entryPosition + 2) + ')', text);
    },
    getEntryTitle: function(entryPosition) {
        return element(by.css('.nav-node-editor-entry-item:nth-child(' + (entryPosition + 2) + ') .nav-node-editor-entry-item__text .nav-node-editor-entry-item__name')).getText();
    },
    clickItemSuperTypeDropdown: function() {
        return browser.click(element(by.id('itemSuperType')));
    },
    clickItemIdDropdown: function() {
        var el = browser.click(by.css('#itemId .ui-select-match'));
        this.waitForDropdownToBeLoaded("itemId");
        return el;
    },
    getItemIdDropdown: function(entryPosition) {
        return element(by.id('itemId-selector'));
    },
    getItemIdDropdownOptions: function() {
        return element.all(by.css('[id=\'itemId\'] ul[role=\'listbox\'] li[role=\'option\']'));
    },
    getItemIdScrollElement: function() {
        return element(by.xpath("//ul[@id='itemId-list']//y-infinite-scrolling/div"));
    },
    getAddNewEntryButton: function() {
        return element(by.id('navigation-node-editor-add-entry'));
    },
    _saveEntryButtonLocator: function() {
        return by.id('navigation-node-editor-save-entry');
    },
    getSaveEntryButton: function() {
        return element(this._saveEntryButtonLocator());
    },
    getCancelEntryButton: function() {
        return element(by.id('navigation-node-editor-cancel'));
    },
    getEntrySearchDropdown: function() {
        return element(by.xpath("//div[@id='itemId-selector']/a"));
    },
    getValidationErrorElements: function(qualifier) {
        return element(by.css('[id="' + qualifier + '"] se-generic-editor-field-messages span.se-help-block--has-error'));
    },
    waitForDropdownToBeLoaded: function(dropdown) {
        var el = element(by.xpath("//ul[@id = '" + dropdown + "-list']//y-infinite-scrolling//li"));
        browser.waitForPresence(el);
    },
    sendKeys: function(keys) {
        return browser.sendKeys(by.css("entry-search-selector input[type='Search']"), keys);
    },
    selectTypeOption: function(dropdown, type) {
        var option;
        if (dropdown === 'itemId') {
            option = element.all(by.xpath("//*[@id = '" + dropdown + "-list']//y-infinite-scrolling//*[contains(text(),'" + type + "')]")).first();
        } else {
            option = element.all(by.xpath("//*[@id = '" + dropdown + "-list']//*[contains(text(),'" + type + "')]")).first();
        }

        browser.waitUntil(function() {
            return option.click().then(function() {
                return true;
            }, function() {
                return false;
            }).then(function(clickable) {
                return clickable;
            });
        }, "Option type '" + type + "'not clickable");
    },
    selectOption: function(dropdown, index) {

        var option;
        if (dropdown === 'itemId') {
            option = element(by.xpath("//ul[@id = '" + dropdown + "-list']//y-infinite-scrolling//li[" + index + "]"));
        } else {
            option = element(by.xpath("//ul[@id = '" + dropdown + "-list']/li/ul/li[" + index + "]"));
        }

        browser.wait(function() {
            return option.click().then(function() {
                return true;
            }, function() {
                return false;
            }).then(function(clickable) {
                return clickable;
            });
        }, 5000, "Option at index '" + index + "'not clickable");
    },
    selectOptionByLabel: function(dropdown, label, isNotPaged) {

        var option;
        if (!isNotPaged) {
            option = element.all(by.xpath("//*[@id = '" + dropdown + "-list']//y-infinite-scrolling//*[contains(text(),'" + label + "')]")).first();
        } else {
            option = element.all(by.xpath("//*[@id = '" + dropdown + "-list']//*[contains(text(),'" + label + "')]")).first();
        }

        return browser.waitUntil(function() {
            return browser.click(option).then(function() {
                return true;
            }, function() {
                return false;
            }).then(function(clickable) {
                return clickable;
            });
        }, "Option containing '" + label + "'not clickable");
    },
    clickSaveButton: function() {
        return browser.click(element(by.id('save')));
    },
    clickCancelButton: function() {
        return browser.click(element(by.id('cancel')));
    },
    saveNodeEntry: function() {
        return browser.click(this._saveEntryButtonLocator());
    },
    editNodeName: function(text) {
        return browser.sendKeys(by.css('[id="name-shortstring"]'), text);
    },
    editNodeTitle: function(text) {
        return browser.sendKeys(element.all(by.css('[id="title-shortstring"]')).first(), text);
    },
    getErrorProneEntriesCount: function() {
        return element.all(by.xpath('//div[contains(@class, "nav-node-editor-entry-item")]//div[@class="nav-node-editor-entry-item__name"]/span[contains(@class, " error-input")]')).count();
    },
    assertNumberOfEntryElements: function(expectedNumber) {
        browser.driver.wait(function() {
            return this.getAllEntryNames().count().then(function(cnt) {
                return cnt === expectedNumber;
            });
        }.bind(this), 5000);
        this.getAllEntryNames().count().then(function(cnt) {
            expect(cnt).toEqual(expectedNumber);
        });
    }
};

module.exports = navigationNodeEditor;
