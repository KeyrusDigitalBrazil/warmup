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
angular.module('languageMocks', ['ngMockE2E', 'smarteditServicesModule'])
    .run(function($httpBackend) {
        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/languages/).respond({
            languages: [{
                nativeName: 'English',
                isocode: 'en',
                required: true
            }, {
                nativeName: 'French',
                isocode: 'fr',
                required: true
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
        });
    });
angular.module('genericEditorApp').requires.push('languageMocks');
