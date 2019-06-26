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
var page = require('./../utils/components/Page.js');
var login = require('./../utils/components/Login.js');
var perspectives = require('./../utils/components/Perspectives.js');

describe("Authentication", function() {
    beforeEach(function() {
        page.actions.getAndWaitForLogin('test/e2e/apiAuthentication/apiAuthenticationTest.html');
    });
    afterEach(function() {
        browser.waitForAngularEnabled(true);
    });

    it("WHEN the user is not logged in THEN the user is presented with a login dialog", function() {
        expect(login.mainLoginUsernameInput().isPresent()).toBe(true);
        expect(login.mainLoginPasswordInput().isPresent()).toBe(true);
        expect(login.mainLoginSubmitButton().isPresent()).toBe(true);
    });

    it("WHEN the user submits an empty auth form THEN an error is displayed", function() {
        login.mainLoginSubmitButton().click();
        expect(login.authenticationError().getText()).toBe('Username and password required');
    });

    it("WHEN the user submits incorrect credentials THEN an error is displayed", function() {
        login.loginAsInvalidUser();
        expect(login.authenticationError().getText()).toBe('Invalid username or password');
    });

    describe('After Login', function() {
        beforeEach(function(done) {

            login.loginAsCmsManager().then(function() {
                return perspectives.actions.selectPerspective(perspectives.constants.DEFAULT_PERSPECTIVES.ALL).then(function() {
                    done();
                });
            });
        });

        afterEach(function() {
            login.logoutUser();
        });

        it("WHEN the user is not authenticated to fake1 or fake2 API THEN fake 1 nor fake 2 are visible", function() {
            browser.switchToIFrame();
            expect(browser.isAbsent(by.id('fake1'))).toBe(true);
            expect(browser.isAbsent(by.id('fake2'))).toBe(true);

            browser.switchToParent();
            login.loginToAuthForFake2();
            browser.switchToIFrame();
            expect(browser.isAbsent(by.id('fake1'))).toBe(true);
            expect(browser.isPresent(by.id('fake2'))).toBe(true);

            browser.switchToParent();
            login.loginToAuthForFake1();
            browser.switchToIFrame();
            expect(browser.isPresent(by.id('fake1'))).toBe(true);
            expect(browser.isPresent(by.id('fake2'))).toBe(true);
        });
    });

    describe('Custom Iframe', function() {
        it("WHEN the user is logged in THEN the custom iframe successfully retrieves access token map", function() {
            //for this test we don't need authenticate with iframe APIs, we just need to redirect to the custom view
            login.loginAsCmsManager().then(function() {
                //since it is custom iframe, no angular is to be expected inside
                browser.ignoreSynchronization = true;
                browser.click(by.id('navigateButtonOuter'));
                browser.switchToIFrame(false);
                browser.click(by.id('retrieveAccessTokens'));
                element(by.id('tokens')).getText().then(function(stringifiedToken) {
                    var token = JSON.parse(stringifiedToken);
                    expect(token["/authorizationserver/oauth/token"]).toEqual({
                        access_token: "access-token0",
                        token_type: "bearer"
                    });
                });
            });
        });
    });
});
