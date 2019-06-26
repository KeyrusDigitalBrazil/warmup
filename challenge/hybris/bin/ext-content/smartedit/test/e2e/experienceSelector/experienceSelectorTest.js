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
describe('Experience Selector - ', function() {

    var ELECTRONICS_SITE = {
        CATALOGS: {
            ONLINE: "Electronics Content Catalog - Online",
            STAGED: "Electronics Content Catalog - Staged"
        },
        LANGUAGES: {
            ENGLISH: "English",
            POLISH: "Polish",
            ITALIAN: "Italian"
        }
    };

    var APPAREL_SITE = {
        CATALOGS: {
            ONLINE: "Apparel UK Content Catalog - Online",
            STAGED: "Apparel UK Content Catalog - Staged"
        },
        LANGUAGES: {
            ENGLISH: "English",
            FRENCH: "French"
        }
    };

    var APPAREL_PRODUCT_CATALOG_CLOTHING = {
        ID: "apparel-ukProductCatalog-clothing",
        VERSIONS: {
            ONLINE: "Online",
            STAGED: "Staged"
        }
    };

    var APPAREL_PRODUCT_CATALOG_SHOES = {
        ID: "apparel-ukProductCatalog-shoes",
        VERSIONS: {
            ONLINE: "Online",
            STAGED_1: "Staged-1",
            STAGED_2: "Staged-2"
        }
    };

    var experienceSelector, alerts, page;

    beforeEach(function() {
        experienceSelector = require('./../utils/components/ExperienceSelector.js');
        alerts = require("../utils/components/systemAlertsComponentObject");
        page = require("../utils/components/Page.js");
        page.actions.getAndWaitForWholeApp('test/e2e/experienceSelector/experienceSelectorTest.html');
    });

    it("GIVEN I'm in the SmartEdit application WHEN I click the Experience Selector button THEN I expect to see the Experience Selector", function() {
        //WHEN
        experienceSelector.actions.widget.openExperienceSelector();

        //THEN
        expect(experienceSelector.elements.catalog.label().getText()).toBe('CATALOG');
        expect(experienceSelector.elements.dateAndTime.label().getText()).toBe('DATE/TIME');
        expect(experienceSelector.elements.language.label().getText()).toBe('LANGUAGE');
        expect(experienceSelector.elements.productCatalogs.label().getText()).toBe('PRODUCT CATALOGS');

        expect(experienceSelector.elements.buttons.ok().getText()).toBe('APPLY');
        expect(experienceSelector.elements.buttons.cancel().getText()).toBe('CANCEL');
    });

    it("GIVEN I'm in the SmartEdit application WHEN I click the Experience Selector for a site that has a single product catalog THEN I expect to see the currently selected experience in the Experience Selector", function() {
        //WHEN
        experienceSelector.actions.widget.openExperienceSelector();

        //THEN
        expect(experienceSelector.elements.catalog.selectedOption().getText()).toBe('Apparel UK Content Catalog - Staged');
        expect(experienceSelector.elements.language.selectedOption().getText()).toBe('English');
        expect(experienceSelector.elements.dateAndTime.field().getAttribute('placeholder')).toBe('Select a Date and Time');
        expect(experienceSelector.elements.multiProductCatalogVersionsSelector.selectedOptions()).toBe('Clothing Product Catalog (Online), Shoes Product Catalog (Online)');
    });

    it("GIVEN I'm in the SmartEdit application WHEN I click the Experience Selector for a site that has multiple product catalogs THEN I expect to see the currently selected experience in the Experience Selector", function() {
        //WHEN
        experienceSelector.actions.switchToCatalogVersion(ELECTRONICS_SITE.CATALOGS.ONLINE);

        //THEN
        expect(experienceSelector.elements.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Online');
        expect(experienceSelector.elements.language.selectedOption().getText()).toBe('English');
        expect(experienceSelector.elements.dateAndTime.field().getAttribute('placeholder')).toBe('Select a Date and Time');
        expect(experienceSelector.elements.singleProductCatalogVersionSelector.selectedOption().getText()).toBe('Online');
    });

    it("GIVEN I'm in the experience selector WHEN I click on the catalog selector dropdown THEN I expect to see all catalog/catalog versions combinations", function() {

        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();

        //WHEN
        experienceSelector.actions.catalog.selectDropdown();

        // THEN
        experienceSelector.assertions.catalog.assertNumberOfOptions(4);
        experienceSelector.assertions.catalog.assertOptionText(2, 'Apparel UK Content Catalog - Online');
        experienceSelector.assertions.catalog.assertOptionText(3, 'Apparel UK Content Catalog - Staged');
        experienceSelector.assertions.catalog.assertOptionText(4, 'Electronics Content Catalog - Online');
        experienceSelector.assertions.catalog.assertOptionText(5, 'Electronics Content Catalog - Staged');
    });

    it("GIVEN I'm in the experience selector WHEN I select a catalog THEN I expect to see the apply button enabled", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();

        //WHEN
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);

        //THEN
        expect(experienceSelector.elements.buttons.ok().getAttribute('disabled')).toBeFalsy();
    });

    it("GIVEN I'm in the experience selector WHEN I select a catalog belonging to the electronics site THEN I expect to see the language dropdown populated with the electronics sites languages", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();

        //WHEN
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);
        experienceSelector.actions.language.selectDropdown();

        //THEN
        experienceSelector.assertions.language.assertNumberOfOptions(3);
        experienceSelector.assertions.language.assertOptionText(2, 'English');
        experienceSelector.assertions.language.assertOptionText(3, 'Polish');
        experienceSelector.assertions.language.assertOptionText(4, 'Italian');
    });

    it("GIVEN I'm in the experience selector WHEN I select a catalog belonging to the apparel site THEN I expect to see the language dropdown populated with the apprel sites languages", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();

        //WHEN
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
        experienceSelector.actions.language.selectDropdown();

        experienceSelector.assertions.language.assertNumberOfOptions(2);
        experienceSelector.assertions.language.assertOptionText(2, 'English');
        experienceSelector.assertions.language.assertOptionText(3, 'French');
    });

    it("GIVEN I'm in the experience selector WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect the smartEdit application with the new preview ticket", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(APPAREL_SITE.LANGUAGES.ENGLISH);

        //WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();

        //THEN
        var expectedUriPostfix = '/test/utils/storefront.html?cmsTicketId=validTicketId';
        expect(experienceSelector.elements.page.iframe().getAttribute('src')).toContain(expectedUriPostfix);
    });

    // TODO this should be part of a unit test
    it("GIVEN I'm in the experience selector WHEN I click the apply button AND the REST call to the preview service fails due to an invalid catalog and catalog version THEN I expect to see an error displayed", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);
        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(ELECTRONICS_SITE.LANGUAGES.ITALIAN);

        //WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();

        //THEN
        alerts.assertions.assertTotalNumberOfAlerts(1);
        alerts.assertions.assertAlertIsOfTypeByIndex(0, 'danger');
    });

    it("GIVEN I'm in the experience selector AND I click on the apply button to update the experience with the one I chose THEN it should update the experience widget text", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(APPAREL_SITE.LANGUAGES.FRENCH);

        //WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();

        //THEN
        var VALID_EXPERIENCE_WIDGET_TEXT = 'Apparel UK Content Catalog - Online | French';
        expect(experienceSelector.elements.widget.text()).toContain(VALID_EXPERIENCE_WIDGET_TEXT);
    });

    it("GIVEN I'm in the experience selector AND I select a date and time using the date-time picker WHEN I click the apply button THEN it should update the experience widget text", function() {

        // GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);

        browser.click(experienceSelector.elements.dateAndTime.button());
        experienceSelector.actions.selectExpectedDate();
        browser.click(experienceSelector.elements.dateAndTime.button());

        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(APPAREL_SITE.LANGUAGES.FRENCH);


        // WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();

        // THEN
        var VALID_EXPERIENCE_WIDGET_TEXT = 'Apparel UK Content Catalog - Online | French | 1/1/16 1:00 PM | Clothing Product Catalog (Online) | Shoes Product Catalog (Online)';
        expect(experienceSelector.elements.widget.text()).toBe(VALID_EXPERIENCE_WIDGET_TEXT);
    });

    //TO BE DISCUSSED	
    xit("GIVEN I'm in the experience selector WHEN I click outside the experience selector in the SmartEdit container THEN the experience selector is closed and reset", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();

        //WHEN
        experienceSelector.actions.clickInApplication();

        //THEN
        expect(experienceSelector.elements.catalog.label().isDisplayed()).toBe(false);
    });

    it("GIVEN I'm in the experience selector WHEN I click outside the experience selector in the SmartEdit application THEN the experience selector is closed and reset", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        //WHEN
        experienceSelector.actions.clickInIframe();

        //THEN
        expect(experienceSelector.elements.catalog.label().isDisplayed()).toBe(false);
    });

    it("GIVEN I have selected an experience with a time WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the newly selected experience", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.STAGED);

        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(ELECTRONICS_SITE.LANGUAGES.ITALIAN);

        browser.click(experienceSelector.elements.dateAndTime.field());
        experienceSelector.actions.calendar.setDate("1/1/16 12:00 AM");

        //WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();
        experienceSelector.actions.widget.openExperienceSelector();
        //THEN
        expect(experienceSelector.elements.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Staged');
        expect(experienceSelector.elements.language.selectedOption().getText()).toBe('Italian');

        expect(experienceSelector.elements.dateAndTime.field().getAttribute('value')).toBe('1/1/16 12:00 AM');

    });

    it("GIVEN I have selected an experience without a time WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the newly selected experience", function() {

        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);

        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(ELECTRONICS_SITE.LANGUAGES.POLISH);

        //WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();
        experienceSelector.actions.widget.openExperienceSelector();

        //THEN
        expect(experienceSelector.elements.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Online');
        expect(experienceSelector.elements.language.selectedOption().getText()).toBe('Polish');
        expect(experienceSelector.elements.dateAndTime.field().getAttribute('placeholder')).toBe('Select a Date and Time');
    });

    it("GIVEN I'm in the experience selector AND I've changed the values in the editor fields WHEN I click cancel AND I re-open the experience selector THEN I expect to see the currently selected experience", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.STAGED);

        browser.click(experienceSelector.elements.dateAndTime.field());
        experienceSelector.actions.calendar.setDate("1/1/16 12:00 AM");

        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(ELECTRONICS_SITE.LANGUAGES.ITALIAN);

        //WHEN
        experienceSelector.actions.widget.cancel();
        experienceSelector.actions.widget.openExperienceSelector();

        //THEN
        expect(experienceSelector.elements.catalog.selectedOption().getText()).toBe('Apparel UK Content Catalog - Staged');
        expect(experienceSelector.elements.language.selectedOption().getText()).toBe('English');
        expect(experienceSelector.elements.dateAndTime.field().getAttribute('placeholder')).toBe('Select a Date and Time');
    });

    it('GIVEN Im in a site that has multiple product catalogs WHEN I change the target versions and click apply button THEN I expect to see the currently selected experience', function() {
        //GIVEN
        experienceSelector.actions.switchToCatalogVersion(APPAREL_SITE.CATALOGS.ONLINE);

        //WHEN
        experienceSelector.actions.productCatalogs.openMultiProductCatalogVersionsSelectorWidget();

        experienceSelector.actions.productCatalogs.selectOptionFromMultiProductCatalogVersionsSelectorWidget(APPAREL_PRODUCT_CATALOG_CLOTHING.ID, APPAREL_PRODUCT_CATALOG_CLOTHING.VERSIONS.STAGED);
        experienceSelector.actions.productCatalogs.selectOptionFromMultiProductCatalogVersionsSelectorWidget(APPAREL_PRODUCT_CATALOG_SHOES.ID, APPAREL_PRODUCT_CATALOG_SHOES.VERSIONS.STAGED_2);

        experienceSelector.actions.productCatalogs.clickModalWindowDone();

        //THEN
        expect(experienceSelector.elements.multiProductCatalogVersionsSelector.selectedOptions()).toBe('Clothing Product Catalog (Staged), Shoes Product Catalog (Staged-2)');

        //WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();

        experienceSelector.actions.widget.openExperienceSelector();

        //THEN
        expect(experienceSelector.elements.multiProductCatalogVersionsSelector.selectedOptions()).toBe('Clothing Product Catalog (Staged), Shoes Product Catalog (Staged-2)');

        experienceSelector.actions.productCatalogs.openMultiProductCatalogVersionsSelectorWidget();

        expect(experienceSelector.elements.multiProductCatalogVersionsSelector.getSelectedOptionFromMultiProductCatalogVersionsSelectorWidget(APPAREL_PRODUCT_CATALOG_CLOTHING.ID).getText()).toBe('Staged');
        expect(experienceSelector.elements.multiProductCatalogVersionsSelector.getSelectedOptionFromMultiProductCatalogVersionsSelectorWidget(APPAREL_PRODUCT_CATALOG_SHOES.ID).getText()).toBe('Staged-2');
    });

    it("GIVEN I have selected an experience without a time WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect the payload to match the API's expected payload", function() {

        // GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);

        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(ELECTRONICS_SITE.LANGUAGES.POLISH);

        // WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();

        // THEN
        var EXPECTED_URI_SUFFIX = '/smartedit-build/test/e2e/dummystorefront/dummystorefrontElectronics.html?cmsTicketId=validTicketId';
        expect(experienceSelector.elements.page.iframe().getAttribute('src')).toContain(EXPECTED_URI_SUFFIX);
    });

    it("GIVEN I have selected an experience with a time WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect the payload to match the API's expected payload", function() {

        // GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);

        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(ELECTRONICS_SITE.LANGUAGES.POLISH);

        browser.click(experienceSelector.elements.dateAndTime.field());
        experienceSelector.actions.calendar.setDate("1/1/16 1:00 PM");

        // WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();

        // THEN
        var EXPECTED_URI_SUFFIX = '/smartedit-build/test/e2e/dummystorefront/dummystorefrontElectronics.html?cmsTicketId=validTicketId';
        expect(experienceSelector.elements.page.iframe().getAttribute('src')).toContain(EXPECTED_URI_SUFFIX);
    });

    it("GIVEN that I have deep linked and I have selected a new experience with a time WHEN I click the apply button AND the REST call to the preview service succeeds THEN I expect to load the page to which I have deep linked without a preview ticket", function() {
        browser.linkAndBackToParent(by.id("deepLink")).then(function() {

            // GIVEN
            experienceSelector.actions.widget.openExperienceSelector();
            experienceSelector.actions.catalog.selectDropdown();
            experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.ONLINE);

            browser.click(experienceSelector.elements.dateAndTime.field());
            experienceSelector.actions.calendar.setDate("1/1/16 1:00 PM");

            experienceSelector.actions.language.selectDropdown();
            experienceSelector.actions.language.selectOption(ELECTRONICS_SITE.LANGUAGES.POLISH);

            // WHEN
            experienceSelector.actions.widget.submit();
            browser.waitForWholeAppToBeReady();

            // THEN
            var EXPECTED_URI_SUFFIX = '/smartedit-build/test/e2e/dummystorefront/dummystorefrontSecondPage.html';
            expect(experienceSelector.elements.page.iframe().getAttribute('src')).toContain(EXPECTED_URI_SUFFIX);

        });

    });


    // FIXME? not supported when storefront is served in different domain.
    xit('GIVEN that I have deep linked WHEN I select a new experience and the current page does not exist for this new experience THEN I will be redirected to the landing page of the new experience', function() {

        // GIVEN
        browser.linkAndBackToParent(by.id("deepLinkFailsWhenNewExperience")).then(function() {

            // WHEN
            experienceSelector.actions.widget.openExperienceSelector();
            experienceSelector.actions.catalog.selectDropdown();
            experienceSelector.actions.catalog.selectOption(APPAREL_SITE.CATALOGS.ONLINE);
            experienceSelector.actions.language.selectDropdown();
            experienceSelector.actions.language.selectOption(APPAREL_SITE.LANGUAGES.ENGLISH);

            experienceSelector.actions.widget.submit();
            browser.waitForWholeAppToBeReady();

            var APPAREL_UK_ONLINE_HOMEPAGE = 'storefront.html?cmsTicketId=validTicketId';
            expect(experienceSelector.elements.page.iframe().getAttribute('src')).toContain(APPAREL_UK_ONLINE_HOMEPAGE);

        });

    });

    xit("GIVEN I have selected an experience by setting the new field WHEN I click the apply button AND the REST call to the preview service succeeds AND I re-open the experience selector THEN I expect to see the new field set", function() {
        //GIVEN
        experienceSelector.actions.widget.openExperienceSelector();
        experienceSelector.actions.catalog.selectDropdown();
        experienceSelector.actions.catalog.selectOption(ELECTRONICS_SITE.CATALOGS.STAGED);

        experienceSelector.actions.language.selectDropdown();
        experienceSelector.actions.language.selectOption(ELECTRONICS_SITE.LANGUAGES.ITALIAN);

        experienceSelector.elements.otherFields.field('newField').sendKeys('New Data For Preview');

        browser.click(experienceSelector.elements.dateAndTime.field());
        experienceSelector.actions.calendar.setDate("1/1/16 12:00 AM");

        //WHEN
        experienceSelector.actions.widget.submit();
        browser.waitForWholeAppToBeReady();
        experienceSelector.actions.widget.openExperienceSelector();

        //THEN
        expect(experienceSelector.elements.catalog.selectedOption().getText()).toBe('Electronics Content Catalog - Staged');

        expect(experienceSelector.elements.dateAndTime.field().getAttribute('value')).toBe('1/1/16 12:00 AM');
        expect(experienceSelector.elements.otherFields.field('newField').getAttribute('value')).toBe('New Data For Preview');

        expect(experienceSelector.elements.language.selectedOption().getText()).toBe('Italian');
    });


});
