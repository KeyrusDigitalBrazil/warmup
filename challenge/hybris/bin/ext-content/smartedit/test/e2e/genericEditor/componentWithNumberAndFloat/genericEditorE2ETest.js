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
describe("GenericEditor With Number and Float", function() {

    var price;
    var quantity;

    beforeEach(function() {
        browser.get('test/e2e/genericEditor/componentWithNumberAndFloat/genericEditorTest.html');
    });

    beforeEach(function() {
        quantity = element(by.css("[id='quantity-number']"));
        quantity.clear();
        browser.sendKeys(quantity, "10");

        price = element(by.css("[id='price-float']"));
        price.clear();
        browser.sendKeys(price, '100.15');
    });

    describe('GIVEN a quantity Number attribute, ', function() {

        beforeEach(function() {
            quantity = element(by.css("[id='quantity-number']"));
            quantity.clear();
            browser.sendKeys(quantity, "10");
        });

        it("WHEN the component is rendered then quantity is an input (number)", function() {
            expect(quantity.isPresent()).toBe(true);
            expect(quantity.getAttribute('value')).toEqual('10');
        });

        it("WHEN the quantity has an invalid attribute, THEN cancel and submit are not present", function() {
            browser.sendKeys(quantity, "I have changed");
            browser.waitForAbsence(by.id('submit'));
        });

        it("WHEN the quantity has an invalid input, THEN cancel and submit are not present", function() {
            quantity.clear();
            browser.sendKeys(quantity, "123123123");
            browser.waitForPresence(by.id('submit'));
        });
    });


    describe('GIVEN a price Float attribute, ', function() {

        it("WHEN the component is rendered then price is an input (float)", function() {
            expect(price.isPresent()).toBe(true);
            expect(price.getAttribute('value')).toEqual('100.15');
        });

        it("WHEN the price has an invalid input, THEN submit is not present", function() {
            browser.sendKeys(price, "123.123213.123123");
            browser.waitForAbsence(by.id('submit'));
        });

        it("WHEN the price has an invalid attribute, THEN cancel and submit are not present", function() {
            price.clear();
            browser.sendKeys(price, "123.12");
            browser.waitForPresence(by.id('submit'));
        });
    });

});
