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
angular.module('ConfigurationsMockModule', ['ngMockE2E', 'resourceLocationsModule', 'functionsModule', 'AuthorizationMockModule'])
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .value('CONFIGURATION_MOCK', [{
        "value": "[\"*\"]",
        "key": "whiteListedStorefronts"
    }])
    .value('CONFIGURATION_AUTHORIZED', false)
    .value('CONFIGURATION_FORBIDDEN', false)
    .run(function($httpBackend, resourceLocationToRegex, CONFIGURATION_MOCK, CONFIGURATION_COLLECTION_URI, CONFIGURATION_URI,
        ADMIN_AUTH_TOKEN, CMSMANAGER_AUTH_TOKEN, CONFIGURATION_AUTHORIZED, CONFIGURATION_FORBIDDEN) {
        var CONFIGURATIONS = angular.copy(CONFIGURATION_MOCK);

        $httpBackend.whenGET(resourceLocationToRegex(CONFIGURATION_COLLECTION_URI)).respond(function(method, url, data, headers) {
            if (!CONFIGURATION_AUTHORIZED || headers.Authorization === 'bearer ' + ADMIN_AUTH_TOKEN.access_token || headers.Authorization === 'bearer ' + CMSMANAGER_AUTH_TOKEN.access_token) {
                return [200, CONFIGURATIONS];
            } else {
                return [401];
            }
        });

        $httpBackend.whenPUT(resourceLocationToRegex(CONFIGURATION_URI)).respond(function(method, url, data) {
            if (CONFIGURATION_FORBIDDEN) {
                return [403];
            }
            var key = getConfigurationKeyFromUrl(url);
            data = JSON.parse(data);
            var entry = CONFIGURATIONS.find(function(entry) {
                return entry.key === key;
            });
            entry.value = data.value;
            return [200, data];
        });

        $httpBackend.whenPOST(resourceLocationToRegex(CONFIGURATION_COLLECTION_URI)).respond(function(method, url, data) {
            if (CONFIGURATION_FORBIDDEN) {
                return [403];
            }
            data = JSON.parse(data);
            data.id = Math.random();
            CONFIGURATIONS.push(data);
            return [200, data];
        });

        $httpBackend.whenDELETE(resourceLocationToRegex(CONFIGURATION_URI)).respond(function(method, url) {
            if (CONFIGURATION_FORBIDDEN) {
                return [403];
            }
            var key = getConfigurationKeyFromUrl(url);
            CONFIGURATIONS = CONFIGURATIONS.filter(function(entry) {
                return entry.key !== key;
            });
            return [200];
        });

        function getConfigurationKeyFromUrl(url) {
            return /configuration\/(.+)/.exec(url)[1];
        }
    });

try {
    angular.module('smarteditloader').requires.push('ConfigurationsMockModule');
    angular.module('smarteditcontainer').requires.push('ConfigurationsMockModule');
} catch (ex) {}
