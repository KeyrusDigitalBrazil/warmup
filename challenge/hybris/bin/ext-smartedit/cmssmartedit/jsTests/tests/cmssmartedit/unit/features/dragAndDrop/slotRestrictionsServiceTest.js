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
describe('slotRestrictionsService', function() {

    var fixture;
    var slotRestrictionsService;
    var $q;
    var $rootScope;
    var mockComponentHandlerService;
    var mockRestServiceFactory;
    var mockSlotRestrictionsRestService;
    var mockPageContentSlotsComponentsRestService;
    var mockTypePermissionsRestService;
    var crossFrameEventService;
    var pageChangeListener;
    var MOCK_SLOT_RESTRICTIONS;
    var MOCK_PAGE_UID;

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(function() {
        fixture = AngularUnitTestHelper.prepareModule('slotRestrictionsServiceModule')
            .mockConstant('CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI', 'CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI')
            .mockConstant('CONTENT_SLOT_TYPE', 'ContentSlot')
            .mock('gatewayProxy', 'initForService')
            .mock('componentHandlerService', 'getPageUID')
            .mock('componentHandlerService', 'isExternalComponent')
            .mock('slotSharedService', 'emptyCache')
            .mock('slotSharedService', 'isSlotShared')
            .mock('slotSharedService', 'areSharedSlotsDisabled')
            .mock('restServiceFactory', 'get')
            .mock('crossFrameEventService', 'subscribe')
            .mock('pageContentSlotsComponentsRestService', 'getComponentsForSlot')
            .mock('typePermissionsRestService', 'hasUpdatePermissionForTypes')
            .mockConstant('EVENTS', {
                PAGE_CHANGE: 'PAGE_CHANGE'
            })
            .service('slotRestrictionsService');

        slotRestrictionsService = fixture.service;
        $q = fixture.injected.$q;
        $rootScope = fixture.injected.$rootScope;
        mockRestServiceFactory = fixture.mocks.restServiceFactory;
        mockComponentHandlerService = fixture.mocks.componentHandlerService;
        mockTypePermissionsRestService = fixture.mocks.typePermissionsRestService;
        mockPageContentSlotsComponentsRestService = fixture.mocks.pageContentSlotsComponentsRestService;

        crossFrameEventService = fixture.mocks.crossFrameEventService;

        expect(crossFrameEventService.subscribe).toHaveBeenCalledWith("PAGE_CHANGE", jasmine.any(Function));
        pageChangeListener = crossFrameEventService.subscribe.calls.argsFor(0)[1];

        spyOn(slotRestrictionsService, 'emptyCache').and.callThrough();



    });

    beforeEach(function() {
        mockSlotRestrictionsRestService = jasmine.createSpyObj('mockSlotRestrictionsRestService', ['get']);
        mockRestServiceFactory.get.and.returnValue(mockSlotRestrictionsRestService);
    });

    beforeEach(function() {
        MOCK_PAGE_UID = 'SomePageUID';
        MOCK_SLOT_RESTRICTIONS = {
            validComponentTypes: [
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]
        };
    });

    describe('getSlotRestrictions', function() {


        it('initialises with gatewayProxy', function() {
            expect(fixture.mocks.gatewayProxy.initForService).toHaveBeenCalledWith(slotRestrictionsService, ['getAllComponentTypesSupportedOnPage', 'getSlotRestrictions'], 'SLOT_RESTRICTIONS');
        });


        it('should cache the page ID', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID.calls.count())
                .toBe(1, 'Expected componentHandlerService.getPageUID() to have been called only once');
        });

        it('should delegate to the slot restrictions REST service to fetch the components allowed in a given slot and page', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockSlotRestrictionsRestService.get).toHaveBeenCalledWith({
                slotUid: 'SomeSlotUID',
                pageUid: 'SomePageUID'
            });
        });

        it('should return no valid component types for external slots AND not call the REST service', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockComponentHandlerService.isExternalComponent.and.returnValue(true);

            // Act
            var restrictionsPromise = slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(restrictionsPromise).toBeResolvedWithData([]);
            expect(mockSlotRestrictionsRestService.get).not.toHaveBeenCalled();
        });

        it('should cache type restrictions by slot ID', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockSlotRestrictionsRestService.get.calls.count())
                .toBe(1, 'Expected slot restrictions REST service GET to have been called only once');
        });

        it('should return a promise that resolves to a list of valid component types', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            var slotRestrictionsPromise = slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(slotRestrictionsPromise).toBeResolvedWithData([
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]);
        });

        it('should return the cached list of valid component types on subsequent calls with the same slot ID', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            var firstCallSlotRestrictionsPromise = slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            var secondCallSlotRestrictionsPromise = slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(firstCallSlotRestrictionsPromise).toBeResolvedWithData([
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]);
            expect(secondCallSlotRestrictionsPromise).toBeResolvedWithData([
                'SomeComponentType1',
                'SomeComponentType2',
                'SomeComponentType3'
            ]);
        });
    });

    describe('isComponentAllowedInSlot', function() {
        it('should return a promise resolving to true if the component type is allowed in the given slot AND source and target slots are the same AND the target slot already contains the component', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));
            mockPageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue($q.when([{
                uid: 'something'
            }]));
            var slot = {
                id: 'SomeSlotUID',
                components: [{
                    id: 'something'
                }]
            };
            var dragInfo = {
                slotId: 'SomeSlotUID',
                componentType: 'SomeComponentType1',
                componentId: 'something'
            };
            // Act
            var isComponentAllowedInSlotPromise = slotRestrictionsService.isComponentAllowedInSlot(slot, dragInfo);

            // Assert
            expect(isComponentAllowedInSlotPromise).toBeResolvedWithData(true);
        });

        it('should return a promise resolving to true if the component type is allowed in the given slot AND the slot does not already contain the component', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));
            mockPageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue($q.when([{
                uid: 'something'
            }]));
            var slot = {
                id: 'SomeSlotUID',
                components: [{
                    id: 'something'
                }]
            };
            var dragInfo = {
                slotId: 'SomeOtherSlotUID',
                componentType: 'SomeComponentType1',
                componentId: 'SomeComponentId1'
            };
            // Act
            var isComponentAllowedInSlotPromise = slotRestrictionsService.isComponentAllowedInSlot(slot, dragInfo);

            // Assert
            expect(isComponentAllowedInSlotPromise).toBeResolvedWithData(true);
        });

        it('should return a promise resolving to false if the component type is allowed in the given slot AND source and target slots are different AND the slot already contains the component', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));
            mockPageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue($q.when([{
                uid: 'SomeComponentId1'
            }]));
            var slot = {
                id: 'SomeSlotUID',
                components: [{
                    id: 'SomeComponentId1'
                }]
            };
            var dragInfo = {
                slotId: 'SomeOtherSlotUID',
                componentType: 'SomeComponentType1',
                componentId: 'SomeComponentId1'
            };
            // Act
            var isComponentAllowedInSlotPromise = slotRestrictionsService.isComponentAllowedInSlot(slot, dragInfo);

            // Assert
            expect(isComponentAllowedInSlotPromise).toBeResolvedWithData(false);
        });

        it('should return a promise resolving to false if the component type is not allowed in the given slot', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));
            mockPageContentSlotsComponentsRestService.getComponentsForSlot.and.returnValue($q.when([{
                uid: 'something'
            }]));
            var slot = {
                id: 'SomeSlotUID',
                components: [{
                    id: 'something'
                }]
            };
            var dragInfo = {
                slotId: 'SomeOtherSlotUID',
                componentType: 'SomeComponentType4',
                componentId: 'SomeComponentId4'
            };

            // Act
            var isComponentAllowedInSlotPromise = slotRestrictionsService.isComponentAllowedInSlot(slot, dragInfo);

            // Assert
            expect(isComponentAllowedInSlotPromise).toBeResolvedWithData(false);
        });
    });

    describe('emptyCache', function() {

        it('when page changes emptyCache is called', function() {
            expect(slotRestrictionsService.emptyCache).not.toHaveBeenCalled();
            pageChangeListener();
            expect(slotRestrictionsService.emptyCache).toHaveBeenCalled();
        });


        it('should invalidate the cache such that the next call to getSlotRestrictions will fetch the current page ID', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            slotRestrictionsService.emptyCache();
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID.calls.count())
                .toBe(2, 'Expected componentHandlerService.getPageUID() to have been called both before and after cache invalidation');
        });

        it('should invalidate the cache such that the next call to getSlotRestrictions for a slot ID that was once cached should fetch the type restrictions for this slot', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockSlotRestrictionsRestService.get.and.returnValue($q.when(MOCK_SLOT_RESTRICTIONS));

            // Act
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();
            slotRestrictionsService.emptyCache();
            slotRestrictionsService.getSlotRestrictions('SomeSlotUID');
            fixture.detectChanges();

            // Assert
            expect(mockSlotRestrictionsRestService.get.calls.count())
                .toBe(2, 'Expected slot restrictions REST service GET to have been called both before and after cache invalidation');
        });
    });


    describe('slotEditable - ', function() {
        var slotId = 'some slot';
        var areSharedSlotsDisabled, isSlotShared, isExternalComponent;

        beforeEach(function() {
            fixture.mocks.slotSharedService.isSlotShared.and.callFake(function() {
                return $q.when(isSlotShared);
            });

            fixture.mocks.componentHandlerService.isExternalComponent.and.callFake(function() {
                return isExternalComponent;
            });

            fixture.mocks.slotSharedService.areSharedSlotsDisabled.and.callFake(function() {
                return areSharedSlotsDisabled;
            });
        });

        describe("with CHANGE permissions ", function() {

            beforeEach(function() {
                mockTypePermissionsRestService.hasUpdatePermissionForTypes.and.callFake(function() {
                    return $q.when({
                        ContentSlot: true
                    });
                });
            });

            it('GIVEN shared slots are disabled WHEN isSlotEditable is called AND the slot is shared THEN it returns false', function() {
                // GIVEN
                areSharedSlotsDisabled = true;
                isSlotShared = true;
                isExternalComponent = false;

                // WHEN
                var promise = slotRestrictionsService.isSlotEditable(slotId);

                // THEN
                promise.then(function(result) {
                    expect(result).toBe(false);
                });
                $rootScope.$digest();
            });

            it('GIVEN shared slots are disabled WHEN isSlotEditable is called AND the slot is not shared THEN it returns true', function() {
                // GIVEN
                areSharedSlotsDisabled = true;
                isSlotShared = false;
                isExternalComponent = false;

                // WHEN
                var promise = slotRestrictionsService.isSlotEditable(slotId);

                // THEN
                promise.then(function(result) {
                    expect(result).toBe(true);
                });
                $rootScope.$digest();
            });

            it('GIVEN shared slots are enabled WHEN isSlotEditable is called AND the slot is shared THEN it returns true', function() {
                // GIVEN
                areSharedSlotsDisabled = false;
                isSlotShared = true;
                isExternalComponent = false;

                // WHEN
                var promise = slotRestrictionsService.isSlotEditable(slotId);

                // THEN
                promise.then(function(result) {
                    expect(result).toBe(true);
                });
                $rootScope.$digest();
            });

            it('GIVEN shared slots are enabled WHEN isSlotEditable is called AND the slot is external THEN it returns false', function() {
                // GIVEN
                areSharedSlotsDisabled = false;
                isSlotShared = true;
                isExternalComponent = true;

                // WHEN
                var promise = slotRestrictionsService.isSlotEditable(slotId);

                // THEN
                promise.then(function(result) {
                    expect(result).toBe(false);
                });
                $rootScope.$digest();
            });
        });

        describe("without CHANGE permissions ", function() {
            it("GIVEN slot without CHANGE permission WHEN isSlotEditable is called THEN it returns false", function() {
                // GIVEN
                mockTypePermissionsRestService.hasUpdatePermissionForTypes.and.callFake(function() {
                    return $q.when({
                        ContentSlot: false
                    });
                });

                // WHEN
                var promise = slotRestrictionsService.isSlotEditable(slotId);

                // THEN
                promise.then(function(result) {
                    expect(result).toBe(false);
                });
                $rootScope.$digest();
            });
        });
    });
});
