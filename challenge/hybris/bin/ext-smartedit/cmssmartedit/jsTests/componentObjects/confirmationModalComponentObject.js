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
module.exports = (function() {

    var componentObject = {};

    componentObject.elements = {
        getConfirmationModalOkButton: function() {
            return by.id('confirmOk');
        },
        getConfirmationModalCancelButton: function() {
            return by.id('confirmCancel');
        }
    };

    componentObject.assertions = {
        cancelButtonIsNotDisplayed: function() {
            expect(element(componentObject.elements.getConfirmationModalCancelButton())).toBeAbsent();
        }
    };

    componentObject.actions = {
        confirmConfirmationModal: function() {
            browser.click(componentObject.elements.getConfirmationModalOkButton());
        },
        dismissConfirmationModal: function() {
            browser.click(componentObject.elements.getConfirmationModalCancelButton());
        }
    };

    return componentObject;
})();
