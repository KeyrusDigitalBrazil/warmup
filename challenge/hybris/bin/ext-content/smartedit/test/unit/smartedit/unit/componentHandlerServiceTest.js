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
describe('componentHandlerService - inner', function() {

    var yjQueryObject, componentHandlerService, $rootScope;

    beforeEach(angular.mock.module('smarteditServicesModule'));

    beforeEach(inject(function(_componentHandlerService_, _$rootScope_) {
        componentHandlerService = _componentHandlerService_;
        $rootScope = _$rootScope_;
        yjQueryObject = {};
        spyOn(componentHandlerService, "getFromSelector").and.returnValue(yjQueryObject);
    }));

    it('getOverlay will get a yjQuery reference on the overlay by id', function() {

        expect(componentHandlerService.getOverlay()).toBe(yjQueryObject);
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith("#smarteditoverlay");
    });

    it('getComponent will get a yjQuery reference on an object containing the given class and having the given id and type', function() {

        expect(componentHandlerService.getComponent('theid', 'thetype', 'myclass')).toBe(yjQueryObject);
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(".myclass[data-smartedit-component-id='theid'][data-smartedit-component-type='thetype']");
    });

    it('getComponentUnderSlot will get a yjQuery reference on an object containing the given class and having the given id and type', function() {

        expect(componentHandlerService.getComponentUnderSlot('theComponentId', 'thetype', 'theSlotId', 'myclass')).toBe(yjQueryObject);
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith("[data-smartedit-component-id='theSlotId'][data-smartedit-component-type='ContentSlot'] .myclass[data-smartedit-component-id='theComponentId'][data-smartedit-component-type='thetype']");
    });

    it('getOriginalComponent will get a yjQuery reference on an object containing the smartEditComponent class and having the given id and type', function() {

        expect(componentHandlerService.getOriginalComponent('theid', 'thetype')).toBe(yjQueryObject);
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(".smartEditComponent[data-smartedit-component-id='theid'][data-smartedit-component-type='thetype']");
    });

    it('getOriginalComponentWithinSlot will get a yjQuery reference on an object containing the smartEditComponent class and having the given id and type within a given slot ID', function() {

        expect(componentHandlerService.getOriginalComponentWithinSlot('theid', 'thetype', 'theSlotId')).toBe(yjQueryObject);
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith("[data-smartedit-component-id='theSlotId'][data-smartedit-component-type='ContentSlot'] .smartEditComponent[data-smartedit-component-id='theid'][data-smartedit-component-type='thetype']");
    });

    it('getOverlayComponentWithinSlot will get a yjQuery reference on an object containing the smartEditComponent class and having the given id and type within a given slot ID', function() {

        expect(componentHandlerService.getOverlayComponentWithinSlot('theid', 'thetype', 'theSlotId')).toBe(yjQueryObject);
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith("[data-smartedit-component-id='theSlotId'][data-smartedit-component-type='ContentSlot'] .smartEditComponentX[data-smartedit-component-id='theid'][data-smartedit-component-type='thetype']");
    });

    it('getOverlayComponent will fetch component under slot in the overlay', function() {

        var parent = {};
        var originalComponent = jasmine.createSpyObj('originalComponent', ['attr', 'parent']);
        originalComponent.parent.and.returnValue(parent);
        originalComponent.attr.and.callFake(function(attribute) {
            if (attribute === 'data-smartedit-component-id') {
                return 'theid';
            } else if (attribute === 'data-smartedit-component-type') {
                return 'thetype';
            }
        });

        spyOn(componentHandlerService, 'getParentSlotForComponent').and.returnValue("slotId");
        expect(componentHandlerService.getOverlayComponent(originalComponent)).toBe(yjQueryObject);

        expect(componentHandlerService.getParentSlotForComponent).toHaveBeenCalledWith(parent);
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith("[data-smartedit-component-id='slotId'][data-smartedit-component-type='ContentSlot'] .smartEditComponentX[data-smartedit-component-id='theid'][data-smartedit-component-type='thetype']");
    });

    it('getOverlayComponent will fetch slot under in the overlay', function() {

        var parent = {};
        var originalComponent = jasmine.createSpyObj('originalComponent', ['attr', 'parent']);
        originalComponent.parent.and.returnValue(parent);
        originalComponent.attr.and.callFake(function(attribute) {
            if (attribute === 'data-smartedit-component-id') {
                return 'theid';
            } else if (attribute === 'data-smartedit-component-type') {
                return 'thetype';
            }
        });

        spyOn(componentHandlerService, 'getParentSlotForComponent').and.returnValue(undefined);
        expect(componentHandlerService.getOverlayComponent(originalComponent)).toBe(yjQueryObject);

        expect(componentHandlerService.getParentSlotForComponent).toHaveBeenCalledWith(parent);
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(".smartEditComponentX[data-smartedit-component-id='theid'][data-smartedit-component-type='thetype']");
    });

    it('getParent of an original component will fetch closest parent in the storefront layer', function() {

        var parent = {};
        var component = jasmine.createSpyObj('component', ['attr', 'hasClass', 'closest']);
        component.attr.and.callFake(function(attribute) {
            if (attribute === 'data-smartedit-component-id') {
                return 'theid';
            } else if (attribute === 'data-smartedit-component-type') {
                return 'thetype';
            }
        });

        component.hasClass.and.callFake(function(className) {
            if (className === 'smartEditComponent') {
                return true;
            } else if (className === 'smartEditComponentX') {
                return false;
            } else {
                return null;
            }
        });

        component.closest.and.returnValue(parent);
        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getParent(component)).toBe(parent);
        expect(component.closest).toHaveBeenCalledWith(".smartEditComponent[data-smartedit-component-id][data-smartedit-component-id!='theid']");
    });

    it('getParent of an overlay component will fetch closest parent in the overlay', function() {

        var parent = {};
        var component = jasmine.createSpyObj('component', ['attr', 'hasClass', 'closest']);
        component.attr.and.callFake(function(attribute) {
            if (attribute === 'data-smartedit-component-id') {
                return 'theid';
            } else if (attribute === 'data-smartedit-component-type') {
                return 'thetype';
            }
        });

        component.hasClass.and.callFake(function(className) {
            if (className === 'smartEditComponent') {
                return false;
            } else if (className === 'smartEditComponentX') {
                return true;
            } else {
                return null;
            }
        });

        component.closest.and.returnValue(parent);
        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getParent(component)).toBe(parent);
        expect(component.closest).toHaveBeenCalledWith(".smartEditComponentX[data-smartedit-component-id][data-smartedit-component-id!='theid']");
    });

    it('getParent of a component from an unkown layer will throw an exception', function() {

        var parent = {};
        var component = jasmine.createSpyObj('component', ['attr', 'hasClass', 'closest']);
        component.attr.and.callFake(function(attribute) {
            if (attribute === 'data-smartedit-component-id') {
                return 'theid';
            } else if (attribute === 'data-smartedit-component-type') {
                return 'thetype';
            }
        });

        component.hasClass.and.returnValue(null);

        component.closest.and.returnValue(parent);
        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(function() {
            componentHandlerService.getParent(component);
        }).toThrowError("componentHandlerService.getparent.error.component.from.unknown.layer");
    });


    it('setId will set the data-smartedit-component-id field of a given component', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        componentHandlerService.getFromSelector.and.returnValue(component);
        componentHandlerService.setId(originalComponent, 'theid');

        expect(component.attr).toHaveBeenCalledWith('data-smartedit-component-id', 'theid');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getId will get the data-smartedit-component-id field of a given component', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.returnValue('theid');

        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getId(originalComponent)).toBe('theid');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('setType will set the data-smartedit-component-type field of a given component', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        componentHandlerService.getFromSelector.and.returnValue(component);
        componentHandlerService.setType(originalComponent, 'thetype');

        expect(component.attr).toHaveBeenCalledWith('data-smartedit-component-type', 'thetype');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getSlotOperationRelatedId will get the data-smartedit-container-id when it is defined AND data-smartedit-container-type is defined', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.callFake(function(attr) {
            if (attr === 'data-smartedit-component-id') {
                return 'theid';
            } else if (attr === 'data-smartedit-container-id') {
                return 'thecontainerid';
            } else if (attr === 'data-smartedit-container-type') {
                return 'thecontainertype';
            }
        });

        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getSlotOperationRelatedId(originalComponent)).toBe('thecontainerid');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getSlotOperationRelatedId will get the data-smartedit-component-id when data-smartedit-container-id is defined BUT data-smartedit-container-type is undefined', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.callFake(function(attr) {
            if (attr === 'data-smartedit-component-id') {
                return 'theid';
            } else if (attr === 'data-smartedit-container-id') {
                return 'thecontainerid';
            } else if (attr === 'data-smartedit-container-type') {
                return undefined;
            }
        });

        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getSlotOperationRelatedId(originalComponent)).toBe('theid');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getSlotOperationRelatedId will get the data-smartedit-component-id when data-smartedit-container-id is undefined', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.callFake(function(attr) {
            if (attr === 'data-smartedit-component-id') {
                return 'theid';
            } else if (attr === 'data-smartedit-container-id') {
                return undefined;
            }
        });

        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getSlotOperationRelatedId(originalComponent)).toBe('theid');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getType will get the data-smartedit-component-type field of a given component', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.returnValue('thetype');

        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getType(originalComponent)).toBe('thetype');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getSlotOperationRelatedType will get the data-smartedit-container-type when it is defined AND data-smartedit-container-id is defined', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.callFake(function(attr) {
            if (attr === 'data-smartedit-component-type') {
                return 'thetype';
            } else if (attr === 'data-smartedit-container-type') {
                return 'thecontainertype';
            } else if (attr === 'data-smartedit-container-id') {
                return 'thecontainerid';
            }
        });

        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getSlotOperationRelatedType(originalComponent)).toBe('thecontainertype');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getSlotOperationRelatedType will get the data-smartedit-component-type when data-smartedit-container-type is defined BUT data-smartedit-container-id is undefined', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.callFake(function(attr) {
            if (attr === 'data-smartedit-component-type') {
                return 'thetype';
            } else if (attr === 'data-smartedit-container-type') {
                return 'thecontainertype';
            } else if (attr === 'data-smartedit-container-id') {
                return undefined;
            }
        });

        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getSlotOperationRelatedType(originalComponent)).toBe('thetype');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getSlotOperationRelatedType will get the data-smartedit-component-type when data-smartedit-container-type is undefined', function() {

        var originalComponent = {
            key: 'value'
        };
        var component = jasmine.createSpyObj('component', ['attr']);
        component.attr.and.callFake(function(attr) {
            if (attr === 'data-smartedit-component-type') {
                return 'thetype';
            } else if (attr === 'data-smartedit-container-type') {
                return undefined;
            }
        });

        componentHandlerService.getFromSelector.and.returnValue(component);
        expect(componentHandlerService.getSlotOperationRelatedType(originalComponent)).toBe('thetype');
        expect(componentHandlerService.getFromSelector).toHaveBeenCalledWith(originalComponent);
    });

    it('getAllComponentsSelector will return a yjQuery selector matching all non-slots components', function() {

        expect(componentHandlerService.getAllComponentsSelector()).toBe(".smartEditComponent[data-smartedit-component-type!='ContentSlot']");
    });

    it('getAllSlotsSelector will return a yjQuery selector matching all slots components', function() {

        expect(componentHandlerService.getAllSlotsSelector()).toBe(".smartEditComponent[data-smartedit-component-type='ContentSlot']");
    });

    it('getParentSlotForComponent will return slot ID for the given component', function() {

        var parent = jasmine.createSpyObj('parent', ['attr']);
        var component = jasmine.createSpyObj('component', ['closest']);

        parent.attr.and.returnValue('slotId');

        componentHandlerService.getFromSelector.and.returnValue(component);
        component.closest.and.returnValue(parent);

        expect(componentHandlerService.getParentSlotForComponent(component)).toBe('slotId');
    });

    it('getComponentPositionInSlot will return the position for the given component within a slot', function() {
        var slotId = 'slot1';
        var componentId = 'comp2';

        spyOn(componentHandlerService, "getId");
        componentHandlerService.getId.and.callFake(function(component) {
            return component.id;
        });
        spyOn(componentHandlerService, 'getOriginalComponentsWithinSlot').and.returnValue([{
            id: 'comp1'
        }, {
            id: componentId
        }, {
            id: 'comp3'
        }]);

        expect(componentHandlerService.getComponentPositionInSlot(slotId, componentId)).toBe(1);
    });

    describe('getFirstSmartEditComponentChildren', function() {
        var PARENT_CSS_PATH = "body > main > .smartEditComponent";
        var FIRST_LEVEL_CHILD_PATH = "body > main > .smartEditComponent > .smartEditComponent";
        var DEEP_LEVEL_CHILD_PATH = "body > main > .smartEditComponent > div > ul > .smartEditComponent";
        var DEEP_LEVEL_NESTED_CHILD_PATH = "body > main > .smartEditComponent > div > .smartEditComponent > ul > .smartEditComponent";
        var NON_CHILD_PATH = "body > ul > .smartEditComponent > .smartEditComponent";

        var parentComponent, childComponents, actual;

        beforeEach(function() {
            parentComponent = jasmine.createSpyObj('parentComponent', ['getCssPath', 'find']);
            childComponents = [{
                getCssPath: function() {
                    return FIRST_LEVEL_CHILD_PATH;
                }
            }, {
                getCssPath: function() {
                    return DEEP_LEVEL_CHILD_PATH;
                }
            }, {
                getCssPath: function() {
                    return DEEP_LEVEL_NESTED_CHILD_PATH;
                }
            }, {
                getCssPath: function() {
                    return NON_CHILD_PATH;
                }
            }];
        });

        beforeEach(function() {
            parentComponent.getCssPath.and.returnValue(PARENT_CSS_PATH);
            parentComponent.find.and.returnValue(childComponents);

            var callCount = -1;
            componentHandlerService.getFromSelector.and.callFake(function() {
                callCount++;
                var callReturnValues = [parentComponent].concat(childComponents);
                return callReturnValues[callCount];
            });
        });

        beforeEach(function() {
            actual = componentHandlerService.getFirstSmartEditComponentChildren(parentComponent);
        });

        it('should return the first level of SmartEdit components', function() {
            expect(actual.length).toBe(2);
            expect(actual[0]).toBe(childComponents[0]);
            expect(actual[1]).toBe(childComponents[1]);
        });
    });

});
