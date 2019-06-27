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
describe('componentVisibilityAlert', function() {

    var fixture;
    var componentVisibilityAlertService;
    var lodash;
    var mockAlertService;
    var mockActionableAlertService;
    var mockComponentVisibilityAlertServiceInterface;
    var mockEditorModalService;
    var mockSharedDataService;
    var mockGatewayProxy;
    var $q;
    var $rootScope;

    var THROWN_ERROR_MESSAGE = "componentVisibilityAlertService.checkAndAlertOnComponentVisibility - missing properly typed parameters";

    function _getMockPayload(scenario) {
        return {
            itemId: "MOCKED_ITEM_ID",
            itemType: "MOCKED_ITEM_TYPE",
            catalogVersion: "MOCKED_CATALOG_VERSION",
            slotId: "MOCKED_SLOT_ID",
            restricted: (scenario.indexOf("WITH_RESTRICTIONS") !== -1),
            visible: (scenario.indexOf("VISIBLE") !== -1)
        };
    }

    beforeEach(angular.mock.module("componentVisibilityAlertServiceInterfaceModule"));

    beforeEach(function() {
        fixture = AngularUnitTestHelper.prepareModule('componentVisibilityAlertServiceModule')
            .mock('alertService', 'showAlert')
            .mock('actionableAlertService', 'displayActionableAlert')
            .mock('editorModalService', 'openAndRerenderSlot')
            .mock('sharedDataService', 'get')
            .mock('gatewayProxy', 'initForService')
            .service('componentVisibilityAlertService');
        componentVisibilityAlertService = fixture.service;
        mockAlertService = fixture.mocks.alertService;
        mockActionableAlertService = fixture.mocks.actionableAlertService;
        mockEditorModalService = fixture.mocks.editorModalService;
        mockSharedDataService = fixture.mocks.sharedDataService;
        mockGatewayProxy = fixture.mocks.gatewayProxy;

        $q = fixture.injected.$q;
    });

    beforeEach(inject(function(_lodash_, _$rootScope_) {
        lodash = _lodash_;
        $rootScope = _$rootScope_;
    }));

    beforeEach(inject(function(_ComponentVisibilityAlertServiceInterface_) {
        mockComponentVisibilityAlertServiceInterface = _ComponentVisibilityAlertServiceInterface_;
    }));

    describe('checkAndAlertOnComponentVisibility - component not editable', function() {

        beforeEach(function() {
            mockSharedDataService.get.and.returnResolvedPromise({
                pageContext: {
                    catalogVersionUuid: "SOME_OTHER_CATALOG_VERSION"
                }
            });
        });

        it("should display a 'will not be displayed' alert for any hidden component", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(_getMockPayload("HIDDEN_NO_RESTRICTIONS"));
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.showAlert).toHaveBeenCalledWith({
                message: "se.cms.component.visibility.alert.description.hidden"
            });
        });

        it("should display a 'will not be displayed' alert for any hidden and restricted component", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(_getMockPayload("HIDDEN_WITH_RESTRICTIONS"));
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.showAlert).toHaveBeenCalledWith({
                message: "se.cms.component.visibility.alert.description.hidden"
            });
        });

        it("should display a 'might not be displayed' alert for any restricted component", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(_getMockPayload("VISIBLE_WITH_RESTRICTIONS"));
            fixture.detectChanges();

            // Assert
            expect(mockAlertService.showAlert).toHaveBeenCalledWith({
                message: "se.cms.component.visibility.alert.description.restricted"
            });
        });
    });

    describe('checkAndAlertOnComponentVisibility - component editable', function() {

        beforeEach(function() {
            mockSharedDataService.get.and.returnResolvedPromise({
                pageContext: {
                    catalogVersionUuid: "MOCKED_CATALOG_VERSION"
                }
            });
        });

        it("should display a 'will not be displayed' alert for any hidden component", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(_getMockPayload("HIDDEN_NO_RESTRICTIONS"));
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(componentVisibilityAlertService, mockEditorModalService);

            // Assert
            expect(mockActionableAlertService.displayActionableAlert).toHaveBeenCalledWith({
                controller: ['componentVisibilityAlertService', 'editorModalService', jasmine.any(Function)],
                timeoutDuration: 6000
            });
            // Assert
            expect(controllerInstance.description).toBe("se.cms.component.visibility.alert.description.hidden");
            expect(controllerInstance.hyperlinkLabel).toBe("se.cms.component.visibility.alert.hyperlink");
            expect(controllerInstance.onClick).not.toBeNull();

        });

        it("should display a 'will not be displayed' alert for any hidden and restricted component", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(_getMockPayload("HIDDEN_WITH_RESTRICTIONS"));
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(componentVisibilityAlertService, mockEditorModalService);

            // Assert
            expect(mockActionableAlertService.displayActionableAlert).toHaveBeenCalledWith({
                controller: ['componentVisibilityAlertService', 'editorModalService', jasmine.any(Function)],
                timeoutDuration: 6000
            });
            expect(controllerInstance.description).toBe("se.cms.component.visibility.alert.description.hidden");

        });

        it("should display a 'might not be displayed' alert for any restricted component", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(_getMockPayload("VISIBLE_WITH_RESTRICTIONS"));
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(componentVisibilityAlertService, mockEditorModalService);

            // Assert
            expect(mockActionableAlertService.displayActionableAlert).toHaveBeenCalledWith({
                controller: ['componentVisibilityAlertService', 'editorModalService', jasmine.any(Function)],
                timeoutDuration: 6000
            });
            expect(controllerInstance.description).toBe("se.cms.component.visibility.alert.description.restricted");

        });

        it("should throws an exception if provided itemId is not of string type", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(lodash.omit(_getMockPayload("VISIBLE_WITH_RESTRICTIONS"), "itemId"));
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(componentVisibilityAlertService, mockEditorModalService);

            // Assert
            expect(function() {
                controllerInstance.onClick();
            }).toThrow(THROWN_ERROR_MESSAGE);

        });

        it("should throws an exception if provided itemType is not of string type", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(lodash.omit(_getMockPayload("VISIBLE_WITH_RESTRICTIONS"), "itemType"));
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(componentVisibilityAlertService, mockEditorModalService);

            // Assert
            expect(function() {
                controllerInstance.onClick();
            }).toThrow(THROWN_ERROR_MESSAGE);

        });

        it("should throws an exception if provided slotId is not of string type", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(lodash.omit(_getMockPayload("VISIBLE_WITH_RESTRICTIONS"), "slotId"));
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(componentVisibilityAlertService, mockEditorModalService);

            // Assert
            expect(function() {
                controllerInstance.onClick();
            }).toThrow(THROWN_ERROR_MESSAGE);

        });

        it("should trigger 'editorModalService.openAndRerenderSlot' when all expected parameters are properly provided", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(_getMockPayload("HIDDEN_WITH_RESTRICTIONS"));
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(componentVisibilityAlertService, mockEditorModalService);

            mockEditorModalService.openAndRerenderSlot.and.returnValue($q.when({
                visible: false,
                restricted: true
            }));

            controllerInstance.onClick();

            // Assert
            expect(mockEditorModalService.openAndRerenderSlot).toHaveBeenCalledWith(
                "MOCKED_ITEM_TYPE",
                "MOCKED_ITEM_ID",
                "visibilityTab"
            );

        });

        it("should trigger 'editorModalService.openAndRerenderSlot' when all expected parameters are properly provided", function() {

            // Act
            componentVisibilityAlertService.checkAndAlertOnComponentVisibility(_getMockPayload("HIDDEN_WITH_RESTRICTIONS"));
            fixture.detectChanges();

            var controller = mockActionableAlertService.displayActionableAlert.calls.argsFor(0)[0].controller[2];
            var controllerInstance = new controller(componentVisibilityAlertService, mockEditorModalService);

            mockEditorModalService.openAndRerenderSlot.and.returnValue($q.when({
                uuid: "MOCKED_ITEM_ID",
                itemtype: "MOCKED_ITEM_TYPE",
                catalogVersion: "MOCKED_CATALOG_VERSION",
                slotId: "MOCKED_SLOT_ID",
                visible: false,
                restricted: true
            }));

            spyOn(componentVisibilityAlertService, "checkAndAlertOnComponentVisibility");
            controllerInstance.onClick();
            $rootScope.$digest();

            // Assert
            expect(mockEditorModalService.openAndRerenderSlot).toHaveBeenCalledWith(
                "MOCKED_ITEM_TYPE",
                "MOCKED_ITEM_ID",
                "visibilityTab"
            );
            expect(componentVisibilityAlertService.checkAndAlertOnComponentVisibility).toHaveBeenCalledWith(_getMockPayload("HIDDEN_WITH_RESTRICTIONS"));

        });
    });
});
