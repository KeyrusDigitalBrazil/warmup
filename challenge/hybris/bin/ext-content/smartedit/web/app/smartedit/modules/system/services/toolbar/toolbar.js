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
angular.module('toolbarModule', ['smarteditServicesModule', 'toolbarInterfaceModule'])

    /**
     * @ngdoc service
     * @name toolbarModule.toolbarServiceFactory
     *
     * @description
     * The toolbar service factory generates instances of the {@link toolbarModule.ToolbarService ToolbarService} based on
     * the gateway ID (toolbar-name) provided. Only one ToolbarService instance exists for each gateway ID, that is, the
     * instance is a singleton with respect to the gateway ID.
     */
    .factory('toolbarServiceFactory', function($log, gatewayProxy, copy, extend, ToolbarServiceInterface) {

        /**
         * @ngdoc service
         * @name toolbarModule.ToolbarService
         * @constructor
         *
         * @description
         * The inner toolbar service is used to add toolbar actions that affect the inner application, publish aliases to
         * the outer application through the proxy, and store and manage callbacks made by private key. The service is a
         * managed singleton; the {@link toolbarModule.toolbarServiceFactory toolbarServiceFactory} provides a
         * getToolbarService function that returns a single instance of the ToolbarService for the gateway identifier,
         * that is, the toolbar-name provided by the outer toolbar.
         *
         * Uses {@link smarteditCommonsModule.service:GatewayProxy gatewayProxy} for cross iframe communication, using the toolbar name
         * as the gateway ID.
         *
         * <b>Inherited Methods from {@link toolbarInterfaceModule.ToolbarServiceInterface
         * ToolbarServiceInterface}</b>: {@link toolbarInterfaceModule.ToolbarServiceInterface#methods_addItems
         * addItems}
         *
         * @param {String} gatewayId Toolbar name used by the gateway proxy service.
         */
        var ToolbarService = function(gatewayId) {
            this.gatewayId = gatewayId;
            this.actions = {};

            gatewayProxy.initForService(this, ["addAliases", 'removeItemByKey', 'removeAliasByKey', "_removeItemOnInner", "triggerActionOnInner"]);
        };

        ToolbarService = extend(ToolbarServiceInterface, ToolbarService);

        ToolbarService.prototype._removeItemOnInner = function(itemKey) {
            if (itemKey in this.actions) {
                delete this.actions[itemKey];
            }

            $log.warn("removeItemByKey() - Failed to find action for key " + itemKey);
        };

        ToolbarService.prototype.triggerActionOnInner = function(action) {
            if (!this.actions[action.key]) {
                $log.error('triggerActionByKey() - Failed to find action for key ' + action.key);
                return;
            }
            this.actions[action.key]();
        };

        /////////////////////////////////////
        // Factory and Management
        /////////////////////////////////////
        var toolbarServicesByGatewayId = {};

        /**
         * @ngdoc method
         * @name toolbarModule.toolbarServiceFactory#getToolbarService
         * @methodOf toolbarModule.toolbarServiceFactory
         *
         * @description
         * Returns a single instance of the ToolbarService for the given gateway identifier. If one does not exist, an
         * instance is created and cached.
         *
         * @param {string} gatewayId The toolbar name used for cross iframe communication (see {@link
         * smarteditCommonsModule.service:GatewayProxy gatewayProxy})
         * @returns {ToolbarService} Corresponding ToolbarService instance for given gateway ID.
         */
        var getToolbarService = function(gatewayId) {
            if (!toolbarServicesByGatewayId[gatewayId]) {
                toolbarServicesByGatewayId[gatewayId] = new ToolbarService(gatewayId);
            }
            return toolbarServicesByGatewayId[gatewayId];
        };

        return {
            getToolbarService: getToolbarService
        };
    });
