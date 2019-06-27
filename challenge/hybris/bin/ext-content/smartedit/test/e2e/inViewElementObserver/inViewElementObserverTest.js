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
describe('inViewElementObserver -', function() {
    var page = require("../utils/components/Page.js");
    var storefront = require("./../utils/components/Storefront.js");
    var inViewElementObserver = require('./inViewElementObserverPageObject.js');
    var sfBuilder = require('../../../smartedit-build/test/e2e/componentObjects/sfBuilderComponentObject');

    var SLOT_ID = 'headerLinksSlot';

    beforeEach(function() {
        page.actions.getAndWaitForWholeApp('test/e2e/inViewElementObserver/index.html');

        browser.switchToIFrame();
    });

    it('WHEN initializing (no scroll), only 3 eligible elements are in view', function() {

        inViewElementObserver.assertions.inSync(3);
    });

    it('WHEN an out of view component is removed from the DOM, it is removed from the queue but not from the visible elements', function() {

        sfBuilder.actions.removeComponent(SLOT_ID);

        inViewElementObserver.assertions.inSync(3);
    });

    it('WHEN an out of view component is addded to the DOM, it is add to the queue but not to the visible elements', function() {

        sfBuilder.actions.addComponent("blabla");

        inViewElementObserver.assertions.inSync(3);
    });

    it('WHEN a component is added in view and removed in view, it is added to /removed from the queue and added to /removed from the visible elements', function() {

        inViewElementObserver.assertions.inSync(3);

        inViewElementObserver.actions.addComponentAsFirst();

        inViewElementObserver.assertions.inSync(4);

        inViewElementObserver.actions.removeFirstComponent();

        inViewElementObserver.assertions.inSync(3);

    });

    it('WHEN scrolling down and back up, the number of visible components adjusts and then resets', function() {

        storefront.actions.moveToComponent("headerLinksSlot", "ContentSlot");

        inViewElementObserver.assertions.inSync(7);

        storefront.actions.moveToComponent("topHeaderSlot", "ContentSlot");

        inViewElementObserver.assertions.inSync(3);
    });
});
