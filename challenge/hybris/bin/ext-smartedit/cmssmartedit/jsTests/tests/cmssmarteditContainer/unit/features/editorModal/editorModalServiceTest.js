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
describe('editorModalService', function() {

    var editorModalService, mocks, $q, $rootScope;

    //Edit data
    var mockEditComponentAttributes = {
        smarteditComponentUuid: "smarteditComponentUuid",
        smarteditComponentType: "smarteditComponentType",
        catalogVersionUuid: "catalogVersionUuid",
        initialDirty: false
    };

    var mockEditGenericEditorComponentData = {
        componentUuid: mockEditComponentAttributes.smarteditComponentUuid,
        componentType: mockEditComponentAttributes.smarteditComponentType,
        title: 'type.' + mockEditComponentAttributes.smarteditComponentType.toLowerCase() + '.name',
        targetedQualifier: undefined,
        content: undefined,
        initialDirty: false
    };

    //Create data
    var mockCreateComponentAttributes = {
        smarteditComponentType: "smarteditComponentType",
        catalogVersionUuid: "catalogVersionUuid",
        initialDirty: false
    };

    var mockCreateGenericEditorComponentData = {
        componentUuid: undefined,
        componentType: mockCreateComponentAttributes.smarteditComponentType,
        title: 'type.' + mockCreateComponentAttributes.smarteditComponentType.toLowerCase() + '.name',
        targetedQualifier: undefined,
        content: {
            slotId: undefined,
            typeCode: mockCreateComponentAttributes.smarteditComponentType,
            itemtype: mockCreateComponentAttributes.smarteditComponentType,
            catalogVersion: mockCreateComponentAttributes.catalogVersionUuid,
            visible: true,
            position: undefined
        },
        initialDirty: false
    };

    var MOCK_SLOT_IDS = ['johnny'];

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('editorModalServiceModule')
            .mock('genericEditorModalService', 'open').and.returnResolvedPromise("somedata1")
            .mock('componentEditorService', 'init')
            .mock('cmsitemsRestService', '_getByUIdAndType')
            .mock('gatewayProxy', 'initForService')
            .mock('renderService', 'renderSlots')
            .mock('ComponentService', 'getSlotsForComponent').and.returnResolvedPromise(MOCK_SLOT_IDS)
            .service('editorModalService');
        editorModalService = harness.service;
        mocks = harness.mocks;
        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;
    });


    it('open will delegate to genericEditorModalService.open and invoke a rerendering upon closing', function() {

        expect(editorModalService.open(mockEditComponentAttributes)).toBeResolvedWithData('somedata1');
        expect(mocks.genericEditorModalService.open.calls.count()).toBe(1);
        expect(mocks.genericEditorModalService.open.calls.argsFor(0).length).toBe(3);
        expect(mocks.genericEditorModalService.open.calls.argsFor(0)[0]).toEqual(mockEditGenericEditorComponentData);

        expect(mocks.renderService.renderSlots).not.toHaveBeenCalled();
        var callback = mocks.genericEditorModalService.open.calls.argsFor(0)[2];
        callback();
        $rootScope.$digest();
        expect(mocks.renderService.renderSlots).toHaveBeenCalledWith(MOCK_SLOT_IDS);
    });


    it('GIVEN creating a new component THEN renderService.renderComponent is not called', function() {
        expect(editorModalService.open(mockCreateComponentAttributes)).toBeResolvedWithData('somedata1');
        expect(mocks.renderService.renderSlots).not.toHaveBeenCalled();
        expect(mocks.genericEditorModalService.open.calls.argsFor(0)[0]).toEqual(mockCreateGenericEditorComponentData);

        var callback = mocks.genericEditorModalService.open.calls.argsFor(0)[2];
        callback();
        expect(mocks.renderService.renderSlots).not.toHaveBeenCalled();
    });
});
