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
angular.module("componentVisibilityAlertServiceModule", [
        "componentVisibilityAlertServiceInterfaceModule"
    ])

    .factory('componentVisibilityAlertService', function(
        ComponentVisibilityAlertServiceInterface,
        extend,
        gatewayProxy
    ) {

        var ComponentVisibilityAlertService = function() {
            this.gatewayId = 'ComponentVisibilityAlertService';
            gatewayProxy.initForService(this, ["checkAndAlertOnComponentVisibility"]);
        };

        ComponentVisibilityAlertService = extend(ComponentVisibilityAlertServiceInterface, ComponentVisibilityAlertService);

        return new ComponentVisibilityAlertService();

    });
