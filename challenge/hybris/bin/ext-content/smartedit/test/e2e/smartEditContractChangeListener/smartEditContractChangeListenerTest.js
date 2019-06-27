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
describe('DOM Observer -', function() {
    var perspectives;
    var storefront = require("./../utils/components/Storefront.js");
    var smartEditContractChangeListener = require('./smartEditContractChangeListenerPageObject.js');
    var sfBuilder = require('../../../smartedit-build/test/e2e/componentObjects/sfBuilderComponentObject');

    var SLOT_ID = 'slotWrapper';
    var SLOT_TYPE = 'ContentSlot';
    var RESIZE_SLOT_ADDED_ID = 'resizeSlotDomListenerTest';
    var RESIZE_SLOT_ID = 'topHeaderSlot';
    var NEW_COMPONENT_ID = 'asyncComponent';
    var NEW_COMPONENT_TYPE = 'componentType1';
    var RESIZE_COMPONENT_ALIAS = 'resizeComponentDomListenerTest';

    beforeEach(function() {
        browser.get('test/e2e/smartEditContractChangeListener/index.html');
        browser.waitForWholeAppToBeReady();

        perspectives = require("../utils/components/Perspectives.js");
        perspectives.actions.selectPerspective(perspectives.constants.DEFAULT_PERSPECTIVES.ALL);
        browser.waitForWholeAppToBeReady();
    });

    afterEach(function() {
        smartEditContractChangeListener.assertions.overlayAndStoreFrontAreSynced();
    });

    it('WHEN a new component is added and removed THEN the overlay is updated and both slot and component are decorated', function() {

        browser.switchToIFrame();
        storefront.assertions.assertDecoratorShowsOnComponent(SLOT_ID, SLOT_TYPE, "deco3");

        sfBuilder.actions.addComponent(NEW_COMPONENT_ID, SLOT_ID);
        storefront.assertions.assertComponentInOverlayPresent(NEW_COMPONENT_ID, NEW_COMPONENT_TYPE, true);
        storefront.assertions.assertDecoratorShowsOnComponent(NEW_COMPONENT_ID, NEW_COMPONENT_TYPE, "deco1");

        sfBuilder.actions.removeComponent(NEW_COMPONENT_ID, SLOT_ID);
        storefront.assertions.assertComponentInOverlayPresent(NEW_COMPONENT_ID, NEW_COMPONENT_TYPE, false);
        storefront.assertions.assertDecoratorShowsOnComponent(SLOT_ID, SLOT_TYPE, "deco3");
        storefront.assertions.assertDecoratorDoesntShowOnComponent(NEW_COMPONENT_ID, NEW_COMPONENT_TYPE, "deco1");


    });

    it('WHEN a component is added and resized THEN both slot and component in overlay are resized and repositioned', function() {
        browser.switchToIFrame();

        sfBuilder.actions.addComponent(RESIZE_COMPONENT_ALIAS, RESIZE_SLOT_ID);
        smartEditContractChangeListener.actions.enlargeComponent();

        var slotInStoreFront = storefront.elements.getComponentById(RESIZE_SLOT_ID);
        var slotInOverlay = storefront.elements.getComponentInOverlayById(RESIZE_SLOT_ID, SLOT_TYPE);

        smartEditContractChangeListener.assertions.elementsHaveSameDimensions(slotInStoreFront, slotInOverlay);
        smartEditContractChangeListener.assertions.elementsHaveSamePosition(slotInStoreFront, slotInOverlay);

        var newComponentInStoreFront = storefront.elements.getComponentById(RESIZE_COMPONENT_ALIAS);
        var newComponentInOverlay = storefront.elements.getComponentInOverlayById(RESIZE_COMPONENT_ALIAS, NEW_COMPONENT_TYPE);

        smartEditContractChangeListener.assertions.elementsHaveSameDimensions(newComponentInStoreFront, newComponentInOverlay);
        smartEditContractChangeListener.assertions.elementsHaveSamePosition(newComponentInStoreFront, newComponentInOverlay);

    });

    it('WHEN a slot with a component is added THEN both slot and component in overlay are resized and repositioned', function() {

        //otherSlot
        sfBuilder.actions.addComponent(RESIZE_SLOT_ADDED_ID);
        sfBuilder.actions.addComponent(RESIZE_COMPONENT_ALIAS, RESIZE_SLOT_ADDED_ID);
        smartEditContractChangeListener.actions.enlargeComponent();

        var slotInStoreFront = storefront.elements.getComponentById(RESIZE_SLOT_ADDED_ID);
        storefront.actions.moveToComponent(RESIZE_SLOT_ADDED_ID, SLOT_TYPE);
        var slotInOverlay = storefront.elements.getComponentInOverlayById(RESIZE_SLOT_ADDED_ID, SLOT_TYPE);

        smartEditContractChangeListener.assertions.elementsHaveSameDimensions(slotInStoreFront, slotInOverlay);
        smartEditContractChangeListener.assertions.elementsHaveSamePosition(slotInStoreFront, slotInOverlay);

        var newComponentInStoreFront = storefront.elements.getComponentById(RESIZE_COMPONENT_ALIAS);
        var newComponentInOverlay = storefront.elements.getComponentInOverlayById(RESIZE_COMPONENT_ALIAS, NEW_COMPONENT_TYPE);

        smartEditContractChangeListener.assertions.elementsHaveSameDimensions(newComponentInStoreFront, newComponentInOverlay);
        smartEditContractChangeListener.assertions.elementsHaveSamePosition(newComponentInStoreFront, newComponentInOverlay);

    });

    it('WHEN a slot with component is removed, the overlay counterparts are removed and no decorator shows', function() {

        sfBuilder.actions.addComponent(RESIZE_SLOT_ADDED_ID);
        sfBuilder.actions.addComponent(RESIZE_COMPONENT_ALIAS, RESIZE_SLOT_ADDED_ID);

        storefront.actions.moveToComponent(RESIZE_SLOT_ADDED_ID, SLOT_TYPE);

        smartEditContractChangeListener.assertions.overlayAndStoreFrontAreSynced();

        sfBuilder.actions.removeComponent(RESIZE_SLOT_ADDED_ID);

        browser.switchToIFrame();

        storefront.assertions.assertComponentInOverlayPresent(RESIZE_COMPONENT_ALIAS, NEW_COMPONENT_TYPE, false);
        storefront.assertions.assertDecoratorDoesntShowOnComponent(RESIZE_COMPONENT_ALIAS, NEW_COMPONENT_TYPE, "deco1");

        storefront.assertions.assertComponentInOverlayPresent(RESIZE_SLOT_ADDED_ID, SLOT_TYPE, false);
        storefront.assertions.assertDecoratorDoesntShowOnComponent(RESIZE_SLOT_ADDED_ID, SLOT_TYPE, "deco3");
    });

    it('WHEN a component mutates to another type, a new decorator is applied and the former removed', function() {

        sfBuilder.actions.addComponent(NEW_COMPONENT_ID, SLOT_ID);
        smartEditContractChangeListener.actions.toggleComponentType();

        browser.switchToIFrame();

        storefront.assertions.assertDecoratorDoesntShowOnComponent(NEW_COMPONENT_ID, NEW_COMPONENT_TYPE, "deco1");
        storefront.assertions.assertDecoratorDoesntShowOnComponent(NEW_COMPONENT_ID, NEW_COMPONENT_TYPE, "deco2");
    });

    it('WHEN deep-linking to another page THEN the DOM Observer notifies of the change', function() {
        browser.switchToIFrame();

        smartEditContractChangeListener.assertions.pageHasChanged("paged changed to homepage");

        smartEditContractChangeListener.actions.changePage();

        browser.switchToIFrame();

        smartEditContractChangeListener.assertions.pageHasChanged("paged changed to demo_storefront_page_id");
        storefront.assertions.assertDecoratorShowsOnComponent('staticDummyComponent', NEW_COMPONENT_TYPE, "deco4");
    });

    it('WHEN re-rendering a component THEN the component is still visible in the overlay', function() {
        sfBuilder.actions.rerenderComponent('component4', 'bottomHeaderSlot');
        storefront.assertions.assertComponentInOverlayPresent('component4', 'componentType4', true);
    });

});
