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
function PageObject() {
    this.DEFAULT_ALERT_WAIT = 5000;
}

PageObject.prototype.dismissAlert = function() {
    browser.wait(EC.presenceOf(element(by.css('#confirmOk'))), this.DEFAULT_ALERT_WAIT);
    element(by.css('#confirmCancel')).click();
};

PageObject.prototype.acceptAlert = function() {
    browser.wait(EC.presenceOf(element(by.css('#confirmOk'))), this.DEFAULT_ALERT_WAIT);
    element(by.css('#confirmOk')).click();
};

module.exports = PageObject;
