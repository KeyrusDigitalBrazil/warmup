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
module.exports = (function() {

    var NUMBER_OF_ARROWS_IN_PAGINATION_LIST = 4;

    var componentObject = {};

    componentObject.elements = {
        totalPageCount: function() {
            return element(by.css('.paged-list-count span:first-child'));
        },
        displayedPageCount: function() {
            return element.all(by.css('.paged-list-table tbody tr')).count();
        },
        paginationCount: function() {
            return element.all(by.css('.pagination-container  > ul > li')).count();
        },
        searchInput: function() {
            return element(by.css('.page-list-search > input'));
        },
        columnHeaderForKey: function(key) {
            return element(by.css('.paged-list-table thead tr:first-child .paged-list-header-' + key));
        },
        firstRowForKey: function(key) {
            return element(by.css('.paged-list-table tbody tr:first-child .paged-list-item-' + key));
        },
        lastRowForKey: function(key) {
            return element(by.css('.paged-list-table tbody tr:last-child .paged-list-item-' + key));
        },
        elemForKeyAndRow: function(key, row, selector) {
            return element(by.css('.paged-list-table tbody tr:nth-child(' + row + ') .paged-list-item-' + key + ' ' + selector));
        },
        catalogName: function() {
            return element(by.css('.se-page-list__header h4'));
        }
    };

    componentObject.actions = {
        openAndBeReady: function(pageListType, PERSPECTIVE_SERVICE_RESULT) {
            PERSPECTIVE_SERVICE_RESULT = PERSPECTIVE_SERVICE_RESULT || false;

            componentObject.pageURI = '';
            if (pageListType === 'clientPagedList') {
                componentObject.pageURI = 'test/e2e/clientPagedList/clientPagedList.html?perspectiveServiceResult=' + PERSPECTIVE_SERVICE_RESULT;
            } else if (pageListType === 'dynamicPagedList') {
                componentObject.pageURI = 'test/e2e/dynamicPagedList/dynamicPagedList.html?perspectiveServiceResult=' + PERSPECTIVE_SERVICE_RESULT;
            }

            browser.get(componentObject.pageURI);
        },
        openMoreMenuFirstElement: function() {
            return element.all(by.css('.y-dropdown-more-menu')).first().click();
        },
        searchForPage: function(query, expectedNumber) {
            componentObject.elements.searchInput().clear();
            componentObject.elements.searchInput().sendKeys(query);

            expect(componentObject.elements.totalPageCount().getText()).toBe("(" + expectedNumber.toString() + " se.pagelist.countsearchresult)");
            expect(componentObject.elements.displayedPageCount()).toBe(expectedNumber);
        },
        navigateToIndex: function(index) {
            return browser.executeScript('window.scrollTo(0,document.body.scrollHeight);').then(function() {
                browser.click(element(by.css('.pagination-container  > ul > li:nth-child(' + (NUMBER_OF_ARROWS_IN_PAGINATION_LIST / 2 + index) + ') a')));
            });
        },
        clickOnColumnHeader: function(key) {
            browser.click(componentObject.elements.columnHeaderForKey(key));
        }
    };

    componentObject.assertions = {
        searchAndAssertCount: function(query, displayedResults, totalResults) {
            componentObject.elements.searchInput().clear();
            componentObject.elements.searchInput().sendKeys(query);

            expect(componentObject.elements.totalPageCount().getText()).toBe("(" + totalResults.toString() + " se.pagelist.countsearchresult)");
            expect(componentObject.elements.displayedPageCount()).toBe(displayedResults);
        }
    };


    return componentObject;

})();
