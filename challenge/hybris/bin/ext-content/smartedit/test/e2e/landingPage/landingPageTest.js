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
describe('Landing Page - ', function() {

    var landingPage, storefront, inflectionPoint;

    beforeEach(function() {
        landingPage = require('../utils/pageObjects/LandingPagePageObject.js');
        storefront = require('../utils/components/Storefront.js');
        inflectionPoint = require('../utils/components/InflectionPoint.js');

        landingPage.actions.openAndBeReady();
    });

    it('GIVEN I am on the landing page ' +
        'WHEN the page is fully loaded ' +
        'THEN I expect to see the first site selected',
        function() {
            // THEN
            landingPage.assertions.expectedSiteIsSelected(landingPage.constants.ELECTRONICS_SITE);
            landingPage.assertions.selectedSiteHasRightNumberOfCatalogs(1);
            landingPage.assertions.catalogIsExpanded(landingPage.constants.ELECTRONICS_CATALOG);
        });

    it('GIVEN I am on the landing page ' +
        'WHEN I click on the site header ' +
        'THEN the item collapses',
        function() {
            // GIVEN
            landingPage.assertions.catalogIsExpanded(landingPage.constants.ELECTRONICS_CATALOG);

            // WHEN 
            landingPage.actions.clickOnCatalogHeader(landingPage.constants.ELECTRONICS_CATALOG);

            // THEN 
            landingPage.assertions.catalogIsNotExpanded(landingPage.constants.ELECTRONICS_CATALOG);
        });

    it('GIVEN I am on the landing page ' +
        'WHEN I have a site with multiple catalogs ' +
        'THEN only the catalog for the current site is expanded',
        function() {
            // GIVEN
            landingPage.assertions.selectedSiteHasRightNumberOfCatalogs(1);

            // WHEN 
            landingPage.actions.selectSite(landingPage.constants.ACTION_FIGURES_SITE);

            // THEN 
            landingPage.assertions.selectedSiteHasRightNumberOfCatalogs(2);
            landingPage.assertions.catalogIsNotExpanded(landingPage.constants.TOYS_CATALOG);
            landingPage.assertions.catalogIsExpanded(landingPage.constants.ACTION_FIGURES_CATALOG);
        });

    it('GIVEN I am on the landing page ' +
        'WHEN I click on the thumbnail ' +
        'THEN I expect to be redirected to the homepage of the active catalog version',
        function() {
            // WHEN
            landingPage.actions.navigateToStorefrontViaThumbnail(landingPage.constants.ELECTRONICS_CATALOG);

            // THEN 
            storefront.assertions.assertStoreFrontIsDisplayed();
        });

    it('GIVEN I am on the landing page ' +
        'WHEN I click on the homepage link of the active catalog version ' +
        'THEN I expect to be redirected to the homepage of the active catalog version',
        function() {
            // WHEN
            landingPage.actions.navigateToStorefrontViaHomePageLink(landingPage.constants.ELECTRONICS_CATALOG, landingPage.constants.ACTIVE_CATALOG_VERSION);

            // THEN 
            storefront.assertions.assertStoreFrontIsDisplayed();
        });

    it('GIVEN I am on the landing page ' +
        'WHEN I click on the homepage link of a staged catalog version ' +
        'THEN I expect to be redirected to the homepage of that staged catalog version',
        function() {
            // WHEN 
            landingPage.actions.navigateToStorefrontViaHomePageLink(landingPage.constants.ELECTRONICS_CATALOG, landingPage.constants.ACTIVE_CATALOG_VERSION);

            // THEN 
            storefront.assertions.assertStoreFrontIsDisplayed();
        });

    it('GIVEN I am on a store front ' +
        'WHEN I click on the burger menu and the SITES link ' +
        'THEN I will be redirected to the landing page',
        function() {
            // GIVEN
            landingPage.actions.selectSite(landingPage.constants.APPAREL_SITE);
            landingPage.actions.navigateToStorefrontViaThumbnail(landingPage.constants.APPAREL_UK_CATALOG);

            // WHEN 
            landingPage.actions.navigateToLandingPage();

            // THEN 
            landingPage.assertions.assertLandingPageIsDisplayed();
        });

    it('GIVEN I am on the landing page ' +
        'THEN inflection point icon should not be visible on this page',
        function() {
            // THEN 
            inflectionPoint.assertions.inflectionPointSelectorIsNotPresent();
        });

    it('GIVEN I am on the landing page ' +
        'WHEN I click on a multicountry site AND I click on the local catalog ' +
        'THEN I am redirected to the right page',
        function() {
            // GIVEN
            var CONSTANTS = landingPage.constants;
            landingPage.actions.selectSite(CONSTANTS.ACTION_FIGURES_SITE);

            // WHEN
            landingPage.actions.clickOnHomePageLink(CONSTANTS.ACTION_FIGURES_CATALOG, CONSTANTS.ACTIVE_CATALOG_VERSION);

            // THEN 
            landingPage.assertions.assertStorefrontIsLoaded();
        });

    it('GIVEN I am on the landing page ' +
        'WHEN I click on a multicountry site AND I click on a parent catalog ' +
        'THEN I am redirected to the right page',
        function() {
            // GIVEN
            var CONSTANTS = landingPage.constants;
            landingPage.actions.selectSite(CONSTANTS.ACTION_FIGURES_SITE);

            // WHEN
            landingPage.actions.clickOnParentCatalogHomePageLink(CONSTANTS.TOYS_CATALOG, CONSTANTS.ACTIVE_CATALOG_VERSION);

            // THEN 
            landingPage.assertions.assertStorefrontIsLoaded();
        });

    it('GIVEN I am on the landing page ' +
        'WHEN I dopen the dropdown for sites and serach by key' +
        'THEN I expect the data to be filtered by label',
        function() {
            // WHEN - THEN
            landingPage.actions.openSiteSelector();
            landingPage.assertions.searchAndAssertInDropdown('toy', ['Toys']);
        });


});
