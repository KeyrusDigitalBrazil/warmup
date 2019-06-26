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
    var ID_ATTRIBUTE = 'ID';
    var UUID_ATTRIBUTE = 'UUID';
    var TYPE_ATTRIBUTE = 'TYPE';
    var DRAG_AND_DROP_ID = 'se.cms.dragAndDrop';

    var service, mocks, $q, $rootScope;

    // Variables
    var cmsDragAndDropService, dragAndDropService, systemEventService, sharedDataService;
    var gateway, gatewayFactory;

    beforeEach(function() {

        window.addModulesIfNotDeclared(['dragAndDropServiceModule', 'smarteditCommonsModule']);

        angular.mock.module(function($provide) {

            dragAndDropService = jasmine.createSpyObj('dragAndDropService', ['register', 'unregister', 'apply', 'update']);
            $provide.value('dragAndDropService', dragAndDropService);

            systemEventService = jasmine.createSpyObj('systemEventService', ['publishAsync']);
            $provide.value('systemEventService', systemEventService);

            sharedDataService = jasmine.createSpyObj('sharedDataService', ['get']);
            $provide.value('sharedDataService', sharedDataService);

            gateway = jasmine.createSpyObj('gateway', ['publish']);
            gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway']);
            gatewayFactory.createGateway.and.returnValue(gateway);
            $provide.value('gatewayFactory', gatewayFactory);

            $provide.value('ID_ATTRIBUTE', ID_ATTRIBUTE);
            $provide.value('UUID_ATTRIBUTE', UUID_ATTRIBUTE);
            $provide.value('TYPE_ATTRIBUTE', TYPE_ATTRIBUTE);
        });

    });

    beforeEach(angular.mock.module('cmsDragAndDropServiceModule'));

    beforeEach(inject(function(_cmsDragAndDropService_, _$q_, _$rootScope_) {
        cmsDragAndDropService = _cmsDragAndDropService_;
        $q = _$q_;
        $rootScope = _$rootScope_;
    }));

    it('WHEN cmsDragAndDropService is created THEN a gateway is created to communicate with the inner frame', function() {
        // Assert
        expect(gatewayFactory.createGateway).toHaveBeenCalledWith('cmsDragAndDrop');
        expect(cmsDragAndDropService._gateway).toBe(gateway);
    });

    describe('register', function() {

        it('WHEN register is called THEN it is registered in the base drag and drop service.', function() {
            // Arrange

            // Act
            cmsDragAndDropService.register();

            // Assert
            var arg = dragAndDropService.register.calls.argsFor(0)[0];
            expect(dragAndDropService.register).toHaveBeenCalled();
            expect(arg.id).toBe(DRAG_AND_DROP_ID);
            expect(arg.sourceSelector).toBe(".smartEditComponent[data-smartedit-component-type!='ContentSlot']");
            expect(arg.targetSelector).toBe("");
            expect(arg.enableScrolling).toBe(false);
        });

        it('WHEN register is called THEN it is registered with the right onStart callback.', function() {
            // Arrange
            var expectedResult = 'some result';
            spyOn(cmsDragAndDropService, '_onStart').and.returnValue(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            var arg = dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.startCallback();
            expect(result).toBe(expectedResult);
        });

        it('WHEN register is called THEN it is registered with the right onStop callback.', function() {
            // Arrange
            var expectedResult = 'some result';
            spyOn(cmsDragAndDropService, '_onStop').and.returnValue(expectedResult);

            // Act
            cmsDragAndDropService.register();

            // Assert
            var arg = dragAndDropService.register.calls.argsFor(0)[0];
            var result = arg.stopCallback();
            expect(result).toBe(expectedResult);
        });

    });

    it('WHEN apply is called THEN the cms service is applied in the base drag and drop service', function() {
        // Arrange

        // Act
        cmsDragAndDropService.apply();

        // Assert
        expect(dragAndDropService.apply).toHaveBeenCalled();
    });

    it('WHEN update is called THEN the cms service is updated in the base drag and drop service', function() {
        // Arrange

        // Act
        cmsDragAndDropService.update();

        // Assert
        expect(dragAndDropService.update).toHaveBeenCalledWith(DRAG_AND_DROP_ID);
    });

    it('WHEN unregister is called THEN the cms service is unregistered from the base drag and drop service', function() {
        // Arrange

        // Act
        cmsDragAndDropService.unregister();

        // Assert
        expect(dragAndDropService.unregister).toHaveBeenCalledWith([DRAG_AND_DROP_ID]);
    });

    it('WHEN drag is started THEN the service informs other components', function() {
        // Arrange
        var componentInfo = {
            id: 'some id',
            uuid: 'some uuid',
            type: 'some type'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.callFake(function(arg) {
            if (arg === ID_ATTRIBUTE) {
                return componentInfo.id;
            } else if (arg === UUID_ATTRIBUTE) {
                return componentInfo.uuid;
            } else if (arg === TYPE_ATTRIBUTE) {
                return componentInfo.type;
            }
        });

        var event = {
            target: 'some target'
        };
        var draggedElement = {
            closest: function() {
                return component;
            }
        };

        spyOn(cmsDragAndDropService, '_getSelector').and.returnValue(draggedElement);
        sharedDataService.get.and.returnValue($q.when(false));

        // Act
        cmsDragAndDropService._onStart(event);
        $rootScope.$digest();

        // Assert
        expect(cmsDragAndDropService._gateway.publish).toHaveBeenCalledWith('CMS_DRAG_STARTED', {
            componentId: componentInfo.id,
            componentUuid: componentInfo.uuid,
            componentType: componentInfo.type,
            slotId: null,
            slotUuid: null,
            cloneOnDrop: false
        });
        expect(systemEventService.publishAsync).toHaveBeenCalledWith('CMS_DRAG_STARTED');
    });

    it('WHEN drag is stopped THEN the inner frame is informed', function() {
        // Arrange

        // Act
        cmsDragAndDropService._onStop();

        // Assert
        expect(cmsDragAndDropService._gateway.publish).toHaveBeenCalledWith('CMS_DRAG_STOPPED');
    });

});
