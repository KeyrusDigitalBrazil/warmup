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
    angular.module('renderServiceModule', ['smarteditServicesModule', 'renderServiceInterfaceModule'])
        .factory('renderService', function createRenderService($q, extend, gatewayProxy, RenderServiceInterface) {
            var RENDERER_CHANNEL_ID = "Renderer";

            var RenderService = function(gatewayId) {
                RenderServiceInterface.call(this);

                this.gatewayId = gatewayId;
                gatewayProxy.initForService(this, ["blockRendering", "isRenderingBlocked", "renderSlots", "renderComponent", "renderRemoval", "toggleOverlay", "refreshOverlayDimensions", "renderPage"]);
            };

            RenderService = extend(RenderServiceInterface, RenderService);

            RenderService.prototype.blockRendering = function(block) {
                this.RenderingBlocked = block;
            };

            RenderService.prototype.isRenderingBlocked = function() {
                return $q.when(this.RenderingBlocked || false);
            };

            // Rest of the methods are delegated to the SmartEdit implementation of the service.

            return new RenderService(RENDERER_CHANNEL_ID);
        });

})();
