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
angular.module('userMocks', ['ngMockE2E', 'smarteditServicesModule'])
    .run(function($httpBackend) {

        var USER_ID = 'cmsmanager';
        var ALL_LANGUAGES = ['en', 'fr', 'it', 'pl', 'hi'];

        var rawConfig = window.sessionStorage.getItem("TEST_CONFIGS");
        var config = (rawConfig) ? JSON.parse(rawConfig) : {};

        $httpBackend.whenGET(/authorizationserver\/oauth\/whoami/).respond(function() {
            return [200, {
                displayName: "CMS Manager",
                uid: USER_ID
            }];
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/users\/*/).respond(function(method, url) {
            var userUid = url.substring(url.lastIndexOf("/") + 1);

            var readableLanguages = (config.readableLanguages) ?
                config.readableLanguages :
                ALL_LANGUAGES;
            var writeableLanguages = (config.writeableLanguages) ?
                config.writeableLanguages :
                ALL_LANGUAGES;

            return [200, {
                uid: userUid,
                readableLanguages: readableLanguages,
                writeableLanguages: writeableLanguages
            }];
        });
    });
angular.module('genericEditorApp').requires.push('userMocks');
