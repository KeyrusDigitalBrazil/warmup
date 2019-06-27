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

        var payload_headline_2_invalid = {
            visible: false,
            active: true,
            content: {
                'en': 'the content to edit',
                "fr": "le contenu a editer",
                'pl': 'tresc edytowac',
                'it': 'il contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            media: {
                'en': 'contextualmenu_delete_off'
            },
            external: false,
            urlLink: "/url-link",
            headline: "I have changed to an invalid headline with two validation errors, % and lots of text",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };

        var payload_headline_1_invalid = {
            visible: false,
            active: true,
            content: {
                'en': 'the content to edit',
                "fr": "le contenu a editer",
                'pl': 'tresc edytowac',
                'it': 'il contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            media: {
                'en': 'contextualmenu_delete_off'
            },
            external: false,
            urlLink: "/url-link",
            headline: "I have changed to an invalid headline with one validation error, %",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };

        var payload_urllink_invalid = {
            visible: false,
            "id": "thesmarteditComponentId",
            "headline": "The Headline",
            "active": true,
            "content": {
                "en": "the content to edit",
                "fr": "le contenu a editer",
                "pl": "tresc edytowac",
                "it": "il contenuto da modificare",
                "hi": "Sampaadit karanee kee liee saamagree"
            },
            "media": {
                "en": "contextualmenu_delete_off"
            },
            "external": false,
            "urlLink": "/url-link-invalid",
            "identifier": "thesmarteditComponentId"
        };

        var payload_all_valid = {
            visible: false,
            active: true,
            content: {
                'en': 'the content to edit',
                "fr": "le contenu a editer",
                'pl': 'tresc edytowac',
                'it': 'il contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            media: {
                'en': 'contextualmenu_delete_off'
            },
            external: false,
            urlLink: "/url-link",
            headline: "I have no errors",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };

        var payload_content_invalid = {
            visible: false,
            active: true,
            content: {
                'en': 'I have changed to an invalid content with one validation errorthe content to edit',
                "fr": "le contenu a editer",
                'pl': 'tresc edytowac',
                'it': 'Ho cambiato ad un contenuto non valido con un errore di validazioneil contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            media: {
                'en': 'contextualmenu_delete_off'
            },
            external: false,
            urlLink: "/url-link",
            headline: "The Headline",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };


        var payload_without_image = {
            visible: false,
            active: true,
            content: {
                'en': 'the content to edit',
                "fr": "le contenu a editer",
                'pl': 'tresc edytowac',
                'it': 'il contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            media: {},
            external: false,
            urlLink: "/url-link",
            headline: "The Headline",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };

        var payload_for_unknown_type = {
            visible: false,
            active: true,
            content: {
                'en': 'I have changed to an invalid content with one validation error',
                "fr": "le contenu a editer",
                'pl': 'tresc edytowac',
                'it': 'Ho cambiato ad un contenuto non valido con un errore di validazione',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            media: {
                'en': 'contextualmenu_delete_off'
            },
            external: false,
            urlLink: "/url-link",
            headline: "Checking unknown type",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };

        var payload_with_two_media_selected = {
            visible: false,
            active: true,
            content: {
                'en': 'the content to edit',
                "fr": "le contenu a editer",
                'pl': 'tresc edytowac',
                'it': 'il contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            media: {
                'en': 'contextualmenu_delete_off',
                'fr': 'contextual_menu_delete_on'
            },
            external: false,
            urlLink: "/url-link",
            headline: "The Headline",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_headline_2_invalid).respond(function() {
            return [400, validationErrors_Headline];
        });

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_urllink_invalid).respond(function() {
            return [400, validationErrors_UrlLink];
        });

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_content_invalid).respond(function() {
            return [400, validationErrors_content_2tab];
        });

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_headline_1_invalid).respond(function() {
            return [400, validationErrors_1];
        });

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_without_image).respond(function() {
            return [400, validationErrors_No_Media];
        });

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_for_unknown_type).respond(function() {
            return [400, validationErrors_Unknown_Type];
        });

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_all_valid).respond(function() {
            return [400, validationErrors_1];
        });

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_with_two_media_selected).respond(function() {
            return [200];
        });

        var validationErrors_Headline = {
            "errors": [{
                "message": "This field is required and must to be between 1 and 255 characters long.",
                "reason": "missing",
                "subject": "headline",
                "subjectType": "parameter",
                "type": "ValidationError"
            }, {
                "message": "This field cannot contain special characters",
                "reason": "missing",
                "subject": "headline",
                "subjectType": "parameter",
                "type": "ValidationError"
            }]
        };

        var validationErrors_UrlLink = {
            "errors": [{
                "message": "This field is required and must to be between 1 and 255 characters long.",
                "reason": "missing",
                "subject": "urlLink",
                "subjectType": "parameter",
                "type": "ValidationError"
            }]
        };

        var validationErrors_content_2tab = {
            "errors": [{
                "message": "This field is required and must to be between 1 and 255 characters long. Language: [en]",
                "reason": "missing",
                "subject": "content",
                "subjectType": "parameter",
                "type": "ValidationError"
            }, {
                "message": "This field is required and must to be between 1 and 255 characters long. Language: [it]",
                "reason": "missing",
                "subject": "content",
                "subjectType": "parameter",
                "type": "ValidationError"
            }]
        };

        var validationErrors_1 = {
            "errors": [{
                "message": "This field is required and must to be between 1 and 255 characters long.",
                "reason": "missing",
                "subject": "headline",
                "subjectType": "parameter",
                "type": "ValidationError"
            }]
        };

        var validationErrors_No_Media = {
            "errors": [{
                "message": "A Media must be selected. Language: [en]",
                "reason": "missing",
                "subject": "media",
                "subjectType": "parameter",
                "type": "ValidationError"
            }, {
                "message": "A Media must be selected. Language: [fr]",
                "reason": "missing",
                "subject": "media",
                "subjectType": "parameter",
                "type": "ValidationError"
            }]
        };

        var validationErrors_Unknown_Type = {
            "errors": [{
                "message": "The type is not valid",
                "reason": "missing",
                "subject": "unknownType",
                "subjectType": "parameter",
                "type": "ValidationError"
            }]
        };

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "se.genericeditor.dropdown.placeholder": "Search...",
            "se.componentform.actions.cancel": "Cancel",
            "se.componentform.actions.submit": "Submit",
            "se.componentform.actions.replaceImage": "Replace Image",
            "type.thesmarteditComponentType.id.name": "id",
            "type.thesmarteditComponentType.headline.name": "Headline",
            "type.thesmarteditComponentType.active.name": "Activation",
            "type.thesmarteditComponentType.content.name": "Content",
            "type.thesmarteditComponentType.create.name": "Creation date",
            "type.thesmarteditComponentType.media.name": "Media",
            "type.thesmarteditComponentType.external.name": "External Link",
            "type.thesmarteditComponentType.urlLink.name": "Url Link",
            "se.editor.linkto.label": "Link to",
            "se.editor.linkto.external.label": "External Link",
            "se.editor.linkto.internal.label": "Existing Page"
        });

        $httpBackend.whenGET(/cmswebservices\/v1\/types\/thesmarteditComponentType/).respond(function() {
            var structure = {
                attributes: [{
                        cmsStructureType: "ShortString",
                        qualifier: "id",
                        i18nKey: 'type.thesmarteditComponentType.id.name',
                        localized: false
                    }, {
                        cmsStructureType: "Media",
                        qualifier: "media",
                        i18nKey: 'type.thesmarteditComponentType.media.name',
                        localized: true
                    }, {
                        cmsStructureType: "Boolean",
                        qualifier: "visible",
                        i18nKey: 'type.thesmarteditComponentType.visible.name',
                        localized: false
                    },
                    {
                        cmsStructureType: "LongString",
                        qualifier: "headline",
                        i18nKey: 'type.thesmarteditComponentType.headline.name',
                        localized: false
                    }, {
                        cmsStructureType: "Boolean",
                        qualifier: "active",
                        i18nKey: 'type.thesmarteditComponentType.active.name',
                        localized: false
                    }, {
                        cmsStructureType: "RichText",
                        qualifier: "content",
                        i18nKey: 'type.thesmarteditComponentType.content.name',
                        localized: true
                    }
                ]
            };

            return [200, structure];
        });

        var component = {

            id: 'thesmarteditComponentId',
            headline: 'The Headline',
            active: true,
            content: {
                'en': 'the content to edit',
                'fr': 'le contenu a editer',
                'pl': 'tresc edytowac',
                'it': 'il contenuto da modificare',
                'hi': 'Sampaadit karanee kee liee saamagree'
            },
            media: {
                'en': 'contextualmenu_delete_off'
            },
            external: false,
            urlLink: "/url-link"
        };

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

        $httpBackend.whenGET(URL_FOR_ITEM).respond(component);

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
