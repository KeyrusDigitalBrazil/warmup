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
module.exports = function selectComponentObjectFactory() {

    /**
     * Represents a standard html select with options inside
     * Will work with ng-options
     */
    function Select(selectLocator) {

        var locator = selectLocator;

        function getElement() {
            return element(locator);
        }

        // =============== ACTIONS ===============

        this.actions = {
            selectOptionByText: function(text) {
                return browser.click(getElement().element(by.cssContainingText('option', text)));
            }
        };
    }

    return {

        /**
         * To protect again stale elements, no element will even be stored.
         * Each time the elements is needed it will be recreated from the locator.
         *
         * @returns Select A new Instance of Select component object
         */
        byLocator: function(locator) {
            return new Select(locator);
        },

        /**
         * To protect again stale elements, no element will even be stored.
         * Each time the elements is needed it will be recreated from the locator.
         *
         * @returns Select A new Instance of Select component object
         */
        byElement: function(element) {
            return new Select(element.locator);
        }

    };

}();
