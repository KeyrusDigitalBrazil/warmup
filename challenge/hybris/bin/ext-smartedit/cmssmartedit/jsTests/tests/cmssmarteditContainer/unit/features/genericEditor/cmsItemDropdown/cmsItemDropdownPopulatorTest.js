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
describe('cmsItemDropdownPopulatorTest', function() {
    // ---------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------
    var STACK_ID = 'some stack id';
    var PAYLOAD = {
        field: {
            editorStackId: STACK_ID
        }
    };
    var EXPECTED_URI = '/cmswebservices/v1/sites/CURRENT_CONTEXT_SITE_ID/cmsitems';

    // ---------------------------------------------------------------
    // Variables
    // ---------------------------------------------------------------
    var $q, $rootScope;
    var cmsItemDropdownPopulator, extend, mocks;
    var DropdownPopulatorInterface, uriDropdownPopulator;

    // ---------------------------------------------------------------
    // Set Up
    // ---------------------------------------------------------------
    beforeEach(angular.mock.module('dropdownPopulatorModule', function($provide) {
        extend = jasmine.createSpy('extend');
        extend.and.callFake(function(parent, child) {
            return child;
        });

        DropdownPopulatorInterface = function() {};
        DropdownPopulatorInterface.prototype = jasmine.createSpyObj('DropdownPopulatorInterfacePrototype', ['fetchAll', 'fetchPage', 'getItem']);

        uriDropdownPopulator = jasmine.createSpyObj('uriDropdownPopulator', ['fetchAll', 'fetchPage', 'getItem']);

        $provide.value('extend', extend);
        $provide.value('uriDropdownPopulator', uriDropdownPopulator);

        $provide.value('DropdownPopulatorInterface', DropdownPopulatorInterface);
    }));

    beforeEach(function() {
        var harness = AngularUnitTestHelper
            .prepareModule('cmsItemDropdownDropdownPopulatorModule')
            .mock('genericEditorStackService', 'getEditorsStack').and.returnValue([{
                    component: {
                        uuid: 'comp3'
                    }
                },
                {
                    component: {
                        uuid: 'comp1'
                    }
                }
            ])
            .service('CMSItemDropdownDropdownPopulator');

        cmsItemDropdownPopulator = harness.service;
        mocks = harness.mocks;
        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;
    });

    // ---------------------------------------------------------------
    // Tests
    // ---------------------------------------------------------------
    it('WHEN the populator is created THEN it is properly initialized', function() {
        // THEN 
        expect(extend).toHaveBeenCalledWith(DropdownPopulatorInterface, jasmine.any(Function));
    });

    it('WHEN fetchAll is called THEN it is properly delegated AND removes nested items', function() {
        // GIVEN 
        var items = [{
            uuid: 'comp1'
        }, {
            uuid: 'comp2'
        }];
        uriDropdownPopulator.fetchAll.and.returnValue($q.when(items));

        // WHEN 
        var promise = cmsItemDropdownPopulator.fetchAll(PAYLOAD);
        $rootScope.$digest();

        // THEN 
        expect(uriDropdownPopulator.fetchAll).toHaveBeenCalledWith(PAYLOAD);
        expect(PAYLOAD.field.uri).toBe(EXPECTED_URI);
        promise.then(function(resultList) {
            expect(resultList.length).toBe(1);
            expect(resultList[0]).toEqual({
                uuid: 'comp2'
            });
        });
        $rootScope.$digest();
    });

    it('WHEN fetchPage is called THEN it is properly delegated AND removes nested items', function() {
        // GIVEN 
        var pagedResult = {
            response: [{
                uuid: 'comp1'
            }, {
                uuid: 'comp2'
            }],
            pagination: {
                count: 2
            }
        };
        uriDropdownPopulator.fetchPage.and.returnValue($q.when(pagedResult));

        // WHEN 
        var promise = cmsItemDropdownPopulator.fetchPage(PAYLOAD);
        $rootScope.$digest();

        // THEN 
        expect(uriDropdownPopulator.fetchPage).toHaveBeenCalledWith(PAYLOAD);
        expect(PAYLOAD.field.uri).toBe(EXPECTED_URI);
        promise.then(function(actualPagedResult) {
            expect(actualPagedResult.pagination.count).toBe(1);
            expect(actualPagedResult.response.length).toBe(1);
            expect(actualPagedResult.response[0]).toEqual({
                uuid: 'comp2'
            });
        });
        $rootScope.$digest();
    });

    it('WHEN fetchItem is called THEN it is properly delegated AND does not remove nested items', function() {
        // GIVEN 
        var expectedResult = 'some item';
        uriDropdownPopulator.getItem.and.returnValue(expectedResult);

        // WHEN 
        var result = cmsItemDropdownPopulator.getItem(PAYLOAD);

        // THEN 
        expect(PAYLOAD.field.uri).toBe(EXPECTED_URI);
        expect(result).toBe(expectedResult);
        expect(mocks.genericEditorStackService.getEditorsStack).not.toHaveBeenCalled();
    });
});
