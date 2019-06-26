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
 * @name slotSharedServiceModule.pageContentSlotsService
 * @description
 * pageContentSlotsServiceModule provides methods to load and act on the contentSlots for the page loaded in the storefront.
 */
angular.module('pageContentSlotsServiceModule', ['resourceModule', 'smarteditServicesModule', 'cmsResourceLocationsModule', 'yLoDashModule'])
    .service('pageContentSlotsService', function($q, lodash, restServiceFactory, crossFrameEventService, EVENTS, componentHandlerService, PAGES_CONTENT_SLOT_RESOURCE_URI) {
        var pagesContentSlotsResource = restServiceFactory.get(PAGES_CONTENT_SLOT_RESOURCE_URI);
        var pageContentSlotsPromise;

        this._reloadPageContentSlots = function() {
            return componentHandlerService.getPageUID().then(function(pageId) {
                pageContentSlotsPromise = pagesContentSlotsResource.get({
                    pageId: pageId
                }).then(function(pagesContentSlotsResponse) {
                    return $q.when(lodash.uniq(pagesContentSlotsResponse.pageContentSlotList || []));
                });
                return pageContentSlotsPromise;
            });
        };

        /**
         * @ngdoc method
         * @name slotSharedServiceModule.slotSharedService#getPageContentSlots
         * @methodOf slotSharedServiceModule.slotSharedService
         *
         * @description
         * This function fetches all the slots of the loaded page.
         * 
         * @returns {Promise} promise that resolves to a collection of content slots information for the loaded page.
         */
        this.getPageContentSlots = function() {
            return pageContentSlotsPromise ? $q.when(pageContentSlotsPromise) : this._reloadPageContentSlots();
        };

        /**
         * @ngdoc method
         * @name slotSharedServiceModule.slotSharedService#getSlotStatus
         * @methodOf slotSharedServiceModule.slotSharedService
         *
         * @description
         * retrieves the slot status of the proved slotId. It can be one of TEMPLATE, PAGE and OVERRIDE.
         *
         * @param {String} slotId of the slot
         * 
         * @returns {Promise} promise that resolves to the status of the slot.
         */
        this.getSlotStatus = function(slotId) {
            return (pageContentSlotsPromise ? $q.when(pageContentSlotsPromise) : this._reloadPageContentSlots()).then(function(pageContentSlots) {
                var matchedSlotData = lodash.first(pageContentSlots.filter(function(pageContentSlot) {
                    return pageContentSlot.slotId === slotId;
                }));
                return matchedSlotData ? matchedSlotData.slotStatus : undefined;
            });
        };

        crossFrameEventService.subscribe(EVENTS.PAGE_CHANGE, function() {
            this._reloadPageContentSlots();
        }.bind(this));
    });
