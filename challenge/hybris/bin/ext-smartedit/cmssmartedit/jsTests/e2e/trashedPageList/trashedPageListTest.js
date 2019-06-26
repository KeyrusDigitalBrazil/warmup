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
describe('Trashed Page List', function() {

    var FIRST_CATALOG_NAME = 'APPAREL UK CONTENT CATALOG - STAGED';

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

    it('GIVEN I am on the trashed page list of the first catalog WHEN I open click on more menu THEN i see the trash dropdown', function() {
        pageList.assertions.assertTotalTrashedpagesCountInButtonText(3);
    });

    it('GIVEN I am on the trashed page list of the first catalog WHEN the page is fully loaded THEN I expect to see a paginated list of 10 pages max, sorted by name ascending', function() {
        pageList.actions.openTrashedPageList();

        expect(pageList.elements.getTotalPageCount().getText()).toBe("(3 Pages found)", 'Expected the page collection size to be 3');
        expect(pageList.elements.getDisplayedPageCount()).toBe(3, 'Expected the number of page displayed to be 3');
        expect(pageList.elements.getFirstRowForKey('name').getText()).toBe("Trashed Category Page", 'Expect TRASHED CATEGORY PAGE to be the first in the list');

    });

    it('GIVEN I am on the trashed page list of the first catalog WHEN I search for a page by its name, the results are filtered', function() {
        pageList.actions.openTrashedPageList();

        pageList.assertions.searchAndAssertCount('content', 1, 1);
        expect(pageList.elements.getPaginationCount()).toBe(1, 'Expected pagination count to be 1');

    });

    it('GIVEN I am on the trashed page list of the first catalog WHEN I search for a page and clear the filter THEN I see all the results in the first page', function() {
        pageList.actions.openTrashedPageList();

        pageList.assertions.searchAndAssertCount('cat', 1, 1);
        expect(pageList.elements.getPaginationCount()).toBe(1, 'Expected pagination count to be 1');

        pageList.elements.clearSearchFilter().then(function() {
            expect(pageList.elements.getTotalPageCount().getText()).toBe("(3 Pages found)", 'Expected the page collection size to be 3');
        });

    });

    it('GIVEN I am on the trashed page list of the first catalog WHEN I click on the name column header THEN I expect the list to be re-sorted by this key in the descending order', function() {
        pageList.actions.openTrashedPageList();

        expect(pageList.elements.getFirstRowForKey('name').getText()).toBe('Trashed Category Page');
        expect(pageList.elements.getLastRowForKey('name').getText()).toBe('Trashed Product Page');

        pageList.actions.clickOnColumnHeader('name');
        expect(pageList.elements.getFirstRowForKey('name').getText()).toBe('Trashed Product Page');
        expect(pageList.elements.getLastRowForKey('name').getText()).toBe('Trashed Category Page');

    });

    it('GIVEN I am on the trashed page list of the first catalog WHEN I click on the removed time column header THEN I expect the list to be re-sorted by this key in the descending order', function() {
        pageList.actions.openTrashedPageList();

        expect(pageList.elements.getFirstRowForKey('modifiedtime').getText()).toContain('6/28/16');
        expect(pageList.elements.getLastRowForKey('modifiedtime').getText()).toContain('9/26/17');

        pageList.actions.clickOnColumnHeader('modifiedtime');
        expect(pageList.elements.getFirstRowForKey('modifiedtime').getText()).toContain('9/26/17');
        expect(pageList.elements.getLastRowForKey('modifiedtime').getText()).toContain('6/28/16');

    });

    it('GIVEN I am on the trashed page list of the first catalog WHEN I click on the type code header THEN I expect the list to be re-sorted by this key in the descending order', function() {
        pageList.actions.openTrashedPageList();

        expect(pageList.elements.getFirstRowForKey('itemtype').getText()).toBe('CategoryPage');
        expect(pageList.elements.getLastRowForKey('itemtype').getText()).toBe('ProductPage');

        pageList.actions.clickOnColumnHeader('itemtype');
        expect(pageList.elements.getFirstRowForKey('itemtype').getText()).toBe('ProductPage');
        expect(pageList.elements.getLastRowForKey('itemtype').getText()).toBe('CategoryPage');

    });

    it('GIVEN I am on the page list of the first catalog WHEN the page is fully loaded THEN I expect to see the catalog name and catalog version', function() {
        expect(pageList.elements.getCatalogName().getText()).toBe(FIRST_CATALOG_NAME);
    });

    it('GIVEN I am on the trashed page list of the first catalog WHEN I sync the first Page THEN the status must be updated', function() {
        pageList.actions.openTrashedPageList();

        pageList.actions.syncPageFromSyncModal("Trashed Category Page");
        pageList.assertions.assertOnPageSyncIconStatusByPageIndex(1, "IN_SYNC");

    });

    it('GIVEN I am on the trashed page list of the first catalog WHEN I permanently delete the first page THEN the page should be deleted and list should be updated', function() {
        pageList.actions.openTrashedPageList();

        pageList.assertions.searchAndAssertCount('content', 1, 1);
        pageList.assertions.searchAndAssertCount('', 3, 3);
        pageList.actions.permanentlyDeletePageByName("Trashed Content Page");

        pageList.assertions.searchAndAssertCount('content', 0, 0);
        pageList.assertions.searchAndAssertCount('', 2, 2);

    });

    it('GIVEN I am on the trashed page list of the first catalog AND a published page is not synced THEN it can not be permanently deleted', function() {
        // GIVEN
        pageList.actions.openTrashedPageList();
        pageList.assertions.assertOnPageSyncIconStatusByPageIndex(1, "NOT_SYNC");

        // THEN
        pageList.assertions.permanentlyDeleteButtonCanNotBeClicked('Trashed Category Page');
    });

    it('GIVEN I am on the trashed page list of the first catalog AND I permanently delete an unpublished page that is not synced THEN the page should be deleted and list should be updated', function() {
        // GIVEN
        pageList.actions.openTrashedPageList();
        pageList.assertions.searchAndAssertCount('product', 1, 1);
        pageList.assertions.searchAndAssertCount('', 3, 3);
        pageList.assertions.assertOnPageSyncIconStatusByPageIndex(3, "NOT_SYNC");
        pageList.actions.permanentlyDeletePageByName("Trashed Product Page");

        // THEN
        pageList.assertions.searchAndAssertCount('product', 0, 0);
        pageList.assertions.searchAndAssertCount('', 2, 2);
    });

    it('GIVEN I am on the trashed page list of the first staged catalog WHEN I open the drop down menu THEN I expect to see sync option on dropdown', function() {
        //GIVEN
        pageList.actions.openTrashedPageList();
        pageList.actions.openPageDropdownByPageName('Trashed Category Page');

        //THEN
        pageList.assertions.assertHasSyncOptionAvailableOnDropdown();
    });
});
