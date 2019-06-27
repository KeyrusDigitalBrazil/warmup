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
describe('Landing page', function() {

    var landingPage, catalogDetailsPage;

    beforeEach(function() {
        landingPage = require('../utils/pageObjects/LandingPagePageObject.js');
        catalogDetailsPage = require('../utils/pageObjects/CatalogDetailsPageObject.js');

        catalogDetailsPage.actions.openAndBeReady();
    });

    it('GIVEN I am on the landing page WHEN the page is fully loaded THEN I expect to see the injected tempplate via the bridge', function() {
        // GIVEN
        var template1Name = "Hello";
        var template2Name = "World";

        // THEN
        landingPage.assertions.catalogVersionContainsItem(landingPage.constants.ELECTRONICS_CATALOG, landingPage.constants.STAGED_CATALOG_VERSION, template1Name);
        landingPage.assertions.catalogVersionContainsItem(landingPage.constants.ELECTRONICS_CATALOG, landingPage.constants.STAGED_CATALOG_VERSION, template2Name);

        landingPage.assertions.catalogVersionContainsItem(landingPage.constants.ELECTRONICS_CATALOG, landingPage.constants.ACTIVE_CATALOG_VERSION, template1Name);
        landingPage.assertions.catalogVersionContainsItem(landingPage.constants.ELECTRONICS_CATALOG, landingPage.constants.ACTIVE_CATALOG_VERSION, template2Name);
    });

});
