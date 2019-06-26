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
angular.module('componentTypeModule', ['cmsSmarteditServicesModule'])
    .controller('componentTypeController', function(assetsService) {

        this.$onInit = function() {
            this.imageRoot = assetsService.getAssetsRoot();
        };

    })
    .component('componentType', {
        templateUrl: 'componentTypeTemplate.html',
        controller: 'componentTypeController',
        bindings: {
            typeInfo: '<'
        }
    });
