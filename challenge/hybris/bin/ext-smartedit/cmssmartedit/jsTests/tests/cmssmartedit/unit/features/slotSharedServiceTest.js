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
describe('slotSharedService', function() {

    var $q, $rootScope, harness, pageContentSlotsService, slotSharedService, cmsitemsRestService, componentHandlerService, $translate;
    var pagescontentslots = [{
        pageId: "homepage",
        slotId: "topHeaderSlot",
        position: 0,
        slotShared: true
    }, {
        pageId: "homepage",
        slotId: "bottomHeaderSlot",
        position: 1,
        slotShared: false
    }, {
        pageId: "homepage",
        slotId: "footerSlot",
        position: 2,
        slotShared: false
    }, {
        pageId: "homepage",
        slotId: "otherSlot",
        position: 3,
        slotShared: true
    }];

    var newCustomSlot = {
        "catalogVersion": "apparel-ukContentCatalog/Staged",
        "uid": "cmsitem_1234567890",
        "itemtype": "ContentSlot",
        "name": "new-component",
        "uuid": "uuid-1234567890",
        "cmsComponents": []
    };

    var getComponentAttributes = function() {
        return {
            smarteditComponentId: "smarteditComponentId",
            contentSlotUuid: "contentSlotUuid",
            componentType: "componentType",
            catalogVersionUuid: "catalogVersionUuid"
        };
    };

    var PAGE_UUID = "1234";

    beforeEach(function() {

        harness = AngularUnitTestHelper.prepareModule('slotSharedServiceModule')
            .mock('pageContentSlotsService', 'getPageContentSlots')
            .mock('cmsitemsRestService', 'create')
            .mock('componentHandlerService', 'getPageUID')
            .mock('confirmationModalService', 'confirm')
            .service('slotSharedService');

        $translate = jasmine.createSpy('$translate');

        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

        slotSharedService = harness.service;
        pageContentSlotsService = harness.mocks.pageContentSlotsService;
        cmsitemsRestService = harness.mocks.cmsitemsRestService;
        componentHandlerService = harness.mocks.componentHandlerService;

        pageContentSlotsService.getPageContentSlots.and.returnResolvedPromise(pagescontentslots);
        cmsitemsRestService.create.and.returnResolvedPromise(newCustomSlot);
        componentHandlerService.getPageUID.and.returnValue(PAGE_UUID);
        $translate.and.returnValue("clone");
    });

    describe('isSlotShared ', function() {
        it('should return a promise which resolves to true when the backend response indicates the slot is shared', function() {
            var resolvedPromise = slotSharedService.isSlotShared("topHeaderSlot");
            $rootScope.$digest();
            expect(resolvedPromise).toBeResolvedWithData(true);
        });

        it('should return a promise which resolves to false when the backend response indicates the slot is not shared', function() {
            var resolvedPromise = slotSharedService.isSlotShared("footerSlot");
            $rootScope.$digest();
            expect(resolvedPromise).toBeResolvedWithData(false);
        });
    });

    describe('convertSharedSlotToNonSharedSlot ', function() {
        it('should return a promise which resolves to the new slot object', function() {
            var componentAttributes = getComponentAttributes();
            var resolvedPromise = slotSharedService.convertSharedSlotToNonSharedSlot(componentAttributes, false);
            $rootScope.$digest();
            resolvedPromise.then(function(cmsItem) {
                expect(cmsItem).toEqual(newCustomSlot);
            });
        });

        it('should return a constructed cmsItem Parameter', function() {
            var componentAttributes = getComponentAttributes();
            var constructedCmsItemParam = slotSharedService._constructCmsItemParameter(componentAttributes, false);
            $rootScope.$digest();
            constructedCmsItemParam.then(function(constructedCmsItemParam) {
                expect(constructedCmsItemParam).toEqual({
                    name: PAGE_UUID + "-" + "smarteditComponentId-se.cms.slot.shared.clone",
                    smarteditComponentId: 'smarteditComponentId',
                    contentSlotUuid: 'contentSlotUuid',
                    pageUuid: PAGE_UUID,
                    cloneComponents: false,
                    itemtype: 'componentType',
                    catalogVersion: 'catalogVersionUuid'
                });
            });
        });

        it('should throw an error if componentAttributes is not set', function() {
            //GIVEN
            var componentAttributes = null;

            expect(function() {
                //WHEN
                slotSharedService.convertSharedSlotToNonSharedSlot(componentAttributes);

                //THEN
            }).toThrow(new Error("Parameter: componentAttributes needs to be supplied!"));
        });

        it('should throw an error if componentAttributes.smarteditComponentId parameter is not set', function() {
            //GIVEN
            var componentAttributes = getComponentAttributes();
            componentAttributes.smarteditComponentId = null;

            expect(function() {
                //WHEN
                slotSharedService.convertSharedSlotToNonSharedSlot(componentAttributes);

                //THEN
            }).toThrow(new Error("Parameter: componentAttributes.smarteditComponentId needs to be supplied!"));
        });

        it('should throw an error if componentAttributes.contentSlotUuid parameter is not set', function() {
            //GIVEN
            var componentAttributes = getComponentAttributes();
            componentAttributes.contentSlotUuid = null;

            expect(function() {
                //WHEN
                slotSharedService.convertSharedSlotToNonSharedSlot(componentAttributes);

                //THEN
            }).toThrow(new Error("Parameter: componentAttributes.contentSlotUuid needs to be supplied!"));
        });

        it('should throw an error if componentAttributes.componentType parameter is not set', function() {
            //GIVEN
            var componentAttributes = getComponentAttributes();
            componentAttributes.componentType = null;

            expect(function() {
                //WHEN
                slotSharedService.convertSharedSlotToNonSharedSlot(componentAttributes);

                //THEN
            }).toThrow(new Error("Parameter: componentAttributes.componentType needs to be supplied!"));
        });

        it('should throw an error if componentAttributes.catalogVersionUuid parameter is not set', function() {
            //GIVEN
            var componentAttributes = getComponentAttributes();
            componentAttributes.catalogVersionUuid = null;

            expect(function() {
                //WHEN
                slotSharedService.convertSharedSlotToNonSharedSlot(componentAttributes);

                //THEN
            }).toThrow(new Error("Parameter: componentAttributes.catalogVersionUuid needs to be supplied!"));
        });
    });

});
