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
/* jshint unused:false, undef:false */
angular.module('backendMocksUtilsModule', [])
    .service('backendMocksUtils', function() {
        this._backendMocks = {};

        this.storeBackendMock = function(key, backendMock) {
            this._backendMocks[key] = backendMock;
        };

        this.getBackendMock = function(key) {
            return this._backendMocks[key];
        };
    });

try {
    angular.module('smarteditloader').requires.push('backendMocksUtilsModule');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('backendMocksUtilsModule');
} catch (e) {}
