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
describe("Editor Enabler Service: ", function() {

    // Service Under Test
    var editorEnablerService;

    // Spy Objects
    var $q;
    var $rootScope;
    var componentVisibilityAlertService;
    var componentHandlerService;
    var editorModalService;
    var featureService;
    var slotRestrictionsService;
    var typePermissionsRestService;

    var gigo = {
        ctr: 0,
        generate: function() {
            return {
                gigoCount: gigo.ctr++
            };
        }
    };

    function _cloneDefaultPayload() {
        return JSON.parse(JSON.stringify({
            uuid: "MOCK_ITEM_UUID",
            itemtype: "MOCK_ITEM_TYPE",
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

    // Set-up Module Under Test
    beforeEach(function() {

        // is cmssmartedit module
        angular.mock.module('slotRestrictionsServiceModule');

        // module under test
        angular.mock.module('editorEnablerServiceModule');

        // mocks/spies
        angular.mock.module(function($provide) {
            featureService = jasmine.createSpyObj("featureService", ["addContextualMenuButton"]);
            $provide.value("featureService", featureService);

            componentVisibilityAlertService = jasmine.createSpyObj("componentVisibilityAlertService", ["checkAndAlertOnComponentVisibility"]);
            $provide.value('componentVisibilityAlertService', componentVisibilityAlertService);

            editorModalService = jasmine.createSpyObj("editorModalService", ["open"]);
            $provide.value('editorModalService', editorModalService);

            componentHandlerService = jasmine.createSpyObj("componentHandlerService", ["getOriginalComponent", "getParentSlotForComponent", "isExternalComponent"]);
            $provide.value("componentHandlerService", componentHandlerService);

            slotRestrictionsService = jasmine.createSpyObj("slotRestrictionsService", ["isSlotEditable"]);
            $provide.value("slotRestrictionsService", slotRestrictionsService);

            typePermissionsRestService = jasmine.createSpyObj("typePermissionsRestService", ["hasUpdatePermissionForTypes"]);
            $provide.value('typePermissionsRestService', typePermissionsRestService);

            var gatewayProxy = jasmine.createSpyObj("gatewayProxy", ["initForService"]);
            $provide.value("gatewayProxy", gatewayProxy);
        });
    });

    beforeEach(
        inject(function(_editorEnablerService_, _$q_, _$rootScope_) {
            editorEnablerService = _editorEnablerService_;
            $q = _$q_;
            $rootScope = _$rootScope_;
        })
    );

    // Tests
    it("enableForComponent will add the Edit button to the contextual menu for specified component types", function() {

        var TYPE = "SimpleResponsiveBannerComponent";

        // Act
        editorEnablerService.enableForComponents([TYPE]);

        // Assert
        expect(featureService.addContextualMenuButton).toHaveBeenCalledWith({
            key: 'se.cms.edit',
            nameI18nKey: 'se.cms.contextmenu.nameI18nKey.edit',
            descriptionI18nKey: 'se.cms.contextmenu.descriptionI18n.edit',
            regexpKeys: [TYPE],
            displayClass: 'editbutton',
            displayIconClass: 'hyicon hyicon-edit',
            displaySmallIconClass: 'hyicon hyicon-edit',
            i18nKey: 'se.cms.contextmenu.title.edit',
            callback: editorEnablerService._editButtonCallback,
            condition: editorEnablerService._condition,
            permissions: ['se.context.menu.edit.component']
        });
    });

    it('_editButtonCallback delegates to the editor modal service', function() {

        // Given
        var gigoForComponentAttributes = gigo.generate();
        editorModalService.open.and.returnValue($q.when(_getMockPayload("VISIBLE_NO_RESTRICTIONS")));

        // Act
        editorEnablerService._editButtonCallback({
            componentAttributes: gigoForComponentAttributes,
            slotUuid: "MOCK_SLOT_ID"
        });
        $rootScope.$digest();

        // Assert
        expect(editorModalService.open).toHaveBeenCalledWith(gigoForComponentAttributes);
        expect(componentVisibilityAlertService.checkAndAlertOnComponentVisibility).not.toHaveBeenCalledWith();

    });

    it("_editButtonCallback delegates to the editor modal service which triggers a 'hidden' cmsItem Alert", function() {

        // Given
        var gigoForComponentAttributes = gigo.generate();
        editorModalService.open.and.returnValue($q.when(_getMockPayload("HIDDEN_NO_RESTRICTIONS")));

        // Act
        editorEnablerService._editButtonCallback({
            componentAttributes: gigoForComponentAttributes,
            slotUuid: "MOCK_SLOT_ID"
        });
        $rootScope.$digest();

        // Assert
        expect(editorModalService.open).toHaveBeenCalledWith(gigoForComponentAttributes);
        expect(componentVisibilityAlertService.checkAndAlertOnComponentVisibility).toHaveBeenCalledWith({
            itemId: "MOCK_ITEM_UUID",
            itemType: "MOCK_ITEM_TYPE",
            catalogVersion: "MOCK_CATALOG_VERSION",
            restricted: false,
            slotId: "MOCK_SLOT_ID",
            visible: false
        });

    });

    it("_editButtonCallback delegates to the editor modal service which triggers a 'hidden' cmsItem Alert", function() {

        // Given
        var gigoForComponentAttributes = gigo.generate();
        editorModalService.open.and.returnValue($q.when(_getMockPayload("HIDDEN_WITH_RESTRICTIONS")));

        // Act
        editorEnablerService._editButtonCallback({
            componentAttributes: gigoForComponentAttributes,
            slotUuid: "MOCK_SLOT_ID"
        });
        $rootScope.$digest();

        // Assert
        expect(editorModalService.open).toHaveBeenCalledWith(gigoForComponentAttributes);
        expect(componentVisibilityAlertService.checkAndAlertOnComponentVisibility).toHaveBeenCalledWith({
            itemId: "MOCK_ITEM_UUID",
            itemType: "MOCK_ITEM_TYPE",
            catalogVersion: "MOCK_CATALOG_VERSION",
            restricted: true,
            slotId: "MOCK_SLOT_ID",
            visible: false
        });

    });

    it("_editButtonCallback delegates to the editor modal service which triggers an 'restricted' cmsItem Alert", function() {

        // Given
        var gigoForComponentAttributes = gigo.generate();
        editorModalService.open.and.returnValue($q.when(_getMockPayload("VISIBLE_WITH_RESTRICTIONS")));

        // Act
        editorEnablerService._editButtonCallback({
            componentAttributes: gigoForComponentAttributes,
            slotUuid: "MOCK_SLOT_ID"
        });
        $rootScope.$digest();

        // Assert
        expect(editorModalService.open).toHaveBeenCalledWith(gigoForComponentAttributes);
        expect(componentVisibilityAlertService.checkAndAlertOnComponentVisibility).toHaveBeenCalledWith({
            itemId: "MOCK_ITEM_UUID",
            itemType: "MOCK_ITEM_TYPE",
            catalogVersion: "MOCK_CATALOG_VERSION",
            restricted: true,
            slotId: "MOCK_SLOT_ID",
            visible: true
        });

    });

    it('_condition delegates to the componentHandlerService and slotRestrictionsService', function() {

        // Given
        var gigoForElementHandle = gigo.generate();

        var contextualMenuParams = {
            componentId: "someId",
            componentType: "someType",
            element: gigoForElementHandle
        };
        var gigoForisSlotEditable = gigo.generate();
        var mockSlotId = "dummySlotId";
        componentHandlerService.getParentSlotForComponent.and.returnValue(mockSlotId);
        slotRestrictionsService.isSlotEditable.and.returnValue($q.when(gigoForisSlotEditable));

        // Act
        var result = editorEnablerService._condition(contextualMenuParams);
        $rootScope.$digest();

        // Assert
        expect(componentHandlerService.getParentSlotForComponent).toHaveBeenCalledWith(gigoForElementHandle);
        expect(slotRestrictionsService.isSlotEditable).toHaveBeenCalledWith(mockSlotId);
        expect(typePermissionsRestService.hasUpdatePermissionForTypes).toHaveBeenCalledWith([contextualMenuParams.componentType]);
    });

});
