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
angular.module('slotSynchronizationPanelModule', ['synchronizationPanelModule', 'slotSynchronizationServiceModule'])
    .controller('slotSynchronizationPanelController', function(slotSynchronizationService, componentHandlerService) {

        this.getSyncStatus = function() {
            return componentHandlerService.getPageUID().then(function(pageId) {
                return slotSynchronizationService.getSyncStatus(pageId, this.slotId);
            }.bind(this));
        }.bind(this);

        this.performSync = function(array) {
            return slotSynchronizationService.performSync(array);
        };

    })
    .component('slotSynchronizationPanel', {
        templateUrl: 'slotSynchronizationPanelTemplate.html',
        controller: 'slotSynchronizationPanelController',
        controllerAs: 'slotSync',
        bindings: {
            slotId: '<'
        }
    });
