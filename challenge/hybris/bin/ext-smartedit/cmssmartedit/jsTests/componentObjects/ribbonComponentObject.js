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
/* jshint unused:false, undef:false */
var PLUS_BUTTON_SELECTOR = "div.ySmartEditPerspectiveToolbar button.toolbar-action--button span[class$='hyicon hyicon-addlg se-toolbar-menu-ddlb--button__icon']";

module.exports = {
    elements: {
        addComponentButton: function() {
            return element(by.css(PLUS_BUTTON_SELECTOR));
        }
    },
    hasAddComponentButton: function() {
        expect(this.elements.addComponentButton().isPresent()).toBe(true);
    },

    doesNotHaveAddComponentButton: function() {
        browser.waitForAbsence(this.elements.addComponentButton());
    }
};
