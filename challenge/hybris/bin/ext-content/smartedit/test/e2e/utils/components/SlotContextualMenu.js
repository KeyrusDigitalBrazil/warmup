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
var storefront = require('./Storefront.js');
var perspectives = require('./Perspectives.js');

module.exports = {
    getDottedSlotBorderForNonEmptySlot: function() {
        return perspectives.elements.deprecated_getElementInOverlay(storefront.constants.TOP_HEADER_SLOT_ID)
            .element(by.css('.decorator-basic-slot-border'));
    },
    getDottedSlotBorderForEmptySlot: function() {
        return perspectives.elements.deprecated_getElementInOverlay(storefront.constants.OTHER_SLOT_ID)
            .element(by.css('.decorator-basic-slot-border'));
    }
};
