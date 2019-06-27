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
describe('customizationsListModule', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var mockVariation = {
        code: "testVariation"
    };
    var mockCustomization1 = {
        code: "testCustomization1",
        variations: [mockVariation]
    };
    var mockCustomization2 = {
        code: "testCustomization2",
        variations: [mockVariation]
    };
    var mockCustomizationCollapsed = {
        code: "testCustomizationCollapsed",
        variations: [mockVariation],
        collapsed: true
    };

    var mockComponentList = ['component1', 'component2'];
    var mockVariationList = [mockVariation];

    var $componentController, personalizationsmarteditContextService, scope;

    beforeEach(module('personalizationsmarteditServicesModule', function($provide) {
        mockModules.personalizationsmarteditRestService = jasmine.createSpyObj('personalizationsmarteditRestService', ['getCustomizations', 'getComponenentsIdsForVariation', 'getVariationsForCustomization', 'getCxCmsActionsOnPageForCustomization']);
        $provide.value('personalizationsmarteditRestService', mockModules.personalizationsmarteditRestService);
    }));

    beforeEach(module('personalizationsmarteditPreviewServiceModule', function($provide) {
        mockModules.personalizationsmarteditPreviewService = jasmine.createSpyObj('personalizationsmarteditPreviewService', ['updatePreviewTicketWithVariations']);
        $provide.value('personalizationsmarteditPreviewService', mockModules.personalizationsmarteditPreviewService);
    }));

    beforeEach(module('personalizationsmarteditManageCustomizationViewModule', function($provide) {
        mockModules.personalizationsmarteditManager = jasmine.createSpyObj('personalizationsmarteditManager', ['openCreateCustomizationModal']);
        $provide.value('personalizationsmarteditManager', mockModules.personalizationsmarteditManager);
    }));

    beforeEach(module('personalizationsmarteditDataFactory', function($provide) {
        mockModules.customizationDataFactory = jasmine.createSpyObj('customizationDataFactory', ['updateData', 'resetData', 'items']);
        $provide.value('customizationDataFactory', mockModules.customizationDataFactory);
    }));

    beforeEach(module('personalizationsmarteditCustomizeViewServiceModule', function($provide) {
        mockModules.personalizationsmarteditCustomizeViewProxy = jasmine.createSpyObj('personalizationsmarteditCustomizeViewProxy', ['getSourceContainersInfo']);
        $provide.value('personalizationsmarteditCustomizeViewProxy', mockModules.personalizationsmarteditCustomizeViewProxy);
    }));

    beforeEach(module('customizationsListModule'));
    beforeEach(inject(function(_$rootScope_, _$q_, _$componentController_, _personalizationsmarteditContextService_) {
        scope = _$rootScope_.$new();
        $componentController = _$componentController_;

        mockModules.personalizationsmarteditRestService.getComponenentsIdsForVariation.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({
                components: mockComponentList
            });
            return deferred.promise;
        });

        mockModules.personalizationsmarteditRestService.getVariationsForCustomization.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({
                variations: mockVariationList
            });
            return deferred.promise;
        });

        mockModules.personalizationsmarteditRestService.getCxCmsActionsOnPageForCustomization.and.callFake(function() {
            return _$q_.defer().promise;
        });

        mockModules.personalizationsmarteditPreviewService.updatePreviewTicketWithVariations.and.callFake(function() {
            return _$q_.defer().promise;
        });

        mockModules.personalizationsmarteditCustomizeViewProxy.getSourceContainersInfo.and.callFake(function() {
            return _$q_.defer().promise;
        });

        personalizationsmarteditContextService = _personalizationsmarteditContextService_;

    }));

    describe('Component API', function() {

        it('should have proper api when initialized without parameters', function() {
            var ctrl = $componentController('customizationsList', null);

            expect(ctrl.initCustomization).toBeDefined();
            expect(ctrl.editCustomizationAction).toBeDefined();
            expect(ctrl.customizationClick).toBeDefined();
            expect(ctrl.customizationRowClick).toBeDefined();
            expect(ctrl.getSelectedVariationClass).toBeDefined();
            expect(ctrl.variationClick).toBeDefined();
            expect(ctrl.hasCommerceActions).toBeDefined();
            expect(ctrl.clearAllSubMenu).toBeDefined();
            expect(ctrl.getActivityStateForCustomization).toBeDefined();
            expect(ctrl.getActivityStateForVariation).toBeDefined();
            expect(ctrl.getEnablementTextForCustomization).toBeDefined();
            expect(ctrl.getEnablementTextForVariation).toBeDefined();
            expect(ctrl.isEnabled).toBeDefined();
            expect(ctrl.getDatesForCustomization).toBeDefined();
            expect(ctrl.customizationSubMenuAction).toBeDefined();
        });

        it('should have proper api when initialized with parameters', function() {
            var bindings = {
                customizationsList: [mockCustomization1, mockCustomization2]
            };
            var ctrl = $componentController('customizationsList', null, bindings);

            expect(ctrl.initCustomization).toBeDefined();
            expect(ctrl.editCustomizationAction).toBeDefined();
            expect(ctrl.customizationRowClick).toBeDefined();
            expect(ctrl.customizationClick).toBeDefined();
            expect(ctrl.getSelectedVariationClass).toBeDefined();
            expect(ctrl.variationClick).toBeDefined();
            expect(ctrl.hasCommerceActions).toBeDefined();
            expect(ctrl.clearAllSubMenu).toBeDefined();
            expect(ctrl.getActivityStateForCustomization).toBeDefined();
            expect(ctrl.getActivityStateForVariation).toBeDefined();
            expect(ctrl.getEnablementTextForCustomization).toBeDefined();
            expect(ctrl.getEnablementTextForVariation).toBeDefined();
            expect(ctrl.isEnabled).toBeDefined();
            expect(ctrl.getDatesForCustomization).toBeDefined();
            expect(ctrl.customizationSubMenuAction).toBeDefined();

            expect(ctrl.customizationsList.length).toBe(2);
        });
    });

    describe('customizationClick', function() {
        it('after called all objects in contex service are set properly', function() {
            // given
            var bindings = {
                customizationsList: [mockCustomization1, mockCustomization2]
            };
            var ctrl = $componentController('customizationsList', null, bindings);
            expect(personalizationsmarteditContextService.getCustomize().selectedCustomization).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(null);
            // when
            ctrl.customizationClick(mockCustomization1);
            scope.$digest();
            // then
            expect(personalizationsmarteditContextService.getCustomize().selectedCustomization).toBe(mockCustomization1);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations[0].code).toBe(mockCustomization1.variations[0].code);
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(mockComponentList);
        });
    });

    describe('variationClick', function() {
        it('after called all objects in contex service are set properly', function() {
            // given
            var bindings = {
                getCustomizations: function() {
                    return [mockCustomization1, mockCustomization2];
                }
            };
            var ctrl = $componentController('customizationsList', null, bindings);

            expect(personalizationsmarteditContextService.getCustomize().selectedCustomization).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(null);
            // when
            ctrl.variationClick(mockCustomization1, mockVariation);
            scope.$digest();
            // then
            expect(personalizationsmarteditContextService.getCustomize().selectedCustomization).toBe(mockCustomization1);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(mockVariation);
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(mockComponentList);
        });
    });

    describe('customizationRowClick', function() {
        it('after called all objects in contex service are set properly', function() {
            // given
            var bindings = {
                customizationsList: [mockCustomizationCollapsed]
            };
            var ctrl = $componentController('customizationsList', null, bindings);
            expect(personalizationsmarteditContextService.getCustomize().selectedCustomization).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations).toBe(null);
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(null);
            // when
            ctrl.customizationRowClick(mockCustomizationCollapsed, true);
            scope.$digest();
            // then
            expect(personalizationsmarteditContextService.getCustomize().selectedCustomization).toBe(mockCustomizationCollapsed);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations[0].code).toBe(mockCustomizationCollapsed.variations[0].code);
            expect(personalizationsmarteditContextService.getCustomize().selectedVariations[0].numberOfAffectedComponents).toBe(undefined);
            expect(personalizationsmarteditContextService.getCustomize().selectedComponents).toBe(mockComponentList);
        });
    });
});
