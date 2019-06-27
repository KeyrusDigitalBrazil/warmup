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
var page = {};
page.elements = {
    getTriggerError404JsonButton: function() {
        return by.id('trigger-error-404-json');
    },
    getTriggerError400JsonButton: function() {
        return by.id('trigger-error-400-json');
    },
    getTriggerError404HtmlButton: function() {
        return by.id('trigger-error-404-html');
    },
    getTriggerError501JsonButton: function() {
        return by.id('trigger-error-501-json');
    },
    getTriggerError503: function() {
        return by.id('trigger-error-503');
    },
    getTriggerError502: function() {
        return by.id('trigger-error-502');
    },
    getGraceFulDegradationStatus: function() {
        return element(by.id('gd-status'));
    },
};
page.actions = {
    navigate: function() {
        return browser.get('test/e2e/a_httpErrorInterceptorService/index.html');
    },
    triggerError404Json: function() {
        return browser.click(page.elements.getTriggerError404JsonButton());
    },
    triggerError400Json: function() {
        return browser.click(page.elements.getTriggerError400JsonButton());
    },
    triggerError404Html: function() {
        return browser.click(page.elements.getTriggerError404HtmlButton());
    },
    triggerError501Json: function() {
        return browser.click(page.elements.getTriggerError501JsonButton());
    },
    triggerError503: function() {
        return browser.click(page.elements.getTriggerError503());
    },
    triggerError502: function() {
        return browser.click(page.elements.getTriggerError502());
    },
};
page.assertions = {

};

module.exports = page;
