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
function PageObject() {
    this.DEFAULT_ALERT_WAIT = 1000;
}

PageObject.prototype.get = function(url) {
    browser.driver.manage().deleteAllCookies();
    return browser.get(url);
};
PageObject.prototype.getAndWaitForWholeApp = function(url) {
    return this.get(url).then(function() {
        return browser.waitForWholeAppToBeReady();
    });

};
PageObject.prototype.getAndWaitForLogin = function(url) {
    return this.get(url).then(function() {
        return this.clearCookies().then(function() {
            return this.waitForLoginModal();
        }.bind(this));
    }.bind(this));
};


PageObject.prototype.runWithExplicitWait = function(fn, millis) {
    browser.driver.manage().timeouts().implicitlyWait(millis);
    try {
        fn();
    } catch (err) {
        throw err;
    } finally {
        browser.driver.manage().timeouts().implicitlyWait(browser.params.implicitWait);
    }
};

PageObject.prototype.goToSecondStorefrontPage = function() {
    return browser.switchToIFrame().then(function() {
        return browser.click(element(by.id('deepLink')), 'Timed out waiting for deep link to be clickable');
    }).then(function() {
        return browser.switchToParent();
    }).then(function() {
        return browser.waitForWholeAppToBeReady();
    });
};

PageObject.prototype.dismissAlert = function() {
    browser.wait(protractor.ExpectedConditions.alertIsPresent(), this.DEFAULT_ALERT_WAIT);
    browser.switchTo().alert().dismiss();
};

PageObject.prototype.acceptAlert = function() {
    browser.wait(protractor.ExpectedConditions.alertIsPresent(), this.DEFAULT_ALERT_WAIT);
    browser.switchTo().alert().accept();
};

PageObject.prototype.open = function() {
    browser.get(this.pageURI);
};

module.exports = PageObject;
