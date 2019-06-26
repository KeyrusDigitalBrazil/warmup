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
    .run(function($httpBackend, languageService, I18N_RESOURCE_URI) {

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({});

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/languages/).respond({
            languages: [{
                nativeName: 'English',
                isocode: 'en',
                required: true
            }, {
                nativeName: 'Polish',
                isocode: 'pl'
            }, {
                nativeName: 'Italian',
                isocode: 'it'
            }]
        });

        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/web\/common\/services\/select\/.*\.html/).passThrough();
    });

angular.module('ySelectApp').requires.push('backendMocks');
