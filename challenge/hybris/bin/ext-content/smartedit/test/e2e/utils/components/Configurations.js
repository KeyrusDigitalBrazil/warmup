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
module.exports = {
    openConfigurationEditor: function() {
        return browser.click(this.getConfigurationCenterButton());
    },

    getConfigurationsAsList: function() {
        browser.wait(function() {
            return element.all(by.css('#editConfigurationsBody .ySECfgEntity')).count().then(function(count) {
                return count > 0;
            });
        }, 5000, "could not find config list");
        return element.all(by.css('#editConfigurationsBody .ySECfgEntity'));
    },

    waitForModal: function() {
        return browser.wait(function() {
            return element(by.css('div.modal-content'));
        }, 5000, "could not find modal content div");
    },

    setConfigurationValue: function(rowIndex, value) {
        this.getConfigurationsAsList()
            .get(rowIndex)
            .element(by.model('entry.value'))
            .clear()
            .sendKeys(value);
    },

    setConfigurationKeyAndValue: function(rowIndex, key, value) {
        this.getConfigurationsAsList()
            .get(rowIndex)
            .element(by.model('entry.key'))
            .clear()
            .sendKeys(key);

        this.setConfigurationValue(rowIndex, value);
    },

    clickSave: function() {
        browser.click(element(by.cssContainingText('.modal-dialog .modal-footer button', 'Save')));
    },

    clickCancel: function() {
        return browser.click(element(by.css('.modal-dialog .modal-footer button#cancel')));
    },

    getCancelButton: function() {
        return element(by.cssContainingText('.modal-dialog .modal-footer button', 'Cancel'));
    },

    getSaveButton: function() {
        return element(by.cssContainingText('.modal-dialog .modal-footer button', 'Save'));
    },

    clickConfirmOk: function() {
        browser.click(by.id('confirmOk'), 'could not find confirmOk button');
    },

    getConfigurationTitle: function() {
        return element(by.id('smartedit-modal-title-se.modal.administration.configuration.edit.title'));
    },

    clickAdd: function() {
        return browser.click(by.css('.y-add-btn'), "could not find add button");
    },

    deleteConfiguration: function(rowIndex) {
        browser.wait(protractor.ExpectedConditions.elementToBeClickable(this.getConfigurationsAsList()
            .get(rowIndex)
            .element(by.css("button[id*=removeButton]"))), 5000, "could not find removeButton");
        this.getConfigurationsAsList()
            .get(rowIndex)
            .element(by.css("button[id*=removeButton]"))
            .click();
    },

    getEntry: function(array, rows, index) {
        var deferred = protractor.promise.defer();
        var that = this;
        rows[index].element(by.model('entry.key')).getAttribute('value').then(function(key) {
            rows[index].element(by.model('entry.value')).getAttribute('value').then(function(value) {
                array.push({
                    key: key,
                    value: value
                });
                if (index < rows.length - 1) {
                    that.getEntry(array, rows, index + 1).then(function(array) {
                        deferred.fulfill(array);
                    });
                } else {
                    deferred.fulfill(array);
                }
            });
        });
        return deferred.promise;
    },

    getConfigurationCenterButton: function() {
        return element(by.id('generalConfiguration'));
    },

    getConfigurations: function() {
        var deferred = protractor.promise.defer();
        var that = this;
        element.all(by.css('#editConfigurationsBody .ySECfgEntity')).then(function(rows) {
            that.getEntry([], rows, 0).then(function(array) {
                deferred.fulfill(array);
            });
        });
        return deferred.promise;
    },

    waitForConfigurationModal: function(size, message) {
        return this.waitForModal().then(function() {
            return this.waitForConfigurationsToPopulate(size, message);
        }.bind(this));
    },

    waitForConfigurationsToPopulate: function(size, message) {
        message = message || "could not find initial list of entries";
        return browser.wait(function() {
            return element.all(by.css('#editConfigurationsBody .ySECfgEntity')).count().then(function(count) {
                return count === size;
            });
        }, 5000, message);
    },

    waitForErrorForKey: function(key) {
        return browser.wait(protractor.ExpectedConditions.visibilityOf(element(by.css('#' + key + '_error_0'))), 5000);
    },

    getErrorForKey: function(key) {
        return element(by.id(key + "_error_0"));
    },

    getAbsoluteUrlCheckbox: function() {
        return element(by.id('previewTicketURI_absoluteUrl_check_0'));
    },

    getAbsoluteUrlText: function() {
        return element(by.id('previewTicketURI_absoluteUrl_msg_0'));
    },
};
