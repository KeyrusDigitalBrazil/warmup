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
describe('pageContentSlotsService', function() {

    var $q, $rootScope, harness, pageContentSlotsService, pageChangeListener;
    var mockRestServiceFactory, mockPagesContentSlotsResource, mockCrossFrameEventService;

    var pagescontentslots = {
        pageContentSlotList: [{
            pageId: "homepage",
            slotId: "topHeaderSlot",
            position: 0,
            slotShared: true,
            slotStatus: 'TEMPLATE'
        }, {
            pageId: "homepage",
            slotId: "bottomHeaderSlot",
            position: 1,
            slotShared: false,
            slotStatus: 'OVERRIDE'
        }, {
            pageId: "homepage",
            slotId: "footerSlot",
            position: 2,
            slotShared: false,
            slotStatus: 'PAGE'
        }, {
            pageId: "homepage",
            slotId: "otherSlot",
            position: 3,
            slotShared: true,
            slotStatus: 'TEMPLATE'
        }]
    };

    beforeEach(function() {
        mockPagesContentSlotsResource = jasmine.createSpyObj('mockPagesContentSlotsResource', ['get']);
    });

    beforeEach(function() {

        harness = AngularUnitTestHelper.prepareModule('pageContentSlotsServiceModule')
            .mock('restServiceFactory', 'get').and.returnValue(mockPagesContentSlotsResource)
            .mock('componentHandlerService', 'getPageUID')
            .mock('crossFrameEventService', 'subscribe')
            .mockConstant('PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI', 'PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI')
            .mockConstant('EVENTS', {
                PAGE_CHANGE: 'PAGE_CHANGE'
            })
            .service('pageContentSlotsService');

        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

        pageContentSlotsService = harness.service;
        mockRestServiceFactory = harness.mocks.restServiceFactory;
        mockCrossFrameEventService = harness.mocks.crossFrameEventService;

        harness.mocks.componentHandlerService.getPageUID.and.returnValue($q.when('somePageUid'));

        mockPagesContentSlotsResource.get.and.returnValue($q.when(pagescontentslots));

        expect(mockCrossFrameEventService.subscribe).toHaveBeenCalledWith("PAGE_CHANGE", jasmine.any(Function));
        pageChangeListener = mockCrossFrameEventService.subscribe.calls.argsFor(0)[1];
    });

    describe('_reloadPageContentSlots ', function() {
        it('should resolve with a list of page content slots', function() {
            $rootScope.$digest();
            expect(pageContentSlotsService._reloadPageContentSlots()).toBeResolvedWithData(pagescontentslots.pageContentSlotList);
        });


        it('when PAGE_CHANGE event will reload its cached data ', function() {
            expect(mockPagesContentSlotsResource.get).not.toHaveBeenCalled();
            pageChangeListener();
            $rootScope.$digest();
            expect(mockPagesContentSlotsResource.get).toHaveBeenCalled();
        });

    });

    describe('getPageContentSlots ', function() {
        it('should resolve with a list of page content slots', function() {
            $rootScope.$digest();
            expect(pageContentSlotsService.getPageContentSlots()).toBeResolvedWithData(pagescontentslots.pageContentSlotList);
        });
    });

    describe('getSlotStatus ', function() {
        it('should return the slotStatus parameter for the provide slot', function() {
            var resolvedPromise = pageContentSlotsService.getSlotStatus("topHeaderSlot");
            $rootScope.$digest();
            expect(resolvedPromise).toBeResolvedWithData("TEMPLATE");

            var resolvedPromise1 = pageContentSlotsService.getSlotStatus("bottomHeaderSlot");
            $rootScope.$digest();
            expect(resolvedPromise1).toBeResolvedWithData("OVERRIDE");

            var resolvedPromise2 = pageContentSlotsService.getSlotStatus("footerSlot");
            $rootScope.$digest();
            expect(resolvedPromise2).toBeResolvedWithData("PAGE");
        });
    });

});
