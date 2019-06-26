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
describe('personalizationsmarteditContextModalHelper', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var personalizationsmarteditContextModalHelper, personalizationsmarteditContextModal;

    var mockConfigProperties = {
        name: "name",
        smarteditPersonalizationActionId: "actionId",
        smarteditPersonalizationVariationId: "variationCode",
        smarteditPersonalizationCustomizationId: "customizationCode",
        smarteditComponentUuid: "componentUuid",
        smarteditContainerSourceId: "containerSourceId",
        smarteditCatalogVersionUuid: "catalogName/Online",
        smarteditComponentType: "type"
    };
    var mockConfig = {
        properties: JSON.stringify(mockConfigProperties),
        componentId: "id",
        componentType: "type",
        containerId: "containerId",
        slotId: "slotId"
    };
    var mockCustomization1 = {
        code: "mockNameCustomization1",
        catalog: "catalogName",
        catalogVersion: "Online"
    };
    var mockCustomization2 = {
        code: "mockNameCustomization2",
        catalog: "catalogName2",
        catalogVersion: "Online"
    };
    var mockCustomization3 = {
        code: "customizationCode",
        catalog: "catalogName3",
        catalogVersion: "Online"
    };

    var mockCustomize = {
        selectedCustomization: mockCustomization1,
        selectedVariations: {
            code: "mockNameVariation"
        },
        selectedComponents: null
    };

    beforeEach(module('personalizationsmarteditContextMenu', function($provide) {
        mockModules.personalizationsmarteditContextService = jasmine.createSpyObj('personalizationsmarteditContextService', ['getCustomize', 'getCombinedView']);
        $provide.value('personalizationsmarteditContextService', mockModules.personalizationsmarteditContextService);
        mockModules.personalizationsmarteditComponentHandlerService = jasmine.createSpyObj('personalizationsmarteditComponentHandlerService', ['getParentSlotForComponent', 'getCatalogVersionUuid', 'getAllSlotsSelector', 'getFromSelector']);
        $provide.value('personalizationsmarteditComponentHandlerService', mockModules.personalizationsmarteditComponentHandlerService);
    }));
    beforeEach(inject(function(_personalizationsmarteditContextModalHelper_, _personalizationsmarteditContextModal_) {
        personalizationsmarteditContextModalHelper = _personalizationsmarteditContextModalHelper_;
        personalizationsmarteditContextModal = _personalizationsmarteditContextModal_;
        spyOn(personalizationsmarteditContextModal, 'openDeleteAction').and.callThrough();
        spyOn(personalizationsmarteditContextModal, 'openAddAction').and.callThrough();
        spyOn(personalizationsmarteditContextModal, 'openEditAction').and.callThrough();
        spyOn(personalizationsmarteditContextModal, 'openEditComponentAction').and.callThrough();
        mockModules.personalizationsmarteditContextService.getCustomize.and.callFake(function() {
            return mockCustomize;
        });
        mockModules.personalizationsmarteditContextService.getCombinedView.and.callFake(function() {
            return {
                enabled: false
            };
        });
        mockModules.personalizationsmarteditComponentHandlerService.getFromSelector.and.callFake(function() {
            return [];
        });
    }));

    describe('openDeleteAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModalHelper.openDeleteAction).toBeDefined();
        });

        it('should call proper service with parameters if customization in context', function() {
            personalizationsmarteditContextModalHelper.openDeleteAction(mockConfig);
            expect(personalizationsmarteditContextModal.openDeleteAction).toHaveBeenCalledWith({
                containerId: 'containerId',
                containerSourceId: 'containerSourceId',
                slotId: 'slotId',
                actionId: "actionId",
                selectedVariationCode: "variationCode",
                selectedCustomizationCode: "customizationCode",
                catalog: "catalogName",
                catalogVersion: "Online",
                componentCatalog: "catalogName",
                componentCatalogVersion: "Online",
                slotsToRefresh: []
            });
        });

        it('should call proper service with parameters if combined view enabled and customization in context', function() {
            mockModules.personalizationsmarteditContextService.getCombinedView.and.callFake(function() {
                return {
                    enabled: true,
                    customize: {
                        selectedCustomization: mockCustomization2
                    }
                };
            });
            personalizationsmarteditContextModalHelper.openDeleteAction(mockConfig);
            expect(personalizationsmarteditContextModal.openDeleteAction).toHaveBeenCalledWith({
                containerId: 'containerId',
                containerSourceId: 'containerSourceId',
                slotId: 'slotId',
                actionId: "actionId",
                selectedVariationCode: "variationCode",
                selectedCustomizationCode: "customizationCode",
                catalog: "catalogName2",
                catalogVersion: "Online",
                componentCatalog: "catalogName",
                componentCatalogVersion: "Online",
                slotsToRefresh: []
            });
        });

        it('should call proper service with parameters if combined view enabled and customization not in context', function() {
            mockModules.personalizationsmarteditContextService.getCombinedView.and.callFake(function() {
                return {
                    enabled: true,
                    customize: {
                        selectedCustomization: null
                    },
                    selectedItems: [{
                            customization: mockCustomization2
                        },
                        {
                            customization: mockCustomization3
                        },
                    ]
                };
            });
            personalizationsmarteditContextModalHelper.openDeleteAction(mockConfig);
            expect(personalizationsmarteditContextModal.openDeleteAction).toHaveBeenCalledWith({
                containerId: 'containerId',
                containerSourceId: 'containerSourceId',
                slotId: 'slotId',
                actionId: "actionId",
                selectedVariationCode: "variationCode",
                selectedCustomizationCode: "customizationCode",
                catalog: "catalogName3",
                catalogVersion: "Online",
                componentCatalog: "catalogName",
                componentCatalogVersion: "Online",
                slotsToRefresh: []
            });
        });

    });

    describe('openAddAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModalHelper.openAddAction).toBeDefined();
        });

        it('should call proper service with parameters', function() {

            mockModules.personalizationsmarteditContextService.selectedCustomizations = {
                code: "mockNameCustomization",
            };
            mockModules.personalizationsmarteditContextService.selectedVariations = {
                code: "mockNameVariation",
            };
            mockModules.personalizationsmarteditComponentHandlerService.getParentSlotForComponent.and.callFake(function() {
                return {};
            });
            mockModules.personalizationsmarteditComponentHandlerService.getCatalogVersionUuid.and.callFake(function() {
                return "slotCatalogName";
            });

            personalizationsmarteditContextModalHelper.openAddAction(mockConfig);
            expect(personalizationsmarteditContextModal.openAddAction).toHaveBeenCalledWith({
                componentType: "type",
                componentId: "id",
                containerId: 'containerId',
                containerSourceId: 'containerSourceId',
                slotId: 'slotId',
                actionId: "actionId",
                selectedVariationCode: "mockNameVariation",
                selectedCustomizationCode: "mockNameCustomization1",
                catalog: "catalogName",
                slotCatalog: "slotCatalogName",
                componentCatalog: "catalogName",
                slotsToRefresh: ['slotId']
            });
        });

    });

    describe('openEditAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModalHelper.openEditAction).toBeDefined();
        });

        it('should call proper service', function() {
            personalizationsmarteditContextModalHelper.openEditAction(mockConfig);
            expect(personalizationsmarteditContextModal.openEditAction).toHaveBeenCalledWith({
                componentType: "type",
                componentId: "id",
                containerId: 'containerId',
                containerSourceId: 'containerSourceId',
                slotId: 'slotId',
                actionId: "actionId",
                selectedVariationCode: "variationCode",
                selectedCustomizationCode: "customizationCode",
                componentUuid: "componentUuid",
                slotsToRefresh: []
            });
        });

    });

    describe('openEditComponentAction', function() {

        it('should be defined', function() {
            expect(personalizationsmarteditContextModalHelper.openEditComponentAction).toBeDefined();
        });

        it('should call proper service', function() {
            personalizationsmarteditContextModalHelper.openEditComponentAction(mockConfig);
            expect(personalizationsmarteditContextModal.openEditComponentAction).toHaveBeenCalledWith({
                smarteditComponentType: "type",
                smarteditComponentUuid: "componentUuid",
                smarteditCatalogVersionUuid: "catalogName/Online"
            });
        });

    });

});
