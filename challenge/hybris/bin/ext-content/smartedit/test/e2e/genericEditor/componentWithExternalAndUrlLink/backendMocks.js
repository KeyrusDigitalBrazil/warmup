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
    .constant('URL_FOR_ITEM', /cmswebservices\/v1\/catalogs\/electronics\/versions\/staged\/items\/thesmarteditComponentId/)
    .run(function($httpBackend, filterFilter, parseQuery, URL_FOR_ITEM, I18N_RESOURCE_URI, languageService) {

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "se.genericeditor.dropdown.placeholder": "Select an image",
            "se.componentform.actions.cancel": "Cancel",
            "se.componentform.actions.submit": "Submit",
            "se.componentform.actions.replaceImage": "Replace Image",
            "type.thesmarteditcomponenttype.id.name": "id",
            "type.thesmarteditcomponenttype.headline.name": "Headline",
            "type.thesmarteditcomponenttype.active.name": "Activation",
            "type.thesmarteditcomponenttype.enabled.name": "Enabled",
            "type.thesmarteditcomponenttype.content.name": "Content",
            "type.thesmarteditcomponenttype.created.name": "Creation date",
            "type.thesmarteditcomponenttype.media.name": "Media",
            "type.thesmarteditcomponenttype.orientation.name": "Orientation",
            "type.thesmarteditcomponenttype.external.name": "External Link",
            "type.thesmarteditcomponenttype.urlLink.name": "Url Link",
            "se.editor.linkto.label": "Link to",
            "se.editor.linkto.external.label": "External Link",
            "se.editor.linkto.internal.label": "Existing Page"
        });


        $httpBackend.whenGET(/cmswebservices\/v1\/types\/thesmarteditComponentType/).respond(function() {
            var structure = {
                attributes: [{
                    cmsStructureType: "ShortString",
                    qualifier: "id",
                    i18nKey: 'type.thesmarteditcomponenttype.id.name',
                    localized: false,
                    required: true
                }, {
                    cmsStructureType: "LongString",
                    qualifier: "headline",
                    i18nKey: 'type.thesmarteditcomponenttype.headline.name',
                    localized: false
                }, {
                    cmsStructureType: "Boolean",
                    qualifier: "active",
                    i18nKey: 'type.thesmarteditcomponenttype.active.name',
                    localized: false
                }, {
                    cmsStructureType: "Boolean",
                    qualifier: "enabled",
                    i18nKey: 'type.thesmarteditcomponenttype.enabled.name',
                    localized: false
                }, {
                    type: "Date",
                    qualifier: "created",
                    i18nKey: 'type.thesmarteditcomponenttype.created.name',
                    localized: false
                }, {
                    cmsStructureType: "RichText",
                    qualifier: "content",
                    i18nKey: 'type.thesmarteditcomponenttype.content.name',
                    localized: true,
                    required: true
                }, {
                    cmsStructureType: "Media",
                    qualifier: "media",
                    i18nKey: 'type.thesmarteditcomponenttype.media.name',
                    localized: true,
                    required: true
                }, {
                    cmsStructureType: "Enum",
                    cmsStructureEnumType: 'de.mypackage.Orientation',
                    qualifier: "orientation",
                    i18nKey: 'type.thesmarteditcomponenttype.orientation.name',
                    localized: false,
                    required: true
                }, {
                    cmsStructureType: "Boolean",
                    qualifier: "external",
                    i18nKey: 'type.thesmarteditcomponenttype.external.name',
                    localized: false
                }, {
                    cmsStructureType: "ShortString",
                    qualifier: "urlLink",
                    i18nKey: 'type.thesmarteditcomponenttype.urlLink.name',
                    localized: false
                }]
            };

            return [200, structure];
        });


        var component = {

            id: 'Component ID',
            headline: 'The Headline',
            active: true,
            content: {
                'en': 'the content to edit',
                'fr': 'le contenu a editer',
                'pl': 'tresc edytowac',
                'it': 'il contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            created: new Date().getTime(),
            enabled: false,
            media: {
                'en': 'contextualmenu_delete_off',
                'hi': 'contextualmenu_delete_on'
            },
            orientation: 'vertical',
            external: false,
            urlLink: "myPageUrl"

        };

        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/languages/).respond({
            languages: [{
                nativeName: 'English',
                isocode: 'en',
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
        });

        $httpBackend.whenGET(URL_FOR_ITEM).respond(component);
        $httpBackend.whenPUT(URL_FOR_ITEM).respond(function(method, url, data) {
            component = JSON.parse(data);
            return [200, component];
        });

        var medias = [{
            id: '1',
            code: 'contextualmenu_delete_off',
            description: 'contextual_menu_delete_off',
            altText: 'contextual_menu_delete_off alttext',
            realFileName: 'contextual_menu_delete_off.png',
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


        var orientationEnums = {
            enums: [{
                code: 'vertical',
                label: 'Vertical'
            }, {
                code: 'horizontal',
                label: 'Horizontal'
            }, ]
        };


        $httpBackend.whenGET(/cmswebservices\/v1\/enums/).respond(function(method, url) {
            var enumClass = parseQuery(url).enumClass;
            if (enumClass === 'de.mypackage.Orientation') {
                return [200, orientationEnums];
            } else {
                return [404];
            }
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
                readableLanguages: ["en", "it", "fr", "pl", 'hi'],
                writeableLanguages: ["en", "it", "fr", "pl", 'hi']
            }];
        });

    });
angular.module('genericEditorApp').requires.push('backendMocks');
