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
var yTabComponent = e2e.componentObjects.yTab;

selectLocalizedTab = function(language, qualifier, isHidden) {

    if (isHidden) {
        element.all(by.xpath("//*[@id='" + qualifier + "']//*[@data-toggle='dropdown']")).click();
    }

    element.all(by.xpath('//*[@id="' + qualifier + '"]//li[@data-tab-id="' + language + '"]')).click();

};

switchToIframeForRichTextAndAddContent = function(iframeId, content) {
    browser.wait(EC.presenceOf(element(by.css(iframeId))), 5000);
    browser.switchTo().frame(element(by.css(iframeId)).getWebElement(''));
    browser.driver.findElement(by.tagName('body')).sendKeys(content);

};

switchToIframeForRichTextAndValidateContent = function(iframeId, content) {
    browser.wait(EC.presenceOf(element(by.css(iframeId))), 5000);
    browser.switchTo().frame(element(by.css(iframeId)).getWebElement(''));
    expect(browser.driver.findElement(by.tagName('body')).getText()).toEqual(content);

};

getValidationErrorElements = function(qualifier) {
    return element.all(by.css('[id="' + qualifier + '"] se-generic-editor-field-messages span.se-help-block--has-error'));
};

getValidationErrorElementByLanguage = function(qualifier, language) {
    return element(by.css('[data-tab-id="' + language + '"] [validation-id="' + qualifier + '"] se-generic-editor-field-messages span.se-help-block--has-error'));
};

selectMediaTab = function(language) {
    return browser.click(by.xpath("//*[@id='media']//li[@data-tab-id='" + language + "']"), "could not find tab for language: " + language);
};

addMedia = function(language, searchKey) {
    return selectMediaTab(language).then(function() {
        return browser.click(by.xpath("//*[@data-tab-id='" + language + "']//*[text()='Search...']"), "could not find 'Search...' placeholder for language tab: " + language).then(function() {
            return browser.sendKeys(by.xpath("//*[@data-tab-id='" + language + "']//div[@id='media-selector']//input[@aria-label='Select box']"), searchKey, "could not enter mask in media search for language tab: " + language).then(function() {
                var optionLocator = by.xpath("//*[@data-tab-id='" + language + "']//li[@role='option']//*[text()='" + searchKey + "']");
                return browser.waitForPresence(optionLocator).then(function() {
                    return browser.waitUntil(function() {
                        return element(optionLocator).click().then(function() {
                            return true;
                        });
                    }, "could not click on media selection for language tab: " + language);
                });
            });
        });
    });
};

getMediaElement = function(language) {
    return element(by.css('[id="media"] y-tab[data-tab-id="' + language + '"] .se-media-preview-image-wrapper img'));
};

getMediaElementWrapper = function(language) {
    return element(by.css('[id="media"] y-tab[data-tab-id="' + language + '"] .se-media-preview-image-wrapper'));
};

assertOnMediaInTab = function(language, expectedContent) {
    return selectMediaTab(language).then(function() {
        return browser.waitUntil(function() {
            return getMediaElementCaption(language).getText().then(function(content) {
                return content.indexOf(expectedContent) > -1;
            });
        }, "failed to see the src " + expectedContent + " in tab: " + language);
    });
};
getMediaElementCaption = function(language) {
    return element(by.css('[id="media"] y-tab[data-tab-id="' + language + '"] .media-preview-code'));
};

switchToDefaultContent = function() {
    browser.driver.switchTo().defaultContent();
};

clickSubmit = function() {
    return browser.click(by.id('submit'));
};

/**
 * Simplifies entering value to localized short string component
 * @param {String} qualifier
 * @param {String} text
 * @param {String} language
 */
enterLocalizedShortStringText = function(qualifier, text, language) {
    return yTabComponent.elements.getTabById(language || 'en').element(by.id(qualifier + "-shortstring")).sendKeys(text);
};

/**
 * Simplifies extracting localized short string element
 * @param {String} qualifier
 * @param {String} language
 * @returns {*|string|!webdriver.promise.Promise.<?string>}
 */
getLocalizedShortString = function(qualifier, language) {
    return yTabComponent.elements.getTabById(language || 'en').element(by.id(qualifier + "-shortstring"));
};

/**
 * Simplifies entering value to short string component
 * @param {String} qualifier
 * @param {String} text
 */
enterShortStringText = function(qualifier, text) {
    element(by.id(qualifier + "-shortstring")).sendKeys(text);
};

/**
 * Simplifies extracting short string element
 * @param {String} qualifier
 * @returns {!webdriver.promise.Promise.<?string>}
 */
getShortString = function(qualifier) {
    return element(by.id(qualifier + "-shortstring"));
};

/**
 * productMocks return only one product catalog or more than one product catalog depending the value of the sessionStorage.getItem('returnOneCatalog')
 */
setReturnOneCatalog = function(returnOneCatalog) {
    return browser.executeScript('window.sessionStorage.setItem("returnOneCatalog", arguments[0])', returnOneCatalog);
};
