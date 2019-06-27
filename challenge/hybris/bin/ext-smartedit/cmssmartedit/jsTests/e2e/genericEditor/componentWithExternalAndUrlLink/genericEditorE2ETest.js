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
describe("GenericEditor With External and URL Link", function() {

    beforeEach(function() {

        browser.bootstrap(__dirname);

    });

    beforeEach(function() {
        require("../commonFunctions.js");
    });

    it("will set Media attribute of 'en' and 'hi' tabs to display 'contextualmenu_delete_off.png' and 'contextualmenu_delete_on.png' respectively and no media is selected for the rest of the tabs", function() {
        expectAttributeToContain(getMediaElement('en'), 'src', '/web/webroot/images/contextualmenu_delete_off.png');

        selectLocalizedTab('fr', 'media', false);
        expectAttributeToContain(getMediaElement('fr'), 'src', '');

        selectLocalizedTab('it', 'media', false);
        expectAttributeToContain(getMediaElement('it'), 'src', '');

        selectLocalizedTab('pl', 'media', true);
        expectAttributeToContain(getMediaElement('pl'), 'src', '');

        selectLocalizedTab('hi', 'media', true);
        expectAttributeToContain(getMediaElement('hi'), 'src', '/web/webroot/images/contextualmenu_delete_on.png');
    });

    it("will set the description of the selected media", function() {
        expectElementToContainText(getMediaElementCaption('en'), 'contextualmenu_delete_off');
    });

    it("will allow to search for media and auto filter contents", function() {

        browser.click(getMediaElementWrapper('en'), "could not find selected media for English");

        browser.sendKeys(by.xpath("//*[@data-tab-id='en']//div[@id='media-selector']//input[@aria-label='Select box']"), "contextualmenu");
        assertOnCount(6);

        browser.clearAndSendKeys(by.xpath("//*[@data-tab-id='en']//div[@id='media-selector']//input[@aria-label='Select box']"), "delete");
        assertOnCount(2);

    });

    it("will set the description of the search result media", function() {
        browser.click(getMediaElementWrapper('en'), "could not find selected media for English");

        browser.sendKeys(by.xpath("//*[@data-tab-id='en']//div[@id='media-selector']//input[@aria-label='Select box']"), "delete_off");
        assertOnCount(1);
        var theCaption = element(by.xpath("//div[@id='media-selector']//ul[@id='media-list']//ul[@role='listbox']/li[@role='option']")).getText();
        expect(theCaption).toContain("contextualmenu_delete_off");
    });

    function assertOnCount(count) {
        browser.waitUntil(function() {
            return element.all(by.xpath("//div[@id='media-selector']//ul[@id='media-list']//ul[@role='listbox']/li[@role='option']")).count().then(function(value) {
                return value === count;
            }, function() {
                return false;
            });
        }, 'media dropdown failed to return expected number of options');
    }

    function expectAttributeToContain(element, attr, expectedValue) {
        browser.wait(function() {
            return element.getAttribute(attr).then(function(value) {
                return (value || '').indexOf(expectedValue) >= 0;
            }, function() {
                return false;
            });
        }, 5000, 'Expected element ' + element.locator() + ' to have attribute ' + attr + ' containing ' + expectedValue);
    }

    function expectElementToContainText(element, expectedText) {
        browser.wait(function() {
            return element.getText().then(function(text) {
                return text.indexOf(expectedText) >= 0;
            }, function() {
                return false;
            });
        }, 5000, 'Expected element ' + element.locator() + ' to contain text ' + expectedText);
    }
});
