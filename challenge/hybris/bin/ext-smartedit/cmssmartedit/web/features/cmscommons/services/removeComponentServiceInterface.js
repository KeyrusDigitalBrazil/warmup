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
 * @name removeComponentServiceInterfaceModule
 * @description
 * # The removeComponentServiceInterfaceModule
 *
 * Provides a service with the ability to remove a component from a slot.
 */
angular.module('removeComponentServiceInterfaceModule', [])
    /**
     * @ngdoc service
     * @name removeComponentServiceInterfaceModule.service:RemoveComponentServiceInterface
     * @description
     * Service interface specifying the contract used to remove a component from a slot.
     *
     * This class serves as an interface and should be extended, not instantiated.
     */
    .factory('RemoveComponentServiceInterface', function() {
        function RemoveComponentServiceInterface() {}

        /**
         * @ngdoc method
         * @name removeComponentServiceInterfaceModule.service:RemoveComponentServiceInterface#removeComponent
         * @methodOf removeComponentServiceInterfaceModule.service:RemoveComponentServiceInterface
         *
         * @description
         * Removes the component specified by the given ID from the component specified by the given ID.
         *
         * @param {String} slotId The ID of the slot from which to remove the component.
         * @param {String} componentId The ID of the component to remove from the slot.
         */
        RemoveComponentServiceInterface.prototype.removeComponent = function() {};

        return RemoveComponentServiceInterface;
    });
