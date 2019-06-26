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
angular.module('componentInfoServiceModule', [
        'yjqueryModule',
        'yLoDashModule',
        'cmsitemsRestServiceModule',
        'renderServiceInterfaceModule'
    ])
    /**
     * This service automatically fetch and cache the components information when they are visible in the viewport.
     * This service is intended to be used to improve the performance of the application by reducing the number of xhr calls to the cmsitems api.
     * Example: a component in the overlay that is doing a fetch to the cmsitems api should use this service instead of using cmsitemsRestService.
     * When a lot of components are rendered in the overlay we want to avoid one xhr call per component, but instead use this service that is listening
     * to the 'OVERLAY_RERENDERED_EVENT' and fetch components information in batch (POST to cmsitems endpoint with an Array of uuids).
     */
    .service('componentInfoService', function($log, $q, yjQuery, lodash, crossFrameEventService, cmsitemsRestService, UUID_ATTRIBUTE, EVENTS, OVERLAY_RERENDERED_EVENT) {
        var cachedComponents = {};
        var deferredMap = {};

        // returns a Promise that will be resolved only if the component was added previously in the overlay and if not will resolve only when the component is added to the overlay.
        this.getById = function(uuid) {
            if (cachedComponents[uuid]) {
                return $q.when(cachedComponents[uuid]);
            } else {
                var deferred = deferredMap[uuid] || $q.defer();
                if (!deferredMap[uuid]) {
                    deferredMap[uuid] = deferred;
                }
                return deferred.promise;
            }
        };

        this._getComponentsDataByUUIDs = function(uuids) {
            cmsitemsRestService.getByIds(uuids).then(function(data) {
                data.response.forEach(function(component) {
                    cachedComponents[component.uuid] = component;
                    if (deferredMap[component.uuid]) {
                        deferredMap[component.uuid].resolve(component);
                        delete deferredMap[component.uuid];
                    }
                });
            }, function(e) {
                $log.error('componentInfoService::_getComponentsDataByUUIDs error:', e.message);
                uuids.forEach(function(uuid) {
                    if (deferredMap[uuid]) {
                        deferredMap[uuid].reject(e);
                        delete deferredMap[uuid];
                    }
                });
            });
        };

        this._onComponentsAdded = function(addedComponents) {
            var uuids = lodash.map(addedComponents, function(component) {
                return yjQuery(component).attr(UUID_ATTRIBUTE);
            }).filter(function(uuid) {
                return !lodash.includes(Object.keys(cachedComponents), uuid);
            });
            if (uuids.length) {
                this._getComponentsDataByUUIDs(uuids);
            }
        };

        // delete from the cache the components that were removed from the DOM
        // note: components that are still in the DOM were only removed from the overlay
        this._onComponentsRemoved = function(removedComponents) {
            removedComponents.filter(function(component) {
                return !yjQuery.find('[' + UUID_ATTRIBUTE + '=\'' + yjQuery(component).attr(UUID_ATTRIBUTE) + '\']').length;
            }).filter(function(component) {
                return lodash.includes(Object.keys(cachedComponents), yjQuery(component).attr(UUID_ATTRIBUTE));
            }).map(function(component) {
                return yjQuery(component).attr(UUID_ATTRIBUTE);
            }).forEach(function(uuid) {
                delete cachedComponents[uuid];
            });
        };

        this._clearCache = function() {
            cachedComponents = {};
            deferredMap = {};
        };

        // components added & removed
        crossFrameEventService.subscribe(OVERLAY_RERENDERED_EVENT, function(evtId, data) {
            if (data) {
                if (data.addedComponents && data.addedComponents.length) {
                    this._onComponentsAdded(data.addedComponents);
                }
                if (data.removedComponents && data.removedComponents.length) {
                    this._onComponentsRemoved(data.removedComponents);
                }
            }
        }.bind(this));

        // clear cache
        crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, this._clearCache.bind(this));
        crossFrameEventService.subscribe(EVENTS.USER_HAS_CHANGED, this._clearCache.bind(this));
    });
