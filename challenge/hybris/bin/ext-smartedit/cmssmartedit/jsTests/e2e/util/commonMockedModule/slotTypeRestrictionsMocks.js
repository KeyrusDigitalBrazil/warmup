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
/* jshint unused:false, undef:false */
angular
    .module('slotTypeRestrictionsMocks', ['ngMockE2E'])
    .run(function($httpBackend) {

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/topHeaderSlot\/typerestrictions/).respond({
            contentSlotName: 'topHeaderSlot',
            validComponentTypes: [
                'componentType1',
                'componentType2',
                'componentType3',
                'CMSParagraphComponent',
                'AbstractCMSComponent'
            ]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/bottomHeaderSlot\/typerestrictions/).respond({
            contentSlotName: 'bottomHeaderSlot',
            validComponentTypes: [
                'componentType4',
                'CMSParagraphComponent',
                'AbstractCMSComponent',
                'SimpleBannerComponent'
            ]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/footerSlot\/typerestrictions/).respond({
            contentSlotName: 'footerSlot',
            validComponentTypes: [
                'componentType0',
                'componentType2',
                'componentType3',
                'componentType4',
                'componentType5',
                'SimpleResponsiveBannerComponent',
                'CMSParagraphComponent',
                'AbstractCMSComponent',
            ]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/otherSlot\/typerestrictions/).respond({
            contentSlotName: 'otherSlot',
            validComponentTypes: [
                'componentType0',
                'componentType1',
                'componentType2',
                'componentType3',
                'componentType4',
                'componentType5'
            ]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/staticDummySlot\/typerestrictions/).respond({
            contentSlotName: 'otherSlot',
            validComponentTypes: [
                'componentType0',
                'componentType2',
                'componentType3',
                'componentType4',
                'componentType5',
                'AbstractCMSComponent',
                'SimpleResponsiveBannerComponent'
            ]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/emptyDummySlot\/typerestrictions/).respond({
            contentSlotName: 'emptyDummySlot',
            validComponentTypes: [
                'AbstractCMSComponent',
                'componentType4'
            ]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/searchBoxSlot\/typerestrictions/).respond({
            contentSlotName: 'searchBoxSlot',
            validComponentTypes: [
                'componentType0',
                'componentType2',
                'componentType3',
                'componentType4',
                'componentType5',
                'AbstractCMSComponent',
                'SimpleResponsiveBannerComponent'
            ]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/pages\/homepage\/contentslots\/.*\/typerestrictions/).respond({});

        // mock for customize components
        $httpBackend.whenGET(/\/cmswebservices\/v1\/sites\/apparel-uk\/catalogs\/apparel-ukContentCatalog\/versions\/Staged\/items\?currentPage=0&mask=&pageSize=10&sort=name/).respond({
            'componentItems': [{
                'creationtime': '2016-08-17T16:05:47+0000',
                'modifiedtime': '2016-08-17T16:05:47+0000',
                'name': 'Component 1',
                'pk': '1',
                'typeCode': 'CMSParagraphComponent',
                'uid': 'component1',
                'visible': true
            }, {
                'creationtime': '2016-08-17T16:05:47+0000',
                'modifiedtime': '2016-08-17T16:05:47+0000',
                'name': 'Component 2',
                'pk': '2',
                'typeCode': 'componentType2',
                'uid': 'component2',
                'visible': true
            }, {
                'creationtime': '2016-08-17T16:05:47+0000',
                'modifiedtime': '2016-08-17T16:05:47+0000',
                'name': 'Component 3',
                'pk': '3',
                'typeCode': 'componentType3',
                'uid': 'component3',
                "visible": true
            }, {
                'creationtime': '2016-08-17T16:05:47+0000',
                'modifiedtime': '2016-08-17T16:05:47+0000',
                'name': 'Component 4',
                'pk': '4',
                'typeCode': 'componentType4',
                'uid': 'component4',
                "visible": true
            }, {
                'creationtime': '2016-08-17T16:05:47+0000',
                'modifiedtime': '2016-08-17T16:05:47+0000',
                'name': 'Component 5',
                'pk': '5',
                'typeCode': 'componentType5',
                'uid': 'component5',
                "visible": true
            }]
        });
    });
try {
    angular.module('smarteditloader').requires.push('slotTypeRestrictionsMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('slotTypeRestrictionsMocks');
} catch (e) {}
