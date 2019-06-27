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
    .run(function($httpBackend, languageService, $log, parseQuery, I18N_RESOURCE_URI, I18N_LANGUAGES_RESOURCE_URI) {

        $httpBackend.whenGET(I18N_LANGUAGES_RESOURCE_URI).respond({
            languages: [{
                "isoCode": "en",
                "name": "English"
            }, {
                "isoCode": "fr",
                "name": "French"
            }]
        });

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/en").respond(function() {
            return [200, {
                'se.authentication.form.input.username': 'Name',
                'se.authentication.form.input.password': 'Password',
                'se.authentication.form.button.submit': 'Submit',
                'se.perspective.none.name': 'Preview',
                'localization.field': 'I am a localized link',
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

        //        var regex = new RegExp(I18N_RESOURCE_URI + "/(.*)");
        $httpBackend.whenGET(I18N_RESOURCE_URI + "/kl").respond(function() {
            return [200, {
                'se.authentication.form.input.username': 'klName',
                'se.authentication.form.input.password': 'klPassword',
                'se.authentication.form.button.submit': 'Submit',
                'se.perspective.none.name': 'Preview',
                'localization.field': 'I am a localized link',
                'left.toolbar.cmsuser.name': 'CM User',
                'se.left.toolbar.sign.out': 'Sign Out',
                'experience.selector.language': 'Language',
                'experience.selector.date.and.time': 'Date and Time',
                'experience.selector.catalog': 'Catalog'
            }];
        });

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/en_US").respond(function() {
            return [200, {
                'se.authentication.form.input.username': 'en_USName',
                'se.authentication.form.input.password': 'en_USPass',
                'se.authentication.form.button.submit': 'dfa',
                'se.perspective.none.name': 'adsa',
                'localization.field': 'I am a localizesdasd link',
                'left.toolbar.cmsuser.name': 'CM User',
                'se.left.toolbar.sign.out': 'Sign Out',
                'experience.selector.language': 'Language',
                'experience.selector.date.and.time': 'Date and Time',
                'experience.selector.catalog': 'Catalog'
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

        $httpBackend.whenPOST(/authorizationserver\/oauth\/token/).respond(function(method, url, data) {
            var query = parseQuery(data);
            if (query.client_id === 'smartedit' && query.client_secret === undefined && query.grant_type === 'password' && query.username === 'customermanager' && query.password === '123') {
                return [200, oauthToken0];
            } else {
                return [401];
            }
        });

        var allSites = [{
            previewUrl: '/test/utils/storefront.html',
            name: {
                en: "Electronics"
            },
            redirectUrl: 'redirecturlElectronics',
            uid: 'electronics',
            contentCatalogs: ['electronicsContentCatalog']
        }, {
            previewUrl: '/test/utils/storefront.html',
            name: {
                en: "Apparels"
            },
            redirectUrl: 'redirecturlApparels',
            uid: 'apparel-uk',
            contentCatalogs: ['apparel-ukContentCatalog']
        }];

        $httpBackend.whenGET(/cmswebservices\/v1\/sites$/).respond(function(method, url, data, headers) {
            if (headers.Authorization === 'bearer ' + oauthToken0.access_token) {
                return [200, {
                    sites: allSites
                }];
            } else {
                return [401];
            }
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\?catalogIds=.*/).respond(function(method, url, data, headers) {
            var params = parseQuery(url);
            var catalogIds = params.catalogIds && params.catalogIds.split(',');

            if (headers.Authorization === 'bearer ' + oauthToken0.access_token && catalogIds) {
                var filteredItems = allSites.filter(function(site) {
                    return catalogIds.indexOf(site.contentCatalogs[site.contentCatalogs.length - 1]) > -1;
                });

                return [200, {
                    sites: filteredItems
                }];
            } else {
                return [401];
            }

        });

        var map = [{
            "value": "\"thepreviewTicketURI\"",
            "key": "previewTicketURI"
        }, {
            "value": "{\"smartEditLocation\":\"/test/e2e/languageSelector/innerMocks.js\"}",
            "key": "applications.BackendMockModule"
        }, {
            "value": "\"somepath\"",
            "key": "i18nAPIRoot"
        }, {
            "value": "{\"smartEditContainerLocation\":\"/test/e2e/utils/commonMockedModules/outerMocksForPermissions.js\"}",
            "key": "applications.e2ePermissionsMocks"
        }, {
            "value": "[\"*\"]",
            "key": "whiteListedStorefronts"
        }, {
            "value": "{\"smartEditLocation\":\"/test/e2e/languageSelector/dummyFakeModuleDecorators.js\"}",
            "key": "applications.FakeModule"
        }];

        $httpBackend.whenGET(/\/configuration/).respond(
            function(method, url, data, headers) {
                if (headers.Authorization === 'bearer ' + oauthToken0.access_token) {
                    return [200, map];
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
