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
 * @name slotSharedServiceModule.slotSharedService
 * @description
 * SlotSharedService provides methods to interact with the backend for shared slot information. 
 */
angular.module('slotSharedServiceModule', ['pageContentSlotsServiceModule', 'yLoDashModule', 'cmsitemsRestServiceModule'])
    .service('slotSharedService', function(pageContentSlotsService, lodash, cmsitemsRestService, componentHandlerService, $translate, confirmationModalService) {

        /**
         * @ngdoc method
         * @name slotSharedServiceModule.slotSharedService#isSlotShared
         * @methodOf slotSharedServiceModule.slotSharedService
         *
         * @description
         * Checks if the slot is shared and returns true in case slot is shared and returns false if it is not. 
         * Based on this service method the slot shared button is shown or hidden for a particular slotId
         *
         * @param {String} slotId of the slot
         */
        this.isSlotShared = function(slotId) {
            return pageContentSlotsService.getPageContentSlots().then(function(pageContentSlots) {
                var matchedSlotData = lodash.first(pageContentSlots.filter(function(pageContentSlot) {
                    return pageContentSlot.slotId === slotId;
                }));
                return matchedSlotData ? matchedSlotData.slotShared : undefined;
            });
        };

        /**
         * @ngdoc method
         * @name slotSharedServiceModule.slotSharedService#setSharedSlotEnablementStatus
         * @methodOf slotSharedServiceModule.slotSharedService
         *
         * @description
         * Sets the status of the disableSharedSlot feature
         *
         * @param {Boolean} status of the disableSharedSlot feature
         */
        this.setSharedSlotEnablementStatus = function(status) {
            this.disableShareSlotStatus = status;
        };

        /**
         * @ngdoc method
         * @name slotSharedServiceModule.slotSharedService#isSharedSlotDisabled
         * @methodOf slotSharedServiceModule.slotSharedService
         *
         * @description
         * Checks the status of the disableSharedSlot feature
         *
         */
        this.areSharedSlotsDisabled = function() {
            return this.disableShareSlotStatus;
        };

        /**
         * @ngdoc method
         * @name slotSharedServiceModule.slotSharedService#convertSharedSlotToNonSharedSlot
         * @methodOf slotSharedServiceModule.slotSharedService
         *
         * @description
         * Converts a shared slot to a non-shared slot.
         *
         * @param {Object} componentAttributes Component attributes context
         * @param {String} componentAttributes.smarteditComponentId SmartEdit componenent Id.
         * @param {String} componentAttributes.contentSlotUuid Unique identifier of the shared slot.
         * @param {String} componentAttributes.componentType Type of component.
         * @param {String} componentAttributes.catalogVersionUuid Catalog version.
         * @param {Boolean} cloneComponents If set to "true", the components in the shared slot will be cloned to the new non-shared slot.
         * If set to "false", the new non-shared slot will be empty.
         *
         * @returns {Promise} A promise that resolves to the data that defines the new component.
         *
         */
        this.convertSharedSlotToNonSharedSlot = function(componentAttributes, cloneComponents) {
            this._validateComponentAttributes(componentAttributes);
            return this._constructCmsItemParameter(componentAttributes, cloneComponents).then(function(cmsItem) {

                var message = {};
                message.title = "se.cms.slot.shared.convert.to.unshared.confirmation.title";
                message.description = cloneComponents ? "se.cms.slot.shared.convert.to.unshared.clone.components.confirmation.message" : "se.cms.slot.shared.convert.to.unshared.remove.components.confirmation.message";

                return confirmationModalService.confirm(message).then(function() {
                    return cmsitemsRestService.create(cmsItem);
                });

            });
        };

        this._constructCmsItemParameter = function(componentAttributes, cloneComponents) {
            return $translate("se.cms.slot.shared.clone").then(function(cloneText) {
                return componentHandlerService.getPageUID().then(function(pageUid) {
                    var componentName = pageUid + "-" + componentAttributes.smarteditComponentId + "-" + cloneText;
                    var cmsItem = {
                        name: componentName,
                        smarteditComponentId: componentAttributes.smarteditComponentId,
                        contentSlotUuid: componentAttributes.contentSlotUuid,
                        itemtype: componentAttributes.componentType,
                        catalogVersion: componentAttributes.catalogVersionUuid,
                        pageUuid: pageUid,
                        cloneComponents: cloneComponents
                    };
                    return cmsItem;
                });
            }.bind(this));
        };

        this._validateComponentAttributes = function(componentAttributes) {
            if (!componentAttributes) {
                throw new Error("Parameter: componentAttributes needs to be supplied!");
            }

            var validationAttributes = ["smarteditComponentId", "contentSlotUuid", "componentType", "catalogVersionUuid"];
            for (var i = 0; i < validationAttributes.length; i++) {
                var attribute = validationAttributes[i];
                if (!componentAttributes[attribute]) {
                    throw new Error("Parameter: componentAttributes." + attribute + " needs to be supplied!");
                }
            }
        };
    });
