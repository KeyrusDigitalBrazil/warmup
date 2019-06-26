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
describe('sharedSlotDecorator', function() {
    var perspective = e2e.componentObjects.modeSelector;
    var storefront = e2e.componentObjects.storefront;
    var disabledSlotDecorator = e2e.componentObjects.disabledSlotDecorator;
    var contextualMenu = e2e.componentObjects.componentContextualMenu;

    var TOP_HEADER_SLOT_ID = storefront.constants.TOP_HEADER_SLOT_ID;
    var BOTTOM_HEADER_SLOT_ID = storefront.constants.BOTTOM_HEADER_SLOT_ID;
    var FOOTER_SLOT_ID = storefront.constants.FOOTER_SLOT_ID;
    var COMPONENT1_ID = storefront.constants.COMPONENT1_NAME;
    var COMPONENT4_ID = storefront.constants.COMPONENT4_NAME;

    beforeEach(function() {
        browser.bootstrap(__dirname);
    });

    beforeEach(function(done) {
        browser.waitForWholeAppToBeReady().then(function() {
            done();
        });
    });

    describe(' basic edit mode - ', function() {

        beforeEach(function(done) {
            perspective.select(perspective.BASIC_CMS_PERSPECTIVE).then(function() {
                browser.switchToIFrame().then(function() {
                    done();
                });
            });
        });

        it('GIVEN basic edit mode is selected THEN the shared slot decorator is only shown for shared slots',
            function() {
                // WHEN
                storefront.actions.moveToComponent(COMPONENT1_ID);

                // THEN
                disabledSlotDecorator.assertions.assertDisabledDecoratorIsDisplayedOnSlot(TOP_HEADER_SLOT_ID);

                storefront.actions.moveToComponent(storefront.constants.BOTTOM_HEADER_SLOT_ID);
                disabledSlotDecorator.assertions.assertDisabledDecoratorIsNotDisplayedOnSlot(BOTTOM_HEADER_SLOT_ID);

                storefront.actions.moveToComponent(storefront.constants.FOOTER_SLOT_ID);
                disabledSlotDecorator.assertions.assertExternalDisabledDecoratorIsDisplayedOnSlot(FOOTER_SLOT_ID);
            });

        it('GIVEN basic edit mode is selected WHEN a component in a shared slot is hovered THEN its contextual menu buttons are not displayed',
            function() {
                // WHEN
                storefront.actions.moveToComponent(COMPONENT1_ID);

                // THEN
                contextualMenu.elements.getRemoveButtonForComponentId(COMPONENT1_ID).then(function(removeButton) {
                    contextualMenu.elements.getMoveButtonForComponentId(COMPONENT1_ID).then(function(moveButton) {
                        contextualMenu.elements.getEditButtonForComponentId(COMPONENT1_ID).then(function(editButton) {
                            contextualMenu.elements.getCloneButtonForComponentId(COMPONENT1_ID).then(function(cloneButton) {
                                browser.waitForAbsence(removeButton, 'Expected remove button not to be available');
                                browser.waitForAbsence(moveButton, 'Expected move button not to be available');
                                browser.waitForAbsence(editButton, 'Expected edit button not to be available');
                                browser.waitForAbsence(cloneButton, 'Expected clone button not to be available');
                            });
                        });
                    });
                });
            });

        it('GIVEN basic edit mode is selected WHEN a component in a non-shared slot is hovered THEN its contextual menu buttons are displayed',
            function() {
                // WHEN
                storefront.actions.moveToComponent(COMPONENT4_ID);

                // THEN
                contextualMenu.elements.getRemoveButtonForComponentId(COMPONENT4_ID).then(function(removeButton) {
                    contextualMenu.elements.getMoveButtonForComponentId(COMPONENT4_ID).then(function(moveButton) {
                        contextualMenu.elements.getEditButtonForComponentId(COMPONENT4_ID).then(function() {
                            expect(removeButton.isPresent()).toBe(true, 'Expected remove button to be available');
                            expect(moveButton.isPresent()).toBe(true, 'Expected move button to be available');
                        });
                    });
                });
            });

        it('GIVEN basic edit mode is selected WHEN a slot is being shared THEN a specific classname and HTML attribute are added to ensure the proper display of CSS-based popover',
            function() {
                // WHEN
                storefront.actions.moveToComponent(COMPONENT1_ID);

                // THEN
                disabledSlotDecorator.assertions.disabledSlotContainsSpecificHTMLAttribute(TOP_HEADER_SLOT_ID);
            }
        );

    });

    describe(' advanced edit mode - ', function() {

        beforeEach(function(done) {
            perspective.select(perspective.ADVANCED_CMS_PERSPECTIVE).then(function() {
                browser.switchToIFrame().then(function() {
                    done();
                });
            });
        });

        it('GIVEN advanced edit mode is selected THEN the shared slot decorator is only shown for external shared slots',
            function() {

                disabledSlotDecorator.assertions.assertDisabledDecoratorIsNotDisplayedOnSlot(TOP_HEADER_SLOT_ID);

                storefront.actions.moveToComponent(storefront.constants.BOTTOM_HEADER_SLOT_ID);
                disabledSlotDecorator.assertions.assertDisabledDecoratorIsNotDisplayedOnSlot(BOTTOM_HEADER_SLOT_ID);

                storefront.actions.moveToComponent(storefront.constants.FOOTER_SLOT_ID);
                disabledSlotDecorator.assertions.assertExternalDisabledDecoratorIsDisplayedOnSlot(FOOTER_SLOT_ID);
            });

    });
});
