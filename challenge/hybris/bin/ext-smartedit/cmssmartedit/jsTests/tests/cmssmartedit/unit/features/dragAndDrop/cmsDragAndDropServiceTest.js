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
describe('cmsDragAndDropService', function() {

    // Constants
    var DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';

    // Variables
    var mocks, cmsDragAndDropService;
    var gateway, gatewayFactory, $rootScope, $q, highlightedHint;

    beforeEach(angular.mock.module('smarteditServicesModule', function($provide) {
        gateway = jasmine.createSpyObj('gateway', ['subscribe']);
        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway']);
        gatewayFactory.createGateway.and.returnValue(gateway);

        $provide.value('gatewayFactory', gatewayFactory);
    }));

    beforeEach(function() {
        var harness = AngularUnitTestHelper
            .prepareModule('cmsDragAndDropServiceModule')
            .mock('dragAndDropService', 'register')
            .mock('dragAndDropService', 'apply')
            .mock('dragAndDropService', 'update')
            .mock('dragAndDropService', 'unregister')
            .mock('dragAndDropService', 'markDragStarted')
            .mock('dragAndDropService', 'markDragStopped')
            .mock('componentHandlerService', 'getSlotOperationRelatedId')
            .mock('componentHandlerService', 'getSlotOperationRelatedUuid')
            .mock('componentHandlerService', 'getSlotOperationRelatedType')
            .mock('componentHandlerService', 'getId')
            .mock('componentHandlerService', 'getUuid')
            .mock('componentHandlerService', 'getOverlay')
            .mock('componentHandlerService', 'getCatalogVersionUuid')
            .mock('componentHandlerService', 'getComponentPositionInSlot')
            .mock('componentHandlerService', 'getComponentUnderSlot')
            .mock('componentHandlerService', 'getComponent')
            .mock('waitDialogService', 'showWaitModal')
            .mock('waitDialogService', 'hideWaitModal')
            .mock('systemEventService', 'subscribe')
            .mock('systemEventService', 'publishAsync').and.returnResolvedPromise(true)
            .mock('systemEventService', 'publish').and.returnResolvedPromise(true)
            .mock('slotRestrictionsService', 'emptyCache')
            .mock('alertService', 'showDanger')
            .mock('componentEditingFacade', 'addNewComponentToSlot')
            .mock('componentEditingFacade', 'addExistingComponentToSlot')
            .mock('componentEditingFacade', 'moveComponent')
            .mock('assetsService', 'getAssetsRoot')
            .mock('browserService', 'isSafari')
            .mockConstant('OVERLAY_RERENDERED_EVENT', 'overlayRerendered')
            .mockConstant('CONTENT_SLOT_TYPE', 'CONTENT_SLOT_TYPE')
            .mockConstant('COMPONENT_REMOVED_EVENT', 'COMPONENT_REMOVED_EVENT')
            .mockConstant('CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS', {
                PROCESS_COMPONENTS: 'contractChangeListenerProcessComponents',
                RESTART_PROCESS: 'contractChangeListenerRestartProcess'
            })
            .mockConstant('SMARTEDIT_COMPONENT_PROCESS_STATUS', 'smartEditComponentProcessStatus')
            .mockConstant('CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS', {
                PROCESS: 'processComponent',
                REMOVE: 'removeComponent',
                KEEP_VISIBLE: 'keepComponentVisible'
            })
            .service('cmsDragAndDropService');

        mocks = harness.mocks;
        $rootScope = harness.injected.$rootScope;
        $q = harness.injected.$q;
        cmsDragAndDropService = harness.service;

        harness.mocks.componentHandlerService.getCatalogVersionUuid.and.returnValue('ANY_UUID');
    });

    it('WHEN cmsDragAndDropService is created THEN the service creates a gateway to communicate with the other frame.', function() {
        // Assert
        expect(gatewayFactory.createGateway).toHaveBeenCalledWith('cmsDragAndDrop');
        expect(cmsDragAndDropService._gateway).toBe(gateway);
    });

    describe('register', function() {

        it('WHEN register is called THEN the right configuration is stored in the base drag and drop service.', function() {
            // Arrange

            // Act
            cmsDragAndDropService.register();

            // Assert
            expect(mocks.dragAndDropService.register).toHaveBeenCalled();
            var arg = mocks.dragAndDropService.register.calls.argsFor(0)[0];
            expect(arg.id).toBe(DRAG_AND_DROP_ID);
            expect(arg.sourceSelector).toEqual(["#smarteditoverlay .smartEditComponentX[data-smartedit-component-type!='ContentSlot'] .movebutton",
                ".movebutton"
            ]);
            expect(arg.targetSelector).toBe("#smarteditoverlay .smartEditComponentX[data-smartedit-component-type='ContentSlot']");
            expect(arg.enableScrolling).toBe(true);
        });

        it('WHEN register is called THEN the right onStart callback is registered', function() {
            // Arrange
            var expectedResult = 'someResult';
            spyOn(cmsDragAndDropService, 'onStart').and.returnValue(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            expect(mocks.dragAndDropService.register).toHaveBeenCalled();
            var arg = mocks.dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.startCallback();
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN the right onStop callback is registered', function() {
            // Arrange
            var expectedResult = 'someResult';
            spyOn(cmsDragAndDropService, 'onStop').and.returnValue(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            expect(mocks.dragAndDropService.register).toHaveBeenCalled();
            var arg = mocks.dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.stopCallback();
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN the right onDragEnter callback is registered', function() {
            // Arrange
            var expectedResult = 'someResult';
            spyOn(cmsDragAndDropService, 'onDragEnter').and.returnValue(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            expect(mocks.dragAndDropService.register).toHaveBeenCalled();
            var arg = mocks.dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.dragEnterCallback();
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN the right onDragOver callback is registered', function() {
            // Arrange
            var expectedResult = 'someResult';
            spyOn(cmsDragAndDropService, 'onDragOver').and.returnValue(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            expect(mocks.dragAndDropService.register).toHaveBeenCalled();
            var arg = mocks.dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.dragOverCallback();
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN the right onDragEnd callback is registered', function() {
            // Arrange
            var expectedResult = 'someResult';
            spyOn(cmsDragAndDropService, 'onStop').and.returnValue(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            expect(mocks.dragAndDropService.register).toHaveBeenCalled();
            var arg = mocks.dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.stopCallback();
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN the right onDrop callback is registered', function() {
            // Arrange
            var expectedResult = 'someResult';
            spyOn(cmsDragAndDropService, 'onDrop').and.returnValue(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            expect(mocks.dragAndDropService.register).toHaveBeenCalled();
            var arg = mocks.dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.dropCallback();
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN the right helper function is registered', function() {
            // Arrange 
            var expectedResult = 'some Result';
            spyOn(cmsDragAndDropService, '_getDragImageSrc').and.returnValue(expectedResult);

            // Act 
            cmsDragAndDropService.register();

            // Assert
            expect(mocks.dragAndDropService.register).toHaveBeenCalled();
            var arg = mocks.dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.helper();
            expect(result).toBe(expectedResult);
        });
    });

    it('WHEN unregister is called THEN the service is cleaned.', function() {

        spyOn(cmsDragAndDropService, '_overlayRenderedUnSubscribeFn');
        spyOn(cmsDragAndDropService, '_componentRemovedUnSubscribeFn');

        // Act
        cmsDragAndDropService.unregister();

        // Assert
        expect(mocks.dragAndDropService.unregister).toHaveBeenCalledWith([DRAG_AND_DROP_ID]);
        expect(mocks.slotRestrictionsService.emptyCache).toHaveBeenCalled();

        expect(cmsDragAndDropService._overlayRenderedUnSubscribeFn).toHaveBeenCalled();
        expect(cmsDragAndDropService._componentRemovedUnSubscribeFn).toHaveBeenCalled();
    });

    describe('apply', function() {
        beforeEach(function() {
            spyOn(cmsDragAndDropService, '_addUIHelpers');
            spyOn(cmsDragAndDropService, '_initializeDragOperation');
            spyOn(cmsDragAndDropService, '_cleanDragOperation');
        });

        it('WHEN apply is called THEN the page is prepared for the drag and drop operations', function() {
            // Arrange

            // Act
            cmsDragAndDropService.apply();

            // Assert
            expect(mocks.dragAndDropService.apply).toHaveBeenCalled();
            expect(cmsDragAndDropService._addUIHelpers).toHaveBeenCalled();
            expect(mocks.systemEventService.subscribe).toHaveBeenCalledWith('overlayRerendered', jasmine.any(Function));
            expect(mocks.systemEventService.subscribe).toHaveBeenCalledWith('COMPONENT_REMOVED_EVENT', jasmine.any(Function));
            expect(gateway.subscribe).toHaveBeenCalledWith('CMS_DRAG_STARTED', jasmine.any(Function));
            expect(gateway.subscribe).toHaveBeenCalledWith('CMS_DRAG_STOPPED', jasmine.any(Function));
        });

        it('WHEN drag is started in the outer frame THEN the right callback is called.', function() {
            // Arrange

            // Act
            var eventId = '';
            var data = 'some data';
            cmsDragAndDropService.apply();
            var callback = gateway.subscribe.calls.argsFor(0)[1];
            callback(eventId, data);

            // Assert
            expect(mocks.dragAndDropService.markDragStarted).toHaveBeenCalled();
            expect(cmsDragAndDropService._initializeDragOperation).toHaveBeenCalledWith(data);
        });

        it('WHEN drop is stopped from the outer frame THEN the right callback is called.', function() {
            // Arrange

            // Act
            var eventId = '';
            var data = 'some data';
            cmsDragAndDropService.apply();
            var callback = gateway.subscribe.calls.argsFor(1)[1];
            callback(eventId, data);

            // Assert
            expect(mocks.dragAndDropService.markDragStopped).toHaveBeenCalled();
            expect(cmsDragAndDropService._cleanDragOperation).toHaveBeenCalled();
        });
    });

    // Event Handlers
    describe('event handlers', function() {
        var event, component, hint, otherComponent, otherHint, slot;
        var componentId, componentType, slotId, initialValues;
        var componentUuid, slotUuid;

        beforeEach(function() {
            componentId = 'some component id';
            componentUuid = 'some component Uuid';
            componentType = 'some component type';
            slotId = 'some slot id';
            slotUuid = 'some slot id';

            event = {
                target: 'someTarget'
            };

            slot = jasmine.createSpyObj('slot', ['closest']);
            slot.id = 'initial slot ID';
            slot.isAllowed = true;

            component = jasmine.createSpyObj('component', ['closest', 'addClass', 'removeClass', 'attr']);
            component.id = 'initial component ID';
            component.original = component;

            hint = jasmine.createSpyObj('hint', ['addClass', 'removeClass']);
            hint.id = 'some hint id';
            hint.original = hint;

            component.closest.and.callFake(function(arg) {
                if (arg === ".smartEditComponentX[data-smartedit-component-type!='ContentSlot']") {
                    return component;
                } else {
                    return slot;
                }
            });

            component.attr.and.callFake(function(arg) {
                if (arg === "data-component-id") {
                    return componentId;
                } else if (arg === "data-component-uuid") {
                    return componentUuid;
                } else if (arg === "data-component-type") {
                    return componentType;
                } else {
                    return slotId;
                }
            });

            mocks.componentHandlerService.getSlotOperationRelatedId.and.returnValue(componentId);
            mocks.componentHandlerService.getSlotOperationRelatedUuid.and.returnValue(componentUuid);
            mocks.componentHandlerService.getSlotOperationRelatedType.and.returnValue(componentType);
            mocks.componentHandlerService.getId.and.returnValue(slotId);
            mocks.componentHandlerService.getUuid.and.returnValue(slotUuid);

            initialValues = {
                hint: hint,
                component: component,
                slot: slot
            };

            cmsDragAndDropService._highlightedHint = initialValues.hint;
            cmsDragAndDropService._highlightedComponent = initialValues.component;
            cmsDragAndDropService._highlightedSlot = initialValues.slot;

            cmsDragAndDropService._dragInfo = {
                componentId: 'dragged component'
            };

            cmsDragAndDropService._cachedSlots = {};
            cmsDragAndDropService._cachedSlots[slotId] = {
                components: [component, otherComponent]
            };

            otherComponent = jasmine.createSpyObj('otherComponent', ['addClass']);
            otherComponent.id = 'other component ID';

            cmsDragAndDropService._cachedSlots = {};
            cmsDragAndDropService._cachedSlots[slotId] = {
                components: [component, otherComponent]
            };

            otherHint = jasmine.createSpyObj('otherHint', ['addClass', 'removeClass']);
            otherHint.id = 'other hint id';
            otherHint.original = otherHint;

            otherComponent.hints = [initialValues.hint, otherHint];
        });

        it('WHEN onStart is called THEN it prepares the page for the drag operation', function() {
            // Arrange
            var expectedDragInfo = {
                componentId: componentId,
                componentUuid: componentUuid,
                componentType: componentType,
                slotId: slotId,
                slotUuid: slotUuid,
                slotOperationRelatedId: componentId,
                slotOperationRelatedType: componentType
            };
            spyOn(cmsDragAndDropService, '_initializeDragOperation');
            spyOn(cmsDragAndDropService, '_getSelector').and.returnValue(component);

            // Act
            cmsDragAndDropService.onStart(event);

            // Assert
            expect(component.addClass).toHaveBeenCalledWith('component_dragged');
            expect(cmsDragAndDropService._initializeDragOperation).toHaveBeenCalledWith(expectedDragInfo);
        });

        it('GIVEN the cursor enters a slot WHEN onDragEnter is called THEN the slot is highlighted', function() {
            // Arrange
            spyOn(cmsDragAndDropService, '_highlightSlot');

            // Act
            cmsDragAndDropService.onDragEnter(event);

            // Assert
            expect(cmsDragAndDropService._highlightSlot).toHaveBeenCalledWith(event);
        });

        it('GIVEN the cursor is over a slot and the hints are already highlighted WHEN onDragOver is called THEN nothing is done', function() {
            // Arrange
            spyOn(cmsDragAndDropService, '_isMouseInRegion').and.returnValue(true);
            spyOn(cmsDragAndDropService, '_clearHighlightedHint');
            spyOn(cmsDragAndDropService, '_clearHighlightedComponent');

            // Act
            cmsDragAndDropService.onDragOver(event);
            $rootScope.$digest();

            // Assert
            expect(cmsDragAndDropService._clearHighlightedHint).not.toHaveBeenCalled();
            expect(cmsDragAndDropService._clearHighlightedComponent).not.toHaveBeenCalled();

            expect(cmsDragAndDropService._highlightedHint).toBe(initialValues.hint);
            expect(cmsDragAndDropService._highlightedComponent).toBe(initialValues.component);
            expect(cmsDragAndDropService._highlightedSlot).toBe(initialValues.slot);
        });

        it('GIVEN the cursor is over a slot and the hint changes WHEN onDragOver is called THEN the hints are updated', function() {
            // Arrange
            spyOn(cmsDragAndDropService, '_isMouseInRegion').and.callFake(function(evt, item) {
                if (item === initialValues.hint) {
                    return false;
                } else {
                    return true;
                }
            });
            spyOn(cmsDragAndDropService, '_clearHighlightedHint');
            spyOn(cmsDragAndDropService, '_clearHighlightedComponent');

            // Act
            cmsDragAndDropService.onDragOver(event);
            $rootScope.$digest();

            // Assert
            expect(cmsDragAndDropService._clearHighlightedHint).toHaveBeenCalled();
            expect(cmsDragAndDropService._clearHighlightedComponent).not.toHaveBeenCalled();

            expect(cmsDragAndDropService._highlightedHint).toBe(initialValues.hint);
            expect(cmsDragAndDropService._highlightedComponent).toBe(initialValues.component);
            expect(cmsDragAndDropService._highlightedSlot).toBe(initialValues.slot);
        });

        it('GIVEN the cursor is over a slot and the component changes WHEN onDragOver is called THEN the component hints are updated', function() {
            // Arrange
            spyOn(cmsDragAndDropService, '_isMouseInRegion').and.callFake(function(evt, item) {
                if (item === initialValues.hint) {
                    return false;
                } else if (item === initialValues.component) {
                    return false;
                } else if (item === otherComponent) {
                    return true;
                }

                return true;
            });
            spyOn(cmsDragAndDropService, '_clearHighlightedHint').and.callThrough();
            spyOn(cmsDragAndDropService, '_clearHighlightedComponent').and.callThrough();

            // Act
            cmsDragAndDropService.onDragOver(event);
            $rootScope.$digest();

            // Assert
            expect(cmsDragAndDropService._clearHighlightedHint).toHaveBeenCalled();
            expect(cmsDragAndDropService._clearHighlightedComponent).toHaveBeenCalled();

            expect(cmsDragAndDropService._highlightedHint).toBe(otherHint);
            expect(cmsDragAndDropService._highlightedComponent).toBe(otherComponent);
            expect(cmsDragAndDropService._highlightedSlot).toBe(initialValues.slot);
        });

        it('GIVEN the cursor is over a slot and the slot changes WHEN onDragOver is called THEN the slot hints are updated', function() {
            // Arrange
            spyOn(cmsDragAndDropService, '_isMouseInRegion').and.callFake(function(evt, item) {
                if (item === initialValues.hint) {
                    return false;
                } else if (item === initialValues.component) {
                    return false;
                } else if (item === otherComponent) {
                    return true;
                }

                return true;
            });
            spyOn(cmsDragAndDropService, '_clearHighlightedHint').and.callThrough();
            spyOn(cmsDragAndDropService, '_clearHighlightedComponent').and.callThrough();

            cmsDragAndDropService._cachedSlots = {};
            cmsDragAndDropService._cachedSlots[slotId] = {
                components: [component, otherComponent]
            };

            // Act
            cmsDragAndDropService.onDragOver(event);
            $rootScope.$digest();

            // Assert
            expect(cmsDragAndDropService._clearHighlightedHint).toHaveBeenCalled();
            expect(cmsDragAndDropService._clearHighlightedComponent).toHaveBeenCalled();

            expect(cmsDragAndDropService._highlightedHint).toBe(otherHint);
            expect(cmsDragAndDropService._highlightedComponent).toBe(otherComponent);
            expect(cmsDragAndDropService._highlightedSlot).toBe(initialValues.slot);

            expect(cmsDragAndDropService._highlightedHint.addClass).toHaveBeenCalledWith('overlayDropzone--hovered');
        });

        it('GIVEN the cursor is over the dragged element WHEN onDragOver is called THEN the component is highlighted', function() {
            // Arrange
            cmsDragAndDropService._dragInfo.slotOperationRelatedId = initialValues.component.id;
            cmsDragAndDropService._highlightedComponent = null;
            mocks.componentHandlerService.getSlotOperationRelatedId.and.returnValue(component.id);
            spyOn(cmsDragAndDropService, '_isMouseInRegion').and.callFake(function(evt, item) {
                if (item === initialValues.hint) {
                    return false;
                } else if (item === initialValues.component) {
                    return true;
                } else if (item === otherComponent) {
                    return false;
                }

                return true;
            });
            spyOn(cmsDragAndDropService, '_clearHighlightedHint').and.callThrough();
            spyOn(cmsDragAndDropService, '_clearHighlightedComponent').and.callThrough();

            // Act
            cmsDragAndDropService.onDragOver(event);
            $rootScope.$digest();

            // Assert
            expect(cmsDragAndDropService._clearHighlightedHint).toHaveBeenCalled();

            expect(cmsDragAndDropService._highlightedHint).toBe(null);
            expect(cmsDragAndDropService._highlightedComponent).toBe(component);
            expect(cmsDragAndDropService._highlightedSlot).toBe(initialValues.slot);

            expect(component.addClass).toHaveBeenCalledWith('component_dragged_hovered');
        });

        it('WHEN mouse leaves drag area THEN the slot is cleared', function() {
            // Arrange
            spyOn(cmsDragAndDropService, '_isMouseInRegion').and.returnValue(false);
            spyOn(cmsDragAndDropService, '_clearHighlightedSlot').and.callThrough();
            var currentSlot = jasmine.createSpyObj('highlightedSlot', ['removeClass']);
            currentSlot.original = currentSlot;
            cmsDragAndDropService._highlightedSlot = currentSlot;

            // Act
            cmsDragAndDropService.onDragLeave();
            $rootScope.$digest();

            // Assert
            expect(cmsDragAndDropService._clearHighlightedSlot).toHaveBeenCalled();
            expect(mocks.systemEventService.publish.calls.count()).toBe(2);
            expect(mocks.systemEventService.publish.calls.argsFor(0)[0]).toEqual('HIDE_SLOT_MENU');
            expect(mocks.systemEventService.publish.calls.argsFor(1)[0]).toEqual('CMS_DRAG_LEAVE');
        });

        it('GIVEN the mouse is still in a drag area WHEN a drag leave event is triggered THEN the slot is kept highlighted', function() {
            // Arrange
            spyOn(cmsDragAndDropService, '_isMouseInRegion').and.returnValue(true);
            spyOn(cmsDragAndDropService, '_clearHighlightedSlot');

            // Act
            cmsDragAndDropService.onDragLeave(event);
            $rootScope.$digest();

            // Assert
            expect(cmsDragAndDropService._clearHighlightedSlot).not.toHaveBeenCalled();
        });

        it('WHEN onStop is called THEN it cleans the drag operation', function() {
            // Arrange
            spyOn(cmsDragAndDropService, '_cleanDragOperation');
            spyOn(cmsDragAndDropService, '_getSelector').and.returnValue(component);

            // Act
            cmsDragAndDropService.onStop(event);

            // Assert
            expect(mocks.systemEventService.publish).toHaveBeenCalledWith('contractChangeListenerRestartProcess');
            expect(cmsDragAndDropService._cleanDragOperation).toHaveBeenCalledWith(component);
        });
    });

    describe('onDrop', function() {
        var event, slot, dragInfo, targetSlotId, targetSlotUuid, slotInfo;

        beforeEach(function() {
            spyOn(cmsDragAndDropService, '_scrollToModifiedSlot');
            mocks.componentEditingFacade.addNewComponentToSlot.and.returnValue($q.when());
            mocks.componentEditingFacade.addExistingComponentToSlot.and.returnValue($q.when());
            mocks.componentEditingFacade.moveComponent.and.returnValue($q.when());

            var expectedResult = 'someResult';
            spyOn(cmsDragAndDropService, 'onStop').and.returnValue(expectedResult);

            dragInfo = {
                slotId: 'some slot id',
                componentId: 'some component id',
                componentType: 'some component type',
                slotOperationRelatedId: 'some component id'
            };
            cmsDragAndDropService._dragInfo = dragInfo;
            cmsDragAndDropService._highlightedSlot = {
                isAllowed: true,
                components: ['some component']
            };

            event = {
                target: 'someTarget'
            };

            targetSlotId = 'some target slot id';
            targetSlotUuid = 'some target slot uuid';

            slotInfo = {
                targetSlotId: targetSlotId,
                targetSlotUUId: targetSlotUuid
            };

            mocks.componentHandlerService.getId.and.returnValue(targetSlotId);
            mocks.componentHandlerService.getUuid.and.returnValue(targetSlotUuid);

            highlightedHint = {
                position: 2
            };
        });

        it('WHEN a component is dropped outside a drop area THEN nothing happens', function() {
            // Arrange
            cmsDragAndDropService._highlightedSlot = null;

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.alertService.showDanger).not.toHaveBeenCalled();
        });

        it('WHEN a component is dropped in an invalid slot THEN an alert is displayed', function() {
            // Arrange
            cmsDragAndDropService._highlightedSlot.isAllowed = false;
            var expectedTranslation = 'se.drag.and.drop.not.valid.component.type';
            var expectedResult = {
                message: expectedTranslation
            };

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.alertService.showDanger).toHaveBeenCalledWith(expectedResult);
            expect(mocks.componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(cmsDragAndDropService._scrollToModifiedSlot).not.toHaveBeenCalled();
            expect(cmsDragAndDropService.onStop).not.toHaveBeenCalled();
        });

        it('WHEN a new component is dropped THEN the generic editor modal is displayed for it.', function() {
            // Arrange
            cmsDragAndDropService._dragInfo.slotId = null;
            cmsDragAndDropService._dragInfo.componentId = null;
            cmsDragAndDropService._highlightedHint = highlightedHint;
            var expectedPosition = highlightedHint.position;

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledWith(slotInfo, 'ANY_UUID', dragInfo.componentType, expectedPosition);
            expect(mocks.componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.moveComponent).not.toHaveBeenCalled();
            expect(cmsDragAndDropService._scrollToModifiedSlot).toHaveBeenCalledWith(targetSlotId);
            expect(mocks.waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(mocks.waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN an existing component is dropped into a slot that already has an instance THEN an error message is displayed.', function() {
            // Arrange
            mocks.componentEditingFacade.addExistingComponentToSlot.and.returnValue($q.reject());
            cmsDragAndDropService._dragInfo.slotId = null;
            cmsDragAndDropService._highlightedHint = highlightedHint;
            var expectedPosition = highlightedHint.position;
            var expectedDragInfo = {
                componentId: dragInfo.componentId,
                componentUuid: dragInfo.componentUuid,
                componentType: dragInfo.componentType
            };

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledWith(targetSlotId, expectedDragInfo, expectedPosition);
            expect(cmsDragAndDropService.onStop).toHaveBeenCalled();
            expect(mocks.waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(mocks.waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN an existing component is dropped THEN the generic editor modal is displayed for it.', function() {
            // Arrange
            cmsDragAndDropService._dragInfo.slotId = null;
            cmsDragAndDropService._highlightedHint = highlightedHint;
            var expectedPosition = highlightedHint.position;
            var expectedDragInfo = {
                componentId: dragInfo.componentId,
                componentUuid: dragInfo.componentUuid,
                componentType: dragInfo.componentType
            };

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledWith(targetSlotId, expectedDragInfo, expectedPosition);
            expect(mocks.componentEditingFacade.moveComponent).not.toHaveBeenCalled();
            expect(cmsDragAndDropService._scrollToModifiedSlot).toHaveBeenCalledWith(targetSlotId);
            expect(mocks.waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(mocks.waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN a new component is moved between slots THEN page is updated accordingly.', function() {
            // Arrange
            cmsDragAndDropService._highlightedHint = highlightedHint;
            var expectedPosition = highlightedHint.position;

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.moveComponent).toHaveBeenCalledWith(dragInfo.slotId, targetSlotId, dragInfo.componentId, expectedPosition);
            expect(cmsDragAndDropService._scrollToModifiedSlot).toHaveBeenCalledWith(targetSlotId);
            expect(mocks.waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(mocks.waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN a new component is moved within the same slot before THEN page is updated accordingly.', function() {
            // Arrange
            var currentPosition = 3;
            mocks.componentHandlerService.getComponentPositionInSlot.and.returnValue(currentPosition);

            cmsDragAndDropService._highlightedHint = highlightedHint;
            var expectedPosition = highlightedHint.position;
            cmsDragAndDropService._dragInfo.slotId = targetSlotId;

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.moveComponent).toHaveBeenCalledWith(dragInfo.slotId, targetSlotId, dragInfo.componentId, expectedPosition);
            expect(cmsDragAndDropService._scrollToModifiedSlot).toHaveBeenCalledWith(targetSlotId);
            expect(mocks.waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(mocks.waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN a new component is moved within the same slot after THEN page is updated accordingly.', function() {
            // Arrange
            var currentPosition = 1;
            mocks.componentHandlerService.getComponentPositionInSlot.and.returnValue(currentPosition);

            cmsDragAndDropService._highlightedHint = highlightedHint;
            var expectedPosition = highlightedHint.position - 1;
            cmsDragAndDropService._dragInfo.slotId = targetSlotId;

            // Act 
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.moveComponent).toHaveBeenCalledWith(dragInfo.slotId, targetSlotId, dragInfo.componentId, expectedPosition);
            expect(cmsDragAndDropService._scrollToModifiedSlot).toHaveBeenCalledWith(targetSlotId);
            expect(mocks.waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(mocks.waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN a new component is dropped on an empty slot THEN the generic editor modal is displayed for it.', function() {
            // Arrange
            cmsDragAndDropService._dragInfo.slotId = null;
            cmsDragAndDropService._dragInfo.componentId = null;
            cmsDragAndDropService._highlightedSlot.components = [];
            var expectedPosition = 0;

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.componentEditingFacade.addNewComponentToSlot).toHaveBeenCalledWith(slotInfo, 'ANY_UUID', dragInfo.componentType, expectedPosition);
            expect(mocks.componentEditingFacade.addExistingComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.moveComponent).not.toHaveBeenCalled();
            expect(cmsDragAndDropService._scrollToModifiedSlot).toHaveBeenCalledWith(targetSlotId);
            expect(mocks.waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(mocks.waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

        it('WHEN an existing component is dropped on an empty slot THEN the generic editor modal is displayed for it.', function() {
            // Arrange
            cmsDragAndDropService._dragInfo.slotId = null;
            cmsDragAndDropService._highlightedSlot.components = [];
            var expectedPosition = 0;
            var expectedDragInfo = {
                componentId: dragInfo.componentId,
                componentUuid: dragInfo.componentUuid,
                componentType: dragInfo.componentType
            };

            // Act
            cmsDragAndDropService.onDrop(event);
            $rootScope.$digest();

            // Assert
            expect(mocks.componentEditingFacade.addNewComponentToSlot).not.toHaveBeenCalled();
            expect(mocks.componentEditingFacade.addExistingComponentToSlot).toHaveBeenCalledWith(targetSlotId, expectedDragInfo, expectedPosition);
            expect(mocks.componentEditingFacade.moveComponent).not.toHaveBeenCalled();
            expect(cmsDragAndDropService._scrollToModifiedSlot).toHaveBeenCalledWith(targetSlotId);
            expect(mocks.waitDialogService.showWaitModal).toHaveBeenCalled();
            expect(mocks.waitDialogService.hideWaitModal).toHaveBeenCalled();
        });

    });

    it('WHEN a hint is cleared THEN the classes are removed', function() {
        // Arrange
        var currentHint = jasmine.createSpyObj('currentHint', ['removeClass']);
        currentHint.original = currentHint;
        cmsDragAndDropService._highlightedHint = currentHint;

        // Act
        cmsDragAndDropService._clearHighlightedHint();

        // Assert
        expect(currentHint.removeClass).toHaveBeenCalledWith('overlayDropzone--hovered');
        expect(cmsDragAndDropService._highlightedHint).toBe(null);
    });

    it('WHEN a component is cleared THEN the classes are removed', function() {
        // Arrange
        spyOn(cmsDragAndDropService, '_clearHighlightedHint');

        var currentComponent = jasmine.createSpyObj('highlightedComponent', ['removeClass']);
        currentComponent.original = currentComponent;
        cmsDragAndDropService._highlightedComponent = currentComponent;

        // Act
        cmsDragAndDropService._clearHighlightedComponent();

        // Assert
        expect(cmsDragAndDropService._clearHighlightedHint).toHaveBeenCalled();
        expect(currentComponent.removeClass).toHaveBeenCalledWith('component_dragged_hovered');
        expect(cmsDragAndDropService._highlightedComponent).toBe(null);
    });

    it('WHEN a slot is cleared THEN the classes are removed', function() {
        // Arrange
        spyOn(cmsDragAndDropService, '_clearHighlightedComponent');
        var currentSlot = jasmine.createSpyObj('highlightedSlot', ['removeClass']);
        currentSlot.original = currentSlot;
        cmsDragAndDropService._highlightedSlot = currentSlot;

        // Act
        cmsDragAndDropService._clearHighlightedSlot();

        // Assert
        expect(cmsDragAndDropService._clearHighlightedComponent).toHaveBeenCalled();
        expect(currentSlot.removeClass).toHaveBeenCalledWith('over-slot-enabled');
        expect(currentSlot.removeClass).toHaveBeenCalledWith('over-slot-disabled');
        expect(cmsDragAndDropService._highlightedSlot).toBe(null);
    });

    // Helper Methods
    it('WHEN _onOverlayUpdate is called THEN update is called', function() {
        // Arrange
        spyOn(cmsDragAndDropService, 'update');

        // Act
        var result = cmsDragAndDropService._onOverlayUpdate();
        $rootScope.$digest();

        // Assert
        result.then(function() {
            expect(cmsDragAndDropService.update).toHaveBeenCalled();
        });
    });

    it('WHEN update is called THEN the page is refreshed', function() {
        // Arrange'
        spyOn(cmsDragAndDropService, '_addUIHelpers');
        spyOn(cmsDragAndDropService, '_cacheElements');

        // Act
        cmsDragAndDropService.update();

        // Assert
        expect(mocks.dragAndDropService.update).toHaveBeenCalledWith(DRAG_AND_DROP_ID);
        expect(cmsDragAndDropService._addUIHelpers).toHaveBeenCalled();
        expect(cmsDragAndDropService._cacheElements).toHaveBeenCalled();
    });


    it('WHEN _initializeDragOperation is called THEN the page is prepared for dragging components', function() {
        // Arrange
        var dragInfo = 'some drag info';
        var overlay = jasmine.createSpyObj('overlay', ['addClass']);
        mocks.componentHandlerService.getOverlay.and.returnValue(overlay);
        spyOn(cmsDragAndDropService, '_cacheElements');

        // Act
        cmsDragAndDropService._initializeDragOperation(dragInfo);

        // Assert
        expect(cmsDragAndDropService._cacheElements).toHaveBeenCalled();
        expect(cmsDragAndDropService._dragInfo).toBe(dragInfo);
        expect(overlay.addClass).toHaveBeenCalledWith('smarteditoverlay_dndRendering');
        expect(mocks.systemEventService.publishAsync).toHaveBeenCalledWith('CMS_DRAG_STARTED');
    });

    it('WHEN _cleanDragOperation is called THEN the page is cleaned up', function() {
        // Arrange
        var draggedComponent = jasmine.createSpyObj('draggedComponent', ['removeClass']);
        var overlay = jasmine.createSpyObj('overlay', ['removeClass']);
        mocks.componentHandlerService.getOverlay.and.returnValue(overlay);
        spyOn(cmsDragAndDropService, '_clearHighlightedSlot');

        // Act
        cmsDragAndDropService._cleanDragOperation(draggedComponent);

        // Assert
        expect(cmsDragAndDropService._clearHighlightedSlot).toHaveBeenCalled();
        expect(draggedComponent.removeClass).toHaveBeenCalledWith('component_dragged');
        expect(overlay.removeClass).toHaveBeenCalledWith('smarteditoverlay_dndRendering');
        expect(mocks.systemEventService.publishAsync).toHaveBeenCalledWith('CMS_DRAG_STOPPED');
        expect(cmsDragAndDropService._dragInfo).toBe(null);
        expect(cmsDragAndDropService._cachedSlots).toEqual({});
        expect(cmsDragAndDropService._highlightedSlot).toBe(null);
    });

    it('GIVEN user is in Safari WHEN _getDragImageSrc is called THEN it returns the right image path', function() {
        // Arrange 
        var basePath = '/some_base';
        mocks.browserService.isSafari.and.returnValue(true);
        mocks.assetsService.getAssetsRoot.and.returnValue(basePath);

        var expectedPath = basePath + '/images/contextualmenu_move_on.png';

        // Act
        resultPath = cmsDragAndDropService._getDragImageSrc();

        // Assert
        expect(resultPath).toBe(expectedPath);
    });

    it('GIVEN user is not in Safari WHEN _getDragImageSrc is called THEN it returns empty image path', function() {
        // Arrange
        var expectedResult = '';

        // Act
        // - Chrome
        mocks.browserService.isSafari.and.returnValue(false);

        // Assert
        expect(cmsDragAndDropService._getDragImageSrc()).toBe(expectedResult, 'No drag image needed for other browsers than safari');
        expect(mocks.browserService.isSafari).toHaveBeenCalled();
    });
});
