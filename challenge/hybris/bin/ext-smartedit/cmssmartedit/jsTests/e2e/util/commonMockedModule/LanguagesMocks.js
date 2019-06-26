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
angular.module('LanguagesMocks', ['ngMockE2E', 'resourceLocationsModule', 'functionsModule'])
    .run(function($httpBackend, resourceLocationToRegex, LANGUAGE_RESOURCE_URI, I18N_LANGUAGES_RESOURCE_URI) {
        $httpBackend.whenGET(resourceLocationToRegex(LANGUAGE_RESOURCE_URI)).respond(function(method, url) {
            return [200, {
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    name: 'English',
                    required: true
                }, {
                    nativeName: 'French',
                    isocode: 'fr',
                    required: false
                }, {
                    nativeName: 'Italian',
                    isocode: 'it'
                }, {
                    nativeName: 'Polish',
                    isocode: 'pl'
                }, {
                    nativeName: 'Hindi',
                    isocode: 'hi'
                }]
            }];
        });

        $httpBackend.whenGET(resourceLocationToRegex(I18N_LANGUAGES_RESOURCE_URI)).respond({
            languages: [{
                "isoCode": "en",
                "name": "English"
            }, {
                "isoCode": "fr",
                "name": "French"
            }]
        });

    });

try {
    angular.module('smarteditloader').requires.push('LanguagesMocks');
} catch (ex) {}
try {
    angular.module('smarteditcontainer').requires.push('LanguagesMocks');
} catch (ex) {}
