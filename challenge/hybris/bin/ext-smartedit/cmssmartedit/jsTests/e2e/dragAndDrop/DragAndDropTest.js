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
describe('Drag and Drop -', function() {

    var dragAndDrop = e2e.pageObjects.DragAndDrop;
    var perspective = e2e.componentObjects.modeSelector;
    var slots = dragAndDrop.structure.slots;
    var components = dragAndDrop.structure.components;
    var editorModal = e2e.componentObjects.editorModal;
    var storefront = e2e.componentObjects.storefront;
    var contextualMenu = e2e.componentObjects.componentContextualMenu;

    // see layouts/sf-layout-default.js
    var BOTTOM_HEADER_TOTAL_COMPONENTS = 21;

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    beforeEach(function(done) {
        browser.driver.manage().window().maximize();
        browser.waitForWholeAppToBeReady();
        perspective.selectBasicPerspective().then(function() {
            done();
        });
    });

    describe('within frame', function() {
        it('GIVEN the user does not have EDIT permissions on a type WHEN the user hovers on the component of that type THEN the move button should not be available', function() {

            // WHEN
            storefront.actions.moveToComponent(storefront.constants.COMPONENT2_NAME);

            // THEN
            contextualMenu.assertions.assertMoveButtonIsNotPresent(storefront.constants.COMPONENT2_NAME);
        });


        it('WHEN I grab a component from one slot AND hover over another slot in which its type is not allowed THEN I should see the slot highlighted to indicate that drop is not allowed',
            function() {
                // Arrange
                dragAndDrop.actions.moveToStoreFront();

                // Act
                dragAndDrop.actions.grabComponent(slots.TOP_HEADER_SLOT, components.COMPONENT1);
                storefront.actions.moveToComponent(storefront.constants.BOTTOM_HEADER_SLOT_ID);
                var bottomSlot = storefront.elements.getComponentInOverlayById(slots.BOTTOM_HEADER_SLOT);
                dragAndDrop.actions.hoverOverElement(bottomSlot);

                // Assert
                expect(dragAndDrop.actions.isSlotEnabled(slots.BOTTOM_HEADER_SLOT)).toBe(false, 'Expected slot not to display that it allows the component');
                expect(dragAndDrop.actions.isSlotDisabled(slots.BOTTOM_HEADER_SLOT)).toBe(true, 'Expected slot to display that the component is forbidden');
            });


        it('WHEN I grab a component from one slot AND hover over another slot in which its type is allowed THEN I should see the slot highlighted to indicate that drop is allowed',
            function() {
                // Arrange
                dragAndDrop.actions.moveToStoreFront();

                // Act
                storefront.actions.moveToComponent(storefront.constants.BOTTOM_HEADER_SLOT_ID);
                dragAndDrop.actions.grabComponent(slots.BOTTOM_HEADER_SLOT, components.COMPONENT4);
                storefront.actions.moveToComponent(storefront.constants.SEARCH_BOX_SLOT);
                var searchBoxSlot = storefront.elements.getComponentInOverlayById(slots.SEARCH_BOX_SLOT);
                dragAndDrop.actions.hoverOverElement(searchBoxSlot);

                //Assert
                expect(dragAndDrop.actions.isSlotEnabled(slots.SEARCH_BOX_SLOT)).toBe(true, 'Expected slot to display that it allows the component');
                expect(dragAndDrop.actions.isSlotDisabled(slots.SEARCH_BOX_SLOT)).toBe(false, 'Expected slot not to display that the component is forbidden');
            });

        it('WHEN I grab a component from a slot and drop it in a new slot THEN I should see the component in the new slot', function() {
            dragAndDrop.actions.moveToStoreFront();

            storefront.actions.moveToComponent(storefront.constants.BOTTOM_HEADER_SLOT_ID);
            dragAndDrop.actions.grabComponent(slots.BOTTOM_HEADER_SLOT, components.COMPONENT4);
            storefront.actions.moveToComponent(storefront.constants.EMPTY_DUMMY_SLOT_ID);

            var targetSlot = storefront.elements.getComponentInOverlayById(storefront.constants.EMPTY_DUMMY_SLOT_ID);
            dragAndDrop.actions.hoverOverElement(targetSlot);
            dragAndDrop.actions.dropComponent(components.COMPONENT4, storefront.constants.EMPTY_DUMMY_SLOT_ID);

            expect(dragAndDrop.actions.isComponentInPosition(storefront.constants.EMPTY_DUMMY_SLOT_ID, components.COMPONENT4, 0, 1)).toBe(true, 'Expected new component to be at the first position of the empty dummy slot');
        });
    });

    describe('across frames', function() {
        it('WHEN I grab a component from the component menu AND hover over a slot in which its type is not allowed THEN I should see the slot highlighted to indicate that drop is not allowed',
            function() {
                // Act
                dragAndDrop.actions.grabComponentInComponentMenu();
                dragAndDrop.actions.dragComponentInComponentMenuOverSlot(slots.SEARCH_BOX_SLOT);

                // Assert
                expect(dragAndDrop.actions.isSlotEnabled(slots.SEARCH_BOX_SLOT)).toBe(false, 'Expected slot not to display that it allows the component');
                expect(dragAndDrop.actions.isSlotDisabled(slots.SEARCH_BOX_SLOT)).toBe(true, 'Expected slot to display that the component is forbidden');
            });

        it('WHEN I grab a component from the component menu AND hover over a slot in which its type is allowed THEN I should see the slot highlighted to indicate that drop is allowed',
            function() {
                // Act
                dragAndDrop.actions.grabComponentInComponentMenu();
                dragAndDrop.actions.dragComponentInComponentMenuOverSlot(slots.BOTTOM_HEADER_SLOT);

                // Assert
                expect(dragAndDrop.actions.isSlotEnabled(slots.BOTTOM_HEADER_SLOT)).toBe(true, 'Expected slot to display that it allows the component');
                expect(dragAndDrop.actions.isSlotDisabled(slots.BOTTOM_HEADER_SLOT)).toBe(false, 'Expected slot not to display that the component is forbidden');
            });

        it('WHEN I grab a component from the component menu AND hover over an empty slot in which its type is allowed THEN I should be able to drop the component',
            function() {
                // Arrange
                dragAndDrop.actions.moveToStoreFront();
                storefront.actions.moveToComponent(storefront.constants.EMPTY_DUMMY_SLOT_ID);
                dragAndDrop.actions.moveToParent();

                // Act
                dragAndDrop.actions.grabCustomizedComponentInComponentMenu();
                dragAndDrop.actions.dragComponentInComponentMenuOverSlot(storefront.constants.EMPTY_DUMMY_SLOT_ID);
                dragAndDrop.actions.dropComponentFromComponentMenu();

                // Assert
                expect(dragAndDrop.actions.isComponentInPosition(storefront.constants.EMPTY_DUMMY_SLOT_ID, components.COMPONENT10, 0, 1)).toBe(true, 'Expected new component to be at the first position of the empty dummy slot');
            });

        it('WHEN I grab a component from the component menu AND drop it in a slot THEN the component is moved to the new position',
            function() {
                dragAndDrop.actions.grabCustomizedComponentInComponentMenu();
                dragAndDrop.actions.addFromComponentMenuToSlotInPosition(slots.BOTTOM_HEADER_SLOT, 10, BOTTOM_HEADER_TOTAL_COMPONENTS);

                // Assert
                expect(dragAndDrop.actions.isComponentInPosition(slots.BOTTOM_HEADER_SLOT, components.COMPONENT10, 10, BOTTOM_HEADER_TOTAL_COMPONENTS + 1)).toBe(true, 'Expected new component to move to the 10nth position of the bottom header slot');
            });

        it('GIVEN I am in a perspective in which drag and drop is enabled WHEN I enable "clone on drop" and grab a component from the component menu AND hover over a slot in which its type is allowed and drop it THEN I should see an editor modal open with the name pre fixed and saving will close the modal',
            function() {

                // Arrange
                perspective.selectBasicPerspective();

                // Act
                dragAndDrop.actions.enableCloneOnDropAndGrabCustomizedComponentInComponentMenu();
                dragAndDrop.actions.addFromComponentMenuToSlotInPosition(slots.BOTTOM_HEADER_SLOT, 5, BOTTOM_HEADER_TOTAL_COMPONENTS);

                // Assert
                expect(editorModal.elements.getAttributeValueByName('name')).toContain('Clone of');
                expect(editorModal.elements.getSaveButton().isEnabled()).toBe(true);

                editorModal.actions.modalDialogClickSave();
                editorModal.assertions.assertModalIsNotPresent();
            });
    });

    describe('scrolling', function() {
        it('WHEN I grab a component AND I position the mouse in the lower hint THEN the page scrolls down',
            function() {
                dragAndDrop.actions.grabComponentInComponentMenu();
                dragAndDrop.actions.moveToStoreFront();
                dragAndDrop.actions.getPageVerticalScroll().then(function(oldScroll) {

                    // Act
                    dragAndDrop.actions.hoverOverBottomHintFromOuter();

                    // Assert
                    dragAndDrop.assertions.pageHasScrolledDown(oldScroll);


                });
            });

        it('WHEN I grab a component AND I position the mouse in the top hint THEN the page scrolls down',
            function() {
                dragAndDrop.actions.grabComponentInComponentMenu();
                dragAndDrop.actions.moveToStoreFront();
                dragAndDrop.actions.scrollToBottomFromOuter();
                dragAndDrop.actions.getPageVerticalScroll().then(function(oldScroll) {
                    // Act
                    dragAndDrop.actions.hoverOverTopHintFromOuter();

                    // Assert
                    dragAndDrop.assertions.pageHasScrolledUp(oldScroll);
                });
            });

        it('WHEN I grab a component AND I position the mouse in the lower hint THEN the page scrolls down',
            function() {
                // Arrange
                dragAndDrop.actions.moveToStoreFront();
                storefront.actions.moveToComponent(storefront.constants.BOTTOM_HEADER_SLOT_ID);
                dragAndDrop.actions.grabComponent(slots.BOTTOM_HEADER_SLOT, components.COMPONENT4);
                dragAndDrop.actions.getPageVerticalScroll().then(function(oldScroll) {

                    // Act
                    dragAndDrop.actions.hoverOverBottomHint();

                    // Assert
                    dragAndDrop.assertions.pageHasScrolledDown(oldScroll);
                });
            });

        it('and the page is at the bottom WHEN I grab a component AND I position the mouse in the upper hint THEN the page scrolls up',
            function() {
                // Arrange
                dragAndDrop.actions.moveToStoreFront();
                storefront.actions.moveToComponent(storefront.constants.STATIC_SLOT_ID);
                dragAndDrop.actions.grabComponent(storefront.constants.STATIC_SLOT_ID, storefront.constants.STATIC_COMPONENT_NAME);
                dragAndDrop.actions.getPageVerticalScroll().then(function(oldScroll) {

                    // Act
                    dragAndDrop.actions.hoverOverTopHint();

                    // Assert
                    dragAndDrop.assertions.pageHasScrolledUp(oldScroll);
                });
            });
    });

});
