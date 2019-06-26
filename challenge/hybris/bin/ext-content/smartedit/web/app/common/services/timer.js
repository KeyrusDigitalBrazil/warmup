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
/**
 * @ngdoc overview
 * @name timerModule
 *
 * @description
 * A module that provides a Timer object that can invoke a callback after a certain period of time.
 * * {@link timerModule.service:Timer Timer}
 */
angular.module('timerModule', [])

    /**
     * @ngdoc service
     * @name timerModule.service:Timer
     *
     * @description
     * A `Timer` must be instanciated calling **`timerService.createTimer()`**.
     * This `Timer` service wraps `Angular`'s {@link https://docs.angularjs.org/api/ng/service/$timeout $timeout} service and adds additional functions to it.
     *
     * @param {Function} callback The function that will be invoked upon timeout.
     * @param {Number} [duration=1000] The number of milliseconds to wait before the callback is invoked. 
     * @param {Boolean} [invokeApply=true] Thise parameter can be set to false in order to instruct `Angular`'s {@link https://docs.angularjs.org/api/ng/service/$timeout $timeout} service to skip model dirty checking.
     *
     */
    .factory('Timer', function($timeout) {


        function Timer(callback, duration, invokeApply) {

            if (!callback) {
                throw 'please provide a callback';
            }

            this._callback = callback;
            this._duration = (duration || 1000);
            this._invokeApply = (invokeApply !== false);

            // I hold the $timeout promise. This will only be non-null when the
            // timer is actively counting down to callback invocation.
            this._timer = null;
        }

        // Define the instance methods.
        Timer.prototype = {

            /**
             * @ngdoc method
             * @name timerModule.service:Timer#isActive
             * @methodOf timerModule.service:Timer
             *
             * @description
             * This method can be used to know whether or not the timer is currently active (counting down).
             *
             * @returns {Boolean} Returns true if the timer is active (counting down).
             */
            isActive: function() {
                return (!!this._timer);
            },


            /**
             * @ngdoc method
             * @name timerModule.service:Timer#restart
             * @methodOf timerModule.service:Timer
             *
             * @description
             * Stops the timer, and then starts it again. If a new duration is given, the timer's duration will be set to that new value.
             *
             * @param {Number} [duration=The previously set duration] The new number of milliseconds to wait before the callback is invoked. 
             */
            restart: function(duration) {
                this._duration = duration || this._duration;
                this.stop();
                this.start();
            },


            /**
             * @ngdoc method
             * @name timerModule.service:Timer#start
             * @methodOf timerModule.service:Timer
             *
             * @description
             * Start the timer, which will invoke the callback upon timeout.
             *
             * @param {Number} [duration=The previously set duration] The new number of milliseconds to wait before the callback is invoked. 
             */
            start: function(duration) {

                this._duration = duration || this._duration;

                // NOTE: Instead of passing the callback directly to the timeout,
                // we're going to wrap it in an anonymous function so we can set
                // the enable flag. We need to do this approach, rather than
                // binding to the .then() event since the .then() will initiate a
                // digest, which the user may not want.
                this._timer = $timeout(
                    function handleTimeoutResolve() {
                        try {
                            if (this._callback) {
                                this._callback.call(null);
                            } else {
                                this.stop();
                            }

                        } finally {
                            this.start();
                        }
                    }.bind(this),
                    this._duration,
                    this._invokeApply
                );
            },


            /**
             * @ngdoc method
             * @name timerModule.service:Timer#stop
             * @methodOf timerModule.service:Timer
             *
             * @description
             * Stop the current timer, if it is running, which will prevent the callback from being invoked.
             */
            stop: function() {
                $timeout.cancel(this._timer);
                this._timer = false;
            },


            /**
             * @ngdoc method
             * @name timerModule.service:Timer#resetDuration
             * @methodOf timerModule.service:Timer
             *
             * @description
             * Sets the duration to a new value.
             *
             * @param {Number} [duration=The previously set duration] The new number of milliseconds to wait before the callback is invoked. 
             */
            resetDuration: function(duration) {
                this._duration = duration || this._duration;
            },


            /**
             * @ngdoc method
             * @name timerModule.service:Timer#teardown
             * @methodOf timerModule.service:Timer
             *
             * @description
             * Clean up the internal object references 
             */
            teardown: function() {
                this.stop();
                this._callback = null;
                this._duration = null;
                this._invokeApply = null;
                this._timer = null;
            }
        };

        return Timer;
    })


    .factory('timerService', function(Timer) {

        function createTimer(callback, duration, invokeApply) {
            return new Timer(callback, duration, invokeApply);
        }

        return {
            createTimer: createTimer
        };

    });
