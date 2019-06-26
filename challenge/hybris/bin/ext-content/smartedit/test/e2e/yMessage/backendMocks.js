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
angular.module('backendMocks', ['ngMockE2E', 'functionsModule', 'resourceLocationsModule', 'smarteditServicesModule'])
    .run(function($httpBackend, filterFilter, parseQuery, I18N_RESOURCE_URI, languageService) {
        $httpBackend.when('GET', I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "test.title": "TEST TITLE",
            "test.description": "TEST DESCRIPTION"
        });
    });

angular.module('yMessageApp').requires.push('backendMocks');
