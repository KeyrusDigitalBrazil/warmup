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
 * @name componentVisibilityAlertServiceInterfaceModule
 *
 * @description
 * This module defines the componentVisibilityAlertServiceInterfaceModule Angular component
 * and its associated service. It uses the cmsContextualAlertModule to display
 * a contextual alert whenever a component get either hidden or restricted.
 */
angular.module("componentVisibilityAlertServiceInterfaceModule", [])

    /**
     * @ngdoc service
     * @name componentVisibilityAlertServiceInterfaceModule.service:componentVisibilityAlertServiceInterface
     *
     * @description
     * The componentVisibilityAlertServiceInterface is used by external modules to check
     * on a component visibility and trigger the display of a contextual alert when
     * the component is either hidden or restricted.
     */
    .factory('ComponentVisibilityAlertServiceInterface', function() {

        var ComponentVisibilityAlertServiceInterface = function() {};

        /**
         * @ngdoc method
         * @name componentVisibilityAlertServiceInterfaceModule.service:componentVisibilityAlertServiceInterface#checkAndAlertOnComponentVisibility
         * @methodOf componentVisibilityAlertServiceInterfaceModule.service:componentVisibilityAlertServiceInterface
         *
         * @description
         * Method checks on a component visibility and triggering the display of a
         * contextual alert when the component is either hidden or restricted. This
         * method defines a custom Angular controller which will get passed to and
         * consumed by the contextual alert.
         *
         * @param {Object} component A JSON object containing the specific configuration to be applied on the actionableAlert.
         * @param {String} component.componentId Uuid of the cmsItem
         * @param {String} component.componentType Type of the cmsItem
         * @param {String} component.catalogVersion CatalogVersion uuid of the cmsItem
         * @param {String} component.restricted Boolean stating whether a restriction is applied to the cmsItem.
         * @param {String} component.slotId Id of the slot where the cmsItem was added or modified.
         * @param {String} component.visibility Boolean stating whether the cmsItem is rendered.
         */
        ComponentVisibilityAlertServiceInterface.prototype.checkAndAlertOnComponentVisibility = function() {};

        return ComponentVisibilityAlertServiceInterface;

    });
