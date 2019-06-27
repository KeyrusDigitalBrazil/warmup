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
    .run(function($httpBackend, languageService, I18N_RESOURCE_URI, I18N_LANGUAGES_RESOURCE_URI) {

        $httpBackend.whenGET(I18N_LANGUAGES_RESOURCE_URI).respond({
            languages: [{
                "isoCode": "en",
                "name": "English"
            }, {
                "isoCode": "fr",
                "name": "French"
            }]
        });

        $httpBackend.whenGET(/ctxTemplate.html/).respond('<dummy></dummy>');

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/en").respond(function() {
            return [200, {
                'se.authentication.form.input.username': 'Name',
                'se.authentication.form.input.password': 'Password',
                'se.authentication.form.button.submit': 'Submit',
                'se.perspective.none.name': 'Preview',
                'localization.field': 'I am localized',
                'left.toolbar.cmsuser.name': 'CM User',
                'se.left.toolbar.sign.out': 'Sign Out',
                'experience.selector.language': 'Language',
                'experience.selector.date.and.time': 'Date and Time',
                'experience.selector.catalog': 'Catalog'
            }];
        });


        $httpBackend.whenGET(I18N_RESOURCE_URI + "/fr").respond(function() {
            return [200, {
                'se.authentication.form.input.username': 'Nom',
                'se.authentication.form.input.password': 'Mot de passe',
                'se.authentication.form.button.submit': 'Soumettre',
                'se.perspective.none.name': 'Aperçu',
                'localization.field': 'Je suis localisée',
                'left.toolbar.cmsuser.name': 'Utilisateur',
                'se.left.toolbar.sign.out': 'Deconnexion',
                'experience.selector.language': 'Langue',
                'experience.selector.date.and.time': 'Date et Heure',
                'experience.selector.catalog': 'Catalogue'
            }];
        });

        $httpBackend.whenGET(/^\w+.*/).passThrough();
    });
