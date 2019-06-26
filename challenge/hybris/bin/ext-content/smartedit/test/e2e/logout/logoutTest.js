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
var page = require("../utils/components/Page.js");
var landingPage = require('../utils/pageObjects/LandingPagePageObject.js');
var login = require('../utils/components/Login.js');
var storefront = require('../utils/components/Storefront.js');

describe("Logout Redirection", function() {
    beforeEach(function() {
        page.actions.getAndWaitForLogin('test/e2e/logout/logoutTest.html');
        login.loginAsCmsManager();
    });

    afterEach(function() {
        login.logoutUser();
        browser.waitForAngularEnabled(true);
    });

    it("when the user goes back to the landing page, he will be re-directed to the home page of the storefront after selecting the same catalog", function() {
        storefront.actions.deepLink();
        landingPage.actions.navigateToLandingPage();
        landingPage.actions.selectSite(landingPage.constants.APPAREL_SITE);
        landingPage.actions.navigateToStorefrontViaThumbnail(landingPage.constants.APPAREL_UK_CATALOG);

        element(by.css('#js_iFrameWrapper iframe')).getAttribute('src').then(function(src) {
            expect(src.indexOf(login.STORE_FRONT_HOME_PAGE) > 0).toBe(true);
        });

    });
});
