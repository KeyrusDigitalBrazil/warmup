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
describe('Create Version Menu - \n', function() {

    var page = e2e.pageObjects.PageVersioningMenu;
    var perspective = e2e.componentObjects.modeSelector;
    var pageVersionsMenu = e2e.componentObjects.pageVersions;
    var genericEditor = e2e.componentObjects.genericEditor;
    var editorModal = e2e.componentObjects.editorModal;

    var VERSION_INVALID_DATA_DUPLICATE_LABEL = [{
        qualifier: 'label',
        type: 'shortString',
        value: 'Version 1'
    }, {
        qualifier: 'description',
        type: 'shortString',
        value: 'The provided label already exists in pageVersionMock, hence the validation will fail.'
    }];
    var VERSION_INVALID_DATA_MISSING_MANDATORY = [{
        qualifier: 'description',
        type: 'shortString',
        value: 'This version does not have any label, hence the validation will fail.'
    }];
    var VERSION_VALID_DATA = [{
        qualifier: 'label',
        type: 'shortString',
        value: 'New Test Version'
    }, {
        qualifier: 'description',
        type: 'shortString',
        value: 'First page version for homepage'
    }];
    var VERSION_VALID_LABEL = 'New - Version 2';
    var EDITOR_TITLE_KEY = 'se.cms.versions.create';
    var FIELD_WITH_ERROR_LABEL = 'label';
    var NOT_LOCALIZED = null;

    describe('Do not consider type permissions', function() {
        beforeEach(function(done) {
            allCmsVersionTypePermissions();
            page.actions.navigateToPage(true, __dirname, done);
        });

        it('GIVEN advanced edit mode is selected WHEN the page is loaded THEN it hides the create version button', function() {
            // GIVEN
            perspective.selectAdvancedPerspective();

            // THEN
            pageVersionsMenu.assertions.createVersionButtonIsNotDisplayed();
        });

        it('GIVEN versioning mode is selected WHEN the page is loaded THEN it shows a create version button', function() {
            // GIVEN
            perspective.selectVersioningPerspective();

            // THEN
            pageVersionsMenu.assertions.createVersionButtonIsDisplayed();
        });

        it('GIVEN versioning mode is selected WHEN the user clicks a page version in the menu and the version is selected THEN it hides the create version button', function() {
            // GIVEN
            perspective.selectVersioningPerspective();
            pageVersionsMenu.actions.openMenu();

            // WHEN
            pageVersionsMenu.assertions.createVersionButtonIsDisplayed();
            pageVersionsMenu.actions.selectPageVersionByLabel(VERSION_VALID_LABEL);

            // THEN
            pageVersionsMenu.assertions.createVersionButtonIsNotDisplayed();
        });

        it('GIVEN create version button is clicked WHEN the user fills the form and clicks on the save button THEN the version is loaded AND the create button is hidden', function() {
            // GIVEN
            perspective.selectVersioningPerspective();
            pageVersionsMenu.assertions.createVersionButtonIsDisplayed();
            pageVersionsMenu.actions.clickCreateVersionMenuButton();

            // WHEN
            genericEditor.actions.waitForEditorModalWithTitleToBeOpen(EDITOR_TITLE_KEY);
            genericEditor.actions.setEditorData(VERSION_VALID_DATA);
            genericEditor.actions.save();
            pageVersionsMenu.actions.waitForEditorModalWithTitleToBeClosed(EDITOR_TITLE_KEY);

            // THEN
            editorModal.assertions.assertSuccessAlertIsDisplayed();
            pageVersionsMenu.assertions.createVersionButtonIsNotDisplayed();
        });

        it('GIVEN create version button is clicked WHEN the user does not fill the mandatory label field THEN the save button is disabled', function() {
            // GIVEN
            perspective.selectVersioningPerspective();
            pageVersionsMenu.assertions.createVersionButtonIsDisplayed();
            pageVersionsMenu.actions.clickCreateVersionMenuButton();

            // WHEN
            genericEditor.actions.waitForEditorModalWithTitleToBeOpen(EDITOR_TITLE_KEY);
            genericEditor.actions.setEditorData(VERSION_INVALID_DATA_MISSING_MANDATORY);

            // THEN
            genericEditor.assertions.saveIsDisabled();
        });

        it('GIVEN create version button is clicked WHEN the user fills the label using an existing value THEN the label field has a validation error', function() {
            // GIVEN
            perspective.selectVersioningPerspective();
            pageVersionsMenu.assertions.createVersionButtonIsDisplayed();
            pageVersionsMenu.actions.clickCreateVersionMenuButton();

            // WHEN
            genericEditor.actions.waitForEditorModalWithTitleToBeOpen(EDITOR_TITLE_KEY);
            genericEditor.actions.setEditorData(VERSION_INVALID_DATA_DUPLICATE_LABEL);
            genericEditor.actions.save();

            // THEN
            genericEditor.assertions.fieldHasValidationErrors(FIELD_WITH_ERROR_LABEL, NOT_LOCALIZED, 1);
        });
    });

    describe('Create type permission is absent', function() {
        beforeEach(function(done) {
            noCmsVersionCreatePermission();
            page.actions.navigateToPage(true, __dirname, done);
        });

        it('GIVEN user without CREATE CMSVersion permission WHEN versioning mode is selected THEN create version button is absent', function() {
            // WHEN
            perspective.selectVersioningPerspective();

            // THEN
            pageVersionsMenu.assertions.createVersionButtonIsNotDisplayed();
        });
    });

    describe('Create type permission exists', function() {
        beforeEach(function(done) {
            allCmsVersionTypePermissions();
            page.actions.navigateToPage(true, __dirname, done);
        });

        it('GIVEN user without CREATE CMSVersion permission WHEN versioning mode is selected THEN create version button is absent', function() {
            // WHEN
            perspective.selectVersioningPerspective();

            // THEN
            pageVersionsMenu.assertions.createVersionButtonIsDisplayed();
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

    function noCmsVersionCreatePermission() {
        pageVersionsMenu.utils.setCMSVersionTypePermission({
            read: true,
            change: true,
            create: false,
            remove: true
        });
    }
});
