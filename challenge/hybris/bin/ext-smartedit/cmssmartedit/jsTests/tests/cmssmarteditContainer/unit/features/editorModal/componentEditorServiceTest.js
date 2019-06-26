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
describe('componentEditorService test - ', function() {

    var componentEditorService,
        mockComponentService,
        componentEditor;
    var $q, $rootScope;
    var harness;

    var tab1 = 'tab1',
        tab2 = 'tab2',
        tab3 = 'tab3';

    var componentInfo = {
        'key1': 'value1',
        'key2': 'value2'
    };

    var payload_tab1 = {
        'field1': 'field1_value',
        'field2': 'field2_value',
        'field3': 'field3_value'
    };

    var payload_tab2 = {
        'field4': 'field4_value',
        'field5': 'field5_value',
    };

    var payload_tab3 = {
        'field6': 'field7_value',
        'field7': 'field7_value',
    };

    var tab1_fields = [{
        qualifier: 'field1'
    }, {
        qualifier: 'field2'
    }, {
        qualifier: 'field3'
    }];

    var tab2_fields = [{
        qualifier: 'field4'
    }, {
        qualifier: 'field5'
    }];

    var tab3_fields = [{
        qualifier: 'field6'
    }, {
        qualifier: 'field7'
    }];

    var error_response = [{
        'subject': 'field1',
        'message': 'cannot be empty'
    }, {
        'subject': 'field6',
        'message': 'only alphanumerics allowed'
    }];

    beforeEach(function() {

        harness = AngularUnitTestHelper.prepareModule('componentEditorModule')
            .mock('ComponentService', 'createNewComponent')
            .mock('ComponentService', 'loadComponentItem')
            .mock('ComponentService', 'updateComponent')
            .mock('restServiceFactory', 'get')
            .service('componentEditorService');

        componentEditorService = harness.service;
        mockComponentService = harness.mocks.ComponentService;

        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

        //GIVEN
        componentEditor = componentEditorService.getInstance('componentId');

        componentEditor.registerTab(tab1, componentInfo); //register tabs
        componentEditor.registerTab(tab2, componentInfo);
        componentEditor.registerTab(tab3, componentInfo);

    });

    describe('saveTabData create mode - ', function() {

        it('GIVEN only one tab is modified' +
            'WHEN save is clicked (saveTabData is called once)' +
            'THEN save success will save data and return an object containing both payload and response',
            function() {

                //GIVEN
                mockComponentService.createNewComponent.and.returnValue($q.when(payload_tab1));

                //WHEN
                componentEditor.setTabDirtyState(tab1, true); //modify tab1
                var promise = componentEditor.saveTabData(payload_tab1, tab1, tab1_fields); //save tab1

                //THEN
                expect(mockComponentService.createNewComponent).toHaveBeenCalledWith(componentInfo, payload_tab1);

                expect(promise).toBeResolvedWithData({
                    payload: payload_tab1,
                    response: payload_tab1
                });

            });

        it('GIVEN when two tab are modified' +
            'WHEN save is clicked (saveTabData is called twice)' +
            'THEN the first tab will wait until the second tab payload is ready and then createNewComponent will be called once with a concatenated payload',
            function() {

                //GIVEN
                mockComponentService.createNewComponent.and.returnValue($q.when(window.smarteditJQuery.extend({}, payload_tab1, payload_tab2)));

                //WHEN
                componentEditor.setTabDirtyState(tab1, true); //modify tab1
                componentEditor.setTabDirtyState(tab2, true); //modify tab2
                componentEditor.setTabDirtyState(tab3, false); //do not modify tab2
                var promise1 = componentEditor.saveTabData(payload_tab1, tab1, tab1_fields); //save tab1

                //THEN
                expect(promise1).not.toBeResolved();

                var promise2 = componentEditor.saveTabData(payload_tab2, tab2, tab2_fields); //save tab1

                //THEN
                expect(mockComponentService.createNewComponent.calls.count()).toBe(1);
                expect(mockComponentService.createNewComponent).toHaveBeenCalledWith(componentInfo, {
                    field1: 'field1_value',
                    field2: 'field2_value',
                    field3: 'field3_value',
                    field4: 'field4_value',
                    field5: 'field5_value'
                });

                expect(promise1).toBeResolvedWithData({
                    payload: payload_tab1,
                    response: {
                        field1: 'field1_value',
                        field2: 'field2_value',
                        field3: 'field3_value',
                        field4: 'field4_value',
                        field5: 'field5_value'
                    }
                });
                expect(promise2).toBeResolvedWithData({
                    payload: payload_tab2,
                    response: {
                        field1: 'field1_value',
                        field2: 'field2_value',
                        field3: 'field3_value',
                        field4: 'field4_value',
                        field5: 'field5_value'
                    }
                });

            });

        it('GIVEN when all three are modified' +
            'WHEN save is clicked (saveTabData is called once)' +
            'THEN save failure will reject with an error response',
            function() {

                //GIVEN
                mockComponentService.createNewComponent.and.returnValue($q.reject(error_response));

                //WHEN
                componentEditor.setTabDirtyState(tab1, true); //modify tab1
                componentEditor.setTabDirtyState(tab2, true); //modify tab2
                componentEditor.setTabDirtyState(tab3, true); //modify tab3
                var promise1 = componentEditor.saveTabData(payload_tab1, tab1, tab1_fields); //save tab1
                var promise2 = componentEditor.saveTabData(payload_tab2, tab2, tab2_fields); //save tab2
                var promise3 = componentEditor.saveTabData(payload_tab3, tab3, tab3_fields); //save tab3

                //THEN
                expect(mockComponentService.createNewComponent).toHaveBeenCalledWith(componentInfo, window.smarteditJQuery.extend({}, payload_tab1, payload_tab2, payload_tab3));

                expect(promise1).toBeRejected();
                expect(promise2).toBeRejected();
                expect(promise3).toBeRejected();

            });

    });

    describe('saveTabData update mode - ', function() {

        it('GIVEN in edit mode ' +
            'WHEN component editor is opened' +
            'THEN fetchTabsContent is called for each tab to fetch and load data',
            function() {

                //GIVEN 
                mockComponentService.loadComponentItem.and.returnValue($q.when(window.smarteditJQuery.extend({}, payload_tab1, payload_tab2, payload_tab3)));

                //WHEN
                var promise = componentEditor.fetchTabsContent('componentId');

                //THEN
                expect(promise).toBeResolvedWithData(window.smarteditJQuery.extend({}, payload_tab1, payload_tab2, payload_tab3));
            });

        it('GIVEN when all three tabs are modified ' +
            'WHEN save is clicked (saveTabData is called thrice)' +
            'THEN componentService.updateComponent is called when payloads of all tabs are ready and then return individual objects containing respective payloads and the full response',
            function() {

                var finalResponse = window.smarteditJQuery.extend({}, payload_tab1, payload_tab2, payload_tab3);
                var additionalFields = {
                    identifier: 'componentId',
                    additionalField1: 'additionalField1',
                    additionalField2: 'additionalField2'
                };
                var totalPayload = window.smarteditJQuery.extend({}, payload_tab1, payload_tab2, payload_tab3, additionalFields);

                //GIVEN 
                mockComponentService.updateComponent.and.returnValue($q.when(finalResponse));

                //WHEN
                componentEditor.setTabDirtyState(tab1, true); //modify tab1
                componentEditor.setTabDirtyState(tab2, true); //modify tab2
                componentEditor.setTabDirtyState(tab3, true); //modify tab3

                var promise1 = componentEditor.saveTabData(window.smarteditJQuery.extend({}, payload_tab1, additionalFields), tab1, tab1_fields); //save tab1
                var promise2 = componentEditor.saveTabData(window.smarteditJQuery.extend({}, payload_tab2, additionalFields), tab2, tab2_fields); //save tab2
                var promise3 = componentEditor.saveTabData(window.smarteditJQuery.extend({}, payload_tab3, additionalFields), tab3, tab3_fields); //save tab3

                //THEN
                expect(mockComponentService.updateComponent.calls.count()).toBe(1);
                expect(mockComponentService.updateComponent).toHaveBeenCalledWith(totalPayload);

                expect(promise1).toBeResolvedWithData({
                    payload: payload_tab1,
                    response: finalResponse
                });

                expect(promise2).toBeResolvedWithData({
                    payload: payload_tab2,
                    response: finalResponse
                });

                expect(promise3).toBeResolvedWithData({
                    payload: window.smarteditJQuery.extend({}, payload_tab3, additionalFields),
                    response: finalResponse
                });


            });

    });

});
