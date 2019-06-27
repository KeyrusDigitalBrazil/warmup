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
describe("GenericEditor POST then PUT (standard case)", function() {

    beforeEach(function() {
        require("../commonFunctions.js");
        browser.get('test/e2e/genericEditor/componentPOSTPUT/genericEditorTest.html');
    });

    it("will hit server with POST then PUT and keep same id", function() {

        expect(element(by.id('componentId')).getText()).toBe('');

        element(by.name('description')).clear().sendKeys("some description");
        browser.click(by.id('submit'));

        element(by.id('componentId')).getText().then(function(initialValue) {

            expect(initialValue).toBeDefined();
            expect(initialValue).not.toBe('');

            element(by.name('description')).clear().sendKeys("some other description");
            browser.click(by.id('submit'));
            expect(element(by.id('componentId')).getText()).toBe(initialValue);
        });

    });

});
