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
 * @name eventServiceModule
 * @description
 * @deprecated since 1808 - use {@link smarteditCommonsModule.service:SystemEventService SystemEventService} instead.
 * eventServiceModule contains an event service which is supported by the SmartEdit {@link smarteditCommonsModule.service:GatewayFactory gatewayFactory} to propagate events between SmartEditContainer and SmartEdit.
 *
 */
angular.module('eventServiceModule', ['functionsModule'])

    /**
     * @ngdoc object
     * @name eventServiceModule.EVENT_SERVICE_MODE_ASYNCH
     * @deprecated since 1808
     * @description
     * A constant used in the constructor of the Event Service to specify asynchronous event transmission.
     */
    .constant('EVENT_SERVICE_MODE_ASYNCH', 'EVENT_SERVICE_MODE_ASYNCH')

    /**
     * @ngdoc object
     * @name eventServiceModule.EVENT_SERVICE_MODE_SYNCH
     * @deprecated since 1808
     * @description
     * A constant that is used in the constructor of the Event Service to specify synchronous event transmission.
     */
    .constant('EVENT_SERVICE_MODE_SYNCH', 'EVENT_SERVICE_MODE_SYNCH')

    /**
     * @ngdoc object
     * @name eventServiceModule.EVENTS
     * @deprecated since 1808
     * @description
     * Events that are fired/handled in the SmartEdit application
     */
    .constant('EVENTS', {
        AUTHORIZATION_SUCCESS: 'AUTHORIZATION_SUCCESS',
        LOGOUT: 'SE_LOGOUT_EVENT',
        CLEAR_PERSPECTIVE_FEATURES: 'CLEAR_PERSPECTIVE_FEATURES',
        EXPERIENCE_UPDATE: 'experienceUpdate',
        PAGE_CHANGE: 'PAGE_CHANGE',
        PAGE_CREATED: 'PAGE_CREATED_EVENT',
        PAGE_DELETED: 'PAGE_DELETED_EVENT'
    })

    /**
     * @ngdoc service
     * @name eventServiceModule.EventService
     * @description
     * @deprecated since 1808
     * The event service is used to transmit events synchronously or asynchronously. It also contains options to send
     * events, as well as register and unregister event handlers.
     *
     * @param {String} defaultMode Uses constants to set event transmission. The EVENT_SERVICE_MODE_ASYNCH constant sets
     * event transmission to asynchronous mode and the EVENT_SERVICE_MODE_SYNCH constant sets the event transmission to
     * synchronous mode.
     *
     */
    .factory('EventService', function($timeout, $q, $log, toPromise, EVENT_SERVICE_MODE_ASYNCH, EVENT_SERVICE_MODE_SYNCH) {
        var EventService = function(defaultMode) {


            this.eventHandlers = {};

            this.mode = EVENT_SERVICE_MODE_ASYNCH;
            if (defaultMode === EVENT_SERVICE_MODE_ASYNCH || defaultMode === EVENT_SERVICE_MODE_SYNCH) {
                this.mode = defaultMode;
            }

            this._recursiveCallToEventHandlers = function(eventId, data) {
                return $q.all(this.eventHandlers[eventId].map(function(event) {
                    var promiseClosure = toPromise(event);
                    return promiseClosure(eventId, data);
                })).then(function(results) {
                    return results.pop();
                });
            };

            /**
             * @ngdoc method
             * @name eventServiceModule.EventService#sendEvent
             * @methodOf eventServiceModule.EventService
             * @deprecated since 1808
             * @description
             * Send the event with data. The event is sent either synchronously or asynchronously depending on the event
             * mode.
             *
             * @param {String} eventId The identifier of the event.
             * @param {String} data The event payload.
             */
            this.sendEvent =
                function(eventId, data) {
                    if (this.mode === EVENT_SERVICE_MODE_ASYNCH) {
                        this.sendAsynchEvent(eventId, data);
                    } else if (this.mode === EVENT_SERVICE_MODE_SYNCH) {
                        this.sendSynchEvent(eventId, data);
                    } else {
                        throw ('Unknown event service mode: ' + this.mode);
                    }
                };

            /**
             * @ngdoc method
             * @name eventServiceModule.EventService#sendEvent
             * @methodOf eventServiceModule.EventService
             * @deprecated since 1808
             * @description
             * send the event with data synchronously.
             *
             * @param {String} eventId The identifier of the event.
             * @param {String} data The event payload.
             */
            this.sendSynchEvent = function(eventId, data) {
                var deferred = $q.defer();
                if (!eventId) {
                    $log.error('Failed to send event. No event ID provided for data: ' + data);
                    deferred.reject();
                    return;
                }
                if (this.eventHandlers[eventId] && this.eventHandlers[eventId].length > 0) {
                    this._recursiveCallToEventHandlers(eventId, data).then(
                        function(resolvedDataOfLastSubscriber) {
                            deferred.resolve(resolvedDataOfLastSubscriber);
                        },
                        function(error) {
                            deferred.reject(error);
                        }
                    );
                } else {
                    deferred.resolve();
                }
                return deferred.promise;
            };

            this.sendAsynchEvent = function(eventId, data) {
                var deferred = $q.defer();
                $timeout(function() {
                    this.sendSynchEvent(eventId, data).then(
                        function(resolvedData) {
                            deferred.resolve(resolvedData);
                        },
                        function(error) {
                            deferred.reject(error);
                        }
                    );
                }.bind(this), 0);
                return deferred.promise;
            };

            this.registerEventHandler = function(eventId, handler) {
                if (!eventId || !handler) {
                    $log.error('Failed to register event handler for event: ' + eventId);
                    return;
                }
                // create handlers array for this event if not already created
                if (this.eventHandlers[eventId] === undefined) {
                    this.eventHandlers[eventId] = [];
                }
                this.eventHandlers[eventId].push(handler);

                var unregisterFn = function() {
                    this.unRegisterEventHandler(eventId, handler);
                }.bind(this);

                return unregisterFn;
            };

            this.unRegisterEventHandler = function(eventId, handler) {
                var handlersArray = this.eventHandlers[eventId];
                var index = handlersArray ? this.eventHandlers[eventId].indexOf(handler) : -1;
                if (index >= 0) {
                    this.eventHandlers[eventId].splice(index, 1);
                } else {
                    $log.warn('Attempting to remove event handler for ' + eventId + ' but handler not found.');
                }
            };
        };



        return EventService;
    });
