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
describe("GenericEditor form save", function() {

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    beforeEach(function() {
        require("../commonFunctions.js");
        browser.driver.manage().timeouts().implicitlyWait(0);
    });

    it("will display validation error when submit is clicked without image being uploaded (image is removed)", function() {
        browser.click(by.css('[data-tab-id="en"] .replace-image')).then(function() {
            clickSubmit().then(function() {

                expect(getValidationErrorElementByLanguage('media', 'en').isDisplayed()).toBe(true);
                browser.waitForAbsence(getValidationErrorElementByLanguage('media', 'fr'));
                browser.waitForAbsence(getValidationErrorElementByLanguage('media', 'it'));
                browser.waitForAbsence(getValidationErrorElementByLanguage('media', 'pl'));
                browser.waitForAbsence(getValidationErrorElementByLanguage('media', 'hi'));

            });
        });
    });

    xit("will show the selected media selected for only 'fr' language when a media is selected for 'fr' language and submit is clicked", function() {
        addMedia('fr', 'contextualmenu_delete_on').then(function() {
            clickSubmit().then(function() {
                assertOnMediaInTab('fr', 'contextualmenu_delete_on');
                assertOnMediaInTab('en', 'contextualmenu_delete_off');

            });
        });

    });

});
