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
describe('alertFactory', function() {

    // service under test
    var alertFactory;

    // spies
    var alertCollectionServiceFacade;
    var $timeout;

    var INFO = 'info';
    var SE_ALERT_SERVICE_TYPES = {
        INFO: INFO,
        SUCCESS: "success",
        WARNING: "warning",
        DANGER: "danger" // Using the same states as the ones used by Bootstrap, but we could use our own instead? (i.e. 'error' instead of 'danger')
    };
    var SE_ALERT_DEFAULTS = {
        type: INFO,
        message: '',
        closeable: true,
        timeout: 3000
    };

    beforeEach(angular.mock.module('alertCollectionFacadesModule', function($provide) {
        alertCollectionServiceFacade = jasmine.createSpyObj('alertCollectionServiceFacade', ['addAlert', 'removeAlert']);
        $provide.constant('alertCollectionServiceFacade', alertCollectionServiceFacade);
    }));

    beforeEach(angular.mock.module('alertFactoryModule'));

    beforeEach(inject(function(_alertFactory_, _$timeout_) {
        alertFactory = _alertFactory_;
        $timeout = _$timeout_;
    }));

    it('Legacy alerts with successful property is changed to type property', function() {
        var successAlert = alertFactory.createAlert({
            successful: true
        });
        var dangerAlert = alertFactory.createAlert({
            successful: false
        });

        expect(successAlert.type).toBe(SE_ALERT_SERVICE_TYPES.SUCCESS);
        expect(dangerAlert.type).toBe(SE_ALERT_SERVICE_TYPES.DANGER);
    });

    it('Alerts are created with the expected default values', function() {
        var alert = alertFactory.createAlert();
        expect(alert).toEqual(jasmine.objectContaining(SE_ALERT_DEFAULTS));
    });

    it('Will convert a string param into an AlertConfig with message', function() {
        var MESSAGE = "my alert message";
        var alert = alertFactory.createAlert(MESSAGE);
        expect(alert.message).toBe(MESSAGE);
    });

    it('Will sanitized alert message', function() {
        // GIVEN
        var MESSAGE = "{{0[a='constructor'][a]('alert(\"XSS\")')()}}password";
        var SANITIZED_MESSAGE = "{{0[a='constructor'][a]\\('alert\\(\"XSS\"\\)'\\)\\(\\)}}password";

        // WHEN
        var alert = alertFactory.createAlert(MESSAGE);

        // THEN
        expect(alert.message).toBe(SANITIZED_MESSAGE);
    });

    it('isDisplayed() correctly returns displayed state for show() and hide() functions', function() {
        var alert = alertFactory.createAlert();
        expect(alert.isDisplayed()).toBe(false);

        alert.show();
        expect(alert.isDisplayed()).toBe(true);

        alert.hide();
        expect(alert.isDisplayed()).toBe(false);
    });

    it('Alert is hidden automatically after correct timeout', function() {
        var alert = alertFactory.createAlert({
            timeout: 3000
        });
        alert.show();
        $timeout.flush(2000);
        expect(alert.isDisplayed()).toBe(true);
        $timeout.flush(1000);
        expect(alert.isDisplayed()).toBe(false);
    });

    it('Alert promise is resolved with timedout value', function() {
        var alert = alertFactory.createAlert({
            timeout: 3000
        });
        alert.show();
        $timeout.flush();

        expect(alert.promise).toBeResolvedWithData(true);
    });

    it('Alert promise is resolved with manual hide value', function() {
        var alert = alertFactory.createAlert({
            timeout: 3000
        });
        alert.show();
        alert.hide();

        expect(alert.promise).toBeResolvedWithData(false);
    });

    it('Alert contains the type of template that is inputted', function() {
        var alert_1 = alertFactory.createAlert({
            message: 'A string message'
        });
        var alert_2 = alertFactory.createAlert({
            template: '<h1>This is a sentence.</h1>'
        });
        var alert_3 = alertFactory.createAlert({
            templateUrl: 'somehtmlfile.html'
        });

        expect(alert_1.message).toBeDefined();
        expect(alert_2.template).toBeDefined();
        expect(alert_3.templateUrl).toBeDefined();
    });

    it('Alert message placeholder should be of type object', function() {

        var error = 'alertService._validateAlertConfig - property messagePlaceholders should be an object';

        expect(function() {
            alertFactory.createAlert({
                message: 'A string message',
                messagePlaceholders: 'should be an object'
            });
        }).toThrowError(error);

        expect(function() {
            alertFactory.createAlert({
                message: 'A string message',
                messagePlaceholders: {
                    value: 12345
                }
            });
        }).not.toThrowError(error);

    });

    it('Alert contains only one type of template', function() {

        var error = 'alertService._validateAlertConfig - only one template type is allowed for the alert: message, template, or templateUrl';

        expect(function() {
            alertFactory.createAlert({
                message: 'A string message.',
                template: '<h1>This is a sentence.</h1>'
            });
        }).toThrowError(error);

        expect(function() {
            alertFactory.createAlert({
                message: 'A string message.',
                templateUrl: 'somehtmlfile.html'
            });
        }).toThrowError(error);

        expect(function() {
            alertFactory.createAlert({
                template: '<h1>This is a sentence.</h1>',
                templateUrl: 'somehtmlfile.html'
            });
        }).toThrowError(error);

    });

    it('Factory recipe interface properly assigns the Alert type', function() {
        var info = alertFactory.createInfo({});
        var danger = alertFactory.createDanger({});
        var warning = alertFactory.createWarning({});
        var success = alertFactory.createSuccess({});

        expect(info.type).toBe(SE_ALERT_SERVICE_TYPES.INFO);
        expect(danger.type).toBe(SE_ALERT_SERVICE_TYPES.DANGER);
        expect(warning.type).toBe(SE_ALERT_SERVICE_TYPES.WARNING);
        expect(success.type).toBe(SE_ALERT_SERVICE_TYPES.SUCCESS);
    });

});
