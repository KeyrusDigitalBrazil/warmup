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
angular.module('BackendMockModule', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule'])
    .run(function($httpBackend, languageService, I18N_RESOURCE_URI) {
        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond(function() {
            var map = {};
            return [200, map];
        });

        $httpBackend.whenGET(/^\w+.*/).passThrough();
    });
