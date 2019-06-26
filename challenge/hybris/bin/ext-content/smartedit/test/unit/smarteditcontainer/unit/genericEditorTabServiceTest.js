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
describe('genericEditorTabService', function() {

    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    var TAB_ID = 'someTabId';
    var TAB_CONFIGURATION = {
        value: 'someConfiguration'
    };

    // --------------------------------------------------------------------------------------
    // Variables
    // --------------------------------------------------------------------------------------
    var genericEditorTabService;

    // --------------------------------------------------------------------------------------
    // Before Each
    // --------------------------------------------------------------------------------------
    beforeEach(angular.mock.module('genericEditorServicesModule'));
    beforeEach(inject(function(_genericEditorTabService_) {
        genericEditorTabService = _genericEditorTabService_;
    }));

    // --------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------
    it('WHEN a tab configuration is added THEN the service stores it', function() {
        // GIVEN 
        expect(Object.keys(genericEditorTabService._tabsConfiguration).length).toBe(0);

        // WHEN 
        genericEditorTabService.configureTab(TAB_ID, TAB_CONFIGURATION);

        // THEN 
        expect(Object.keys(genericEditorTabService._tabsConfiguration).length).toBe(1);
        expect(genericEditorTabService._tabsConfiguration[TAB_ID]).toBe(TAB_CONFIGURATION);
    });

    it('GIVEN the tab configuration does not exist WHEN a tab configuration is retrieved THEN the service returns null', function() {
        // GIVEN 

        // WHEN 
        var result = genericEditorTabService.getTabConfiguration(TAB_ID);

        // THEN 
        expect(result).toBe(null);
    });

    it('GIVEN the tab configuration exists WHEN a tab configuration is retrieved THEN the service returns the config object', function() {
        // GIVEN 
        genericEditorTabService.configureTab(TAB_ID, TAB_CONFIGURATION);

        // WHEN 
        var result = genericEditorTabService.getTabConfiguration(TAB_ID);

        // THEN 
        expect(result).toBe(TAB_CONFIGURATION);
    });

    it('WHEN a tab configuration is added AND it collides with an existing one THEN the existing one is overwritten', function() {
        // GIVEN
        genericEditorTabService.configureTab(TAB_ID, TAB_CONFIGURATION);
        expect(genericEditorTabService.getTabConfiguration(TAB_ID)).toBe(TAB_CONFIGURATION);

        var newConfiguration = {
            value: 'otherValue',
            otherProperty: 'otherPropertyValue'
        };

        // WHEN
        genericEditorTabService.configureTab(TAB_ID, newConfiguration);
        var result = genericEditorTabService.getTabConfiguration(TAB_ID);

        // THEN 
        expect(result).toBe(newConfiguration);
    });

    describe('Tab sorting -', function() {

        var TAB_ID_1 = 'ABC';
        var TAB_ID_2 = 'DEF';
        var TAB_ID_3 = 'GHI';
        var TAB_ID_4 = 'JKL';

        var tabsList;

        beforeEach(function() {
            tabsList = [{
                id: TAB_ID_2
            }, {
                id: TAB_ID_4
            }, {
                id: TAB_ID_3
            }, {
                id: TAB_ID_1
            }];
        });

        it('GIVEN no priority has been given to any tab THEN the tabs must be sorted alphabetically', function() {
            // GIVEN 

            // WHEN 
            genericEditorTabService.sortTabs(tabsList);

            // THEN 
            expect(tabsList[0].id).toBe(TAB_ID_1);
            expect(tabsList[1].id).toBe(TAB_ID_2);
            expect(tabsList[2].id).toBe(TAB_ID_3);
            expect(tabsList[3].id).toBe(TAB_ID_4);
        });

        it('GIVEN some tabs have been given priority THEN the tabs must be sorted by priority first AND then aphabetically', function() {
            // GIVEN 
            genericEditorTabService.configureTab(TAB_ID_3, {
                priority: 10
            });
            genericEditorTabService.configureTab(TAB_ID_4, {
                priority: 12
            });

            // WHEN 
            genericEditorTabService.sortTabs(tabsList);

            // THEN 
            expect(tabsList[0].id).toBe(TAB_ID_4);
            expect(tabsList[1].id).toBe(TAB_ID_3);
            expect(tabsList[2].id).toBe(TAB_ID_1);
            expect(tabsList[3].id).toBe(TAB_ID_2);
        });

    });

});
