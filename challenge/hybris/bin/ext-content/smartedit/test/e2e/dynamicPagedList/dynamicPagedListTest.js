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
describe('dynamicPagedList - ', function() {

    var list;
    var NUMBER_OF_ARROWS_IN_PAGINATION_LIST = 4;

    describe('', function() {

        beforeEach(function() {
            list = require('../utils/pageObjects/PagedListPageObject.js');
            list.actions.openAndBeReady('dynamicPagedList', false);
        });

        it('GIVEN I am on the page that calls the dynamic paged list directive WHEN the page is fully loaded THEN I expect to see a paginated list of 10 pages max', function() {

            // Expect the page collection size to be 1000
            expect(list.elements.totalPageCount().getText()).toBe("(1000 se.pagelist.countsearchresult)");

            // Expect the number of page displayed to be 10
            expect(list.elements.displayedPageCount()).toBe(10);

            // Expect the list to be sorted by name, ascending
            expect(list.elements.firstRowForKey('name').getText()).toBe("item-1");
            list.actions.navigateToIndex(100);
            expect(list.elements.lastRowForKey('name').getText()).toBe("item-999");

            // Expect the pagination navigation to have 2 pages entries
            list.elements.paginationCount().then(function(totalCount) {
                var pageEntries = totalCount - NUMBER_OF_ARROWS_IN_PAGINATION_LIST;
                expect(pageEntries).toBe(100);
            });

        });

        it('GIVEN I am on the page that calls the dynamic paged list directive WHEN I search for a page THEN I expect the list to show the pages that match the query for any header', function() {

            // Perform a search
            list.assertions.searchAndAssertCount('-99', 10, 11);
            list.elements.paginationCount().then(function(totalCount) {
                var pageEntries = totalCount - NUMBER_OF_ARROWS_IN_PAGINATION_LIST;
                expect(pageEntries).toBe(2);
            });

            // Perform a search for key that does not exist
            list.assertions.searchAndAssertCount('uid1', 0, 0);

        });


        it('GIVEN I am on the page that calls the dynamic paged list directive WHEN I click on the name column header THEN I expect the list to be re-sorted by this key in the descending order', function(done) {

            // Sorting by name
            list.elements.firstRowForKey('name').getText().then(function(text) {
                expect(text.toLowerCase()).toBe("item-1");
                done();
            });

            list.actions.clickOnColumnHeader('name');
            list.elements.firstRowForKey('name').getText().then(function(text) {
                expect(text.toLowerCase()).toBe("item-999");
                done();
            });

            list.actions.navigateToIndex(100);
            expect(list.elements.lastRowForKey('name').getText()).toBe("item-1");

        });


        it('GIVEN I am on the page that calls the dynamic paged list directive WHEN I click on the uid column header THEN I expect the list to not be re-sorted as it is not sortable', function(done) {

            // Sorting by name
            list.elements.firstRowForKey('uid').getText().then(function(text) {
                expect(text.toLowerCase()).toBe("item-1");
                done();
            });

            list.actions.clickOnColumnHeader('uid');
            list.elements.firstRowForKey('name').getText().then(function(text) {
                expect(text.toLowerCase()).toBe("item-1");
                done();
            });

        });

        it('GIVEN I have registered a custom renderer for a key with an on click event and a callback function WHEN I click on this item THEN I expect the on click event to call the callback function', function() {

            // Click on the first page name link
            var firstPageNameLink = list.elements.elemForKeyAndRow('name', 1, 'a');
            browser.click(firstPageNameLink);

            // Expect the provided function in the on click to update the link style
            expect(firstPageNameLink.getAttribute('class')).toMatch('visited');
        });

        it('GIVEN I have registered a custom renderer for a key with inline styling WHEN I am on the page list THEN I expect the element to be rendered with my custom styling', function() {

            var firstPageNameLink = list.elements.elemForKeyAndRow('uid', 1, 'span');

            expect(firstPageNameLink.getAttribute('class')).toMatch('custom');

        });

        it('GIVEN I am on the page list WHEN permission service returns false THEN I expect to a drop down is not present', function() {
            var el = element.all(by.css('.y-dropdown-more-menu'));
            browser.waitForAbsence(el.get(0));
        });
    });

    describe('', function() {

        beforeEach(function() {
            list = require('../utils/pageObjects/PagedListPageObject.js');
            list.actions.openAndBeReady('dynamicPagedList', true);
        });

        it('GIVEN I am on the page list WHEN permission service returns true AND I click on more menu button THEN I expect to open a drop down with the list of actions', function() {
            var el = list.actions.openMoreMenuFirstElement();
            var dropDown = el.all(by.css('.dropdown-menu > li'));
            expect(dropDown.count()).toBe(1);
            expect(dropDown.get(0).getText()).toBe('pagelist.dropdown.edit');
        });

        it('GIVEN I am on the page list WHEN permission service returns true AND I click on more menu button AND click on the first one THEN I expect to execute a callback function to be called which chnages the style of the corresponding element', function() {
            var el = list.actions.openMoreMenuFirstElement();
            var dropDown = el.all(by.css('.dropdown-menu > li'));
            dropDown.get(0).getText().click();

            var firstPageNameLink = list.elements.elemForKeyAndRow('name', 1, 'a');
            expect(firstPageNameLink.getAttribute('class')).toMatch('link-clicked');
        });

    });
});
