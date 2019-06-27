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
angular.module('authenticationModule', ['authenticationInterfaceModule', 'smarteditServicesModule'])

    .factory('authenticationService', function(gatewayProxy, AuthenticationServiceInterface, extend) {

        var AuthenticationService = function() {
            this.reauthInProgress = {};
            this.gatewayId = "authenticationService";
            gatewayProxy.initForService(this);
        };

        AuthenticationService = extend(AuthenticationServiceInterface, AuthenticationService);

        return new AuthenticationService();

    });
