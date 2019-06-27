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
describe("GenericEditor Media Upload", function() {

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    var path;

    beforeEach(function() {
        require("../commonFunctions.js");
        path = require('path');
    });

    it('GIVEN a Media structure type is present in the Generic Editor ' +
        'WHEN I select an invalid image ' +
        'THEN I expect to see the file errors displayed',
        function() {
            selectFileToUpload('invalid.doc').then(function() {
                expect(element(by.css(getCssForEnglish("se-media-selector"))).isPresent()).toBe(true);
                expect(element(by.cssContainingText(getCssForEnglish("se-file-selector"), 'se.upload.image.to.library')).isPresent()).toBe(true);
                browser.waitForAbsence(element(by.css(getCssForEnglish('se-media-upload-form'))));
                expect(element(by.css(getCssForEnglish('se-errors-list'))).isPresent()).toBe(true);
                expect(element(by.css(getCssForEnglish("se-errors-list"))).getText()).toContain("se.upload.file.type.invalid");
            });
        });

    it('GIVEN a Media structure type is present in the generic editor ' +
        'WHEN I select a valid image' +
        'THEN I expect to see the media upload form populated',
        function() {
            selectFileToUpload('more_bckg.png').then(function() {
                browser.wait(EC.presenceOf(element(by.css(getCssForEnglish('.se-media-upload--fn--name')))), 5000, 'Timed out waiting for presence of file name element');
                expect(element(by.css(getCssForEnglish('.se-media-upload--fn--name'))).getText()).toBe('more_bckg.png');
                browser.wait(EC.presenceOf(element(by.css(getCssForEnglish('input[name="code"]')))), 5000, 'Timed out waiting for presence of code input element');
                expect(element(by.css(getCssForEnglish('input[name="code"]'))).getAttribute('value')).toBe('more_bckg.png');
                browser.wait(EC.presenceOf(element(by.css(getCssForEnglish('input[name="description"]')))), 5000, 'Timed out waiting for presence of description input element');
                expect(element(by.css(getCssForEnglish('input[name="description"]'))).getAttribute('value')).toBe('more_bckg.png');
                browser.wait(EC.presenceOf(element(by.css(getCssForEnglish('input[name="altText"]')))), 5000, 'Timed out waiting for presence of alt text input element');
                expect(element(by.css(getCssForEnglish('input[name="altText"]'))).getAttribute('value')).toBe('more_bckg.png');
            });
        });

    it('GIVEN a Media structure type is present in the generic editor ' +
        'WHEN I select a valid image AND upload with a missing code' +
        'THEN I expect to see a field error for code',
        function() {
            selectFileToUpload('more_bckg.png').then(function() {
                return clearCodeField();
            }).then(function() {
                return clickUpload();
            }).then(function() {
                browser.waitForAbsence(element(by.css(getCssForEnglish("se-media-selector"))));
                browser.waitForAbsence(element(by.cssContainingText(getCssForEnglish("se-file-selector"), 'se.upload.image.to.library')));
                expect(element(by.css(getCssForEnglish('se-media-upload-form'))).isPresent()).toBe(true);
                browser.waitForAbsence(element(by.css(getCssForEnglish('se-errors-list'))));

                expect(element(by.css(getCssForEnglish('.upload-field-error-code'))).getText()).toContain('se.uploaded.image.code.is.required');
            });
        });

    it('GIVEN a Media structure type is present in the generic editor ' +
        'WHEN I select a valid image AND upload successfully ' +
        'THEN I expect to see the image selector dropdown with the newly uploaded image',
        function() {
            selectFileToUpload('more_bckg.png').then(function() {
                browser.waitForAbsence(element(by.css(getCssForEnglish("se-media-selector"))));
                browser.waitForAbsence(element(by.cssContainingText(getCssForEnglish("se-file-selector"), 'se.upload.image.to.library')));
                expect(element(by.css(getCssForEnglish('se-media-upload-form'))).isPresent()).toBe(true);
                browser.waitForAbsence(element(by.css(getCssForEnglish('se-errors-list'))));

                return clickUpload();
            }).then(function() {
                expect(element(by.css(getCssForEnglish("se-media-selector"))).isPresent()).toBe(true);
                browser.waitForAbsence(element(by.cssContainingText(getCssForEnglish("se-file-selector"), 'se.upload.image.to.library')));
                browser.waitForAbsence(element(by.css(getCssForEnglish('se-media-upload-form'))));
                browser.waitForAbsence(element(by.css(getCssForEnglish('se-errors-list'))));

                expect(element(by.css(getCssForEnglish('.thumbnail'))).getAttribute('src')).toContain('web/webroot/static-resources/images/more_bckg.png');
                expect(element(by.css(getCssForEnglish('.thumbnail'))).getAttribute('data-ng-src')).toContain('web/webroot/static-resources/images/more_bckg.png');
                expect(element(by.css(getCssForEnglish('.media-preview-code'))).getText()).toBe('more_bckg.png');
            });
        });

    it('GIVEN a media is selected WHEN I click the preview button THEN I expect to see a popover with the image in it.', function() {
        browser.click(by.css(getCssForEnglish(".media-preview-icon"))).then(function() {
            browser.wait(EC.visibilityOf(element(by.css(".preview-image"))), 5000);
            expect(element(by.css(".preview-image")).isDisplayed()).toBe(true, 'Exepcted preview image container to be displayed');
            expect(element(by.css(".preview-image")).getAttribute("src")).toContain("contextualmenu_delete_off.png", 'Expected preview image to be displayed');
            expect(element(by.css(".preview-image")).getAttribute("data-ng-src")).toContain("contextualmenu_delete_off.png", 'Expected preview image to be displayed');
        });
    });

    it('GIVEN a media is selected ' +
        'WHEN I click the advanced information ' +
        'THEN I expect to see a popover with the alt text, code and description in it.',
        function() {
            browser.click(by.css(getCssForEnglish('.media-advanced-information')));
            expect(element(by.css(".advanced-information-description"))).toBeDisplayed();
            expect(element(by.css(".advanced-information-code"))).toBeDisplayed();
            expect(element(by.css(".advanced-information-alt-text"))).toBeDisplayed();
            expectElementToContainText('.advanced-information-description', 'contextualmenu_delete_off');
            expectElementToContainText('.advanced-information-code', 'contextualmenu_delete_off');
            expectElementToContainText('.advanced-information-alt-text', 'contextualmenu_delete_off');
        });

    function expectElementToContainText(selector, expectedText) {
        browser.wait(function() {
            return element(by.css(selector)).getText().then(function(text) {
                return text.indexOf(expectedText) >= 0;
            }, function() {
                return false;
            });
        }, 5000, 'Expected element with selector ' + selector + ' to contain text ' + expectedText);
    }

    function clearCodeField() {
        var keySeries = '';
        for (var i = 0; i < 20; i++) {
            keySeries += protractor.Key.BACK_SPACE;
        }
        return element(by.css(getCssForEnglish('input[name="code"]'))).sendKeys(keySeries);
    }

    function clickUpload() {
        return browser.click(by.css(getCssForEnglish('.se-media-upload-btn__submit')));
    }

    function getCssForEnglish(css) {
        return "[data-tab-id='en'] " + css;
    }

    function selectFileToUpload(fileName) {
        return browser.click(by.css(getCssForEnglish(".media-selector--preview__right.remove-image")), 5000, 'Remove image button not present').then(function() {
            var absolutePath = path.resolve(__dirname, fileName);
            return browser.sendKeys(by.css(getCssForEnglish('input[type="file"]')), absolutePath, 'File input element not present after 5000ms');
        });
    }

});
