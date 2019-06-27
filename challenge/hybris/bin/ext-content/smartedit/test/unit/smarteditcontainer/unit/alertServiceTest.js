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
describe('alertService', function() {

    // service under test
    var alertService;

    // spies
    var alertFactory;
    var gatewayProxy;
    var alertCollectionLegacySupport;

    beforeEach(angular.mock.module('alertFactoryModule', function($provide) {
        alertFactory = jasmine.createSpyObj('alertFactory', ['createInfo', 'createAlert', 'createWarning', 'createSuccess', 'createDanger']);
        $provide.constant('alertFactory', alertFactory);
    }));

    beforeEach(angular.mock.module('smarteditCommonsModule', function($provide) {
        gatewayProxy = jasmine.createSpyObj('gatewayProxy', ['initForService']);
        $provide.constant('gatewayProxy', gatewayProxy);
    }));

    beforeEach(angular.mock.module('alertCollectionModule', function($provide) {
        alertCollectionLegacySupport = jasmine.createSpyObj('alertCollectionLegacySupport', ['removeAlertById']);
        $provide.constant('alertCollectionLegacySupport', alertCollectionLegacySupport);
    }));

    beforeEach(angular.mock.module('alertServiceModule'));

    beforeEach(inject(function(_alertService_) {
        alertService = _alertService_;
    }));

    describe('all alertService.showXZY() functions', function() {

        // spies
        var alertConfig;
        var mockAlert;

        function testShowXYZFunction(alertServiceFn, alertFactoryFn) {
            //given
            alertConfig = jasmine.createSpy();
            mockAlert = jasmine.createSpyObj('mockAlert', ['show']);
            alertFactory[alertFactoryFn].and.returnValue(mockAlert);

            //when
            alertService[alertServiceFn](alertConfig);

            //then
            expect(alertFactory[alertFactoryFn]).toHaveBeenCalledWith(alertConfig);
            expect(mockAlert.show).toHaveBeenCalled();
        }

        it('showAlert creates an alert and calls alert.show() before returning the alert', function() {
            testShowXYZFunction('showAlert', 'createAlert');
        });

        it('showAlert creates an alert and calls alert.show() before returning the alert', function() {
            testShowXYZFunction('showInfo', 'createInfo');
        });

        it('showAlert creates an alert and calls alert.show() before returning the alert', function() {
            testShowXYZFunction('showWarning', 'createWarning');
        });

        it('showAlert creates an alert and calls alert.show() before returning the alert', function() {
            testShowXYZFunction('showSuccess', 'createSuccess');
        });

        it('showAlert creates an alert and calls alert.show() before returning the alert', function() {
            testShowXYZFunction('showDanger', 'createDanger');
        });

    });

    describe('LEGACY functions', function() {


        it('delegates a single legacy alert to showAlert()', function() {

            spyOn(alertService, 'showAlert').and.callThrough();
            var dummyAlertConf = {};
            var mockAlert = jasmine.createSpyObj('mockAlert', ['show']);
            alertFactory.createAlert.and.returnValue(mockAlert);

            alertService.pushAlerts([dummyAlertConf]);

            expect(mockAlert.show).toHaveBeenCalled();
            expect(alertService.showAlert).toHaveBeenCalledWith(dummyAlertConf);
        });

        it('delegates multiple legacy alerts to showAlert()', function() {

            var dummyAlertConf1 = {};
            var mockAlert1 = jasmine.createSpyObj('mockAlert1', ['show']);
            var dummyAlertConf2 = {};
            var mockAlert2 = jasmine.createSpyObj('mockAlert2', ['show']);
            alertFactory.createAlert.and.returnValues(mockAlert1, mockAlert2);

            alertService.pushAlerts([dummyAlertConf1, dummyAlertConf2]);

            expect(mockAlert1.show).toHaveBeenCalled();
            expect(mockAlert2.show).toHaveBeenCalled();
        });

        it('delegates the legacy removeById to the alertCollectionLegacySupport service', function() {
            var dummyInput = 'garbage';
            alertService.removeAlertById(dummyInput);

            expect(alertCollectionLegacySupport.removeAlertById).toHaveBeenCalledWith(dummyInput);
        });



    });




});
