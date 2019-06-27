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
module.exports = function() {

    var pageObject = {};

    pageObject.elements = {

        getDeletePageMenuToolbarItem: function() {
            return browser.findElement(by.css('delete-page-toolbar-item .toolbar-action--button'));
        },
        getDeletePageMenuToolbarPopoverSelector: function() {
            return by.css('delete-page-toolbar-item div.toolbar-delete-action__popover');
        }

    };

    pageObject.actions = {

        clickOnDeletePageMenu: function() {
            browser.switchToParent();
            return browser.click(pageObject.elements.getDeletePageMenuToolbarItem());
        },
        hoverOnDeletePageMenu: function() {
            return browser.hoverElement(pageObject.elements.getDeletePageMenuToolbarItem());
        }

    };

    pageObject.assertions = {

        deletePageMenuIconIsActive: function() {
            expect(pageObject.elements.getDeletePageMenuToolbarItem()).not.toContainClass('toolbar-action__disabled');
        },
        deletePageMenuIconIsInactive: function() {
            expect(pageObject.elements.getDeletePageMenuToolbarItem()).toContainClass('toolbar-action__disabled');
        },
        deletePageMenuIconPopoverAnchorIsNotPresent: function() {
            expect(element(pageObject.elements.getDeletePageMenuToolbarPopoverSelector())).toBeAbsent();
        }

    };

    return pageObject;
}();
