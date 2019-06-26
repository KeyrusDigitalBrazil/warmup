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
describe('Clone Component', function() {

    var contextualMenu = e2e.componentObjects.componentContextualMenu;
    var perspective = e2e.componentObjects.modeSelector;
    var storefront = e2e.componentObjects.storefront;
    var editorModal = e2e.componentObjects.editorModal;

    beforeEach(function(done) {
        browser.bootstrap(__dirname);
        browser.waitForWholeAppToBeReady();
        perspective.selectBasicPerspective().then(function() {
            done();
        });
    });

    it('GIVEN the user is on the storefront content page WHEN the user tries to clone a component that is not cloneable then the more items and clone button are not shown', function() {
        // THEN
        contextualMenu.assertions.assertMoreItemsButtonIsNotPresent(storefront.constants.COMPONENT1_NAME);
    });

    it('GIVEN the user is on the storefront content page WHEN the user tries to clone a component then the component editor is opened', function() {
        // WHEN
        storefront.actions.moveToComponent(storefront.constants.STATIC_COMPONENT_NAME);
        contextualMenu.actions.clickCloneButton(storefront.constants.STATIC_COMPONENT_NAME);

        // THEN
        editorModal.assertions.assertModalIsPresent();
    });

    it('GIVEN the user is on the storefront content page WHEN the user clicks CANCEL on the confirmation for clone action then the component is not cloned the slot', function() {
        // WHEN
        storefront.actions.moveToComponent(storefront.constants.STATIC_COMPONENT_NAME);
        contextualMenu.actions.clickCloneButton(storefront.constants.STATIC_COMPONENT_NAME);
        editorModal.actions.modalDialogClickCancel();

        // THEN
        storefront.elements.getComponentsBySlotId(storefront.constants.STATIC_SLOT_ID, 1);
    });

    it('GIVEN the user is on the storefront content page WHEN the user clicks OK on the confirmation for clone action then the new cloned component is added to the slot', function() {
        // WHEN
        storefront.actions.moveToComponent(storefront.constants.STATIC_COMPONENT_NAME);
        contextualMenu.actions.clickCloneButton(storefront.constants.STATIC_COMPONENT_NAME);
        editorModal.actions.modalDialogClickSave().then(function() {
            // THEN
            editorModal.assertions.assertSuccessAlertIsDisplayed();
        });
    });
});
