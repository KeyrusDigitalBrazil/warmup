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
describe('ComponentService', function() {

    // service under test
    var ComponentService;

    // Mocked dependancies
    var restServiceFactory;
    var cmsitemsRestService;
    var catalogService;
    var slotRestrictionsService;

    // Internal mocks
    var restServiceForTypes;
    var restServiceForAddExistingComponent;
    var pageContentSlotsComponentsRestService;
    var typePermissionsRestService;

    // references
    var $rootScope;
    var $q;

    // test constants
    var CONTEXT_CATALOG = "CURRENT_CONTEXT_CATALOG";
    var CONTEXT_CATALOG_VERSION = "CURRENT_CONTEXT_CATALOG_VERSION";

    /**
     *  Basicaly a unique object generator - replace with ES6 Symbol
     */
    var garbageFactory = function() {
        var ctr = 0;
        return {
            generate: function(key) {
                return {
                    key: key || {},
                    garbage: ctr++
                };
            }
        };
    }();

    beforeEach(angular.mock.module('componentServiceModule'));

    beforeEach(function setupDependencies() {

        angular.mock.module(function($provide) {
            $provide.constant("TYPES_RESOURCE_URI", "TYPES_RESOURCE_URI");
            $provide.constant("PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI", "PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI");

            restServiceFactory = jasmine.createSpyObj("restServiceFactory", ["get"]);
            restServiceForTypes = jasmine.createSpyObj("restServiceForTypes", ["get"]);
            restServiceForAddExistingComponent = jasmine.createSpyObj("restServiceForAddExistingComponent", ["save"]);

            restServiceFactory.get.and.returnValues(restServiceForTypes, restServiceForAddExistingComponent);

            $provide.value("restServiceFactory", restServiceFactory);

            cmsitemsRestService = jasmine.createSpyObj("cmsitemsRestService", ["get", "getById", "create", "update"]);
            $provide.value("cmsitemsRestService", cmsitemsRestService);

            catalogService = jasmine.createSpyObj("catalogService", ["retrieveUriContext"]);
            $provide.value("catalogService", catalogService);

            slotRestrictionsService = jasmine.createSpyObj("slotRestrictionsService", ["getAllComponentTypesSupportedOnPage"]);
            $provide.value("slotRestrictionsService", slotRestrictionsService);

            var pageInfoService = jasmine.createSpyObj("pageInfoService", ["getPageUID"]);
            $provide.value("pageInfoService", pageInfoService);

            pageContentSlotsComponentsRestService = jasmine.createSpyObj("pageContentSlotsComponentsRestService", ["getSlotsToComponentsMapForPageUid"]);
            $provide.value("pageContentSlotsComponentsRestService", pageContentSlotsComponentsRestService);

            typePermissionsRestService = jasmine.createSpyObj("typePermissionsRestService", ["hasCreatePermissionForTypes"]);
            $provide.value("typePermissionsRestService", typePermissionsRestService);
        });
    });

    beforeEach(inject(function(_ComponentService_, _$rootScope_, _$q_) {
        ComponentService = _ComponentService_;
        $rootScope = _$rootScope_;
        $q = _$q_;
    }));

    describe("loadComponentTypes() ", function() {

        it('delegates to the types rest services', function() {
            var gigoResponse = garbageFactory.generate("response");
            restServiceForTypes.get.and.returnValue(gigoResponse);

            var result = ComponentService.loadComponentTypes();

            expect(restServiceForTypes.get).toHaveBeenCalledWith({
                category: 'COMPONENT'
            });
            expect(result).toBe(gigoResponse);
        });

    });

    describe("getSupportedComponentTypesForCurrentPage() ", function() {

        it('Only types supported by BOTH the system and the slots restrictions are returned', function() {

            //GIVEN
            var systemSupportedComponentTypes = $q.when(['A', 'B']);
            restServiceForTypes.get.and.returnValue(systemSupportedComponentTypes);

            var slotRestrictionSupportedComponentTypes = $q.when(['B', 'C']);
            slotRestrictionsService.getAllComponentTypesSupportedOnPage.and.returnValue(slotRestrictionSupportedComponentTypes);

            typePermissionsRestService.hasCreatePermissionForTypes.and.returnValue({
                B: true
            });

            spyOn(ComponentService, 'loadComponentTypes');
            ComponentService.loadComponentTypes.and.callThrough();

            // WHEN
            var result = ComponentService.getSupportedComponentTypesForCurrentPage();

            // THEN
            result.then(function(types) {
                expect(ComponentService.loadComponentTypes).toHaveBeenCalled();
                expect(typePermissionsRestService.hasCreatePermissionForTypes).toHaveBeenCalled();
                expect(types.length).toBe(1);
                expect(types[0]).toBe('B');
            });

        });


        it('Handles edge case of no types supported on the page', function() {

            //GIVEN
            var systemSupportedComponentTypes = $q.when(['A', 'B']);
            restServiceForTypes.get.and.returnValue(systemSupportedComponentTypes);

            var slotRestrictionSupportedComponentTypes = $q.when([]);
            slotRestrictionsService.getAllComponentTypesSupportedOnPage.and.returnValue(slotRestrictionSupportedComponentTypes);

            typePermissionsRestService.hasCreatePermissionForTypes.and.returnValue({});

            spyOn(ComponentService, 'loadComponentTypes');
            ComponentService.loadComponentTypes.and.callThrough();

            // WHEN
            var result = ComponentService.getSupportedComponentTypesForCurrentPage();

            // THEN
            result.then(function(types) {
                expect(ComponentService.loadComponentTypes).toHaveBeenCalled();
                expect(typePermissionsRestService.hasCreatePermissionForTypes).toHaveBeenCalled();
                expect(types.length).toBe(0);
            });

        });


    });

    describe("loadComponentItem() ", function() {

        it('delegates to the cmsitems rest service layer', function() {
            var gigoResponse = garbageFactory.generate("response");
            var gigoInput = garbageFactory.generate("input");
            cmsitemsRestService.getById.and.returnValue(gigoResponse);

            var result = ComponentService.loadComponentItem(gigoInput);

            expect(cmsitemsRestService.getById).toHaveBeenCalledWith(gigoInput);
            expect(result).toBe(gigoResponse);
        });

    });

    describe("updateComponent() ", function() {

        it('delegates to the rest service layer with proper data', function() {
            var gigoResponse = garbageFactory.generate("response");
            var gigoInput = garbageFactory.generate("input");
            cmsitemsRestService.update.and.returnValue(gigoResponse);

            var result = ComponentService.updateComponent(gigoInput);

            expect(cmsitemsRestService.update).toHaveBeenCalledWith(gigoInput);
            expect(result).toBe(gigoResponse);
        });

    });

    describe("loadPagedComponentItems() ", function() {

        it('delegates to the rest service layer with proper data', function() {

            var gigoPageSize = garbageFactory.generate("gigoPageSize");
            var gigoCurrentPage = garbageFactory.generate("gigoCurrentPage");
            var gigoMask = garbageFactory.generate("gigoMask");
            var gigoResponse = garbageFactory.generate("gigoResponse");
            var uriContext = {
                CURRENT_CONTEXT_CATALOG: CONTEXT_CATALOG,
                CURRENT_CONTEXT_CATALOG_VERSION: CONTEXT_CATALOG_VERSION
            };
            var expectedInput = {
                pageSize: gigoPageSize,
                currentPage: gigoCurrentPage,
                mask: gigoMask,
                sort: 'name',
                typeCode: 'AbstractCMSComponent',
                catalogId: CONTEXT_CATALOG,
                catalogVersion: CONTEXT_CATALOG_VERSION
            };
            catalogService.retrieveUriContext.and.returnValue($q.when(uriContext));
            cmsitemsRestService.get.and.returnValue(gigoResponse);

            var result = ComponentService.loadPagedComponentItems(gigoMask, gigoPageSize, gigoCurrentPage);
            $rootScope.$digest();

            expect(cmsitemsRestService.get).toHaveBeenCalledWith(expectedInput);
            expect(result).toBeResolvedWithData(gigoResponse);
        });

    });

    describe("addExistingComponent() ", function() {

        it('delegates to the rest service layer with proper data', function() {

            var gigoResponse = garbageFactory.generate("response");
            var gigoIO = {
                pageId: 'pageId',
                componentId: 'componentId',
                slotId: 'slotId',
                position: 'position'
            };
            restServiceForAddExistingComponent.save.and.returnValue(gigoResponse);

            var result = ComponentService.addExistingComponent(
                gigoIO.pageId, gigoIO.componentId, gigoIO.slotId, gigoIO.position);

            expect(restServiceForAddExistingComponent.save).toHaveBeenCalledWith(gigoIO);
            expect(result).toBe(gigoResponse);
        });

    });


    describe("createNewComponent() ", function() {

        it('delegates to the rest service layer with proper data', function() {
            var gigoResponse = garbageFactory.generate("response");
            var componentInput = {
                name: 'name',
                targetSlotId: 'targetSlotId',
                pageId: 'pageId',
                position: 'position',
                componentType: 'componentType',
                catalogVersionUuid: 'catalogVersionUuid'
            };
            var componentPayload = {
                someKey: 'someValue'
            };
            var restLayerInput = {
                name: componentInput.name,
                slotId: componentInput.targetSlotId,
                pageId: componentInput.pageId,
                position: componentInput.position,
                typeCode: componentInput.componentType,
                itemtype: componentInput.componentType,
                catalogVersion: componentInput.catalogVersionUuid,
                someKey: 'someValue'
            };
            cmsitemsRestService.create.and.returnValue(gigoResponse);

            var result = ComponentService.createNewComponent(componentInput, componentPayload);

            expect(cmsitemsRestService.create).toHaveBeenCalledWith(restLayerInput);
            expect(result).toBe(gigoResponse);
        });

    });




});
