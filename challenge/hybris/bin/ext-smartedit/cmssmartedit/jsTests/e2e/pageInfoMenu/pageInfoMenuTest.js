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
describe('Page Info Menu', function() {

    var pageInfo = e2e.pageObjects.PageInfo;
    var perspective = e2e.componentObjects.modeSelector;
    var storefront = e2e.componentObjects.storefront;
    var sfBuilder = e2e.se.componentObjects.sfBuilder;

    beforeEach(function() {
        browser.bootstrap(__dirname);
        browser.waitForWholeAppToBeReady();
    });

    //FIXME: should be able to clickthrough
    afterEach(function(done) {
        perspective.select(perspective.PREVIEW_PERSPECTIVE).then(function() {
            browser.waitForAngularEnabled(true);
            done();
        });
    });

    describe('content page', function() {
        beforeEach(function(done) {
            perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                done();
            });
        });

        it('GIVEN the user is on the storefront content page WHEN the user opens the page info menu THEN the page info is displayed', function() {
            // GIVEN
            pageInfo.actions.openPageInfoMenu();

            // THEN
            expect(pageInfo.elements.getFieldValue('name'))
                .toBe('Homepage', 'Expected page name to be "Homepage"');
            expect(pageInfo.elements.getFieldValue('label'))
                .toBe('i-love-pandas', 'Expected page label to be "i-love-pandas"');
            expect(pageInfo.elements.getFieldValue('template'))
                .toBe('AccountPageTemplate', 'Expected page template to be "AccountPageTemplate"');
            expect(pageInfo.elements.getFieldValue('localizedType'))
                .toBe('ContentPage', 'Expected localized type to be "ContentPage"');
            expect(pageInfo.elements.getFieldValue('displayCondition'))
                .toBe('Variation', 'Expected page display condition to be "Variation"');
            expect(pageInfo.elements.getFieldValue('title'))
                .toBe('I love pandas', 'Expected page title to be "I love pandas"');
            expect(pageInfo.elements.getTimeFieldValue('creationtime'))
                .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page creation time to be short date format');
            expect(pageInfo.elements.getTimeFieldValue('modifiedtime'))
                .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page modification time to be short date format');
        });

        it('GIVEN the user is in the page info menu WHEN the user clicks the Edit button THEN the page editor modal is opened', function() {
            // WHEN
            pageInfo.actions.openPageInfoMenu();
            pageInfo.actions.clickEditButton();

            // THEN
            expect(pageInfo.elements.getPageEditorModal().isPresent())
                .toBe(true, 'Expected Page Editor modal to be opened');

            pageInfo.actions.dismissEditor();
        });

        it('GIVEN page has restrictions WHEN the user clicks on the restrictions tab THEN the modal shows the page restrictions', function() {
            // GIVEN 
            var expectedRestriction1 = {
                name: 'Some Time restriction A',
                description: 'some description'
            };
            var expectedRestriction2 = {
                name: 'another time B',
                description: 'some description'
            };

            // WHEN 
            pageInfo.actions.openPageInfoMenu();
            pageInfo.actions.clickRestrictionsTab();

            // THEN 
            pageInfo.assertions.hasRestrictionWithRightData(expectedRestriction1);
            pageInfo.assertions.hasRestrictionWithRightData(expectedRestriction2);
        });

        it('GIVEN a user is visiting a current homepage THEN the current homepage icon should be present', function() {
            pageInfo.assertions.hasCurrentHomepageIcon();
        });

    });

    describe('product page', function() {
        beforeEach(function(done) {
            storefront.actions.goToSecondPage().then(function() {
                perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                    pageInfo.actions.openPageInfoMenu();
                    done();
                });
            });
        });

        it('GIVEN the user is on the storefront product page WHEN the user opens the page info menu THEN the page info is displayed', function() {
            // THEN
            expect(pageInfo.elements.getFieldValue('name'))
                .toBe('Some Other Page', 'Expected page name to be "Some Other Page"');
            expect(pageInfo.elements.getFieldValue('template'))
                .toBe('ProductPageTemplate', 'Expected page label to be "ProductPageTemplate"');
            expect(pageInfo.elements.getFieldValue('localizedType'))
                .toBe('ProductPage', 'Expected localized type to be "ProductPage"');
            expect(pageInfo.elements.getFieldValue('displayCondition'))
                .toBe('Primary', 'Expected display condition to be "Primary"');
            expect(pageInfo.elements.getFieldValue('title'))
                .toBe('I hate pandas', 'Expected page title to be "I hate pandas"');
            expect(pageInfo.elements.getTimeFieldValue('creationtime'))
                .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page creation time to be short date format');
            expect(pageInfo.elements.getTimeFieldValue('modifiedtime'))
                .toMatch(/\d+\/\d+\/\d+ \d+:\d+ (?:AM|PM)/, 'Expected page modification time to be short date format');
        });

        it('GIVEN page has no restrictions WHEN the user opens the page info menu THEN there is no restrictions tab', function() {
            // THEN 
            pageInfo.assertions.hasNoRestrictionsTab();
        });

    });

    describe('product page - rich client storefront', function() {
        beforeEach(function(done) {
            perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                done();
            });
        });

        it('GIVEN the user navigates the storefront product page WHEN the user opens the page info menu THEN it shows the information of the right page', function() {
            // WHEN
            browser.switchToIFrame();
            sfBuilder.actions.changePageIdWithoutInteration('secondpage');
            pageInfo.actions.openPageInfoMenu();

            // THEN
            expect(pageInfo.elements.getFieldValue('name'))
                .toBe('Some Other Page', 'Expected page name to be "Some Other Page"');
        });
    });

    describe('product page - old homepage', function() {
        beforeEach(function(done) {
            perspective.select(perspective.ADVANCED_CMS_PERSPECTIVE).then(function() {
                done();
            });
        });

        it('GIVEN a user is visiting an old homepage THEN the old homepage icon should be present', function() {
            sfBuilder.actions.changePageIdAndCatalogVersion('thirdpage', 'apparel-ukContentCatalog/Staged');

            pageInfo.assertions.hasOldHomepageIcon();
        });
    });

});
