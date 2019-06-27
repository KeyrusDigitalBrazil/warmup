/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
window.sfConfigManager.registerLayout(
    "TEST_CMS_LAYOUT", {
        pageId: 'test_CMS_layout_page_id',
        catalogVersion: 'test_CMS_layout-catalog-version',
        nodeType: 'root',
        children: [{
            componentId: 'testSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: [{
                componentId: 'testComp',
                componentType: 'someCompType',
                nodeType: 'component'
            }]
        }]
    }
);
