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

var bigdataLayout = {
    pageId: 'homepage',
    catalogVersion: 'apparel-ukContentCatalog/Staged',
    nodeType: 'root',
    children: [{
        componentId: 'topHeaderSlot',
        componentType: 'ContentSlot',
        nodeType: 'slot',
        children: [{
            componentId: 'component1',
            componentType: 'componentType1',
            nodeType: 'component',
            view: {
                hasTemplate: true,
                basePath: 'BUNDLE_ROOT_PLACEHOLDER',
                showHeader: false
            }
        }, {
            componentId: 'component2',
            componentType: 'componentType2',
            nodeType: 'component',
            view: {
                hasTemplate: true,
                basePath: 'BUNDLE_ROOT_PLACEHOLDER',
            }
        }, {
            componentId: 'component3',
            componentType: 'SimpleResponsiveBannerComponent',
            nodeType: 'component',
            view: {
                hasTemplate: true,
                basePath: 'BUNDLE_ROOT_PLACEHOLDER',
            }
        }]
    }, {
        alias: "sfBuilderFixtures",
        nodeType: 'alias'
    }, {
        componentId: 'bottomHeaderSlot',
        componentType: 'ContentSlot',
        nodeType: 'slot',
        children: [{
            componentId: 'component4',
            componentType: 'componentType4',
            nodeType: 'component',
            catalogVersion: 'apparelContentCatalog/Online',
        }]
    }, {
        componentId: 'footerSlot',
        componentType: 'ContentSlot',
        catalogVersion: 'apparel-euContentCatalog/Staged',
        nodeType: 'slot',
        children: [{
            componentId: 'component5',
            componentType: 'componentType5',
            nodeType: 'component'
        }]
    }, {
        componentId: 'searchBoxSlot',
        componentType: 'ContentSlot',
        nodeType: 'slot',
        children: [{
            componentId: 'component8',
            componentType: 'componentType8',
            nodeType: 'component',
        }]
    }, {
        componentId: 'miniCartSlot',
        componentType: 'ContentSlot',
        nodeType: 'slot',
        children: [{
            componentId: 'component10',
            componentType: 'componentType10',
            nodeType: 'component',
        }, {
            componentId: 'component11',
            componentType: 'componentType11',
            nodeType: 'component',
        }]
    }, {
        alias: 'slotWrapper',
        nodeType: 'alias'
    }, {
        componentId: 'headerLinksSlot',
        componentType: 'ContentSlot',
        nodeType: 'slot',
        children: [{
            componentId: 'component9',
            componentType: 'componentType9',
            nodeType: 'component'
        }]
    }, {
        componentId: 'deepLinksSlot',
        componentType: 'ContentSlot',
        nodeType: 'slot',
        view: {
            hasTemplate: true,
            basePath: 'BUNDLE_ROOT_PLACEHOLDER',
            showHeader: false
        },
        children: []
    }]
};

for (var i = 1; i < 300; i++) {
    bigdataLayout.children[0].children.push({
        componentId: 'component-0' + i,
        componentType: 'componentType1',
        nodeType: 'component',
        view: {
            hasTemplate: false,
            showHeader: true
        }
    });
}

sfConfigManager.registerLayout(
    "BIGDATA_LAYOUT", bigdataLayout
);
