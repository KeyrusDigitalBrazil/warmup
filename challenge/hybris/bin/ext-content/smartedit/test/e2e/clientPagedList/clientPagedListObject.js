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
var PageList = function(PERSPECTIVE_SERVICE_RESULT) {
    PERSPECTIVE_SERVICE_RESULT = PERSPECTIVE_SERVICE_RESULT || false;
    this.pageURI = 'test/e2e/clientPagedList/clientPagedList.html?perspectiveServiceResult=' + PERSPECTIVE_SERVICE_RESULT;
    browser.get(this.pageURI);
};

PageList.prototype = {

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
    },
    openMoreMenuFirstElement: function() {
        return element.all(by.css('.y-dropdown-more-menu')).first().click();
    },
    getDropdownMenuItems: function(openedDropdownMenu) {
        return openedDropdownMenu.all(by.css('.dropdown-menu > li a'));
    },
    clickOnFirstDropdownItemOfFirstElement: function() {
        var openedDropdownMenu = this.openMoreMenuFirstElement();
        browser.click(this.getDropdownMenuItems(openedDropdownMenu).first());
    }

};

module.exports = PageList;
