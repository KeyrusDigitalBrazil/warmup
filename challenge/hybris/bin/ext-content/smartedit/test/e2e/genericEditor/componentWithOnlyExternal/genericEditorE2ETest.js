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
describe("GenericEditor With Only External", function() {
    beforeEach(function(done) {
        browser.get('test/e2e/genericEditor/componentWithOnlyExternal/genericEditorTest.html');
        browser.waitForPresence("generic-editor").then(function() {
            done();
        });
    });

    it("GIVEN only external attribute is present WHEN the component is rendered then external attribute is a checkbox", function() {
        expect(element(by.css("[id='external-checkbox']")).isPresent()).toBe(true);
    });
});
