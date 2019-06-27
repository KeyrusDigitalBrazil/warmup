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
describe('restore pages -', function() {

    // ---------------------------------------------------------------
    // Variables
    // ---------------------------------------------------------------
    var pageList = e2e.pageObjects.PageList;
    var landingPage = e2e.pageObjects.landingPage;
    var restorePageModal = e2e.componentObjects.restorePageModal;
    var confirmationModal = e2e.componentObjects.confirmationModal;

    var VALID_PAGE_NAME = "Valid Page";
    var DUPLICATED_PAGE_NAME = "Page with duplicated name";
    var DUPLICATED_CONTENT_PRIMARY_PAGE_NAME = "Duplicate Primary Content Page";
    var PAGE_WITH_TWO_ERRORS = "Page with two errors";
    var CONTENT_PAGE_NO_PRIMARY = "Content Page without primary";
    var DUPLICATE_PRODUCT_PAGE_NAME = "Duplicate Product page";
    var PRODUCT_PAGE_WITHOUT_PRIMARY = "Product Page with no primary";

    var VALID_LABEL = "New Label";
    var NEW_PRIMARY_CONTENT_PAGE_NAME = "My Little Primary Content Page";

    // ---------------------------------------------------------------
    // Test Setup 
    // ---------------------------------------------------------------
    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    beforeEach(function() {
        landingPage.actions.navigateToFirstStagedCatalogPageList();
        browser.waitForContainerToBeReady();
        browser.waitUntilNoModal();

        pageList.actions.openTrashedPageList();
    });

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------
    it('GIVEN no restore validation error WHEN restore is clicked THEN the page is restored AND a success message is displayed', function() {
        // WHEN 
        pageList.actions.restorePageByName(VALID_PAGE_NAME);

        // THEN 
        restorePageModal.assertions.successAlertIsDisplayed();
    });

    it('GIVEN page has a name already used by another page WHEN restore is clicked THEN the restore modal is shown with the name error', function() {
        // GIVEN 

        // WHEN
        pageList.actions.restorePageByName(DUPLICATED_PAGE_NAME);

        restorePageModal.actions.setName(VALID_PAGE_NAME);
        restorePageModal.actions.saveChanges();

        // THEN 
        restorePageModal.assertions.successAlertIsDisplayed();
    });

    it('GIVEN content page is a primary AND another primary has the same label WHEN restore is clicked THEN the restore modal is shown with the duplicate primary error AND user can overwrite page', function() {
        // GIVEN 

        // WHEN 
        pageList.actions.restorePageByName(DUPLICATED_CONTENT_PRIMARY_PAGE_NAME);

        restorePageModal.actions.saveChanges();

        // THEN 
        restorePageModal.assertions.successAlertIsDisplayed();
    });

    it('GIVEN page has an error AND another error happens while restoring WHEN restored is clicked AND the user clicks a second time THEN the editor structure is refreshed to match the new errors', function() {
        // GIVEN 

        // WHEN 
        pageList.actions.restorePageByName(PAGE_WITH_TWO_ERRORS);

        restorePageModal.actions.setName(DUPLICATED_PAGE_NAME);
        restorePageModal.actions.saveChanges();

        restorePageModal.actions.setName(VALID_PAGE_NAME);
        restorePageModal.actions.changePrimaryPageLabel(VALID_LABEL);
        restorePageModal.actions.saveChanges();

        // THEN 
        restorePageModal.assertions.successAlertIsDisplayed();
    });

    it('GIVEN content page is a primary AND another primary has the same label WHEN restore is clicked THEN the restore modal is shown with the duplicate primary error AND user can select a different label', function() {
        // GIVEN 

        // WHEN 
        pageList.actions.restorePageByName(DUPLICATED_CONTENT_PRIMARY_PAGE_NAME);

        restorePageModal.actions.changePrimaryPageLabel(VALID_LABEL);
        restorePageModal.actions.saveChanges();

        // THEN 
        restorePageModal.assertions.successAlertIsDisplayed();
    });

    it('GIVEN content page is a variation AND its primary does not exist WHEN restore is clicked THEN the restore modal is shown with the missing primary error', function() {
        // GIVEN 

        // WHEN 
        pageList.actions.restorePageByName(CONTENT_PAGE_NO_PRIMARY);

        restorePageModal.actions.selectNewPrimaryContentPage(NEW_PRIMARY_CONTENT_PAGE_NAME);
        restorePageModal.actions.saveChanges();

        // THEN 
        restorePageModal.assertions.successAlertIsDisplayed();
    });

    it('GIVEN non-content page is primary AND another primary already exists WHEN restore is clicked THEN a message is displayed to overwrite or cancel the operation', function() {
        // GIVEN

        // WHEN
        pageList.actions.restorePageByName(DUPLICATE_PRODUCT_PAGE_NAME);
        restorePageModal.actions.saveChanges();

        // THEN 
        restorePageModal.assertions.successAlertIsDisplayed();
    });

    it('GIVEN non-content page is primary AND another primary already exists WHEN restore is clicked AND the user cancels the operation THEN no alert is displayed', function() {
        // GIVEN

        // WHEN
        pageList.actions.restorePageByName(DUPLICATE_PRODUCT_PAGE_NAME);

        restorePageModal.actions.cancelChanges();

        // THEN 
        restorePageModal.assertions.successAlertIsNotDisplayed();
    });

    it('GIVEN non-content page is primary AND another primary already exists WHEN restore is clicked THEN a message is displayed to overwrite or cancel the operation', function() {
        // GIVEN 

        // WHEN 
        pageList.actions.restorePageByName(PRODUCT_PAGE_WITHOUT_PRIMARY);

        confirmationModal.assertions.cancelButtonIsNotDisplayed();
        confirmationModal.actions.confirmConfirmationModal();

        // THEN 
        restorePageModal.assertions.successAlertIsNotDisplayed();
    });


});
