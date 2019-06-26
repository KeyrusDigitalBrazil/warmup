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
var assertModalWindow = function(modal_message) {
    expect(element(by.css('#modalBody')).getText()).toContain(modal_message);
};

describe("Test modal-service", function() {

    beforeEach(function() {
        browser.get('test/e2e/confirmationModalService');
    });

    it("Clicking on \"Open confirmation modal with description\" button + " +
        "Will display the description text \"my.confirmation.message\"",
        function() {

            // When
            browser.click(by.cssContainingText('#test1', 'Open confirmation modal with description'));

            // Then
            assertModalWindow('my.confirmation.message');
        });

    it("Clicking on \"Open confirmation modal with template and scope params\" button + " +
        "Will display the text \"scopeParam: Scope Param Rendered\"",
        function() {

            // When
            browser.click(by.cssContainingText('#test2', 'Open confirmation modal with template and scope params'));

            // Then
            assertModalWindow('scopeParam: Scope Param Rendered');
        });

    it("Clicking on \"Open confirmation modal with templateUrl and scope params\" button + " +
        "Will display the text \"scopeParam: Scope Param Rendered\"",
        function() {

            // When
            browser.click(by.cssContainingText('#test3', 'Open confirmation modal with templateUrl and scope params'));

            // Then
            assertModalWindow('scopeParam: Scope Param Rendered');
        });

});
