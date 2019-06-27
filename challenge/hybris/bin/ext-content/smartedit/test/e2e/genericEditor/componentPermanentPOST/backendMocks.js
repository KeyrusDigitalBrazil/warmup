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

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "se.componentform.actions.cancel": "Cancel",
            "se.componentform.actions.submit": "Submit",
            "type.thesmarteditComponentType.description.name": "Description"
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/types\/thesmarteditComponentType/).respond(function() {
            var structure = {
                attributes: [{
                    cmsStructureType: "ShortString",
                    qualifier: "description",
                    i18nKey: 'type.thesmarteditComponentType.description.name',
                    localized: false
                }]
            };

            return [200, structure];
        });

        var component = {};
        var ticketId = 0;
        $httpBackend.whenPOST(/previewApi/).respond(function(method, url, data) {
            component = JSON.parse(data);
            ticketId++;
            return [200, {
                ticketId: ticketId
            }];
        });

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

        var medias = [{
            id: '1',
            code: 'contextualmenu_delete_off',
            description: 'contextualmenu_delete_off',
            altText: 'contextualmenu_delete_off alttext',
            realFileName: 'contextualmenu_delete_off.png',
            url: '/test/e2e/genericEditor/images/contextualmenu_delete_off.png'
        }, {
            id: '2',
            code: 'contextualmenu_delete_on',
            description: 'contextualmenu_delete_on',
            altText: 'contextualmenu_delete_on alttext',
            realFileName: 'contextualmenu_delete_on.png',
            url: '/test/e2e/genericEditor/images/contextualmenu_delete_on.png'
        }, {
            id: '3',
            code: 'contextualmenu_edit_off',
            description: 'contextualmenu_edit_off',
            altText: 'contextualmenu_edit_off alttext',
            realFileName: 'contextualmenu_edit_off.png',
            url: '/test/e2e/genericEditor/images/contextualmenu_edit_off.png'
        }, {
            id: '3',
            code: 'contextualmenu_edit_on',
            description: 'contextualmenu_edit_on',
            altText: 'contextualmenu_edit_on alttext',
            realFileName: 'contextualmenu_edit_on.png',
            url: '/test/e2e/genericEditor/images/contextualmenu_edit_on.png'
        }];

        $httpBackend.whenGET(/cmswebservices\/v1\/catalogs\/electronics\/versions\/staged\/media\/(.+)/).respond(function(method, url) {
            var identifier = /media\/(.+)/.exec(url)[1];
            var filtered = medias.filter(function(media) {
                return media.code === identifier;
            });
            if (filtered.length === 1) {
                return [200, filtered[0]];
            } else {
                return [404];
            }
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/media/).respond(function(method, url) {

            var params = parseQuery(url).params;
            var search = params.split(",")[0].split(":").pop();
            var filtered = filterFilter(medias, search);
            return [200, {
                media: filtered
            }];
        });

        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/view/).passThrough(); //calls to storefront render API
        $httpBackend.whenPUT(/contentslots/).passThrough();
        $httpBackend.whenGET(/\.html/).passThrough();

    });
angular.module('genericEditorApp').requires.push('backendMocks');
