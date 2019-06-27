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
describe('heartBeatService', function() {

    var $rootScope, $timeout, heartBeatService;
    var $translate, gatewayFactory, gateway, alertService;
    var resetTimerCallback;

    beforeEach(angular.mock.module('heartBeatServiceModule', function($provide) {

        $translate = jasmine.createSpyObj('$translate', ['instant']);
        $provide.value("$translate", $translate);

        gatewayFactory = jasmine.createSpyObj('gatewayFactory', ['createGateway']);

        gateway = jasmine.createSpyObj('gateway', ['subscribe']);

        gatewayFactory.createGateway.and.callFake(function(gatewayId) {
            if (gatewayId === 'heartBeatGateway') {
                return gateway;
            } else {
                return jasmine.createSpyObj('gateway', ['subscribe', 'publish']);
            }
        });

        $provide.value("gatewayFactory", gatewayFactory);

        alertService = jasmine.createSpyObj('alertService', ['showWarning', 'removeAlertById']);

        $provide.value("alertService", alertService);

    }));

    beforeEach(inject(function(_$rootScope_, _$timeout_, _heartBeatService_) {

        $rootScope = _$rootScope_;
        $timeout = _$timeout_;
        heartBeatService = _heartBeatService_;
        spyOn(heartBeatService, 'resetTimer').and.callThrough();

        expect(gateway.subscribe).toHaveBeenCalledWith('heartBeat', jasmine.any(Function));
        resetTimerCallback = gateway.subscribe.calls.argsFor(0)[1];

    }));

    afterEach(inject(function() {

        expect(gatewayFactory.createGateway).toHaveBeenCalledWith('heartBeatGateway');
    }));

    it('when timer is reset after a heartbeat failure, alert is removed', function() {

        heartBeatService._heartBeatFailed = true;

        expect(alertService.removeAlertById).not.toHaveBeenCalled();

        heartBeatService.resetTimer(true);

        expect(alertService.removeAlertById).toHaveBeenCalledWith('heartBeatFailure');

    });


    it('when 10000ms timeout has expired, an alert is raised', function() {

        heartBeatService.resetTimer(true);

        expect(alertService.showWarning).not.toHaveBeenCalled();

        $timeout.flush(1000);

        expect(alertService.showWarning).not.toHaveBeenCalled();

        $timeout.flush(9000);

        expect(alertService.showWarning).toHaveBeenCalledWith({
            id: 'heartBeatFailure',
            message: undefined,
            closeable: false
        });

    });


    it('when route change occurs within 10000ms after the timer has started, timer stops and no alert is raised ever after', function() {

        heartBeatService.resetTimer(true);

        expect(alertService.showWarning).not.toHaveBeenCalled();

        $timeout.flush(1000);

        expect(alertService.showWarning).not.toHaveBeenCalled();

        $rootScope.$broadcast("$routeChangeStart");

        $timeout.flush(10000);

        expect(alertService.showWarning).not.toHaveBeenCalled();

        $timeout.flush(10000);

        expect(alertService.showWarning).not.toHaveBeenCalled();
    });

    it('when heartbeat is received within 10000ms after the timer has started, timer restarts', function() {

        heartBeatService.resetTimer(true);

        expect(alertService.showWarning).not.toHaveBeenCalled();

        $timeout.flush(1000);

        expect(alertService.showWarning).not.toHaveBeenCalled();

        resetTimerCallback();

        $timeout.flush(9000);

        expect(alertService.showWarning).not.toHaveBeenCalled();

        $timeout.flush(1000);

        expect(alertService.showWarning).toHaveBeenCalledWith({
            id: 'heartBeatFailure',
            message: undefined,
            closeable: false
        });
    });

});
