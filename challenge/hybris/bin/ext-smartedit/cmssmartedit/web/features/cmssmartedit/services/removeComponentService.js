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
angular.module('removeComponentServiceModule', ['smarteditServicesModule', 'renderServiceModule', 'removeComponentServiceInterfaceModule', 'experienceInterceptorModule', 'functionsModule', 'resourceLocationsModule'])
    .constant('COMPONENT_REMOVED_EVENT', 'componentRemovedEvent')
    /**
     * @ngdoc service
     * @name removeComponentService.removeComponentService
     *
     * @description
     * Service to remove a component from a slot
     */
    .factory('removeComponentService', function(restServiceFactory, renderService, extend, gatewayProxy, $q, $log, RemoveComponentServiceInterface, experienceInterceptor, systemEventService, PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI, COMPONENT_REMOVED_EVENT) {
        var REMOVE_COMPONENT_CHANNEL_ID = "RemoveComponent";

        var RemoveComponentService = function(gatewayId) {
            this.gatewayId = gatewayId;

            gatewayProxy.initForService(this, ["removeComponent"]);
        };

        RemoveComponentService = extend(RemoveComponentServiceInterface, RemoveComponentService);

        var restServiceForRemoveComponent = restServiceFactory.get(PAGES_CONTENT_SLOT_COMPONENT_RESOURCE_URI + '/contentslots/:slotId/components/:componentId', 'componentId');

        RemoveComponentService.prototype.removeComponent = function(configuration) {

            return restServiceForRemoveComponent.remove({
                slotId: configuration.slotId,
                componentId: configuration.slotOperationRelatedId
            }).then(function() {
                renderService.renderSlots(configuration.slotId);
                // Send an event specifying that some component was removed.
                systemEventService.publishAsync(COMPONENT_REMOVED_EVENT);
            });

        };

        return new RemoveComponentService(REMOVE_COMPONENT_CHANNEL_ID);

    });
