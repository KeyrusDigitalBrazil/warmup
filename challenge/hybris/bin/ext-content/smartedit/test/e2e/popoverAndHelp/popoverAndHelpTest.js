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
describe('YPopover directive and yHelp component', function() {

    var popover = require("../utils/components/popoverComponentObject.js");

    beforeEach(function() {
        browser.get('test/e2e/popoverAndHelp/popoverAndHelp.html');
    });

    it('Given a help with title and a template url, when I hover both title and body show', function() {

        browser.waitForAbsence(popover.getPopover());
        popover.hoverYHelp("#helpWithTitle");
        expect(popover.getTitleText()).toEqual("my translated title");
        expect(popover.getBodyText()).toEqual('some inline template');
    });

    it('Given a help with an inline body, when I hover body shows', function() {

        browser.waitForAbsence(popover.getPopover());
        popover.hoverYHelp("#helpWithoutTitle");
        browser.waitForAbsence(popover.getTitleBox());
        expect(popover.getBodyText()).toEqual("some HTML body");
    });

    it('Given a shows-on-hover popover that has both title and inline body, when I hover both title and body show', function() {

        browser.waitForAbsence(popover.getPopover());
        popover.hoverYPopover("#popoverWithTitleAndTop");
        expect(popover.getTitleText()).toEqual("my translated title");
        expect(popover.getBodyText()).toEqual("some HTML body");
    });

    it('Given a show-on-click popover with template url, when I click body shows', function() {

        browser.waitForAbsence(popover.getPopover());
        popover.hoverYPopover("#popoverWithoutTitleAndRight");
        browser.waitForAbsence(popover.getPopover());
        popover.clickYPopover("#popoverWithoutTitleAndRight");
        expect(popover.getBodyText()).toEqual("some inline template");
    });



});
