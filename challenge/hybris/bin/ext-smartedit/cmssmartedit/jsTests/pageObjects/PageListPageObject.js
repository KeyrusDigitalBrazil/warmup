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
module.exports = function() {
    var landingPage = require('./landingPagePageObject');

    var NUMBER_OF_ARROWS_IN_PAGINATION_LIST = 4;
    var syncPanel = e2e.componentObjects.synchronizationPanel;
    var confirmationModal = e2e.componentObjects.confirmationModal;
    var pageList = {};
    var MOVETOTRASH_LABEL = "Move to Trash";

    pageList.selectors = {
        getColumnHeaderForKeySelector: function(key) {
            return by.css('.paged-list-table thead tr:first-child .paged-list-header-' + key);
        },

        getFirstRowForKeySelector: function(key) {
            return by.css('.paged-list-table tbody tr:first-child .paged-list-item-' + key);
        },

        getLastRowForKeySelector: function(key) {
            return by.css('.paged-list-table tbody tr:last-child .paged-list-item-' + key);
        },
        getTotalPageCountSelector: function() {
            return by.css('.paged-list-count span:first-child');
        },

        getRestrictionsTooltipSelector: function() {
            return by.css('.se-ypopover');
        },
    };

    pageList.elements = {
        getTotalPageCount: function() {
            browser.waitForPresence(element(by.css('.paged-list-count span:first-child')), 'cannot find page list count item');
            return element(by.css('.paged-list-count span:first-child'));
        },

        getDisplayedPageCount: function() {
            return element.all(by.css('.paged-list-table tbody tr')).count();
        },

        getPaginationCount: function() {
            return element.all(
                by.css('.pagination-container  > ul > li')).count().then(
                function(count) {
                    return count - NUMBER_OF_ARROWS_IN_PAGINATION_LIST;
                });
        },

        getPageDropdownMenu: function() {
            return element(by.css('ul.dropdown-menu'));
        },

        getDropdownSyncButton: function() {
            return element(
                by.cssContainingText('y-drop-down-menu .open li', 'Sync'));
        },

        getModalDialog: function() {
            return element(by.css('.modal-dialog'));
        },

        getModalSyncPanel: function() {
            return element(by.css('.modal-dialog synchronization-panel'));
        },

        getSynchronizableItemsForPage: function() {
            return element.all(
                by.css(".se-sync-panel .se-sync-panel__sync-info__row"));
        },

        getModalSyncPanelSyncButton: function() {
            return element(by.css('.modal-dialog #sync'));
        },

        getClickableModalSyncPanelSyncButton: function() {
            return element(by.css('.modal-dialog #sync:not([disabled])'));
        },

        getSyncedPageSyncIcon: function() {
            return element(by.css(
                '.paged-list-table__body > tr:nth-child(2) page-list-sync-icon .IN_SYNC'));
        },

        getFirstPageSyncIcon: function() {
            return element(by.css(
                '.paged-list-table__body > tr:first-child page-list-sync-icon .IN_SYNC'));
        },

        getSearchInput: function() {
            return element(by.css('.ySEPage-list-search > input'));
        },

        clearSearchFilter: function(searchKeys) {
            return browser.click(element(by.css('.glyphicon-remove-sign')));
        },

        getColumnHeaderForKey: function(key) {
            return element(pageList.selectors.getColumnHeaderForKeySelector(key));
        },

        getFirstRowForKey: function(key) {
            return element(pageList.selectors.getFirstRowForKeySelector(key));
        },

        getLastRowForKey: function(key) {
            return element(pageList.selectors.getLastRowForKeySelector(key));
        },

        getLinkForKeyAndRow: function(key, row, selector) {
            return element(by.css('.paged-list-table tbody tr:nth-child(' + row + ') .paged-list-item-' + key + ' ' + selector));
        },

        getCatalogName: function() {
            return element(by.css('.se-page-list__header h4'));
        },

        getRestrictionsIconForHomePage: function() {
            return element(by.cssContainingText('tr', 'homepage')).element(by.css('.restrictionPageListIcon'));
        },

        getRestrictionsIconForPageById: function(id) {
            return element(by.cssContainingText('tr', id)).element(by.css('img.restrictionPageListIcon'));
        },

        getRestrictionsTooltip: function() {
            return element(pageList.selectors.getRestrictionsTooltipSelector());
        },

        getCatalogPageListLink: function(catalog) {
            return element(by.css('.page-list-link-item a[data-ng-href*=' + catalog + ']'));
        },

        getAddNewPageButton: function() {
            return element(by.cssContainingText('.y-add-btn', 'add new page'));
        },

        getPageListTrashLink: function() {
            return element(by.css('.se-page-list__page-link--right '));
        },

        getRowByPageName: function(pageName) {
            return browser.findElement(pageList.utils.getPageRowQuery(pageName));
        },

        getDropDownButtonByPageName: function(pageName) {
            return pageList.elements.getRowByPageName(pageName).element(by.css('.dropdown-toggle'));
        },

        getTrashViewLink: function() {
            return browser.findElement(by.css(".se-page-list__page-link--right a"), true);
        },

        getFailureAlert: function() {
            return element(by.css('system-alerts .alert-danger'));
        },

        getDropdownButtonByName: function(buttonName) {
            return element(by.cssContainingText('y-drop-down-menu .open li', buttonName));
        },

        // Trashed Page List Items
        getDropdownPermanentlyDeleteButton: function() {
            return element(
                by.cssContainingText('y-drop-down-menu .open li', 'Permanently Delete'));
        },

        getDropdownRestoreButton: function() {
            var item = element(by.cssContainingText('y-drop-down-menu .open li', 'Restore'));
            browser.waitForPresence(item, "Expected Restore option to be available in the dropdown.");

            return item;
        }
    };

    pageList.actions = {
        navigateToFirstOnlineCatalogFromPageList: function() {
            browser.waitForPresence(element(by.css('.page-list-link-item a[data-ng-href*=Online]')));
            return element(by.css('.page-list-link-item a[data-ng-href*=Online]')).click();
        },

        navigateToFirstStagedCatalogPageList: function() {
            var pageListLink = pageList.elements.getCatalogPageListLink('Staged');
            browser.waitForPresence(pageListLink);
            return browser.click(pageListLink);
        },

        moveToRestrictionsIconForAdvertisePage: function() {
            return browser.actions()
                .mouseMove(pageList.elements.getRestrictionsIconForAdvertisePage())
                .perform();
        },

        moveToRestrictionsIconForHomePage: function() {
            browser.waitForPresence(pageList.elements.getRestrictionsIconForHomePage());
            return browser.actions()
                .mouseMove(pageList.elements.getRestrictionsIconForHomePage())
                .perform();
        },

        moveToRestrictionsIconForPageById: function(id) {
            browser.waitForPresence(pageList.elements.getRestrictionsIconForPageById(id));
            return browser.actions()
                .mouseMove(pageList.elements.getRestrictionsIconForPageById(id))
                .perform();
        },

        openPageDropdownByPageName: function(pageName) {
            return browser.click(pageList.elements.getDropDownButtonByPageName(pageName)).then(function() {
                return browser.waitForPresence(element(by.css(".dropdown-menu")));
            });
        },

        navigateToIndex: function(index) {
            return browser.executeScript('window.scrollTo(0,document.body.scrollHeight);').then(function() {
                return browser.click(by.css('.pagination-container  > ul > li:nth-child(' + (NUMBER_OF_ARROWS_IN_PAGINATION_LIST / 2 + index) + ') a')).then(function() {
                    return browser.waitUntilNoModal();
                });
            });
        },

        searchForPage: function(query, columnHeader, expectedNumber) {
            pageList.elements.getSearchInput().clear();
            pageList.elements.getSearchInput().sendKeys(query);

            pageList.assertions.totalPageCount(expectedNumber);
            expect(pageList.elements.getDisplayedPageCount()).toBe(expectedNumber);

            pageList.elements.getFirstRowForKey(columnHeader).getText().then(function(text) {
                expect(text.toLowerCase().indexOf(query) >= 0).toBeTruthy();
            });
        },

        syncPageFromSyncModal: function(pageName) {
            pageList.actions.openPageDropdownByPageName(pageName);
            pageList.actions.openSyncModalFromActiveDropdown();
            browser.waitForAbsence(element(by.css("body > .modal.ng-animate")));
            syncPanel.checkItem('All Slots and Page Information');
            pageList.actions.clickSyncPageModalSyncButton();
            browser.waitForPresence(by.css(".se-sync-panel__sync-info__row.active span[data-status='IN_SYNC']"));

        },

        clickOnColumnHeader: function(key) {
            return browser.click(pageList.selectors.getColumnHeaderForKeySelector(key)).then(function() {
                return browser.waitUntilNoModal();
            });
        },

        clickSyncPageModalSyncButton: function() {
            browser.click(pageList.elements.getClickableModalSyncPanelSyncButton());

        },

        openSyncModalFromActiveDropdown: function() {
            browser.waitForPresence(pageList.elements.getDropdownSyncButton(), "Expected sync option to be available in the dropdown.");
            browser.click(pageList.elements.getDropdownSyncButton(), "Could not click on the sync option in the dropdown.");
            browser.waitForPresence(pageList.elements.getModalDialog(), "Expected the presence of a modal window.");
            browser.waitForPresence(pageList.elements.getModalSyncPanel(), "Expected the presence of a synchronization panel inside the modal.");
        },

        openTrashedPageList: function() {
            return browser.click(pageList.elements.getPageListTrashLink());
        },

        clickOnTrashViewLink: function() {
            return browser.click(pageList.elements.getTrashViewLink());
        },

        bringTrashViewLinkIntoView: function() {
            return browser.bringElementIntoView(pageList.elements.getTrashViewLink());
        },

        // Trashed Page List Items
        permanentlyDeletePageByName: function(pageName) {
            pageList.actions.openPageDropdownByPageName(pageName);
            pageList.actions.showConfirmationModalForPermanentDelete();
            browser.waitForAbsence(element(by.css("body > .modal.ng-animate")));
            confirmationModal.actions.confirmConfirmationModal();

        },

        showConfirmationModalForPermanentDelete: function() {
            browser.waitForPresence(pageList.elements.getDropdownPermanentlyDeleteButton(), "Expected Permanently Delete option to be available in the dropdown.");
            browser.click(pageList.elements.getDropdownPermanentlyDeleteButton(), "Could not click on the Permanently Delete option in the dropdown.");
            browser.waitForPresence(pageList.elements.getModalDialog(), "Expected the presence of a modal window.");
        },

        restorePageByName: function(pageName) {
            return pageList.actions.openPageDropdownByPageName(pageName).then(function() {
                return browser.click(pageList.elements.getDropdownRestoreButton(), "Could not click on the Restore option in the dropdown.");
            });
        }
    };

    pageList.assertions = {
        assertRestrictionIconForHomePageIsDisabled: function() {
            expect(pageList.elements.getRestrictionsIconForHomePage().getAttribute('data-ng-src')).toContain('icon_restriction_small_grey.png');
        },
        assertPageListIsDisplayed: function() {
            expect(pageList.elements.getAddNewPageButton().isPresent()).toBe(true);
        },
        assertOnPageSyncIconStatusByPageIndex: function(pageIndex, expectedStatus) {
            browser.waitForPresence(element(by.css(".paged-list-table__body .paged-list-item")));
            var actualStatus = null;
            browser.waitUntil(function() {
                return element(by.css('.paged-list-table__body > tr:nth-child(' + pageIndex + ') page-list-sync-icon span')).getAttribute("data-sync-status").then(function(_actualStatus) {
                    actualStatus = _actualStatus;
                    return actualStatus.indexOf(expectedStatus) > -1;
                });
            }, "Expected sync status to be " + expectedStatus + " but got " + actualStatus);
        },
        assertHasSynchronizableItems: function() {
            browser.waitUntil(function() {
                return pageList.elements.getSynchronizableItemsForPage().count().then(function(count) {
                    return count > 0;
                });
            }, "Expected at least one synchronizable item for the page.");
        },
        assertHasSyncOptionAvailableOnDropdown: function() {
            browser.waitForPresence(pageList.elements.getDropdownSyncButton(), "Expected sync option to be available in the dropdown.");
            expect(pageList.elements.getDropdownSyncButton().isPresent()).toBe(true, "Expected the presence of sync option in the dropdown menu");
        },
        searchAndAssertCount: function(query, displayedResults, totalResults) {
            pageList.elements.getSearchInput().clear();
            pageList.elements.getSearchInput().sendKeys(query);
            browser.waitUntilNoModal().then(function() {
                browser.waitUntil(function() {
                    return pageList.elements.getTotalPageCount().isPresent().then(function(isPresent) {
                        if (isPresent) {
                            return pageList.elements.getTotalPageCount().getText().then(function(text) {
                                return text.indexOf("(" + totalResults + " Pages found)") !== -1;
                            });
                        }
                        return false;
                    });
                });
                pageList.assertions.totalPageCount(totalResults);
                expect(pageList.elements.getDisplayedPageCount()).toBe(displayedResults);
            });

        },
        assertTotalTrashedpagesCountInButtonText: function(totalCount) {
            return pageList.elements.getPageListTrashLink().getText().then(function(buttonText) {
                expect(buttonText).toContain(totalCount);
            });
        },
        trashPagesCountEquals: function(expectedCount) {
            expect(pageList.elements.getTrashViewLink().getText()).toBe("TRASH (" + expectedCount + " PAGES)");
        },
        pageRowIsRenderedByPageName: function(pageName) {
            expect(pageList.elements.getRowByPageName(pageName)).toBeDisplayed();
        },
        pageRowIsNotRenderedByPageName: function(pageName) {
            expect(pageList.utils.getPageRowQuery(pageName)).toBeAbsent();
        },
        assertTrashFailure: function() {
            expect(browser.waitToBeDisplayed(pageList.elements.getFailureAlert())).toBeTruthy();
        },

        firstRowColumnContainText: function(column, value) {
            browser.waitForSelectorToContainText(pageList.selectors.getFirstRowForKeySelector(column), value);
        },

        lastRowColumnContainText: function(column, value) {
            browser.waitForSelectorToContainText(pageList.selectors.getLastRowForKeySelector(column), value);
        },

        totalPageCount: function(count) {
            browser.waitForPresence(element(by.css('.paged-list-count span:first-child')), 'cannot find page list count item');
            browser.waitForSelectorToContainText(pageList.selectors.getTotalPageCountSelector(), "(" + count + " Pages found)");
        },

        restrictionTooltipToContain: function(count) {
            browser.waitForSelectorToContainText(pageList.selectors.getRestrictionsTooltipSelector(), count + ' restrictions');
        },
        permanentlyDeleteButtonCanNotBeClicked: function(pageName) {
            pageList.actions.openPageDropdownByPageName(pageName);
            browser.waitForPresence(pageList.elements.getDropdownPermanentlyDeleteButton().element(by.css('a.se-dropdown-item__disabled')));
        },
        assertOptionNotAvailableOnDropdown: function(buttonName) {
            browser.waitForAbsence(pageList.elements.getDropdownButtonByName(buttonName));
        },
    };


    pageList.utils = {

        getPageRowQuery: function(pageName) {
            return by.cssContainingText('.paged-list-item', pageName);
        }
    };

    return pageList;
}();
