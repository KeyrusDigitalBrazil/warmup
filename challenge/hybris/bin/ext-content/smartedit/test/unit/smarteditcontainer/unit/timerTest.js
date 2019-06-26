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
describe('timerModule', function() {

    var $timeout, Timer;
    var callback = function() {};
    var duration = 3000;
    var invokeApply = false;

    beforeEach(angular.mock.module('timerModule'));
    beforeEach(inject(function(_$timeout_, _Timer_) {
        $timeout = _$timeout_;
        Timer = _Timer_;
    }));

    it('throws an error when callback is not passed', function() {

        expect(function() {
            return new Timer();
        }).toThrow('please provide a callback');
    });

    it('initializes with default values when only callback is passed', function() {

        var timer = new Timer(callback);
        expect(timer._callback).toBe(callback);
        expect(timer._duration).toBe(1000);
        expect(timer._invokeApply).toBe(true);
        expect(timer._timer).toBe(null);
    });

    it('initializes with the right values when all parameters are passed', function() {

        var timer = new Timer(callback, duration, invokeApply);
        expect(timer._callback).toBe(callback);
        expect(timer._duration).toBe(duration);
        expect(timer._invokeApply).toBe(invokeApply);
        expect(timer._timer).toBe(null);
    });

    it('isActive returns false when timer is not started', function() {

        var timer = new Timer(callback, duration);
        expect(timer.isActive()).toBe(false);
    });

    it('isActive returns true when timer is started', function() {

        var timer = new Timer(callback, duration);
        timer.start();
        expect(timer.isActive()).toBe(true);
    });

    it('start will start the timer and keep calling the function every duration provided by the user', function() {

        var timer = new Timer(callback, duration);
        spyOn(timer, '_callback').and.returnValue({});
        timer.start();

        expect(timer._callback).not.toHaveBeenCalled();

        $timeout.flush(duration);
        expect(timer._callback).toHaveBeenCalled();

        $timeout.flush(duration);
        expect(timer._callback).toHaveBeenCalled();
    });

    it('start with a new duration will start the timer and keep calling the function every new duration provided by the user', function() {

        var new_duration = 2000;
        var timer = new Timer(callback, duration);
        expect(timer._duration).toBe(duration);

        spyOn(timer, '_callback').and.returnValue({});

        timer.start(new_duration);
        expect(timer._duration).toBe(new_duration);

        expect(timer._callback).not.toHaveBeenCalled();

        $timeout.flush(new_duration);
        expect(timer._callback).toHaveBeenCalled();

        $timeout.flush(new_duration);
        expect(timer._callback.calls.count()).toEqual(2);
    });

    it('stop will stop the timer if the timer is already running', function() {

        var timer = new Timer(callback, duration);
        timer.start();

        spyOn(timer, '_callback').and.returnValue({});

        $timeout.flush(duration);
        expect(timer._callback).toHaveBeenCalled();

        timer.stop();

        $timeout.flush(duration);
        expect(timer._callback.calls.count()).toEqual(1);

    });

    it('restart will stop and start the timer if the timer is already running', function() {

        var timer = new Timer(callback, duration);

        spyOn(timer, '_callback').and.returnValue({});
        spyOn(timer, 'stop').and.callThrough();
        spyOn(timer, 'start').and.callThrough();

        timer.start();

        timer.restart();

        expect(timer.stop).toHaveBeenCalled();
        expect(timer.start).toHaveBeenCalled();
        expect(timer.start.calls.count()).toEqual(2);


    });

    it('restart will a new duration will stop and start the timer if the timer with the new duration', function() {

        var new_duration = 4000;
        var timer = new Timer(callback, duration);

        spyOn(timer, '_callback').and.returnValue({});
        spyOn(timer, 'stop').and.callThrough();

        timer.start();

        expect(timer._duration).toBe(duration);

        $timeout.flush(duration);
        expect(timer._callback.calls.count()).toEqual(1);

        timer.restart(new_duration);

        expect(timer.stop).toHaveBeenCalled();
        expect(timer._duration).toBe(new_duration);

        $timeout.flush(duration);
        expect(timer._callback.calls.count()).toEqual(1);

        $timeout.flush(new_duration);
        expect(timer._callback.calls.count()).toEqual(2);



    });

    it('teardown will stop the timer and reset all properties to null', function() {

        var timer = new Timer(callback, duration);

        spyOn(timer, '_callback').and.returnValue({});
        spyOn(timer, 'stop').and.callThrough();

        timer.start();
        timer.teardown();

        expect(timer.stop).toHaveBeenCalled();
        expect(timer._callback).toBe(null);
        expect(timer._duration).toBe(null);
        expect(timer._invokeApply).toBe(null);
        expect(timer._timer).toBe(null);

    });

});
