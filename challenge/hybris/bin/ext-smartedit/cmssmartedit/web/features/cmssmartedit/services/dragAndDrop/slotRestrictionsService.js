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
 * @name slotRestrictionsServiceModule
 * @description
 * # The slotRestrictionsServiceModule
 *
 * The slotRestrictionsServiceModule contains a service that caches and returns the restrictions of a slot in a page. This restrictions determine
 * whether a component of a certain type is allowed or forbidden in a particular slot.
 *
 */
angular.module('slotRestrictionsServiceModule', ['yLoDashModule', 'slotSharedServiceModule', 'functionsModule', 'cmsSmarteditServicesModule'])
    /**
     * @ngdoc service
     * @name slotRestrictionsServiceModule.service:slotRestrictionsService
     *
     * @description
     * This service provides methods that cache and return the restrictions of a slot in a page. This restrictions determine
     * whether a component of a certain type is allowed or forbidden in a particular slot.
     */
    .service('slotRestrictionsService', function($q, $log, lodash, isBlank, gatewayProxy, crossFrameEventService, EVENTS, componentHandlerService, slotSharedService, restServiceFactory, pageContentSlotsComponentsRestService, typePermissionsRestService, CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI, CONTENT_SLOT_TYPE) {
        var _slotRestrictions = {};
        var _currentPageId = null;
        var _slotRestrictionsRestService;


        /**
         * @ngdoc method
         * @name slotRestrictionsServiceModule.service:slotRestrictionsService#getAllComponentTypesSupportedOnPage
         * @methodOf slotRestrictionsServiceModule.service:slotRestrictionsService
         *
         * @description
         * This methods retrieves the list of component types droppable in at least one of the slots of the current page
         * @returns {Promise} A promise containing an array with the component types droppable on the current page
         */
        this.getAllComponentTypesSupportedOnPage = function() {
            var slots = componentHandlerService.getFromSelector(componentHandlerService.getAllSlotsSelector());
            var slotIds = Array.prototype.slice.call(slots.map(function() {
                return componentHandlerService.getId(componentHandlerService.getFromSelector(this));
            }));

            return $q.all(slotIds.map(function(slotId) {
                return this.getSlotRestrictions(slotId);
            }.bind(this))).then(function(arrayOfSlotRestrictions) {
                return lodash.flatten(arrayOfSlotRestrictions);
            }, function(error) {
                $log.info(error);
            });
        };

        /**
         * @ngdoc method
         * @name slotRestrictionsServiceModule.service:slotRestrictionsService#getSlotRestrictions
         * @methodOf slotRestrictionsServiceModule.service:slotRestrictionsService
         *
         * @description
         * This methods retrieves the list of restrictions applied to the slot identified by the provided ID.
         *
         * @param {String} slotId The ID of the slot whose restrictions to retrieve.
         * @returns {Promise} A promise containing an array with the restrictions applied to the slot.
         */
        this.getSlotRestrictions = function(slotId) {
            _slotRestrictionsRestService = _slotRestrictionsRestService || restServiceFactory.get(CONTENT_SLOT_TYPE_RESTRICTION_RESOURCE_URI);

            return this._getPageUID(_currentPageId).then(function(pageId) {
                _currentPageId = pageId;
                var restrictionId = this._getEntryId(_currentPageId, slotId);
                if (_slotRestrictions[restrictionId]) {
                    return $q.when(_slotRestrictions[restrictionId]);
                } else if (this._isExternalSlot(slotId)) {
                    _slotRestrictions[restrictionId] = [];
                    return $q.when(_slotRestrictions[restrictionId]);
                }

                return _slotRestrictionsRestService.get({
                    pageUid: _currentPageId,
                    slotUid: slotId
                }).then(function(response) {
                    _slotRestrictions[restrictionId] = response.validComponentTypes;
                    return _slotRestrictions[restrictionId];
                }.bind(this), function(error) {
                    $log.info(error);
                });
            }.bind(this));
        };

        /**
         * @ngdoc method
         * @name slotRestrictionsServiceModule.service:slotRestrictionsService#isComponentAllowedInSlot
         * @methodOf slotRestrictionsServiceModule.service:slotRestrictionsService
         *
         * @description
         * This methods determines whether a component of the provided type is allowed in the slot.
         *
         * @param {Object} slot the slot for which to verify if it allows a component of the provided type.
         * @param {String} slot.id The ID of the slot.
         * @param {Array} slot.components the list of components contained in the slot, they must contain an "id" property.
         * @param {Object} dragInfo contains the dragged object information
         * @param {String} dragInfo.componentType The smartedit type of the component being checked.
         * @param {String} dragInfo.componentId The smartedit id of the component being checked.
         * @param {String} dragInfo.slotId The smartedit id of the slot from which the component originates
         * @param {String} dragInfo.cloneOnDrop The boolean that determines if the component should be cloned or not
         * @returns {Promise} A promise containing a boolean flag that determines whether a component of the provided type is allowed in the slot.
         */
        this.isComponentAllowedInSlot = function(slot, dragInfo) {
            return this.getSlotRestrictions(slot.id).then(function(currentSlotRestrictions) {
                return pageContentSlotsComponentsRestService.getComponentsForSlot(slot.id).then(function(componentsForSlot) {

                    var isComponentIdAllowed = (slot.id === dragInfo.slotId || !componentsForSlot.some(function(component) {
                        return component.uid === dragInfo.componentId;
                    }));
                    return isComponentIdAllowed && lodash.includes(currentSlotRestrictions, dragInfo.componentType);
                });
            });
        };

        /**
         * @ngdoc method
         * @name slotRestrictionsServiceModule.service:slotRestrictionsService#isSlotEditable
         * @methodOf slotRestrictionsServiceModule.service:slotRestrictionsService
         *
         * @description
         * This method determines whether slot is editable or not.
         *
         * @param {String} slotId The ID of the slot.
         *
         * @returns {Promise} A promise containing a boolean flag that shows whether if the slot is editable or not.
         */
        this.isSlotEditable = function(slotId) {
            return typePermissionsRestService.hasUpdatePermissionForTypes([CONTENT_SLOT_TYPE]).then(function(slotPermissions) {
                return slotSharedService.isSlotShared(slotId).then(function(isShared) {
                    var result = slotPermissions[CONTENT_SLOT_TYPE];
                    if (isShared) {
                        var isExternalSlot = this._isExternalSlot(slotId);
                        result = result && !isExternalSlot && !slotSharedService.areSharedSlotsDisabled();
                    }

                    return result;
                }.bind(this));
            }.bind(this));
        };

        this.emptyCache = function() {
            _slotRestrictions = {};
            _currentPageId = null;
        };

        this._getEntryId = function(pageId, slotId) {
            return pageId + '_' + slotId;
        };

        this._isExternalSlot = function(slotId) {
            return componentHandlerService.isExternalComponent(slotId, CONTENT_SLOT_TYPE);
        };

        this._getPageUID = function(pageUID) {
            return !isBlank(pageUID) ? $q.when(pageUID) : componentHandlerService.getPageUID();
        };

        crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, function() {
            this.emptyCache();
        }.bind(this));

        gatewayProxy.initForService(this, ['getAllComponentTypesSupportedOnPage', 'getSlotRestrictions'], "SLOT_RESTRICTIONS");

    });
