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
sfConfigManager.registerLayout(
    sfConfigManager.ALIASES.DEMO_LAYOUT_ALIAS, {
        pageId: 'demo_storefront_page_id',
        catalogVersion: 'apparel-ukContentCatalog/Staged',
        nodeType: 'root',
        children: [{
            componentId: 'slot1',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            children: [{
                componentId: 'comp1',
                componentType: 'compType1',
                nodeType: 'component'
            }, {
                alias: 'asyncComponent',
                nodeType: 'alias'
            }, {
                componentId: 'slot3',
                componentType: 'componentType2',
                nodeType: 'slot',
                children: [{
                    componentId: 'comp4',
                    componentType: 'compType4',
                    nodeType: 'component',
                    children: [{
                        componentId: 'comp5',
                        componentType: 'compType5',
                        nodeType: 'component'
                    }]
                }, {
                    componentId: 'comp6',
                    componentType: 'compType6',
                    nodeType: 'component',
                }]
            }]
        }, {
            componentId: 'slot2',
            componentType: 'ContentSlot',
            nodeType: 'slot'
        }, {
            componentId: 'layersSlot',
            componentType: 'ContentSlot',
            nodeType: 'slot',
            view: {
                hasTemplate: true,
                basePath: "BUNDLE_ROOT_PLACEHOLDER"
            },
            children: []
        }, {
            alias: "sfBuilderFixtures",
            nodeType: 'alias'
        }]
    }
);
