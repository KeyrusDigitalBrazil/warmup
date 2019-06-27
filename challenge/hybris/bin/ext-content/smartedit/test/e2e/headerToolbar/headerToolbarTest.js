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

describe('Header Toolbar', function() {

    describe('in Storefront', function() {
        var headerToolbar, page;

        beforeEach(function() {
            headerToolbar = require("../utils/components/HeaderToolbarComponentObject.js");
            page = require("../utils/components/Page.js");
            page.actions.getAndWaitForWholeApp('test/e2e/headerToolbar/headerToolbarTest.html');
        });

        it('GIVEN I am on the storefront and I am not an admin user THEN I don\'t expect to see configuration centre in header toolbar', function() {
            headerToolbar.assertions.assertConfigurationCenterIsAbsent();
        });

        it('GIVEN I am on the storefront WHEN I click on Logout on User Account dropdown on header toolbar THEN I expect to log out', function() {
            headerToolbar.actions.clickOnLogout();
            headerToolbar.assertions.waitForUrlToMatch();
        });

        it('GIVEN I am on the storefront THEN I expect to see language selector in header toolbar', function() {
            headerToolbar.assertions.assertLanguageSelectorIsPresent('languageSelectorDropdown');
        });
    });

    describe('in landing page', function() {
        var headerToolbar, landingPage;

        beforeEach(function() {
            headerToolbar = require("../utils/components/HeaderToolbarComponentObject.js");
            landingPage = require('../utils/pageObjects/LandingPagePageObject.js');
            landingPage.actions.openAndBeReady();
        });

        it('GIVEN I am on the landing page and I am not an admin user THEN I don\'t expect to see configuration centre in header toolbar', function() {
            headerToolbar.assertions.assertConfigurationCenterIsAbsent();
        });

        it('GIVEN I am on the landing page WHEN I click on Logout on User Account dropdown on header toolbar THEN I expect to log out', function() {
            headerToolbar.actions.clickOnLogout();
            headerToolbar.assertions.waitForUrlToMatch();
        });

        it('GIVEN I am on the landing page THEN I expect to see language selector in header toolbar', function() {
            headerToolbar.assertions.assertLanguageSelectorIsPresent('languageSelectorDropdown');
        });
    });
});
