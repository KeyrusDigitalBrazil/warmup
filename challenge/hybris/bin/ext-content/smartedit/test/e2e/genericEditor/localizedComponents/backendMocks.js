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
    .constant('URL_FOR_ITEM', /cmswebservices\/v1\/catalogs\/someCatalogId\/versions\/someCatalogVersion\/items\/thesmarteditComponentId/)
    .run(function($httpBackend, filterFilter, parseQuery, URL_FOR_ITEM, I18N_RESOURCE_URI, languageService) {

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "se.componentform.actions.cancel": "Cancel",
            "se.componentform.actions.submit": "Submit",
            "type.thesmarteditComponentType.content.name": "Content"
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/types\/thesmarteditComponentType/).respond(function() {
            var structure = {
                attributes: [{
                    cmsStructureType: "RichText",
                    qualifier: "content",
                    i18nKey: 'type.thesmarteditComponentType.content.name',
                    localized: true
                }]
            };

            return [200, structure];
        });

        var component = {
            content: {
                'en': 'the content to edit',
                'pl': 'tresc edytowac',
                'it': 'il contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            }
        };

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/someSiteUid\/languages/).respond({
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
            }, {
                nativeName: 'Hindi',
                isocode: 'hi'
            }]
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/someOtherSiteUid\/languages/).respond({
            languages: [{
                nativeName: 'English',
                isocode: 'en',
                required: true
            }, {
                nativeName: 'German',
                isocode: 'de'
            }]
        });

        $httpBackend.whenGET(URL_FOR_ITEM).respond(component);
        $httpBackend.whenPUT(URL_FOR_ITEM).respond(function(method, url, data) {
            component = JSON.parse(data);
            return [200, component];
        });

        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/view/).passThrough(); //calls to storefront render API
        $httpBackend.whenPUT(/contentslots/).passThrough();
        $httpBackend.whenGET(/\.html/).passThrough();

        var userId = 'cmsmanager';

        $httpBackend.whenGET(/authorizationserver\/oauth\/whoami/).respond(function() {
            return [200, {
                displayName: "CMS Manager",
                uid: userId
            }];
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/users\/*/).respond(function(method, url) {
            var userUid = url.substring(url.lastIndexOf("/") + 1);

            return [200, {
                uid: userUid,
                readableLanguages: ["en", "it", "fr", "pl", 'hi', "de"],
                writeableLanguages: ["en", "it", "fr", "pl", 'hi', "de"]
            }];
        });

    });
angular.module('genericEditorApp').requires.push('backendMocks');
