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
angular.module('e2eBackendMocks', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .run(function($httpBackend, languageService, $log, parseQuery, I18N_RESOURCE_URI) {

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond(function() {
            return [200, {
                "se.logindialogform.username.or.password.invalid": "Invalid username or password",
                "se.logindialogform.username.and.password.required": "Username and password required",
                "se.modal.administration.configuration.edit.title": "edit configuration",
                "se.configurationform.actions.cancel": "cancel",
                "se.configurationform.actions.submit": "submit",
                "se.configurationform.actions.close": "close",
                "se.actions.loadpreview": "load preview",
                'se.unknown.request.error': 'Your request could not be processed! Please try again later!',
                'se.authentication.form.input.username': 'username',
                'se.authentication.form.input.password': 'password',
                'se.authentication.form.button.submit': 'submit'
            }];
        });

        $httpBackend.whenGET(/cmswebservices\/sites\/.*\/languages/).respond({
            languages: [{
                nativeName: 'English',
                isocode: 'en',
                name: 'English',
                required: true
            }]
        });

        var oauthToken0 = {
            access_token: 'access-token0',
            token_type: 'bearer'
        };

        var oauthToken1 = {
            access_token: 'access-token1',
            token_type: 'bearer'
        };

        var oauthToken2 = {
            access_token: 'access-token2',
            token_type: 'bearer'
        };

        $httpBackend.whenPOST(/authorizationserver\/oauth\/token/).respond(function(method, url, data) {
            var query = parseQuery(data);
            if (query.client_id === 'smartedit' && query.client_secret === undefined && query.grant_type === 'password' && query.username === 'cmsmanager' && query.password === '1234') {
                return [200, oauthToken0];
            } else {
                return [401, {
                    error_description: 'Invalid username or password'
                }];
            }
        });
        $httpBackend.whenPOST(/authEntryPoint1/).respond(function(method, url, data) {
            var query = parseQuery(data);
            if (query.client_id === 'client_id_1' && query.client_secret === 'client_secret_1' && query.grant_type === 'password' && query.username === 'fake1' && query.password === '1234') {
                return [200, oauthToken1];
            } else {
                return [401];
            }
        });

        $httpBackend.whenPOST(/authEntryPoint2/).respond(function(method, url, data) {
            var query = parseQuery(data);
            if (query.client_id === 'client_id_2' && query.client_secret === 'client_secret_2' && query.grant_type === 'password' && query.username === 'fake2' && query.password === '1234') {
                return [200, oauthToken2];
            } else {
                return [401];
            }
        });

        $httpBackend.whenGET(/configuration/).respond(function(method, url, data, headers) {
            if (headers.Authorization === 'bearer ' + oauthToken0.access_token) {
                return [200, [{
                    "value": "[\"*\"]",
                    "key": "whiteListedStorefronts"
                }, {
                    "value": "\"/thepreviewTicketURI\"",
                    "key": "previewTicketURI"
                }, {
                    "value": "{\"/authEntryPoint1\":{\"client_id\":\"client_id_1\",\"client_secret\":\"client_secret_1\"},\"/authEntryPoint2\":{\"client_id\":\"client_id_2\",\"client_secret\":\"client_secret_2\"}}",
                    "key": "authentication.credentials"
                }, {
                    "value": "{\"smartEditLocation\":\"/test/e2e/apiAuthentication/mocksforAuthentication.js\"}",
                    "key": "applications.e2eBackendMocks"
                }, {
                    "value": "{\"smartEditLocation\":\"/test/e2e/apiAuthentication/dummyCmsDecorators.js\",\"authenticationMap\":{\"api1\":\"/authEntryPoint1\"}}",
                    "key": "applications.FakeModule"
                }, {
                    "value": "{\"smartEditContainerLocation\":\"/test/e2e/apiAuthentication/dummyContainerModule.js\"}",
                    "key": "applications.dummyContainer"
                }, {
                    "value": "{ \"api2\":\"/authEntryPoint2\"}",
                    "key": "authenticationMap"
                }]];
            } else {
                return [401];
            }

        });

        $httpBackend.whenGET(/api1\/somepath/).respond(function(method, url, data, headers) {
            if (headers.Authorization === 'bearer ' + oauthToken1.access_token) {
                return [200, {
                    status: 'OK'
                }];
            } else {
                return [401];
            }
        });

        $httpBackend.whenGET(/api2\/someotherpath/).respond(function(method, url, data, headers) {
            if (headers.Authorization === 'bearer ' + oauthToken2.access_token) {
                return [200, {
                    status: 'OK'
                }];
            } else {
                return [401];
            }
        });

        $httpBackend.whenGET(/fragments/).passThrough();

    });
try {
    angular.module('smarteditloader').requires.push('e2eBackendMocks');
} catch (e) {} //not longer there when smarteditcontainer is bootstrapped
try {
    angular.module('smarteditcontainer').requires.push('e2eBackendMocks');
} catch (e) {} //not there yet when smarteditloader is bootstrapped or in smartedit
