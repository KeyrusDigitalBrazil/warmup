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
angular.module('nestedComponentModule', ['cmsSmarteditServicesModule', 'smarteditServicesModule'])
    .constant('ON_EDIT_NESTED_COMPONENT_EVENT', 'ON_EDIT_NESTED_COMPONENT')
    .controller('nestedComponentController', function(ON_EDIT_NESTED_COMPONENT_EVENT, assetsService, systemEventService) {

        // ---------------------------------------------------------------
        // Constants
        // ---------------------------------------------------------------

        // ---------------------------------------------------------------
        // Variables
        // ---------------------------------------------------------------

        // ---------------------------------------------------------------
        // Lifecycle Methods
        // ---------------------------------------------------------------
        this.$onInit = function() {
            this.imageRoot = assetsService.getAssetsRoot();
        };

        this.onComponentClick = function() {
            if (this.isSelected) {
                systemEventService.publishAsync(ON_EDIT_NESTED_COMPONENT_EVENT, {
                    qualifier: this.qualifier,
                    item: this.item
                });
            }
        };
    })
    .component('nestedComponent', {
        controller: 'nestedComponentController',
        templateUrl: 'nestedComponentTemplate.html',
        bindings: {
            item: '<',
            qualifier: '<',
            isSelected: '<'
        }
    });
