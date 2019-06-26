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
var clickModalCancelButton = function() {
    browser.click(by.css('#cancel'));
};

var assertModalWindow = function(modal_message) {
    expect(element(by.css('#modalBody')).getText()).toContain(modal_message);
};

var assertFailureMessage = function(failure_message) {
    expect(element(by.css('#failureMessage')).getText()).toBe(failure_message);
};

describe("Test modal-service", function() {

    beforeEach(function() {
        browser.get('test/e2e/modalservice/basic');
    });

    /* Open Modal with templateurl containing directive */

    it("Clicking on \"Open Modal with templateurl containing directive\" button + " +
        "the expected message and cancel button are displayed on a modal window " +
        "and clicking on the button returns a failure message to parent window",
        function() {

            browser.click(by.cssContainingText('#test1', 'open modal with templateUrl containing directive'));

            // Assert modal window is opened
            element(by.css('.modal-content')).isDisplayed();

            // Assert text is displayed in the modal-body in window and contentTemplate.html html is loaded
            assertModalWindow('Hello John');

            clickModalCancelButton();
            assertFailureMessage('Cancelled by John');

        });


    /* open modal with templateUrl and controller ID */
    it("Given As a user I click on \"open modal with templateUrl and controller ID\" button + " +
        "the expected message and cancel button are displayed on a modal window " +
        "and clicking on the button returns a failure message to parent window",
        function() {

            browser.click(by.cssContainingText('#test2', 'open modal with templateUrl and controller ID'));

            // Assert modal window is opened
            element(by.css('.modal-content')).isDisplayed();

            // Assert text is displayed in the modal-body in window and contentTemplate.html html is loaded
            assertModalWindow('Hello Not John');

            clickModalCancelButton();
            assertFailureMessage('Cancelled by Not John');

        });


    /* open modal with templateUrl and controller function */
    it("Given As a user I click on \"open modal with templateUrl and controller function\" button + " +
        "the expected message and cancel button are displayed on a modal window " +
        "and clicking on the button returns a failure message to parent window",
        function() {

            browser.click(by.cssContainingText('#test3', 'open modal with templateUrl and controller function'));

            // Assert modal window is opened
            element(by.css('.modal-content')).isDisplayed();

            // Assert text is displayed in the modal-body in window and contentTemplate.html html is loaded
            assertModalWindow('Hello John Snow');

            clickModalCancelButton();
            assertFailureMessage('Cancelled by John Snow');

        });

    /* open modal with templateUrl and controller function */
    it("Given As a user I click on \"open modal with templateUrl and controller function\" button + " +
        "the expected message and cancel button are displayed on a modal window " +
        "and clicking on the button returns a failure message to parent window",
        function() {

            browser.click(by.cssContainingText('#test4', 'open modal with template and controller'));

            // Assert modal window is opened
            element(by.css('.modal-content')).isDisplayed();

            // Assert text is displayed in the modal-body in window and contentTemplate.html html is loaded
            assertModalWindow('Hello Ned Stark');

            clickModalCancelButton();
            assertFailureMessage('Cancelled by Ned Stark');

        });


    /* open modal with template containing directive */
    it("Given As a user I click on \"open modal with template containing directive\" button + " +
        "Then I would see the modal window popped-up, Assert the modal window message displayed" +
        "And I would click on click modal window close button, Assert no failure message is displayed",
        function() {

            browser.click(by.cssContainingText('#test5', 'open modal with template containing directive'));

            // Assert modal window is opened
            element(by.css('.modal-content')).isDisplayed();

            // Assert text is displayed in the modal-body in window and contentTemplate.html html is loaded
            assertModalWindow('Hello Sansa Stark');

            clickModalCancelButton();
            assertFailureMessage('Cancelled by Sansa Stark');

        });
});
