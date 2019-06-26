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

    componentObject.constants = {
        MODAL_ID: 'y-modal-dialog'
    };

    componentObject.elements = {
        getModalDialog: function() {
            return browser.switchToParent().then(function() {
                return element(by.id(componentObject.constants.MODAL_ID));
            });
        },
        getModalDialogTitle: function() {
            return browser.switchToParent().then(function() {
                return element.all(by.css("div[class*='modal-header']")).first().getText();
            });
        },
        getCancelButton: function() {
            return element(by.css("div[id='" + componentObject.constants.MODAL_ID + "'] button[id='cancel']"));
        },
        getSaveButton: function() {
            return element(by.css("div[id='" + componentObject.constants.MODAL_ID + "'] button[id='save']"));
        },
        getAttributeValueByName: function(attributeName) {
            return browser.switchToParent().then(function() {
                return element(by.name(attributeName)).getAttribute('value');
            });
        },
        getSuccessAlert: function() {
            return element(by.css('system-alerts .alert-success'));
        }
    };

    componentObject.actions = {
        modalDialogClickCancel: function() {
            return browser.switchToParent().then(function() {
                return browser.click(componentObject.elements.getCancelButton()).then(function() {
                    return browser.waitForAbsence(by.id(componentObject.constants.MODAL_ID), 'could not close modal window');
                });
            });
        },
        modalDialogClickSave: function() {
            return browser.switchToParent().then(function() {
                return browser.click(componentObject.elements.getSaveButton()).then(function() {
                    return browser.waitForAbsence(by.id(componentObject.constants.MODAL_ID), 'could not close modal window');
                });
            });
        }
    };

    componentObject.assertions = {
        assertModalIsNotPresent: function() {
            browser.switchToParent().then(function() {
                browser.waitUntil(EC.stalenessOf(element(by.id(componentObject.constants.MODAL_ID))), 'Expected modal to not be present');
                browser.waitForAbsence(element(by.id(componentObject.constants.MODAL_ID)));
            });
        },
        assertModalIsPresent: function() {
            browser.switchToParent().then(function() {
                browser.waitUntil(EC.presenceOf(element(by.id(componentObject.constants.MODAL_ID))), 'Expected modal to be present');
                expect(element(by.id(componentObject.constants.MODAL_ID)).isPresent()).toBe(true);
            });
        },
        assertSuccessAlertIsDisplayed: function() {
            expect(browser.waitToBeDisplayed(componentObject.elements.getSuccessAlert())).toBeTruthy();
        }
    };

    return componentObject;
})();
