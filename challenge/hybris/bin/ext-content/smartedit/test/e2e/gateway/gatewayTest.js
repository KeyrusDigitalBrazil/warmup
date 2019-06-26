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
describe("Test gateway", function() {

    beforeEach(function() {
        browser.get('test/e2e/gateway');
    });


    it("iframe sending message is returned a success acknowledgement and parent frame gets message", function() {

        browser.switchToIFrame();
        browser.click(by.id('sendMessage'));
        expect(element(by.id('acknowledged')).getText()).toBe('(parent acknowledged my message and sent back:hello to you iframe)');
        browser.switchToParent();
        expect(element(by.id('message')).getText()).toBe('hello parent ! (from iframe)');
    });

    it("parent frame sending message is returned a failure acknowledgement and listeners 1 and 2 get a failure", function() {

        browser.click(by.id('sendMessage1'));
        expect(element(by.id('acknowledged')).getText()).toBe('(iframe did not acknowledge my message)');
        browser.switchToIFrame();
        expect(element(by.id('message1')).getText()).toBe('failure');
        expect(element(by.id('message2')).getText()).toBe('failure');
    });

    it("parent frame sending message is returned a failure acknowledgement and listener 1 gets the message while listener 2 gets a failure", function() {

        browser.switchToIFrame();
        browser.click(by.id('check1'));

        browser.switchToParent();
        browser.click(by.id('sendMessage1'));
        expect(element(by.id('acknowledged')).getText()).toBe('(iframe did not acknowledge my message)');

        browser.switchToIFrame();

        expect(element(by.id('message1')).getText()).toBe('hello Iframe ! (from parent)');
        expect(element(by.id('message2')).getText()).toBe('failure');
    });

    it("parent frame sending message is returned a success acknowledgement and both listeners on gateway 1 get message; listener on gateway 2 is not triggered", function() {

        browser.switchToIFrame();
        browser.click(by.id('check1'));
        browser.click(by.id('check2'));

        browser.switchToParent();
        browser.click(by.id('sendMessage1'));
        expect(element(by.id('acknowledged')).getText()).toBe('(iframe acknowledged my message and sent back:hello to you parent from second listener on gateway1)');

        browser.switchToIFrame();

        expect(element(by.id('message1')).getText()).toBe('hello Iframe ! (from parent)');
        expect(element(by.id('message2')).getText()).toBe('hello Iframe ! (from parent)');
        expect(element(by.id('message3')).getText()).toBeEmptyString();
    });

    it("parent frame sending message on gateway 2 is returned a failure acknowledgement " +
        "and listener 1 on gateway 2 gets a failure while listeners 1 and 2 on gateway 1 are not invoked",
        function() {
            browser.click(by.id('sendMessage2'));
            expect(element(by.id('acknowledged')).getText()).toBe('(iframe did not acknowledge my message)');
            browser.switchToIFrame();
            expect(element(by.id('message3')).getText()).toBe('failure');
            expect(element(by.id('message1')).getText()).toBeEmptyString();
            expect(element(by.id('message2')).getText()).toBeEmptyString();
        });

    it("parent frame sending message on gateway 2 is returned a success acknowledgement" +
        "and listener 1 on gateway 2 gets a message while listeners 1 and 2 on gateway 1 are not invoked",
        function() {

            browser.switchToIFrame();
            browser.click(by.id('check3'));

            browser.switchToParent();
            browser.click(by.id('sendMessage2'));
            expect(element(by.id('acknowledged')).getText()).toBe('(iframe acknowledged my message and sent back:hello to you parent from unique listener on gateway2)');

            browser.switchToIFrame();

            expect(element(by.id('message3')).getText()).toBe('hello Iframe ! (from parent)');
            expect(element(by.id('message1')).getText()).toBeEmptyString();
            expect(element(by.id('message2')).getText()).toBeEmptyString();
        });

});
