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

    var pageObjects = {

        elements: {

            getOpenModalButton: function() {
                return browser.findElement("#button_openModal", true);
            },

            getShowSliderPanelButton: function() {
                return browser.findElement("#button_showSliderPanel", true);
            },

            getIsDirtySwitch: function() {
                return browser.findElement("label[for='isDirtySwitch']");
            }

        },

        actions: {

            clickOnIsDirtySwitch: function() {
                return pageObjects.elements.getIsDirtySwitch().click();
            },

            showModal: function() {
                return pageObjects.elements.getOpenModalButton().click();
            },

            showSliderPanel: function() {
                return pageObjects.actions.showModal().then(function() {
                    return pageObjects.elements.getShowSliderPanelButton().click();
                });

            }

        }

    };

    return pageObjects;

})();
