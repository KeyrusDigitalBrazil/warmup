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
describe('Remove Component', function() {

    var contextualMenu = e2e.componentObjects.componentContextualMenu;
    var perspective = e2e.componentObjects.modeSelector;
    var storefront = e2e.componentObjects.storefront;
    var modalDialog = e2e.componentObjects.yModalDialog;

    beforeEach(function(done) {
        browser.bootstrap(__dirname);
        browser.waitForWholeAppToBeReady();
        perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
            done();
        });
    });

    it('GIVEN the user is on the storefront content page WHEN the user tries to remove a component then a confirmation modal is opened', function() {

        // WHEN
        storefront.actions.moveToComponent(storefront.constants.STATIC_COMPONENT_NAME);
        contextualMenu.actions.clickRemoveButton(storefront.constants.STATIC_COMPONENT_NAME);
        // THEN
        expect(modalDialog.elements.getModalDialogTitle()).toContain('Remove Component');
        expect(modalDialog.elements.getModalDialogDescription()).toEqual('Do you want to remove the component from the page?');

    });

    it('GIVEN the user is on the storefront content page WHEN the user clicks CANCEL on the confirmation for REMOVE action then the component is not removed from the slot', function() {

        // WHEN
        storefront.actions.moveToComponent(storefront.constants.STATIC_COMPONENT_NAME);
        storefront.elements.getComponentById(storefront.constants.STATIC_COMPONENT_NAME).then(function(element) {
            expect(element.isPresent()).toBeTruthy();
            contextualMenu.actions.clickRemoveButton(storefront.constants.STATIC_COMPONENT_NAME);
            modalDialog.actions.modalDialogClickCancel();

            // THEN
            storefront.elements.getComponentById(storefront.constants.STATIC_COMPONENT_NAME).then(function(element) {
                expect(element.isPresent()).toBeTruthy();
            });

        });

    });

    it('GIVEN the user is on the storefront content page WHEN the user clicks OK on the confirmation for REMOVE action then the server rendered component is removed fromn the slot', function() {

        // WHEN
        storefront.actions.moveToComponent(storefront.constants.STATIC_COMPONENT_NAME);
        storefront.elements.getComponentById(storefront.constants.STATIC_COMPONENT_NAME).then(function(element) {
            expect(element.isPresent()).toBeTruthy();

            contextualMenu.actions.clickRemoveButton(storefront.constants.STATIC_COMPONENT_NAME);
            modalDialog.actions.modalDialogClickOk();

            // THEN
            storefront.elements.getComponentById(storefront.constants.STATIC_COMPONENT_NAME).then(function(element) {
                browser.waitForAbsence(element);
            });
        });

    });

    it('GIVEN the user does not have REMOVE permissions on a type WHEN the user hovers on the component of that type THEN the remove button should not be available', function() {

        // WHEN
        storefront.actions.moveToComponent(storefront.constants.COMPONENT2_NAME);

        // THEN
        contextualMenu.assertions.assertRemoveButtonIsNotPresent(storefront.constants.COMPONENT2_NAME);

    });
});
