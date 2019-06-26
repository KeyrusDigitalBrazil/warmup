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

    var inflectionPointObject = {

        actions: {
            openInflectionPointMenu: function() {
                return browser.click(inflectionPointObject.elements.getInflectionPointMenuOpenButton());
            }
        },

        assertions: {
            inflectionPointSelectorIsNotPresent: function() {
                browser.waitForAbsence(inflectionPointObject.elements.getInflectionPointSelector(), "Expect inflection point button not to be displayed.");
            }
        },

        constants: {},

        elements: {
            getInflectionPointSelector: function() {
                return element(by.tagName('inflection-point-selector'));
            },
            getInflectionPointMenuOpenButton: function() {
                return element(by.css('inflection-point-selector button'));
            },
            getInflectionPointMenu: function() {
                return element(by.css("inflection-point-selector ul.dropdown-menu"));
            }
        },

    };

    return inflectionPointObject;

})();
