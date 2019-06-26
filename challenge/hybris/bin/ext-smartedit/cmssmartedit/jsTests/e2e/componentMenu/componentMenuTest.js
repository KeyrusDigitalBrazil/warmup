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
describe('Component Menu', function() {

    // --------------------------------------------------------------------------------------------------
    // Imports
    // --------------------------------------------------------------------------------------------------
    var perspective = e2e.componentObjects.modeSelector;
    var page = e2e.pageObjects.componentMenu;
    var componentMenu = e2e.componentObjects.componentMenu;

    // --------------------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------------------
    var APPAREL_ONLINE = componentMenu.constants.APPAREL_ONLINE;
    var APPAREL_ONLINE_NAME = componentMenu.constants.APPAREL_ONLINE_NAME;
    var APPAREL_UK_STAGED = componentMenu.constants.APPAREL_UK_STAGED;
    var APPAREL_UK_STAGED_NAME = componentMenu.constants.APPAREL_UK_STAGED_NAME;

    // --------------------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------------------
    describe('GIVEN user has permission to write in page', function() {
        beforeEach(function(done) {
            page.actions.navigateToPage(__dirname).then(function() {
                perspective.select(perspective.BASIC_CMS_PERSPECTIVE);
                done();
            });
        });

        describe('AND page has more than one catalogs', function() {

            it('WHEN page is displayed THEN it displays the toolbar menu item', function() {
                componentMenu.assertions.assertComponentMenuIsInToolbar();
            });

            it('WHEN the user clicks on the component menu icon THEN the menu is displayed', function() {
                // WHEN
                componentMenu.actions.openMenu();

                // THEN
                componentMenu.assertions.assertComponentMenuIsDisplayed();
            });

            it('WHEN the user clicks on the component menu THEN the menu displays the supported component types in the first tab', function() {
                // WHEN
                componentMenu.actions.openMenu();

                // THEN
                componentMenu.assertions.assertComponentMenuIsInComponentTypesTab();
                componentMenu.assertions.assertComponentTypesTabHasSupportedTypes();
            });

            it('WHEN the user searches for component types THEN the menu displays the component types found', function() {
                // GIVEN
                var searchTerm = 'BANNER';
                componentMenu.actions.openMenu();

                // WHEN
                componentMenu.actions.searchComponentTypes(searchTerm);

                // THEN
                componentMenu.assertions.assertComponentMenuIsInComponentTypesTab();
                componentMenu.assertions.assertComponentTypesTabHasSupportedTypes(searchTerm);
            });

            it('WHEN the user opens the components tab THEN the menu displays the available catalogs', function() {
                // WHEN
                componentMenu.actions.openMenu();
                componentMenu.actions.selectComponentsTab();

                // THEN
                componentMenu.assertions.assertComponentMenuIsInComponentsTab();
                componentMenu.assertions.assertCatalogVersionsSelectorIsDisplayed();
            });

            it('WHEN the user opens the components tab THEN the menu displays the existing components', function() {
                // Arrange 
                var pageNum = 2;
                componentMenu.actions.openMenu();
                componentMenu.actions.selectComponentsTab();
                componentMenu.actions.selectCatalogVersion(APPAREL_UK_STAGED_NAME);

                // WHEN
                componentMenu.actions.scrollComponents(APPAREL_UK_STAGED, pageNum);

                // THEN
                componentMenu.assertions.assertComponentMenuIsInComponentsTab();
                componentMenu.assertions.assertComponentsTabHasExistingComponentsForCatalogVersion(APPAREL_UK_STAGED, pageNum);
            });

            it('WHEN the user searches for a component THEN the menu displays the existing components that match the criteria', function() {
                // GIVEN 
                var searchTerm = 'Component2';
                componentMenu.actions.openMenu();
                componentMenu.actions.selectComponentsTab();
                componentMenu.actions.selectCatalogVersion(APPAREL_UK_STAGED_NAME);

                // WHEN 
                componentMenu.actions.searchComponents(searchTerm);

                // THEN 
                componentMenu.assertions.assertComponentsTabHasExistingComponentsForCatalogVersion(APPAREL_UK_STAGED, 1, searchTerm);
            });

            it('WHEN the user scrolls the components THEN the menu loads the new components until the end', function() {
                // Arrange 
                var pageNum = 3;
                componentMenu.actions.openMenu();
                componentMenu.actions.selectComponentsTab();
                componentMenu.actions.selectCatalogVersion(APPAREL_UK_STAGED_NAME);

                // WHEN
                componentMenu.actions.scrollComponents(APPAREL_UK_STAGED, pageNum);

                // THEN
                componentMenu.assertions.assertComponentMenuIsInComponentsTab();
                componentMenu.assertions.assertComponentsTabHasExistingComponentsForCatalogVersion(APPAREL_UK_STAGED, pageNum);
            });

            it('WHEN the user changes the catalog version in the components tab THEN the menu displays the existing components for that catalog version', function() {
                // WHEN 
                var pageNum = 1;
                componentMenu.actions.openMenu();
                componentMenu.actions.selectComponentsTab();
                componentMenu.actions.selectCatalogVersion(APPAREL_ONLINE_NAME);

                // THEN 
                componentMenu.assertions.assertComponentsTabHasExistingComponentsForCatalogVersion(APPAREL_ONLINE, pageNum);
            });

            it('WHEN the user changes the catalog version AND searches for a component THEN the menu displays the existing components that match the criteria', function() {
                // GIVEN
                var pageNum = 1;
                var searchTerm = 'Component4';
                componentMenu.actions.openMenu();
                componentMenu.actions.selectComponentsTab();

                // WHEN
                componentMenu.actions.selectCatalogVersion(APPAREL_ONLINE_NAME);
                componentMenu.actions.searchComponents(searchTerm);

                // THEN
                componentMenu.assertions.assertComponentsTabHasExistingComponentsForCatalogVersion(APPAREL_ONLINE, pageNum, searchTerm);
            });
        });
    });

});
