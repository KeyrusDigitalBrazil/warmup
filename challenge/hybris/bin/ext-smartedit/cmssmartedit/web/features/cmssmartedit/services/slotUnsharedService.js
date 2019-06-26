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
 * @name slotUnsharedServiceModule.slotUnsharedService
 * @description
 * slotUnsharedService provides methods to interact with the backend for unshared slot information. 
 */
angular.module('slotUnsharedServiceModule', ['cmsitemsRestServiceModule', 'pageContentSlotsServiceModule'])
    .service('slotUnsharedService', function($q, cmsitemsRestService, pageContentSlotsService) {

        var SLOT_UNSHARED_STATUS = 'OVERRIDE';

        /**
         * @ngdoc method
         * @name slotUnsharedServiceModule.slotUnsharedService#isSlotUnshared
         * @methodOf slotUnsharedServiceModule.slotUnsharedService
         *
         * @description
         * Checks if the slot is unshared and returns true in case slot is unshared and returns false if it is not. 
         * Based on this service method the slot unshared button is shown or hidden for a particular slotId.
         *
         * @param {String} slotId The uid of the slot
         */
        this.isSlotUnshared = function(slotId) {
            return pageContentSlotsService.getSlotStatus(slotId).then(function(slotStatus) {
                return slotStatus === SLOT_UNSHARED_STATUS;
            });
        };

        /**
         * @ngdoc method
         * @name slotUnsharedServiceModule.slotUnsharedService#revertToSharedSlot
         * @methodOf slotUnsharedServiceModule.slotUnsharedService
         *
         * @description
         * This method is used to revert an unshared slot to a shared slot.
         * This operation is immutable.
         *
         * @param {String} slotUuid The uuid of the slot
         */
        this.revertToSharedSlot = function(slotUuid) {
            return cmsitemsRestService.delete(slotUuid);
        };

    });
