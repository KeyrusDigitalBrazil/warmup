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
/* jshint undef:false */
getLocalizedTabElement = function(language, qualifier) {
    return element(by.xpath('//*[@id="' + qualifier + '"]//li[@data-tab-id="' + language + '"]'));
};

selectLocalizedTab = function(language, qualifier, isHidden) {
    return (isHidden ? browser.click(by.css("#" + qualifier + " .caret"), "could not click on tab " + language) : protractor.promise.when()).then(function() {
        return browser.click(getLocalizedTabElement(language, qualifier));
    });

};

switchToIframeForRichTextAndAddContent = function(iframeId, content) {
    return browser.wait(EC.presenceOf(element(by.css(iframeId))), 5000).then(function() {
        return browser.switchTo().frame(element(by.css(iframeId)).getWebElement('')).then(function() {
            return browser.driver.findElement(by.tagName('body')).sendKeys(content);
        });
    });
};

switchToIframeForRichTextAndValidateContent = function(iframeId, content) {
    return browser.wait(EC.presenceOf(element(by.css(iframeId))), 5000).then(function() {
        return browser.switchTo().frame(element(by.css(iframeId)).getWebElement('')).then(function() {
            return expect(browser.driver.findElement(by.tagName('body')).getText()).toEqual(content);
        });
    });
};

getValidationErrorElements = function(qualifier) {
    return element.all(by.css('[id="' + qualifier + '"] se-generic-editor-field-messages span.se-help-block--has-error'));
};

getValidationErrorElementByLanguage = function(qualifier, language) {
    return element(by.css('[data-tab-id="' + language + '"] [validation-id="' + qualifier + '"] se-generic-editor-field-messages span.se-help-block--has-error'));
};

addMedia = function(language, searchKey) {
    browser.click(by.xpath("//*[@id='media']//li[@data-tab-id='" + language + "']"), "could not find tab for language: " + language);
    browser.click(by.xpath("//*[@data-tab-id='" + language + "']//*[text()='Search...']"), "could not find 'Search...' placeholder for language tab: " + language);
    browser.sendKeys(by.xpath("//*[@data-tab-id='" + language + "']//div[@id='media-selector']//input[@aria-label='Select box']"), searchKey, "could not enter mask in media search for language tab: " + language);
    browser.click(by.xpath("//*[@data-tab-id='" + language + "']//li[@role='option']"), "could not click on media selection for language tab: " + language);
};

getMediaElement = function(language) {
    return element(by.css('[id="media"] y-tab[data-tab-id="' + language + '"] .se-media-preview-image-wrapper img'));
};

getMediaElementCaption = function(language) {
    return element(by.css('[id="media"] y-tab[data-tab-id="' + language + '"] .media-preview-code'));
};

clickSubmit = function() {
    return browser.click(by.id('submit'));
};
