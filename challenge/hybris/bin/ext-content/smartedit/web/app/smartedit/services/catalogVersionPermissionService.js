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
angular.module('catalogVersionPermissionModule', [
        'smarteditServicesModule',
        'functionsModule',
        'catalogVersionPermissionServiceInterfaceModule'
    ])
    .service('catalogVersionPermissionService', function(gatewayProxy, extend, CatalogVersionPermissionServiceInterface) {

        var CatalogVersionPermissionService = function(gatewayId) {
            this.gatewayId = gatewayId;
            gatewayProxy.initForService(this);
        };

        CatalogVersionPermissionService = extend(CatalogVersionPermissionServiceInterface, CatalogVersionPermissionService);

        return new CatalogVersionPermissionService("CatalogVersionPermissionServiceId");
    });
