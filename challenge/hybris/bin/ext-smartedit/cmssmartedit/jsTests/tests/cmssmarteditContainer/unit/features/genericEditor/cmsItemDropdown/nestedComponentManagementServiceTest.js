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
describe('nestedComponentManagementServiceTest', function() {
    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    var EXCEPTED_RESULT = 'some result';
    var STACK_ID = 'some stack id';

    // ---------------------------------------------------------------
    // Variables
    // ---------------------------------------------------------------
    var nestedComponentManagementService, mocks;

    // ---------------------------------------------------------------
    // Set Up
    // ---------------------------------------------------------------
    beforeEach(function() {
        var harness = AngularUnitTestHelper
            .prepareModule('nestedComponentManagementServiceModule')
            .mock('genericEditorModalService', 'open').and.returnValue(EXCEPTED_RESULT)
            .service('nestedComponentManagementService');

        nestedComponentManagementService = harness.service;
        mocks = harness.mocks;
    });

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------
    it('WHEN openNestedComponentEditor is called with visibility as false THEN the editor is properly opened', function() {
        // GIVEN 
        var COMPONENT_TYPE = 'Some Component Type';
        var COMPONENT_UUID = 'some component uuid';
        var CATALOG_VERSION = 'some catalog version uuid';

        var componentInfo = {
            componentType: COMPONENT_TYPE,
            componentUuid: COMPONENT_UUID,
            content: {
                visible: false,
                catalogVersion: CATALOG_VERSION
            }
        };

        var expectedComponentData = {
            componentUuid: COMPONENT_UUID,
            componentType: COMPONENT_TYPE,
            title: 'type.some component type.name',
            content: {
                typeCode: COMPONENT_TYPE,
                itemtype: COMPONENT_TYPE,
                catalogVersion: CATALOG_VERSION,
                visible: false
            },
            editorStackId: STACK_ID
        };

        // WHEN 
        var result = nestedComponentManagementService.openNestedComponentEditor(componentInfo, STACK_ID);

        // THEN 
        expect(result).toBe(EXCEPTED_RESULT);
        expect(mocks.genericEditorModalService.open).toHaveBeenCalledWith(expectedComponentData, null, jasmine.any(Function));
    });

    it('WHEN openNestedComponentEditor is called THEN the editor is properly opened with the default values', function() {
        // GIVEN
        var COMPONENT_TYPE = 'Some Component Type';
        var COMPONENT_UUID = 'some component uuid';
        var CATALOG_VERSION = 'some catalog version uuid';

        var componentInfo = {
            componentType: COMPONENT_TYPE,
            componentUuid: COMPONENT_UUID,
            content: {
                catalogVersion: CATALOG_VERSION
            }
        };

        var expectedComponentData = {
            componentUuid: COMPONENT_UUID,
            componentType: COMPONENT_TYPE,
            title: 'type.some component type.name',
            content: {
                typeCode: COMPONENT_TYPE,
                itemtype: COMPONENT_TYPE,
                catalogVersion: CATALOG_VERSION,
                visible: true
            },
            editorStackId: STACK_ID
        };

        // WHEN
        var result = nestedComponentManagementService.openNestedComponentEditor(componentInfo, STACK_ID);

        // THEN
        expect(result).toBe(EXCEPTED_RESULT);
        expect(mocks.genericEditorModalService.open).toHaveBeenCalledWith(expectedComponentData, null, jasmine.any(Function));
    });

});
