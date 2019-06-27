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
(function() {

    /**
     * This is an internal service, no ngdocs.
     *
     * The resizeComponentServiceModule contains a service that resizes slots and components in the inner frame.
     */
    angular.module('resizeComponentServiceModule', ['smarteditServicesModule'])

        /**
         * This service provides methods that resize slots when the overlay is enable or disabled.
         */
        .factory('resizeComponentService', function(componentHandlerService) {

            var ResizeComponentService = function() {

                /**
                 * This methods appends CSS classes to inner frame slots and components. Passing a boolean true to showResizing
                 * enables the resizing, and false vice versa.
                 */
                this._resizeComponents = function(showResizing) {

                    var slots = componentHandlerService.getFromSelector(componentHandlerService.getAllSlotsSelector());
                    var components = componentHandlerService.getFromSelector(componentHandlerService.getAllComponentsSelector());

                    if (showResizing) {
                        slots.addClass('ySEEmptySlot');
                        components.addClass('se-storefront-component');
                    } else {
                        slots.removeClass('ySEEmptySlot');
                        components.removeClass('se-storefront-component');
                    }
                };

            };

            return new ResizeComponentService();
        });

})();
