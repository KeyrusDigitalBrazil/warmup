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
describe('Page Type Permissions:', function() {

    var perspective = e2e.componentObjects.modeSelector;
    var toolbarItem = e2e.componentObjects.toolbarItem;
    var pageInfo = e2e.pageObjects.PageInfo;
    var landingPage = e2e.pageObjects.landingPage;
    var pageList = e2e.pageObjects.PageList;

    describe('Storefront View - ', function() {

        beforeEach(function(done) {
            browser.bootstrap(__dirname).then(function() {
                landingPage.actions.navigateToCatalogHomepage(landingPage.constants.APPAREL_UK_CATALOG, landingPage.constants.STAGED_CATALOG_VERSION).then(function() {
                    browser.waitForWholeAppToBeReady().then(function() {
                        perspective.select(perspective.ADVANCED_CMS_PERSPECTIVE);
                        done();
                    });
                });
            });
        });

        it('GIVEN current user has no change permissions on content page WHEN homepage is loaded THEN the user cannot see the "add component" menu button', function() {
            toolbarItem.assertions.assertToolbarItemByNameIsNotPresent('Add Component');
        });

        it('GIVEN current user has no change permissions on content page WHEN homepage is loaded THEN the user cannot see the "move to trash" menu button', function() {
            toolbarItem.assertions.assertToolbarItemByNameIsNotPresent('Move to Trash');
        });

        it('GIVEN current user has no create permissions on content page WHEN homepage is loaded THEN the user cannot see the "clone page" menu button', function() {
            toolbarItem.assertions.assertToolbarItemByNameIsNotPresent('Clone Page');
        });

        it('GIVEN current user has no change permissions on content page WHEN homepage is loaded & user opens page info THEN the user cannot see the edit page link', function() {
            // WHEN
            pageInfo.actions.openPageInfoMenu();

            // THEN
            pageInfo.assertions.hasNoEditButton();
        });

    });

    describe('Pages View - ', function() {

        beforeEach(function(done) {
            browser.bootstrap(__dirname).then(function() {
                landingPage.actions.navigateToFirstStagedCatalogPageList().then(function() {
                    return browser.waitForContainerToBeReady();
                }).then(function() {
                    return browser.waitUntilNoModal();
                }).then(function() {
                    return pageList.actions.openPageDropdownByPageName('Homepage');
                }).then(function() {
                    done();
                });
            });
        });

        it('GIVEN current user has no change permissions on content page WHEN menu for "Homepage" on pages is opened THEN the user cannot see the "Edit" menu item', function() {
            pageList.assertions.assertOptionNotAvailableOnDropdown('Edit');
        });

        it('GIVEN current user has no change permissions on content page WHEN menu for "Homepage" on pages is opened THEN the user cannot see the "Move to Trash" menu item', function() {
            pageList.assertions.assertOptionNotAvailableOnDropdown('Move to Trash');
        });

        it('GIVEN current user has no create permissions on content page WHEN menu for "Homepage" on pages is opened THEN the user cannot see the "Clone" menu item', function() {
            pageList.assertions.assertOptionNotAvailableOnDropdown('Clone');
        });

    });

    describe('Trash Pages View - ', function() {

        beforeEach(function(done) {
            browser.bootstrap(__dirname).then(function() {
                landingPage.actions.navigateToFirstStagedCatalogPageList().then(function() {
                    return browser.waitForContainerToBeReady();
                }).then(function() {
                    return browser.waitUntilNoModal();
                }).then(function() {
                    return pageList.actions.openTrashedPageList();
                }).then(function() {
                    return browser.waitUntilNoModal();
                }).then(function() {
                    return pageList.actions.openPageDropdownByPageName('Trashed Content Page');
                }).then(function() {
                    done();
                });
            });
        });

        it('GIVEN current user has no change permissions on content page WHEN menu for "Trashed Content Page" on pages is opened THEN the user cannot see the "Restore" menu item', function() {
            pageList.assertions.assertOptionNotAvailableOnDropdown('Restore');
        });

        it('GIVEN current user has no remove permissions on content page WHEN menu for "Trashed Content Page" on pages is opened THEN the user cannot see the "Permanently Delete" menu item', function() {
            pageList.assertions.assertOptionNotAvailableOnDropdown('Permanently Delete');
        });

    });




});
