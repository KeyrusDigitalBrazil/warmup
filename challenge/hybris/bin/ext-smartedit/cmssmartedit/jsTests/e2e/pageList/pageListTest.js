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
describe('Page List', function() {

    var FIRST_CATALOG_NAME = 'APPAREL UK CONTENT CATALOG - ONLINE';

    var landingPage = e2e.pageObjects.landingPage;
    var pageList = e2e.pageObjects.PageList;
    var sitesLink = e2e.componentObjects.sitesLink;

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    beforeEach(function(done) {
        landingPage.actions.navigateToFirstOnlineCatalogPageList().then(function() {
            return browser.waitForContainerToBeReady();
        }).then(function() {
            return browser.waitUntilNoModal();
        }).then(function() {
            done();
        });
    });

    it('GIVEN I am on the page list of the first catalog WHEN the page is fully loaded THEN I expect to see a paginated list of 10 pages max, sorted by name ascending', function() {
        expect(pageList.elements.getDisplayedPageCount()).toBe(10, 'Expected the number of page displayed to be 10');
        pageList.assertions.firstRowColumnContainText('name', "Homepage");

        pageList.actions.navigateToIndex(2);
        pageList.assertions.lastRowColumnContainText('name', "Third Page");
        expect(pageList.elements.getPaginationCount()).toBe(2, 'Expected pagination count to be 2');
    });

    it('GIVEN I am on the page list of the first catalog WHEN I search for a page THEN I expect the list to show the pages that match the query for name or uid', function() {
        pageList.assertions.searchAndAssertCount('homepage', 6, 6);
        expect(pageList.elements.getPaginationCount()).toBe(1, 'Expected pagination count to be 1');

        // Perform a search on a page UID
        pageList.assertions.searchAndAssertCount('variationCategoryPage', 1, 1);
    });

    it('GIVEN I am on the page list of the first catalog WHEN I search for a page  and clear the filter THEN I expect the list all pages again and the page count increase from 1 to 2 ', function() {
        pageList.assertions.searchAndAssertCount('homepage', 6, 6);
        pageList.assertions.totalPageCount(6);
        pageList.elements.clearSearchFilter().then(function() {
            pageList.assertions.totalPageCount(19);
        });
    });

    it('GIVEN I am on the page list of the first catalog WHEN I click on the name column header THEN I expect the list to be re-sorted by this key in the descending order', function() {
        pageList.actions.clickOnColumnHeader('name');
        pageList.assertions.firstRowColumnContainText('name', 'Third Page');
        pageList.actions.navigateToIndex(2);
        pageList.assertions.lastRowColumnContainText('name', "Homepage");
    });

    it('GIVEN I am on the page list of the first catalog WHEN I click on the UID column header THEN I expect the list to be re-sorted by this key in the descending order', function() {
        pageList.actions.clickOnColumnHeader('uid');
        pageList.assertions.firstRowColumnContainText('uid', "variationProductPage");
        pageList.actions.navigateToIndex(2);
        pageList.assertions.lastRowColumnContainText('uid', "auid1");
    });

    it('GIVEN I am on the page list of the first catalog WHEN I click on the page type column header THEN I expect the list to be re-sorted by this key in the descending order', function() {
        pageList.actions.clickOnColumnHeader('itemtype');
        pageList.assertions.firstRowColumnContainText('itemtype', 'ProductPage');
        pageList.actions.navigateToIndex(2);
        pageList.assertions.lastRowColumnContainText('itemtype', "CategoryPage");
    });

    it('GIVEN I am on the page list of the first catalog WHEN I click on the name column header THEN I expect the list to not reorder as it is not sortable', function() {
        pageList.actions.clickOnColumnHeader('template');
        pageList.assertions.firstRowColumnContainText('template', 'AccountPageTemplate');
        pageList.actions.navigateToIndex(2);
        pageList.assertions.lastRowColumnContainText('template', "ProductPageTemplate");
    });


    it('GIVEN I am on the page list of the first catalog WHEN the page is fully loaded THEN I expect to see the catalog name and catalog version', function() {
        expect(pageList.elements.getCatalogName().getText()).toBe(FIRST_CATALOG_NAME);
    });

    it('GIVEN I am on the page list of the first catalog WHEN I click on a linkable page name THEN I expect to be redirected to this page', function() {
        var EXPECTED_IFRAME_SRC = "/jsTests/e2e/storefront.html?cmsTicketId=dasdfasdfasdfa";
        var EXPECTED_BROWSER_URL = "/storefront";

        pageList.elements.getLinkForKeyAndRow('name', 1, 'a').click();
        browser.waitForWholeAppToBeReady();
        browser.switchToParent();

        var iframe = element(by.css('#js_iFrameWrapper iframe', 'iFrame not found'));
        expect(iframe.getAttribute('src')).toContain(EXPECTED_IFRAME_SRC);
        expect(browser.getCurrentUrl()).toContain(EXPECTED_BROWSER_URL);
    });

    it('GIVEN I am on the page list of the first catalog WHEN I hover over the restriction icon for a page THEN I expect to see the number of restrictions for the given page', function() {
        pageList.actions.moveToRestrictionsIconForHomePage().then(function() {
            pageList.assertions.restrictionTooltipToContain(2);
        });

    });

    it('GIVEN I am on the page list of the first catalog ' +
        'WHEN I hover over the restriction icon for a page with no restrictions ' +
        'THEN I expect to see zero restrictions',
        function() {
            pageList.actions.moveToRestrictionsIconForPageById('homepage_gloabl_online_copy_disabled').then(function() {
                pageList.assertions.restrictionTooltipToContain(0);
            });
        });

    it('GIVEN I am on the page list of the first catalog WHEN I click on Sites link THEN I expect to go to sites page', function() {
        sitesLink.actions.openSitesPage();
        sitesLink.assertions.waitForUrlToMatch();
    });
});

describe('Synchronization modal in page list', function() {

    var pageList = e2e.pageObjects.PageList;
    var landingPage = e2e.pageObjects.landingPage;

    beforeEach(function() {

        browser.bootstrap(__dirname);

    });

    beforeEach(function() {
        landingPage.actions.navigateToFirstStagedCatalogPageList();
        pageList.actions.openPageDropdownByPageName('Homepage_gloabl_online_variation');
        browser.waitForContainerToBeReady();
    });

    it('GIVEN I am on the page list of the first staged catalog WHEN the page is fully loaded THEN I expect the sync page modal to be openable from the dropdown.', function() {
        pageList.actions.openSyncModalFromActiveDropdown();
        expect(pageList.elements.getModalSyncPanel().isPresent()).toBe(true, "Expected the presence of a synchronization panel inside a modal.");
    });

    it('GIVEN I am on the page list of the first staged catalog WHEN sync page modal is opened THEN I expect a list of synchronizable items and a sync button to display', function() {
        pageList.actions.openSyncModalFromActiveDropdown();
        pageList.assertions.assertHasSynchronizableItems();

        // is it okay to declare is variable
        var syncButton = pageList.elements.getModalSyncPanelSyncButton();
        expect(syncButton.isPresent()).toBe(true, "Expected the presence of a sync button in the synchronization modal.");
    });

});

describe('Synchronization icon in the page list', function() {

    var pageList = e2e.pageObjects.PageList;
    var landingPage = e2e.pageObjects.landingPage;
    var syncPanel = e2e.componentObjects.synchronizationPanel;
    var SYNCED_PAGE_INDEX = 8;

    beforeEach(function() {

        browser.bootstrap(__dirname);

    });

    beforeEach(function() {
        landingPage.actions.navigateToFirstStagedCatalogPageList();
        syncPanel.setupTest();
        browser.waitForContainerToBeReady();
    });

    it('GIVEN I am on the page list of the first staged catalog containing one synced page WHEN the page is fully loaded THEN I expect the sync icon of a synced page to eventually present an "in_sync" sync status for the synced page.', function() {
        pageList.actions.navigateToIndex(2);
        pageList.assertions.assertOnPageSyncIconStatusByPageIndex(SYNCED_PAGE_INDEX, "IN_SYNC");
    });

    it('GIVEN I am on the page list of the first staged catalog WHEN I sync one of the pages through the sync modal THEN I expect the sync icon of a that page to present an "in_sync" sync status.', function() {
        pageList.actions.syncPageFromSyncModal("homepage");
        pageList.assertions.assertOnPageSyncIconStatusByPageIndex(1, "IN_SYNC");
    });

});

describe('sync option in dropdown', function() {

    var pageList = e2e.pageObjects.PageList;
    var landingPage = e2e.pageObjects.landingPage;
    var syncPanel = e2e.componentObjects.synchronizationPanel;

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    beforeEach(function() {
        landingPage.actions.navigateToFirstStagedCatalogPageList();
        syncPanel.setupTest();
        browser.waitForContainerToBeReady();
        browser.waitUntilNoModal();
    });

    it('GIVEN I am on the page list of the first staged catalog WHEN I open the drop down menu THEN I expect to see sync option on dropdown', function() {
        pageList.actions.openPageDropdownByPageName("homepage");
        browser.waitForPresence(pageList.elements.getDropdownSyncButton(), "Expected sync option to be available in the dropdown.");
        expect(pageList.elements.getDropdownSyncButton().isPresent()).toBe(true, "Expected the presence of sync option in the dropdown menu");
    });
});

describe('Soft deletion of a page\n', function() {

    var confirmationModal = e2e.componentObjects.confirmationModal;
    var deletePageItem = e2e.componentObjects.deletePageItem;
    var landingPage = e2e.pageObjects.landingPage;
    var pageList = e2e.pageObjects.PageList;
    var popover = e2e.componentObjects.popover;
    var syncPanel = e2e.componentObjects.synchronizationPanel;
    var systemAlerts = e2e.componentObjects.systemAlerts;

    var DELETABLE_PAGE_NAME = "My Little Variation Category Page";
    var NON_DELETABLE_PAGE_NAME = "Homepage_gloabl_online_copy_disabled";
    var VALIDATION_ERROR_PAGE_NAME = "My Little Primary Category Page";

    beforeEach(function() {
        browser.bootstrap(__dirname);
        landingPage.actions.navigateToFirstStagedCatalogPageList();
        syncPanel.setupTest();
        browser.waitForContainerToBeReady();
    });

    it('GIVEN I am on a page list view\n' +
        'WHEN I open the drop down menu of a non-deletable page\n' +
        'THEN I expect a "Move to trash" option to be displayed inactive\n' +
        'AND I expect a popover to get rendered on hover with a meaningful message.',
        function() {

            pageList.actions.openPageDropdownByPageName(NON_DELETABLE_PAGE_NAME);
            deletePageItem.assertions.deletePageItemIsInactive();
            deletePageItem.actions.hoverDeletePageItemAnchor();
            popover.assertions.isDisplayedWithProvidedText('se.cms.tooltip.movetotrash');

        });

    it('GIVEN I am on a page list view\n' +
        'WHEN I open the drop down menu of a deletable page\n' +
        'THEN I expect a "Move to trash" option to be displayed active\n' +
        'AND I expect no popover anchor to be found for the "move to trash" option.',
        function() {

            pageList.actions.openPageDropdownByPageName(DELETABLE_PAGE_NAME);
            deletePageItem.assertions.deletePageItemIsActive();
            deletePageItem.assertions.deletePageItemPopoverAnchorIsNotPresent();

        });

    it('GIVEN I am on a page list view\n' +
        'WHEN I click on the "move to trash" option of a deletable page\n' +
        'THEN I expect this page not be displayed in the updated page list\n' +
        'AND I expect the count of trashed page to be automatically updated\n' +
        'AND I expect this page to be displayed in the trash page view.',
        function() {

            pageList.assertions.trashPagesCountEquals(3);

            pageList.actions.openPageDropdownByPageName(DELETABLE_PAGE_NAME);
            deletePageItem.actions.clickOnDeletePageAnchor();
            confirmationModal.actions.confirmConfirmationModal();
            systemAlerts.actions.flush();
            pageList.actions.bringTrashViewLinkIntoView();
            pageList.assertions.pageRowIsNotRenderedByPageName(DELETABLE_PAGE_NAME);
            pageList.assertions.trashPagesCountEquals(4);

            pageList.actions.clickOnTrashViewLink();
            pageList.assertions.pageRowIsRenderedByPageName(DELETABLE_PAGE_NAME);

        });

    it('GIVEN I am on a page list view\n' +
        'WHEN I open the drop down menu of a page that has some validation error and delete it\n' +
        'THEN I expect to see a validation error\n',
        function() {

            pageList.assertions.trashPagesCountEquals(3);

            pageList.actions.openPageDropdownByPageName(VALIDATION_ERROR_PAGE_NAME);
            deletePageItem.actions.clickOnDeletePageAnchor();
            confirmationModal.actions.confirmConfirmationModal();
            pageList.assertions.assertTrashFailure();

            systemAlerts.actions.flush();
            pageList.actions.bringTrashViewLinkIntoView();
            pageList.assertions.trashPagesCountEquals(3);

        });

});
