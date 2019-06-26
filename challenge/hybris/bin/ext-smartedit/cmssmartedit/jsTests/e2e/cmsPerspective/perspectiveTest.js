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
(function() {
    var perspective = e2e.componentObjects.modeSelector;
    var ribbon = e2e.componentObjects.ribbon;
    var storefront = e2e.componentObjects.storefront;
    var componentContextualMenu = e2e.componentObjects.componentContextualMenu;
    var externalComponentDecorator = e2e.componentObjects.externalComponentDecorator;
    var contextualMenu = e2e.componentObjects.componentContextualMenu;

    var COMPONENT_NAME = 'component3';
    var COMPONENT4_NAME = 'component4';

    describe('CMS Perspectives', function() {

        var cmsPerspective = "Basic CMS";
        var otherPerspective = "Some other perspective";


        beforeEach(function() {
            browser.bootstrap(__dirname);
        });

        beforeEach(function() {
            browser.waitForWholeAppToBeReady();
        });

        describe('WHEN other perspective than Basic CMS perspective is selected', function() {

            beforeEach(function(done) {
                perspective.select(otherPerspective).then(function() {
                    done();
                });
            });

            it('no contextual menu button shows', function() {
                storefront.actions.moveToComponent('component1');

                expect(componentContextualMenu.elements.getNumContextualMenuItemsForComponentId(COMPONENT_NAME)).toBe(0);
            });

            it('no toolbar button shows', function() {
                ribbon.doesNotHaveAddComponentButton();
            });

        });

        describe('WHEN Basic CMS perspective is selected', function() {

            beforeEach(function(done) {
                perspective.select(cmsPerspective).then(function() {
                    done();
                });
            });

            it('SimpleResponsiveBannerComponent receives 3 contextual menu buttons : move, delete and edit', function() {

                // WHEN
                storefront.actions.moveToComponentByAttributeAndValue('data-smartedit-component-type', 'SimpleResponsiveBannerComponent');

                // THEN
                componentContextualMenu.assertions.removeMenuItemForComponentIdLoadedRightImg(COMPONENT_NAME);
                componentContextualMenu.assertions.editMenuItemForComponentIdLoadedRightImg(COMPONENT_NAME);
                componentContextualMenu.assertions.moveMenuItemForComponentIdLoadedRightImg(COMPONENT_NAME);
            });

            it('GIVEN the user does not have EDIT permissions on a type WHEN the user hovers on the component of that type THEN the edit button should not be available', function() {

                // WHEN
                storefront.actions.moveToComponent(storefront.constants.COMPONENT2_NAME);

                // THEN
                contextualMenu.assertions.assertEditButtonIsNotPresent(storefront.constants.COMPONENT2_NAME);

            });

            it("white ribbon receives 'Add component' button", function() {
                ribbon.hasAddComponentButton();
            });

            it('components coming from parent catalog will have an external component decorator on them', function() {

                storefront.actions.moveToComponent(COMPONENT4_NAME);

                externalComponentDecorator.assertions.externalComponentDecoratorsCount(1);

                storefront.actions.moveToComponentByAttributeAndValue('data-smartedit-component-type', 'componentType4');

                componentContextualMenu.actions.clickExternalComponentButton(COMPONENT4_NAME);

                componentContextualMenu.assertions.externalComponentMenuItemForComponentIdLoadedRightImg(COMPONENT4_NAME);

                componentContextualMenu.actions.clickExternalComponentButton(COMPONENT4_NAME);

                componentContextualMenu.assertions.externalComponentToShowParentCatalogDetails(COMPONENT4_NAME, 'Apparel Content Catalog (Online)');
            });

        });

    });

})();
