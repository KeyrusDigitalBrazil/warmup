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

    var storefront = require('./Storefront.js');

    var DecoratorsObject = {

        actions: {},

        assertions: {},

        constants: {},

        elements: {

            renderDecorator: function(componentId) {
                return storefront.elements.getComponentInOverlayById(componentId).element(by.id(componentId + '-render-button-inner'));
            },
            renderSlotDecorator: function(componentId) {
                return storefront.elements.getComponentInOverlayById(componentId).element(by.id(componentId + '-render-slot-button-inner'));
            },
            dirtyContentDecorator: function(componentId) {
                return storefront.elements.getComponentInOverlayById(componentId).element(by.id(componentId + '-dirty-content-button'));
            }

        }

    };

    return DecoratorsObject;

})();
