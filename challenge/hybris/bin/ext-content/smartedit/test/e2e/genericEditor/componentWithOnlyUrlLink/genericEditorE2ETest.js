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
describe("GenericEditor With Only URL Link", function() {
    beforeEach(function() {
        browser.get('test/e2e/genericEditor/componentWithOnlyUrlLink/genericEditorTest.html');
    });

    it("GIVEN only urlLink attribute is present WHEN the component is rendered then external urlLink is a textbox (shortstring)", function() {
        expect(element(by.css("[id='urlLink-shortstring']")).isPresent()).toBe(true);
    });
});
