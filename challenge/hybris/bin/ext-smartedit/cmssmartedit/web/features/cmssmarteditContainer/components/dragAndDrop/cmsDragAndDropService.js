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
angular.module('cmsDragAndDropServiceModule', [
        'yjqueryModule',
        'dragAndDropServiceModule'
    ])

    .constant('DRAG_AND_DROP_EVENTS', {
        DRAG_STARTED: 'CMS_DRAG_STARTED',
        DRAG_STOPPED: 'CMS_DRAG_STOPPED'
    })

    .constant('ENABLE_CLONE_ON_DROP', 'enableCloneComponentOnDrop')

    .service('cmsDragAndDropService', function(yjQuery, dragAndDropService, systemEventService, gatewayFactory, sharedDataService, ID_ATTRIBUTE, UUID_ATTRIBUTE, TYPE_ATTRIBUTE, DRAG_AND_DROP_EVENTS, ENABLE_CLONE_ON_DROP) {
        // Constants
        var CMS_DRAG_AND_DROP_ID = "se.cms.dragAndDrop";

        var TARGET_SELECTOR = "";
        var SOURCE_SELECTOR = ".smartEditComponent[data-smartedit-component-type!='ContentSlot']";

        var COMPONENT_SELECTOR = ".smartEditComponent";

        // Variables
        this._gateway = gatewayFactory.createGateway("cmsDragAndDrop");

        this.register = function() {
            dragAndDropService.register({
                id: CMS_DRAG_AND_DROP_ID,
                sourceSelector: SOURCE_SELECTOR,
                targetSelector: TARGET_SELECTOR,
                startCallback: this._onStart,
                stopCallback: this._onStop,
                enableScrolling: false
            });
        };

        this.unregister = function() {
            dragAndDropService.unregister([CMS_DRAG_AND_DROP_ID]);
        };

        this.apply = function() {
            dragAndDropService.apply();
        };

        this.update = function() {
            dragAndDropService.update(CMS_DRAG_AND_DROP_ID);
        };

        this._onStart = function(event) {

            sharedDataService.get(ENABLE_CLONE_ON_DROP).then(function(cloneOnDrop) {
                var component = this._getSelector(event.target).closest(COMPONENT_SELECTOR);

                var dragInfo = {
                    componentId: component.attr(ID_ATTRIBUTE),
                    componentUuid: component.attr(UUID_ATTRIBUTE),
                    componentType: component.attr(TYPE_ATTRIBUTE),
                    slotUuid: null,
                    slotId: null,
                    cloneOnDrop: cloneOnDrop
                };

                this._gateway.publish(DRAG_AND_DROP_EVENTS.DRAG_STARTED, dragInfo);
                systemEventService.publishAsync(DRAG_AND_DROP_EVENTS.DRAG_STARTED);
            }.bind(this));

        }.bind(this);

        this._onStop = function() {
            this._gateway.publish(DRAG_AND_DROP_EVENTS.DRAG_STOPPED);
        }.bind(this);

        this._getSelector = function(selector) {
            return yjQuery(selector);
        };

    });
