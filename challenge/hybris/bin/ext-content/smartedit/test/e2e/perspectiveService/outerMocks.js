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
angular
    .module('e2eBackendMocks', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule', 'AuthorizationMockModule'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .run(
        function($httpBackend, languageService, parseQuery, I18N_RESOURCE_URI, ADMIN_AUTH_TOKEN, CMSMANAGER_AUTH_TOKEN) {
            function parse(type) {
                return typeof type === 'string' ? JSON.parse(type) : type;
            }
            $httpBackend.whenGET(/configuration/).respond(function(method, url, data, headers) {
                var hasConfigurations = parse(sessionStorage.getItem('HAS_CONFIGURATIONS')) !== false;
                if (hasConfigurations || headers.Authorization === 'bearer ' + ADMIN_AUTH_TOKEN.access_token || headers.Authorization === 'bearer ' + CMSMANAGER_AUTH_TOKEN.access_token) {
                    return [200, [{
                        "value": "\"thepreviewTicketURI\"",
                        "key": "previewTicketURI"
                    }, {
                        "value": "{\"smartEditLocation\":\"/test/e2e/perspectiveService/innerMocks.js\"}",
                        "key": "applications.BackendMockModule"
                    }, {
                        "value": "{\"smartEditLocation\":\"/test/e2e/perspectiveService/perspectiveServiceInnerApp.js\"}",
                        "key": "applications.perspectiveServiceInnerApp"
                    }, {
                        "value": "{\"smartEditContainerLocation\":\"/test/e2e/perspectiveService/perspectiveServiceOuterApp.js\"}",
                        "key": "applications.perspectiveServiceOuterApp"
                    }, {
                        "value": "\"somepath\"",
                        "key": "i18nAPIRoot"
                    }, {
                        "value": "{\"smartEditContainerLocation\":\"/test/e2e/utils/commonMockedModules/outerMocksForPermissions.js\"}",
                        "key": "applications.e2ePermissionsMocks"
                    }, {
                        "value": "[\"*\"]",
                        "key": "whiteListedStorefronts"
                    }]];
                } else {
                    return [401];
                }
            });

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    name: 'English',
                    required: true
                }]
            });

            $httpBackend
                .whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale())
                .respond({
                    "se.modal.administration.configuration.edit.title": "edit configuration",
                    "se.configurationform.actions.cancel": "cancel",
                    "se.configurationform.actions.submit": "submit",
                    "se.configurationform.actions.close": "close",
                    "se.actions.loadpreview": "load preview",
                    'se.unknown.request.error': 'Your request could not be processed! Please try again later!',
                    'se.some.label': 'Some Item'
                });

            $httpBackend.whenGET(/^\w+.*/).passThrough();
        });
angular.module('smarteditloader').requires.push('e2eBackendMocks');
angular.module('smarteditcontainer').requires.push('e2eBackendMocks');
