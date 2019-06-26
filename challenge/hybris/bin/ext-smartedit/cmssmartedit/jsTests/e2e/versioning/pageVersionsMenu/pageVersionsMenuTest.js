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
describe('Page Versions Menu - \n', function() {

    // ---------------------------------------------------------------
    // Variables
    // ---------------------------------------------------------------
    var page = e2e.pageObjects.PageVersioningMenu;
    var perspective = e2e.componentObjects.modeSelector;
    var pageVersionsMenu = e2e.componentObjects.pageVersions;
    var confirmationModal = e2e.componentObjects.confirmationModal;
    var genericEditor = e2e.componentObjects.genericEditor;
    var landingPage = e2e.pageObjects.landingPage;
    var sitesLink = e2e.componentObjects.sitesLink;

    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    var EDITOR_TITLE_KEY = 'se.cms.versions.edit';
    var VERSION_UPDATE_DETAILS = [{
        qualifier: 'label',
        type: 'shortString',
        value: 'New - Version 2 Updated'
    }];

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------
    describe('Page has no versions -\n', function() {

        beforeEach(function(done) {
            allCmsVersionTypePermissions();
            page.actions.navigateToPage(true, __dirname, done);
        });

        it('GIVEN advanced edit mode is selected WHEN the user opens the menu THEN it shows an empty message AND a manage versions button', function() {
            // GIVEN 
            perspective.selectAdvancedPerspective();

            // WHEN 
            pageVersionsMenu.actions.openMenu();

            // THEN
            pageVersionsMenu.assertions.versionsListIsEmpty();
            pageVersionsMenu.assertions.manageVersionsButtonIsDisplayed();
        });

        it('GIVEN versioning mode is selected WHEN the user opens the menu THEN it shows an empty message AND no manage versions button', function() {
            // GIVEN 
            perspective.selectVersioningPerspective();

            // WHEN 
            pageVersionsMenu.actions.openMenu();

            // THEN
            pageVersionsMenu.assertions.versionsListIsEmpty();
            pageVersionsMenu.assertions.manageVersionsButtonIsNotDisplayed();
        });
    });

    describe('Page has versions -\n', function() {

        beforeEach(function(done) {
            allCmsVersionTypePermissions();
            page.actions.navigateToPage(false, __dirname, done);
        });

        it('GIVEN advanced edit mode is selected WHEN the user opens the menu THEN it has a manage versions button', function() {
            // GIVEN
            perspective.selectAdvancedPerspective();

            // WHEN
            pageVersionsMenu.actions.openMenu();

            // THEN
            pageVersionsMenu.assertions.versionsListIsNotEmpty();
            pageVersionsMenu.assertions.manageVersionsButtonIsDisplayed();
        });

        it('GIVEN versioning mode is selected WHEN the user opens the menu THEN it does not have a manage versions button', function() {
            // GIVEN
            perspective.selectVersioningPerspective();

            // WHEN
            pageVersionsMenu.actions.openMenu();

            // THEN
            pageVersionsMenu.assertions.versionsListIsNotEmpty();
            pageVersionsMenu.assertions.manageVersionsButtonIsNotDisplayed();
        });

        it('GIVEN versioning mode is selected WHEN the user clicks on manage versions THEN it is moved to the versioning mode', function() {
            // GIVEN
            perspective.selectAdvancedPerspective();

            // WHEN
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.clickManageVersionsButton();

            // THEN
            perspective.assertions.expectedModeIsSelected(perspective.VERSIONING_PERSPECTIVE);
        });

        it('GIVEN the page has more versions that can be displayed WHEN the user opens the menu THEN he can scroll AND the new versions appear', function() {
            // GIVEN
            var searchTerm = '';

            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            pageVersionsMenu.assertions.menuHasExpectedPageVersions(searchTerm, 1); // 1 page (batch) of versions expected to be loaded

            // WHEN
            pageVersionsMenu.actions.scrollPageVersions(2);

            // THEN
            pageVersionsMenu.assertions.menuHasExpectedPageVersions(searchTerm, 2); // 2 pages (batches) of versions expected to be loaded
        });

        it('WHEN the user searches for a specific page version THEN the list is filtered to show the right results', function() {
            // GIVEN
            var searchTerm = 'New';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            // WHEN
            pageVersionsMenu.actions.searchVersion(searchTerm);

            // THEN
            pageVersionsMenu.assertions.menuHasExpectedPageVersions(searchTerm, 1);
        });

        it('WHEN the user clicks a page version in the menu THEN the version is selected AND displayed in the toolbar context', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            // WHEN
            pageVersionsMenu.assertions.pageVersionToolbarContextIsNotDisplayed();
            pageVersionsMenu.actions.selectPageVersionByLabel(label);

            // THEN
            pageVersionsMenu.assertions.toolbarContextHasPageVersionSelected(label);
        });
        it('GIVEN I have a page version selected WHEN I go back to the landing page and then come back to the page THEN no version is selected', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.selectPageVersionByLabel(label);
            pageVersionsMenu.assertions.toolbarContextHasPageVersionSelected(label);

            // WHEN
            sitesLink.actions.openSitesPage();
            landingPage.actions.navigateToCatalogHomepage(landingPage.constants.APPAREL_UK_CATALOG, landingPage.constants.STAGED_CATALOG_VERSION);

            // THEN
            pageVersionsMenu.assertions.pageVersionToolbarContextIsNotDisplayed();

        });

        it('GIVEN a version is selected WHEN the user hovers the toolbar context THEN the description is displayed', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();

            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.selectPageVersionByLabel(label);

            pageVersionsMenu.assertions.pageVersionToolbarContextIsNotDisplayed();

            // WHEN
            pageVersionsMenu.actions.hoverOverPageVersionToolbarContext();

            // THEN
            pageVersionsMenu.assertions.pageVersionToolbarContextPopoverIsDisplayed();
        });

        it('GIVEN a version is selected WHEN the user clicks on the version remove button THEN the version is removed', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            pageVersionsMenu.actions.selectPageVersionByLabel(label);
            pageVersionsMenu.assertions.toolbarContextHasPageVersionSelected(label);

            // WHEN
            pageVersionsMenu.actions.removeSelectedPageVersion();

            // THEN
            pageVersionsMenu.assertions.pageVersionToolbarContextPopoverIsNotDisplayed();
        });

    });

    describe('Delete Page Version Menu Item -\n', function() {

        beforeEach(function(done) {
            allCmsVersionTypePermissions();
            page.actions.navigateToPage(false, __dirname, done);
        });

        it('WHEN the user tries to delete a page version that has not been loaded in the versioning mode THEN the version will be successfully deleted', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            // WHEN
            pageVersionsMenu.actions.deletePageVersion(label);
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.assertions.deletedPageVersionIsNotAvailableInTheMenu(label);
        });

        it('WHEN the user tries to delete a page version that he/she is previewing in the versioning mode THEN the version will be successfully deleted and no page version is selected', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();


            pageVersionsMenu.actions.selectPageVersionByLabel(label);
            pageVersionsMenu.assertions.toolbarContextHasPageVersionSelected(label);

            // WHEN
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.deletePageVersion(label);
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            pageVersionsMenu.assertions.pageVersionToolbarContextIsNotDisplayed();

            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.assertions.deletedPageVersionIsNotAvailableInTheMenu(label);
        });

        it('WHEN the user tries to delete a page version that has a backend error THEN the version will not be deleted', function() {
            // GIVEN
            var label = 'Other - Version 4';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            // WHEN
            pageVersionsMenu.actions.deletePageVersion(label);
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.assertions.deletedPageVersionIsAvailableInTheMenu(label);
        });

    });

    describe('Edit Page Version Menu Item -\n', function() {

        beforeEach(function(done) {
            allCmsVersionTypePermissions();
            page.actions.navigateToPage(false, __dirname, done);
        });

        it('WHEN the user tries to edit a page version information that has not been loaded in the versioning mode THEN the version will be successfully edit and its details are updated', function() {
            // GIVEN
            var label = 'New - Version 2';
            var updated_label = 'New - Version 2 Updated';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.clickEditVersionItemMenuButton(label);

            // WHEN
            genericEditor.actions.waitForEditorModalWithTitleToBeOpen(EDITOR_TITLE_KEY);
            genericEditor.actions.setEditorData(VERSION_UPDATE_DETAILS);
            genericEditor.actions.save();
            pageVersionsMenu.actions.waitForEditorModalWithTitleToBeClosed(EDITOR_TITLE_KEY);

            // THEN
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.searchVersion(updated_label);
            expect(pageVersionsMenu.elements.getVersionsCount()).toBe("1 Versions");
        });

        it('WHEN the user tries to edit a page version information that has been loaded in the versioning mode THEN the version will be successfully edit and its details are updated on the context', function() {
            // GIVEN
            var label = 'New - Version 2';
            var updated_label = 'New - Version 2 Updated';
            perspective.selectVersioningPerspective();

            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.selectPageVersionByLabel(label);
            pageVersionsMenu.assertions.toolbarContextHasPageVersionSelected(label);


            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.clickEditVersionItemMenuButton(label);

            // WHEN
            genericEditor.actions.waitForEditorModalWithTitleToBeOpen(EDITOR_TITLE_KEY);
            genericEditor.actions.setEditorData(VERSION_UPDATE_DETAILS);
            genericEditor.actions.save();
            pageVersionsMenu.actions.waitForEditorModalWithTitleToBeClosed(EDITOR_TITLE_KEY);

            // THEN
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.searchVersion(updated_label);
            expect(pageVersionsMenu.elements.getVersionsCount()).toBe("1 Versions");
            pageVersionsMenu.assertions.toolbarContextHasPageVersionSelected(updated_label);
        });

    });

    describe('View Page Version Menu Item -\n', function() {

        beforeEach(function(done) {
            allCmsVersionTypePermissions();
            page.actions.navigateToPage(false, __dirname, done);
        });

        it('WHEN the user tries to view a page version by clicking "View" in th menu item THEN the version is selected AND displayed in the toolbar context', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            // WHEN
            pageVersionsMenu.assertions.pageVersionToolbarContextIsNotDisplayed();
            pageVersionsMenu.actions.clickViewVersionItemMenuButton(label);

            // THEN
            pageVersionsMenu.assertions.toolbarContextHasPageVersionSelected(label);
        });

    });

    describe('Rollback Page Version Menu Item -\n', function() {

        beforeEach(function(done) {
            allCmsVersionTypePermissions();
            page.actions.navigateToPage(false, __dirname, done);
        });

        it('WHEN the user tries to rollback a page version by clicking "Rollback to this version" in th menu item THEN the version is rolled back AND successful alert is displayed', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            // WHEN
            pageVersionsMenu.actions.clickRollbackVersionItemMenuButton(label);
            confirmationModal.actions.confirmConfirmationModal();

            // THEN
            pageVersionsMenu.assertions.rollbackVersionSuccessAlertIsDisplayed();
        });

    });

    describe('User does not have edit CMSVersion permission', function() {
        beforeEach(function(done) {
            noChangeCMSVersionPermission();
            page.actions.navigateToPage(false, __dirname, done);
        });
        it('GIVEN user without EDIT permission WHEN versions menu is open THEN edit button is not available', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();

            // WHEN
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.openItemMenuByLabel(label);

            // THEN
            pageVersionsMenu.assertions.editMenuButtonIsAbsent();
        });
    });

    describe('User does not have delete CMSVersion permission', function() {
        beforeEach(function(done) {
            noRemoveCMSVersionPermission();
            page.actions.navigateToPage(false, __dirname, done);
        });
        it('GIVEN user without DELETE permission WHEN versions menu is open THEN edit button is not available', function() {
            // GIVEN
            var label = 'New - Version 2';
            perspective.selectVersioningPerspective();

            // WHEN
            pageVersionsMenu.actions.openMenu();
            pageVersionsMenu.actions.openItemMenuByLabel(label);

            // THEN
            pageVersionsMenu.assertions.deleteMenuButtonIsAbsent();
        });
    });

    function allCmsVersionTypePermissions() {
        pageVersionsMenu.utils.setCMSVersionTypePermission({
            read: true,
            change: true,
            create: true,
            remove: true
        });
    }

    function noChangeCMSVersionPermission() {
        pageVersionsMenu.utils.setCMSVersionTypePermission({
            read: true,
            change: false,
            create: true,
            remove: true
        });
    }

    function noRemoveCMSVersionPermission() {
        pageVersionsMenu.utils.setCMSVersionTypePermission({
            read: true,
            change: true,
            create: true,
            remove: false
        });
    }

});
