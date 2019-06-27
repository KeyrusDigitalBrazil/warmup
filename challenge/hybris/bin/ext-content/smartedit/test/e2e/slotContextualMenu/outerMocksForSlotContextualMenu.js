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
    .module('e2eBackendMocks', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .run(function($httpBackend, languageService, I18N_RESOURCE_URI) {

        var map = [{
            "value": "\"thepreviewTicketURI\"",
            "key": "previewTicketURI"
        }, {
            "value": "{\"smartEditLocation\":\"/test/e2e/slotContextualMenu/innerMocksForSlotContextualMenu.js\"}",
            "key": "applications.BackendMockModule"
        }, {
            "value": "{\"smartEditLocation\":\"/test/e2e/slotContextualMenu/dummyFakeModuleDecorators.js\"}",
            "key": "applications.FakeModule"
        }, {
            "value": "\"somepath\"",
            "key": "i18nAPIRoot"
        }, {
            "value": "[\"*\"]",
            "key": "whiteListedStorefronts"
        }];

        $httpBackend.whenGET(/configuration/).respond(
            function() {
                return [200, map];
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
            .respond({});

    });
angular.module('smarteditloader').requires.push('e2eBackendMocks');
angular.module('smarteditcontainer').requires.push('e2eBackendMocks');
