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
describe('componentEditingFacadeModule', function() {

    var fixture;
    var componentEditingFacade;
    var mockRestServiceFactory;
    var mockComponentVisibilityAlertService;
    var mockContentSlotComponentsRestService;
    var mockComponentHandlerService;
    var mockComponentService;
    var mockEditorModalService;
    var mockGatewayProxy;
    var mockRenderService;
    var mockRemoveComponentService;
    var mockAlertService;
    var mockSharedDataService;
    var mockSlotVisibilityService;
    var $q;

    var MOCK_COMPONENT_TYPE = "MOCK_COMPONENT_TYPE";
    var MOCK_TARGET_SLOT_UID = "MOCK_TARGET_SLOT_UID";
    var MOCK_TARGET_SLOT_UUID = "MOCK_TARGET_SLOT_UUID";
    var MOCK_EXISTING_COMPONENT_UID = "MOCK_EXISTING_COMPONENT_UID";
    var MOCK_EXISTING_COMPONENT_UUID = "MOCK_EXISTING_COMPONENT_UUID";
    var MOCK_CATALOG_VERSION_UUID = "MOCK_CATALOG_VERSION_UUID";
    var MOCK_PAGE_UID = 'SomePageUID';
    var MOCK_NEW_COMPONENT = {
        uid: 'SomeNewComponentUID'
    };
    var MOCK_ERROR = {
        data: {
            errors: [{
                message: 'Some detailed error message'
            }]
        }
    };

    function _cloneDefaultPayload() {
        return JSON.parse(JSON.stringify({
            itemId: "MOCK_ITEM_UUID",
            itemType: "MOCK_ITEM_TYPE",
            catalogVersion: "MOCK_CATALOG_VERSION",
            slotId: "MOCK_SLOT_ID"
        }));
    }

    function _getMockPayload(scenario) {
        var MOCK_PAYLOAD = _cloneDefaultPayload();
        MOCK_PAYLOAD.visible = (scenario.indexOf("VISIBLE") !== -1);
        MOCK_PAYLOAD.restricted = (scenario.indexOf("WITH_RESTRICTIONS") !== -1);
        return MOCK_PAYLOAD;
    }

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(function() {
        fixture = AngularUnitTestHelper.prepareModule('componentEditingFacadeModule')
            .mock('componentVisibilityAlertService', 'checkAndAlertOnComponentVisibility')
            .mock('ComponentService', 'addNewComponent')
            .mock('ComponentService', 'removeComponent')
            .mock('ComponentService', 'loadComponentItem')
            .mock('ComponentService', 'addExistingComponent')
            .mock('componentHandlerService', 'getPageUID')
            .mock('restServiceFactory', 'get')
            .mock('editorModalService', 'open')
            .mock('removeComponentService', 'removeComponent')
            .mock('renderService', 'renderSlots')
            .mock('alertService', 'showSuccess')
            .mock('alertService', 'showDanger')
            .mock('alertService', 'showInfo')
            .mock('sharedDataService', 'get')
            .mock('slotVisibilityService', 'reloadSlotsInfo')
            .mock('gatewayProxy', 'initForService')
            .mockConstant('PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI', 'PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI')
            .withTranslations({
                'se.cms.draganddrop.error': 'Failed to move {{sourceComponentId}} to slot {{targetSlotId}}: {{detailedError}}',
                'se.cms.draganddrop.move.failed': 'Failed to move {{componentID}} to slot {{slotID}}',
                'se.cms.draganddrop.success': 'The component {{sourceComponentId}} has been successfully added to slot {{targetSlotId}}',
                'se.cms.draganddrop.success.but.hidden': 'The component {{sourceComponentId}} has been successfully added to slot {{targetSlotId}} but is hidden'
            })
            .service('componentEditingFacade');

        componentEditingFacade = fixture.service;
        mockComponentVisibilityAlertService = fixture.mocks.componentVisibilityAlertService;
        mockRestServiceFactory = fixture.mocks.restServiceFactory;
        mockComponentHandlerService = fixture.mocks.componentHandlerService;
        mockComponentService = fixture.mocks.ComponentService;
        mockEditorModalService = fixture.mocks.editorModalService;
        mockGatewayProxy = fixture.mocks.gatewayProxy;
        mockRenderService = fixture.mocks.renderService;
        mockRemoveComponentService = fixture.mocks.removeComponentService;
        mockAlertService = fixture.mocks.alertService;
        mockSharedDataService = fixture.mocks.sharedDataService;
        mockSlotVisibilityService = fixture.mocks.slotVisibilityService;
        $q = fixture.injected.$q;
    });

    beforeEach(function() {
        mockContentSlotComponentsRestService = jasmine.createSpyObj('mockContentSlotComponentsRestService', ['update']);
        mockRestServiceFactory.get.and.returnValue(mockContentSlotComponentsRestService);
    });

    describe('addNewComponentToSlot', function() {
        it('should open the component editor in a modal', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockEditorModalService.open.and.returnValue($q.when());
            var slotInfo = {
                targetSlotId: MOCK_TARGET_SLOT_UID,
                targetSlotUUId: MOCK_TARGET_SLOT_UUID
            };
            var expectedData = {
                smarteditComponentType: MOCK_COMPONENT_TYPE,
                catalogVersionUuid: MOCK_CATALOG_VERSION_UUID,
            };

            // Act
            componentEditingFacade.addNewComponentToSlot(slotInfo, MOCK_CATALOG_VERSION_UUID, MOCK_COMPONENT_TYPE, 1);
            fixture.detectChanges();

            // Assert
            expect(mockEditorModalService.open).toHaveBeenCalledWith(expectedData, MOCK_TARGET_SLOT_UUID, 1);
        });

    });

    describe('addExistingComponentToSlot', function() {

        var dragInfo = {
            componentId: MOCK_EXISTING_COMPONENT_UID,
            componentType: MOCK_COMPONENT_TYPE,
            componentUuid: MOCK_EXISTING_COMPONENT_UUID
        };

        it('should get the current page UID by delegating to the componentHandlerService', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockComponentService.addExistingComponent.and.returnValue($q.when());
            mockComponentService.loadComponentItem.and.returnValue($q.when(_getMockPayload("VISIBLE")));
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot(MOCK_TARGET_SLOT_UID, dragInfo, 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID).toHaveBeenCalled();
        });

        it('should delegate to componentService to add the existing component to the slot and show a success message if the component is visible', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockComponentService.addExistingComponent.and.returnValue($q.when());
            mockComponentService.loadComponentItem.and.returnValue($q.when(_getMockPayload("MOCK_ITEM_VISIBLE_NO_RESTRICTIONS")));
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot(MOCK_TARGET_SLOT_UID, dragInfo, 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentService.addExistingComponent).toHaveBeenCalledWith(MOCK_PAGE_UID, MOCK_EXISTING_COMPONENT_UID, MOCK_TARGET_SLOT_UID, 1);
            expect(mockAlertService.showSuccess).toHaveBeenCalledWith({
                message: "se.cms.draganddrop.success",
                messagePlaceholders: {
                    sourceComponentId: "MOCK_EXISTING_COMPONENT_UID",
                    targetSlotId: "MOCK_TARGET_SLOT_UID"
                }
            });

        });

        it('should delegate to componentService to add the existing component to the slot, show both a success and an info message if the component is hidden and restricted', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockComponentService.addExistingComponent.and.returnValue($q.when());
            mockComponentService.loadComponentItem.and.returnValue($q.when(_getMockPayload("MOCK_ITEM_HIDDEN_WITH_RESTRICTIONS")));
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot(MOCK_TARGET_SLOT_UID, dragInfo, 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID).toHaveBeenCalled();
            expect(mockComponentService.addExistingComponent).toHaveBeenCalledWith('SomePageUID', MOCK_EXISTING_COMPONENT_UID, MOCK_TARGET_SLOT_UID, 1);
            expect(mockComponentService.loadComponentItem).toHaveBeenCalledWith('MOCK_EXISTING_COMPONENT_UUID');
            expect(mockComponentVisibilityAlertService.checkAndAlertOnComponentVisibility).toHaveBeenCalledWith({
                itemId: "MOCK_EXISTING_COMPONENT_UUID",
                itemType: "MOCK_COMPONENT_TYPE",
                catalogVersion: "MOCK_CATALOG_VERSION",
                restricted: true,
                slotId: "MOCK_TARGET_SLOT_UID",
                visible: false
            });
            expect(mockAlertService.showSuccess).toHaveBeenCalledWith({
                message: "se.cms.draganddrop.success",
                messagePlaceholders: {
                    sourceComponentId: "MOCK_EXISTING_COMPONENT_UID",
                    targetSlotId: "MOCK_TARGET_SLOT_UID"
                }
            });
            expect(mockRenderService.renderSlots).toHaveBeenCalledWith('MOCK_TARGET_SLOT_UID');
            expect(mockSlotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();

        });

        it('should delegate to componentService to add the existing component to the slot, show both a success and an info message if the component is restricted', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockComponentService.addExistingComponent.and.returnValue($q.when());
            mockComponentService.loadComponentItem.and.returnValue($q.when(_getMockPayload("MOCK_ITEM_VISIBLE_WITH_RESTRICTIONS")));
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot(MOCK_TARGET_SLOT_UID, dragInfo, 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID).toHaveBeenCalled();
            expect(mockComponentService.addExistingComponent).toHaveBeenCalledWith('SomePageUID', MOCK_EXISTING_COMPONENT_UID, MOCK_TARGET_SLOT_UID, 1);
            expect(mockComponentService.loadComponentItem).toHaveBeenCalledWith('MOCK_EXISTING_COMPONENT_UUID');
            expect(mockComponentVisibilityAlertService.checkAndAlertOnComponentVisibility).toHaveBeenCalledWith({
                itemId: "MOCK_EXISTING_COMPONENT_UUID",
                itemType: "MOCK_COMPONENT_TYPE",
                catalogVersion: "MOCK_CATALOG_VERSION",
                restricted: true,
                slotId: "MOCK_TARGET_SLOT_UID",
                visible: true
            });
            expect(mockAlertService.showSuccess).toHaveBeenCalledWith({
                message: "se.cms.draganddrop.success",
                messagePlaceholders: {
                    sourceComponentId: "MOCK_EXISTING_COMPONENT_UID",
                    targetSlotId: "MOCK_TARGET_SLOT_UID"
                }
            });
            expect(mockRenderService.renderSlots).toHaveBeenCalledWith('MOCK_TARGET_SLOT_UID');
            expect(mockSlotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should delegate to componentService to add the existing component to the slot, show both a success and an info message if the component is hidden', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockComponentService.addExistingComponent.and.returnValue($q.when());
            mockComponentService.loadComponentItem.and.returnValue($q.when(_getMockPayload("MOCK_ITEM_HIDDEN_NO_RESTRICTIONS")));
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot(MOCK_TARGET_SLOT_UID, dragInfo, 1);
            fixture.detectChanges();

            // Assert
            expect(mockComponentHandlerService.getPageUID).toHaveBeenCalled();
            expect(mockComponentService.addExistingComponent).toHaveBeenCalledWith('SomePageUID', MOCK_EXISTING_COMPONENT_UID, MOCK_TARGET_SLOT_UID, 1);
            expect(mockComponentService.loadComponentItem).toHaveBeenCalledWith('MOCK_EXISTING_COMPONENT_UUID');
            expect(mockComponentVisibilityAlertService.checkAndAlertOnComponentVisibility).toHaveBeenCalledWith({
                itemId: "MOCK_EXISTING_COMPONENT_UUID",
                itemType: "MOCK_COMPONENT_TYPE",
                catalogVersion: "MOCK_CATALOG_VERSION",
                restricted: false,
                slotId: "MOCK_TARGET_SLOT_UID",
                visible: false
            });
            expect(mockAlertService.showSuccess).toHaveBeenCalledWith({
                message: "se.cms.draganddrop.success",
                messagePlaceholders: {
                    sourceComponentId: "MOCK_EXISTING_COMPONENT_UID",
                    targetSlotId: "MOCK_TARGET_SLOT_UID"
                }
            });
            expect(mockRenderService.renderSlots).toHaveBeenCalledWith('MOCK_TARGET_SLOT_UID');
            expect(mockSlotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should delegate to the renderService to re-render the slot the componentService has successfully added the existing component to the slot', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockComponentService.addExistingComponent.and.returnValue($q.when());
            mockComponentService.loadComponentItem.and.returnValue($q.when("MOCK_ITEM_VISIBLE"));
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());

            // Act
            componentEditingFacade.addExistingComponentToSlot(MOCK_TARGET_SLOT_UID, dragInfo, 1);
            fixture.detectChanges();

            // Assert
            expect(mockRenderService.renderSlots).toHaveBeenCalledWith(MOCK_TARGET_SLOT_UID);
            expect(mockSlotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should push an alert if adding the existing component to the slot fails', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockComponentService.addExistingComponent.and.returnValue($q.reject(MOCK_ERROR));

            // Act
            componentEditingFacade.addExistingComponentToSlot(MOCK_TARGET_SLOT_UID, dragInfo, 1);
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.showDanger).toHaveBeenCalledWith({
                message: 'se.cms.draganddrop.error',
                messagePlaceholders: {
                    sourceComponentId: 'MOCK_EXISTING_COMPONENT_UID',
                    targetSlotId: 'MOCK_TARGET_SLOT_UID',
                    detailedError: 'Some detailed error message'
                }
            });
        });
    });

    describe('cloneExistingComponentToSlot', function() {

        var dragInfo = {
            componentId: MOCK_EXISTING_COMPONENT_UID,
            componentType: MOCK_COMPONENT_TYPE,
            componentUuid: MOCK_EXISTING_COMPONENT_UUID
        };

        it('should load the source component and should open the component editor in a modal', function() {
            // Arrange
            mockComponentService.loadComponentItem.and.returnValue($q.when({
                'key1': 'value1',
                'key2': 'value2'
            }));
            mockEditorModalService.open.and.returnValue($q.when());
            mockSharedDataService.get.and.returnValue($q.when({
                pageContext: {
                    catalogVersionUuid: 'someCatalogVersion'
                }
            }));

            var componentProperties = {
                targetSlotId: MOCK_TARGET_SLOT_UID,
                dragInfo: dragInfo,
                position: 1
            };

            // Act
            componentEditingFacade.cloneExistingComponentToSlot(componentProperties);
            fixture.detectChanges();

            // Assert
            expect(mockSharedDataService.get).toHaveBeenCalled();
            expect(mockComponentService.loadComponentItem).toHaveBeenCalledWith(MOCK_EXISTING_COMPONENT_UUID);
            expect(mockEditorModalService.open).toHaveBeenCalledWith({
                smarteditComponentType: MOCK_COMPONENT_TYPE,
                catalogVersionUuid: 'someCatalogVersion',
                content: {
                    key1: 'value1',
                    key2: 'value2',
                    cloneComponent: true,
                    catalogVersion: 'someCatalogVersion',
                    name: 'se.cms.component.name.clone.of.prefix undefined'
                },
                initialDirty: true
            }, MOCK_TARGET_SLOT_UID, 1);

        });

        it('should delegate to the renderService to re-render the slot the componentService has successfully added the cloned component to the slot', function() {
            // Arrange
            mockComponentService.loadComponentItem.and.returnValue($q.when({
                catalogVersion: 'someCatalogVersion'
            }));
            mockEditorModalService.open.and.returnValue($q.when({
                uuid: 'someuuid',
                uid: 'someuid',
                itemtype: 'someitemType',
                catalogVersion: 'somecatalogVersion',
                restricted: false,
                visible: false
            }));
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());
            mockSharedDataService.get.and.returnValue($q.when({
                pageContext: {
                    catalogVersionUuid: 'someCatalogVersion'
                }
            }));

            var componentProperties = {
                targetSlotId: MOCK_TARGET_SLOT_UID,
                dragInfo: dragInfo,
                position: 1
            };

            // Act
            componentEditingFacade.cloneExistingComponentToSlot(componentProperties);
            fixture.detectChanges();

            // Assert
            expect(mockComponentVisibilityAlertService.checkAndAlertOnComponentVisibility).toHaveBeenCalledWith({
                itemId: 'someuuid',
                itemType: 'someitemType',
                catalogVersion: 'somecatalogVersion',
                restricted: false,
                slotId: MOCK_TARGET_SLOT_UID,
                visible: false
            });
            expect(mockAlertService.showSuccess).toHaveBeenCalledWith({
                message: "se.cms.draganddrop.success",
                messagePlaceholders: {
                    sourceComponentId: 'someuid',
                    targetSlotId: MOCK_TARGET_SLOT_UID
                }
            });
            expect(mockRenderService.renderSlots).toHaveBeenCalledWith('MOCK_TARGET_SLOT_UID');
            expect(mockSlotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

    });

    describe('moveComponent', function() {
        it('should delegate to the slot update REST service to update the slot', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockContentSlotComponentsRestService.update.and.returnValue($q.when());
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());

            // Act
            componentEditingFacade.moveComponent('SomeSourceSlotUID', MOCK_TARGET_SLOT_UID, MOCK_EXISTING_COMPONENT_UID, 1);
            fixture.detectChanges();

            // Assert
            expect(mockContentSlotComponentsRestService.update).toHaveBeenCalledWith({
                pageId: MOCK_PAGE_UID,
                currentSlotId: 'SomeSourceSlotUID',
                componentId: MOCK_EXISTING_COMPONENT_UID,
                slotId: MOCK_TARGET_SLOT_UID,
                position: 1
            });
        });

        it('should delegate to the renderService to re-render both changed slots', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockContentSlotComponentsRestService.update.and.returnValue($q.when());
            mockRenderService.renderSlots.and.returnValue($q.when());
            mockSlotVisibilityService.reloadSlotsInfo.and.returnValue($q.when());

            // Act
            componentEditingFacade.moveComponent('SomeSourceSlotUID', MOCK_TARGET_SLOT_UID, MOCK_EXISTING_COMPONENT_UID, 1);
            fixture.detectChanges();

            // Assert
            expect(mockRenderService.renderSlots).toHaveBeenCalledWith(['SomeSourceSlotUID', MOCK_TARGET_SLOT_UID]);
            expect(mockSlotVisibilityService.reloadSlotsInfo).toHaveBeenCalled();
        });

        it('should push an alert if updating the slot via the slot update REST service fails', function() {
            // Arrange
            mockComponentHandlerService.getPageUID.and.returnValue($q.when(MOCK_PAGE_UID));
            mockContentSlotComponentsRestService.update.and.returnValue($q.reject());

            // Act
            componentEditingFacade.moveComponent('SomeSourceSlotUID', MOCK_TARGET_SLOT_UID, MOCK_EXISTING_COMPONENT_UID, 1);
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.showDanger).toHaveBeenCalledWith({
                message: "se.cms.draganddrop.move.failed",
                messagePlaceholders: {
                    slotID: "MOCK_TARGET_SLOT_UID",
                    componentID: "MOCK_EXISTING_COMPONENT_UID"
                }
            });

        });
    });

});
