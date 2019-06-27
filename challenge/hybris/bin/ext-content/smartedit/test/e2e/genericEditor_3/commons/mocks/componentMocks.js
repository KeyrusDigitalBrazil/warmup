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
angular.module('componentMocks', ['ngMockE2E'])
    .run(function($httpBackend) {

        var URL_FOR_ITEM = /cmswebservices\/v1\/catalogs\/electronics\/versions\/staged\/items\/thesmarteditComponentId/;
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
            description: "descr",
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
            description: "descr",
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
            description: "descr",
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
            description: "descr",
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
            description: "descr",
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
            description: "descr",
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
            description: "descr",
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
            description: "descr",
            external: false,
            urlLink: "/url-link",
            headline: "The Headline",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };

        var payload_admin_title_invalid = {
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
            description: "descr",
            external: false,
            urlLink: "/url-link",
            headline: "The Headline",
            id: "some wrong content X",
            identifier: "thesmarteditComponentId"
        };

        var payload_description_invalid = {
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
            description: "error description",
            external: false,
            urlLink: "/url-link",
            headline: "The Headline",
            id: "thesmarteditComponentId",
            identifier: "thesmarteditComponentId"
        };

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_description_invalid).respond(function() {
            return [400, validationErrors_Invalid_Description];
        });

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

        $httpBackend.whenPUT(URL_FOR_ITEM, payload_admin_title_invalid).respond(function() {
            return [400, validationErrors_Invalid_Id];
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

        var validationErrors_Invalid_Id = {
            "errors": [{
                "message": "This field cannot contain an X",
                "reason": "missing",
                "subject": "id",
                "subjectType": "parameter",
                "type": "ValidationError"
            }]
        };

        var validationErrors_Invalid_Description = {
            "errors": [{
                "message": "This field contains wrong text about ...",
                "reason": "missing",
                "subject": "description",
                "subjectType": "parameter",
                "type": "ValidationError"
            }]
        };

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
            description: "descr",
            external: false,
            urlLink: "/url-link"
        };

        $httpBackend.whenGET(URL_FOR_ITEM).respond(component);

        $httpBackend.whenGET(/cmswebservices\/v1\/types\/thesmarteditComponentType/).respond(function() {
            var structure = {
                attributes: [{
                        cmsStructureType: "ShortString",
                        qualifier: "id",
                        i18nKey: 'type.thesmarteditComponentType.id.name',
                        localized: false,
                        required: true
                    }, {
                        cmsStructureType: "Media",
                        qualifier: "media",
                        i18nKey: 'type.thesmarteditComponentType.media.name',
                        localized: true
                    },
                    {
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
                    }, {
                        cmsStructureType: "ShortString",
                        qualifier: "description",
                        i18nKey: 'type.thesmarteditComponentType.description.name',
                        localized: false,
                        required: true
                    }
                ]
            };

            return [200, structure];
        });
    });

angular.module('genericEditorApp').requires.push('componentMocks');
