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
    alertMsg: function() {
        var alertMessageElement = element(by.binding('alert.message'));
        browser.wait(EC.presenceOf(alertMessageElement), 5000, 'Alert message not rendered in DOM');
        browser.wait(EC.visibilityOf(alertMessageElement), 5000, 'Alert message not visible');
        return alertMessageElement;
    },

    waitForAlertsToClear: function() {
        var alertMessageElement = element(by.binding('alert.message'));
        browser.wait(EC.stalenessOf(alertMessageElement), 5000, 'Alert did not disappear after 5000ms');
    },

    closeAlert: function() {
        element(by.css('.alert .close')).click();
    }
};
