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
var pageList = require('./clientPagedListObject.js');

describe('clientPagedList - ', function() {

    var list;
    var NUMBER_OF_ARROWS_IN_PAGINATION_LIST = 4;

    describe('', function() {
        beforeEach(function() {
            list = new pageList();
        });

        it('GIVEN I am on the page that calls the client paged list directive WHEN the page is fully loaded THEN I expect to see a paginated list of 10 pages max, sorted by name ascending', function() {

            // Expect the page collection size to be 12
            expect(list.totalPageCount().getText()).toBe("(12 se.pagelist.countsearchresult)");

            // Expect the number of page displayed to be 10
            expect(list.displayedPageCount()).toBe(10);

            // Expect the list to be sorted by name, ascending
            expect(list.firstRowForKey('name').getText()).toBe("Advertise");
            navigateToIndex(2);
            expect(list.lastRowForKey('name').getText()).toBe("welcomePage");

            // Expect the pagination navigation to have 2 pages entries
            list.paginationCount().then(function(totalCount) {
                var pageEntries = totalCount - NUMBER_OF_ARROWS_IN_PAGINATION_LIST;
                expect(pageEntries).toBe(2);
            });

        });

        it('GIVEN I am on the page that calls the client paged list directive WHEN I search for a page THEN I expect the list to show the pages that match the query for any header', function() {

            // Perform a search on a name
            searchForPage('welcomepage', 'name', 1);
            list.paginationCount().then(function(totalCount) {
                var pageEntries = totalCount - NUMBER_OF_ARROWS_IN_PAGINATION_LIST;
                expect(pageEntries).toBe(1);
            });

            // Perform a search on a page UID
            searchForPage('uid1', 'uid', 4);

            // Perform a search on a page type
            searchForPage('product', 'typeCode', 4);

            // Perform a search on a page template
            searchForPage('mycustompagetemplate', 'template', 1);
            list.paginationCount().then(function(totalCount) {
                var pageEntries = totalCount - NUMBER_OF_ARROWS_IN_PAGINATION_LIST;
                expect(pageEntries).toBe(1);
            });

        });


        it('GIVEN I am on the page that calls the client paged list directive WHEN I click on the name column header THEN I expect the list to be re-sorted by this key in the descending order', function() {

            // Sorting by name
            clickOnColumnHeader('name');
            list.firstRowForKey('name').getText().then(function(text) {
                expect(text.toLowerCase()).toBe("welcomepage");
            });
            navigateToIndex(2);
            list.lastRowForKey('name').getText().then(function(text) {
                expect(text.toLowerCase()).toBe("advertise");
            });

        });

        it('GIVEN I am on the page that calls the client paged list directive WHEN I click on the UID column header THEN I expect the list to be re-sorted by this key in the descending order', function() {

            // Sorting by uid
            clickOnColumnHeader('uid');
            expect(list.firstRowForKey('uid').getText()).toBe("zuid12");
            navigateToIndex(2);
            expect(list.lastRowForKey('uid').getText()).toBe("auid1");

        });

        it('GIVEN I am on the page that calls the client paged list directive WHEN I click on the page type column header THEN I expect the list to be re-sorted by this key in the descending order', function() {

            // Sorting by page type
            clickOnColumnHeader('typeCode');
            expect(list.firstRowForKey('typeCode').getText()).toBe("WallPage");
            navigateToIndex(2);
            expect(list.lastRowForKey('typeCode').getText()).toBe("ActionPage");

        });

        it('GIVEN I am on the page that calls the client paged list directive WHEN I click on the name column header THEN I expect the list to be re-sorted by this key in the descending order', function() {

            // Sorting by page template
            clickOnColumnHeader('template');
            expect(list.firstRowForKey('template').getText()).toBe("ZTemplate");
            navigateToIndex(2);
            expect(list.lastRowForKey('template').getText()).toBe("ActionTemplate");

        });

        it('GIVEN I have registered a custom renderer for a key with an on click event and a callback function WHEN I click on this item THEN I expect the on click event to call the callback function', function() {

            // Click on the first page name link
            var firstPageNameLink = list.elemForKeyAndRow('name', 1, 'a');
            browser.click(firstPageNameLink);

            // Expect the provided function in the on click to update the link style
            expect(firstPageNameLink.getAttribute('class')).toMatch('visited');
        });

        it('GIVEN I have registered a custom renderer for a key with inline styling WHEN I am on the page list THEN I expect the element to be rendered with my custom styling', function() {

            var firstPageNameLink = list.elemForKeyAndRow('uid', 1, 'span');

            expect(firstPageNameLink.getAttribute('class')).toMatch('custom');

        });
    });

    describe('', function() {
        it('GIVEN I am on the page list WHEN permission service returns true AND I click on more menu button THEN I expect to open a drop down with the list of actions', function() {
            list = new pageList(true);
            var el = list.openMoreMenuFirstElement();
            var dropDown = el.all(by.css('.dropdown-menu > li'));
            expect(dropDown.count()).toBe(1);
            expect(dropDown.get(0).getText()).toBe('pagelist.dropdown.edit');
        });

        it('GIVEN I am on the page list WHEN permission service returns true AND I click on more menu button AND click on the first one THEN I expect to execute a callback function to remove the items', function() {
            list = new pageList(true);
            list.clickOnFirstDropdownItemOfFirstElement();
            expect(list.totalPageCount().getText()).toBe("(0 se.pagelist.countsearchresult)");
        });

        it('GIVEN I am on the page list WHEN permission service returns false THEN I expect to a drop down is not present', function() {
            list = new pageList(false);
            var el = element.all(by.css('.y-dropdown-more-menu'));
            browser.waitForAbsence(el.get(0));
        });

    });

    function navigateToIndex(index) {
        return browser.executeScript('window.scrollTo(0,document.body.scrollHeight);').then(function() {
            browser.click(element(by.css('.pagination-container  > ul > li:nth-child(' + (NUMBER_OF_ARROWS_IN_PAGINATION_LIST / 2 + index) + ') a')));
        });
    }

    function searchForPage(query, columnHeader, expectedNumber) {
        list.searchInput().clear();
        list.searchInput().sendKeys(query);

        expect(list.totalPageCount().getText()).toBe("(" + expectedNumber.toString() + " se.pagelist.countsearchresult)");
        expect(list.displayedPageCount()).toBe(expectedNumber);

        list.firstRowForKey(columnHeader).getText().then(function(text) {
            expect(text.toLowerCase().indexOf(query) >= 0).toBeTruthy();
        });
    }

    function clickOnColumnHeader(key) {
        browser.click(list.columnHeaderForKey(key));
    }
});
