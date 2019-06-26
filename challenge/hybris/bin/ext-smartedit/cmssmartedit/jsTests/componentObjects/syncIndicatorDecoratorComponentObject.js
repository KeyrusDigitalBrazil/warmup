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

    componentObject.selectors = {

        getDecoratorBySlotIdAndSyncStatus: function(slotId, isInSync) {
            return by.css('[data-smartedit-component-id="' + slotId + '"] .sync-indicator-decorator.' + (isInSync ? 'IN_SYNC' : 'NOT_SYNC'));
        },
        getDecoratorStatusBySlotId: function(slotId) {
            return element(componentObject.selectors.getDecoratorBySlotId(slotId)).getAttribute("data-sync-status");
        }
    };

    componentObject.assertions = {

        slotIsOutOfSync: function(slotId) {
            return browser.waitToBeDisplayed(componentObject.selectors.getDecoratorBySlotIdAndSyncStatus(slotId, false), "expected sync decorator on slot " + slotId);
        },
        slotIsInSync: function(slotId) {
            return browser.waitToBeDisplayed(componentObject.selectors.getDecoratorBySlotIdAndSyncStatus(slotId, true), "expected sync decorator on slot " + slotId);
        }
    };

    return componentObject;
})();
