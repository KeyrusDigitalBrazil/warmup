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
describe('Rollback Version Menu - \n', function() {

    var page = e2e.pageObjects.PageVersioningMenu;
    var perspective = e2e.componentObjects.modeSelector;
    var pageVersionsMenu = e2e.componentObjects.pageVersions;
    var confirmationModal = e2e.componentObjects.confirmationModal;

    var VERSION_VALID_LABEL = 'New - Version 2';
    var VERSION_WITH_ROLLBACK_ERROR = 'Other - Version 4';

    beforeEach(function(done) {
        page.actions.navigateToPage(true, __dirname, done);
    });

    it('GIVEN versioning mode is selected WHEN the page is loaded THEN it doesnot show the rollback version button', function() {
        // GIVEN
        perspective.selectVersioningPerspective();

        // THEN
        pageVersionsMenu.assertions.rollbackVersionButtonIsNotDisplayed();
    });

    it('GIVEN versioning mode is selected WHEN the user clicks a page version in the menu and the version is selected THEN it displays the rollback version button', function() {
        // GIVEN
        perspective.selectVersioningPerspective();
        pageVersionsMenu.actions.openMenu();

        // WHEN
        pageVersionsMenu.assertions.rollbackVersionButtonIsNotDisplayed();
        pageVersionsMenu.actions.selectPageVersionByLabel(VERSION_VALID_LABEL);

        // THEN
        pageVersionsMenu.assertions.rollbackVersionButtonIsDisplayed();
    });

    it('GIVEN versioning mode is selected WHEN the user clicks a page version in the menu that has rollback error and the rollback is clicked THEN rollback fails and we still see the menu item', function() {
        // GIVEN
        perspective.selectVersioningPerspective();
        pageVersionsMenu.actions.openMenu();
        pageVersionsMenu.actions.selectPageVersionByLabel(VERSION_WITH_ROLLBACK_ERROR);

        // WHEN
        pageVersionsMenu.actions.clickRollbackVersionMenuButton();
        confirmationModal.actions.confirmConfirmationModal();

        // THEN
        pageVersionsMenu.assertions.rollbackVersionButtonIsDisplayed();
    });

    it('GIVEN versioning mode is selected WHEN the user clicks a page version in the menu that has no rollback error and the rollback is clicked THEN it rollback will be successful and rollback menu item is not displayed', function() {
        // GIVEN
        perspective.selectVersioningPerspective();
        pageVersionsMenu.actions.openMenu();
        pageVersionsMenu.actions.selectPageVersionByLabel(VERSION_VALID_LABEL);

        // WHEN
        pageVersionsMenu.actions.clickRollbackVersionMenuButton();
        confirmationModal.actions.confirmConfirmationModal();

        // THEN
        pageVersionsMenu.assertions.rollbackVersionButtonIsNotDisplayed();
    });

});
