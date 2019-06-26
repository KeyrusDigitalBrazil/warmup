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
describe("GenericEditor localized components", function() {

    beforeEach(function() {
        require("../commonFunctions.js");
        browser.get('test/e2e/genericEditor/localizedComponents/genericEditorTest.html');
    });

    it("will display content tabs for en and de only", function() {
        expect(getLocalizedTabElement('en', 'content').isPresent()).toBeTruthy();
        expect(getLocalizedTabElement('de', 'content').isPresent()).toBeTruthy();

        expect(browser.waitForAbsence(getLocalizedTabElement('pl', 'content'))).toBeTruthy();
        expect(browser.waitForAbsence(getLocalizedTabElement('it', 'content'))).toBeTruthy();
        expect(browser.waitForAbsence(getLocalizedTabElement('hi', 'content'))).toBeTruthy();
    });
});
