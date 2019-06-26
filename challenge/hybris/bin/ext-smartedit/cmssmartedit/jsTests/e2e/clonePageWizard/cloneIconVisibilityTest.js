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
/* jshint unused:false, undef:false */
describe('Clone Page Toolbar Icon: ', function() {

    var clonePageWizard = e2e.pageObjects.clonePageWizard;
    var perspective = e2e.componentObjects.modeSelector;
    var sfBuilder = e2e.se.componentObjects.sfBuilder;

    beforeEach(function(done) {
        browser.bootstrap(__dirname).then(function() {
            browser.waitForWholeAppToBeReady().then(function() {
                perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
                done();
            });
        });
    });

    it('GIVEN a page belongs to a writable non-active catalog version that has cloneable targets THEN clone icon is visible', function() {

        clonePageWizard.assertions.cloneIconIsDisplayed();

    });

    it('GIVEN a page belongs to a writable active catalog version that has no targets THEN clone icon is not visible', function() {

        sfBuilder.actions.changePageIdAndCatalogVersion('homepage_uk_online', 'apparel-ukContentCatalog/Online');
        clonePageWizard.assertions.cloneIconIsNotDisplayed();

    });

    it('GIVEN a variation page belongs to a writable active catalog version that has targets THEN clone icon is not visible', function() {

        sfBuilder.actions.changePageIdAndCatalogVersion('homepage_gloabl_online_variation', 'apparelContentCatalog/Online');
        clonePageWizard.assertions.cloneIconIsNotDisplayed();

    });

    it('GIVEN a primary page whose copyToCatalogDisabled is set to true and that the page belongs to a writable active catalog version that has targets THEN clone icon is not visible', function() {

        sfBuilder.actions.changePageIdAndCatalogVersion('homepage_gloabl_online_copy_disabled', 'apparelContentCatalog/Online');
        clonePageWizard.assertions.cloneIconIsNotDisplayed();

    });

    it('GIVEN a primary page whose copyToCatalogDisabled is set to false and the page belongs to a writable active catalog version that has targets THEN clone icon is visible', function() {

        sfBuilder.actions.changePageIdAndCatalogVersion('homepage_global_online', 'apparelContentCatalog/Online');
        clonePageWizard.assertions.cloneIconIsDisplayed();

    });

});
