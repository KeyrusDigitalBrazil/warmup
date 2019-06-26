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
/* jshint undef:false */
sfConfigManager.registerRenderStrategy(sfConfigManager.ALIASES.JS_RENDERED_ALIAS,
    function(componentId, componentType, slotId) {
        if (componentId === 'staticDummySlot') {
            return false;
        } else {
            return sfBuilder.rerender(componentId, componentType, slotId);
        }
    }
);
