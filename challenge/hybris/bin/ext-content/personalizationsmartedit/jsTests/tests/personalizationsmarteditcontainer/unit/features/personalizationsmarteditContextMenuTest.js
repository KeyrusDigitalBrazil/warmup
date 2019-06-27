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
describe('personalizationsmarteditContextMenu', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditContextModal, modalService, modalManager, scope, controller;
    var mockSlotRestrictions = ["componentType1", "componentType2"];
    var mockComponentList = [{
        typeCode: "componentType1",
        uid: "component1"
    }, {
        typeCode: "componentType1",
        uid: "component2"
    }];

    var compType1 = {
        category: "COMPONENT",
        code: "componentType1"
    };
    var compType2 = {
        category: "COMPONENT",
        code: "componentType2"
    };
    var compType3 = {
        category: "COMPONENT",
        code: "componentType3"
    };
    var mockComponentTypeList = [compType1, compType2, compType3];
    var restrictedMockComponentTypeList = [compType1, compType2];
    var mockVariation1 = {
        code: "variation1"
    };
    var mockVariation2 = {
        code: "variation2"
    };
    var mockCustomization = {
        code: "customization1",
        variations: [mockVariation1, mockVariation2]
    };

    beforeEach(function() {
        module(function($provide) {
            $provide.value('translateFilter', function(value) {
                return value;
            });
        });
    });

    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        mockModules.personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getComponents', 'getNewComponentTypes', 'getComponent', 'getCustomization']);
        $provide.value('personalizationsmarteditRestService', mockModules.personalizationsmarteditRestService);
    }));

    beforeEach(module('personalizationsmarteditContextMenu'));

    beforeEach(inject(function(_$rootScope_, _$q_, _$controller_, _personalizationsmarteditContextModal_, _modalService_) {
        personalizationsmarteditContextModal = _personalizationsmarteditContextModal_;
        modalService = _modalService_;
        controller = _$controller_;
        modalManager = {
            setButtonHandler: function() {},
            disableButton: function() {},
            enableButton: function() {}
        };
        spyOn(modalManager, 'setButtonHandler').and.callThrough();

        mockModules.modalService.open.and.callFake(function() {
            return _$q_.defer().promise;
        });

        scope = _$rootScope_.$new();
        scope.componentId = "mockComponentId";
        scope.modalManager = modalManager;

        mockModules.personalizationsmarteditRestService.getComponents.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({
                response: mockComponentList
            });
            return deferred.promise;
        });

        mockModules.personalizationsmarteditRestService.getNewComponentTypes.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({
                componentTypes: mockComponentTypeList
            });
            return deferred.promise;
        });

        mockModules.personalizationsmarteditRestService.getComponent.and.callFake(function(componentId) { // jshint ignore:line
            var deferred = _$q_.defer();
            deferred.resolve({});
            return deferred.promise;
        });

        mockModules.personalizationsmarteditRestService.getCustomization.and.callFake(function(customizationCode) { // jshint ignore:line
            var deferred = _$q_.defer();
            deferred.resolve(mockCustomization);
            return deferred.promise;
        });

        mockModules.slotRestrictionsService.getSlotRestrictions.and.callFake(function(slotId) { // jshint ignore:line
            var deferred = _$q_.defer();
            deferred.resolve(mockSlotRestrictions);
            return deferred.promise;
        });
    }));

    describe('openDeleteAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openDeleteAction).toBeDefined();
        });

        it('is called proper functions should be called', function() {
            // when
            personalizationsmarteditContextModal.openDeleteAction({});
            // then
            expect(modalService.open).toHaveBeenCalled();
        });

    });

    describe('openAddAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openAddAction).toBeDefined();
        });

        it('is called proper functions should be called', function() {
            // when
            personalizationsmarteditContextModal.openAddAction({});
            // then
            expect(modalService.open).toHaveBeenCalled();
        });

    });

    describe('openEditAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModal.openEditAction).toBeDefined();
        });

        it('is called proper functions should be called', function() {
            // when
            personalizationsmarteditContextModal.openEditAction({});
            // then
            expect(modalService.open).toHaveBeenCalled();
            expect(scope.$$watchers).toBe(null);
        });

    });

    describe('modalDeleteActionController', function() {

        it('is instantiated scope is properly initialized', function() {
            controller('modalDeleteActionController', {
                $scope: scope
            });
            expect(scope.modalManager.setButtonHandler).toHaveBeenCalled();
        });

    });

    describe('modalAddActionController', function() {

        it('is instantiated scope is properly initialized', function() {
            scope.editEnabled = false;
            scope.selectedVariationCode = 'variation1';
            controller('modalAddEditActionController', {
                $scope: scope
            });
            controller('modalAddActionController', {
                $scope: scope,
            });

            expect(scope.actions).toBeDefined();
            expect(scope.newComponentTypeSelectedEvent).toBeDefined();
            expect(scope.component).toBeDefined({});
            expect(scope.newComponent).toBeDefined({});
            expect(scope.newComponentTypes).toBeDefined({});
            expect(scope.components).toBeDefined([]);
            expect(scope.modalManager.setButtonHandler).toHaveBeenCalled();
            expect(scope.editEnabled).toBe(false);
            expect(scope.action).toEqual({});
            expect(scope.$$watchers).not.toBe(null);
            expect(scope.$$watchers.length).toBe(3);
            expect(scope.selectedCustomization).not.toBeDefined();
            expect(scope.selectedVariation).not.toBeDefined();
            scope.$apply();
            expect(scope.selectedCustomization).toBe(mockCustomization);
            expect(scope.selectedVariation).toBe(mockVariation1);
        });

        it('has a filtered list of components loaded', function() {
            scope.editEnabled = true;
            controller('modalAddEditActionController', {
                $scope: scope
            });
            controller('modalAddActionController', {
                $scope: scope
            });
            scope.componentSearchInputKeypress({
                which: 155
            }, '');
            scope.$apply();
            expect(scope.newComponentTypes).toEqual(
                restrictedMockComponentTypeList
            );
            expect(scope.components).toEqual(mockComponentList);
        });

    });

    describe('modalEditActionController', function() {

        it('is instantiated scope is properly initialized', function() {
            scope.editEnabled = true;
            scope.selectedVariationCode = 'variation1';
            controller('modalAddEditActionController', {
                $scope: scope
            });
            controller('modalEditActionController', {
                $scope: scope
            });

            expect(scope.actions).toBeDefined();
            expect(scope.newComponentTypeSelectedEvent).toBeDefined();
            expect(scope.component).toBeDefined({});
            expect(scope.newComponent).toBeDefined({});
            expect(scope.newComponentTypes).toBeDefined({});
            expect(scope.components).toBeDefined([]);
            expect(scope.modalManager.setButtonHandler).toHaveBeenCalled();
            expect(scope.editEnabled).toBe(true);
            expect(scope.action).not.toEqual({});
            expect(scope.$$watchers).not.toBe(null);
            expect(scope.$$watchers.length).toBe(3);
            expect(scope.selectedCustomization).not.toBeDefined();
            expect(scope.selectedVariation).not.toBeDefined();
            scope.$apply();
            expect(scope.selectedCustomization).toBe(mockCustomization);
            expect(scope.selectedVariation).toBe(mockVariation1);
        });

    });

});
