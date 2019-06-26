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
angular.module('backendMocks', ['ngMockE2E', 'functionsModule', 'translationServiceModule', 'resourceLocationsModule', 'smarteditServicesModule'])
    .run(function($httpBackend, languageService, filterFilter, parseQuery, I18N_RESOURCE_URI) {

        var map = [{
            "value": "[\"*\"]",
            "key": "whiteListedStorefronts"
        }, {
            "value": "\"somepath\"",
            "key": "i18nAPIRoot"
        }];


        $httpBackend.whenGET(/configuration/).respond(function() {
            return [200, map];
        });

        $httpBackend.whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale()).respond({
            "tab1.name": "Tab 1",
            "tab2.name": "Tab 2",
            "tab3.name": "Tab 3",
            "tab4.name": "Tab 4",
            "editorTabset.basicTab.title": "BASIC INFO",
            "editorTabset.adminTab.title": "ADMIN",
            "editorTabset.visibilityTab.title": "VISIBILITY",
            "editorTabset.genericTab.title": "GENERIC",
            "type.Item.name.name": 'Name',
            "type.AbstractItem.creationtime.name": "Created",
            "type.AbstractItem.modifiedtime.name": "Modified",
            "type.AbstractCMSComponent.visible.name": "Component Visibility",
            "type.Item.uid.name": "ID",
            "type.AbstractItem.pk.name": "Key",
            "se.genericeditor.dropdown.placeholder": "Select an image",
            "se.componentform.actions.cancel": "Cancel",
            "se.componentform.actions.submit": "Submit",
            "se.componentform.actions.replaceImage": "Replace Image",
            "type.thesmarteditcomponenttype.id.name": "id",
            "type.thesmarteditcomponenttype.headline.name": "Headline",
            "type.thesmarteditcomponenttype.active.name": "Activation",
            "type.thesmarteditcomponenttype.activationDate.name": "Activation date",
            "type.thesmarteditcomponenttype.enabled.name": "Enabled",
            "type.thesmarteditcomponenttype.content.name": "Content",
            "type.thesmarteditcomponenttype.created.name": "Creation date",
            "type.thesmarteditcomponenttype.media.name": "Media",
            "type.thesmarteditcomponenttype.external.name": "External Link",
            "type.thesmarteditcomponenttype.urlLink.name": "Url Link",
            "se.editor.linkto.label": "Link to",
            "se.editor.linkto.external.label": "External Link",
            "se.editor.linkto.internal.label": "Existing Page"
        });


        $httpBackend.whenGET(/cmswebservices\/v1\/types\/thesmarteditComponentType/).respond(function() {
            var structure = {
                structureProperties: [{
                    type: "ShortText",
                    qualifier: "id",
                    i18nKey: 'type.thesmarteditcomponenttype.id.name'
                }, {
                    type: "LongText",
                    qualifier: "headline",
                    i18nKey: 'type.thesmarteditcomponenttype.headline.name'
                }, {
                    type: "Boolean",
                    qualifier: "active",
                    i18nKey: 'type.thesmarteditcomponenttype.active.name'
                }, {
                    type: "Date",
                    qualifier: "activationDate",
                    i18nKey: 'type.thesmarteditcomponenttype.activationDate.name'
                }, {
                    type: "RichText",
                    qualifier: "content",
                    i18nKey: 'type.thesmarteditcomponenttype.content.name'
                }, {
                    type: "Media",
                    qualifier: "media",
                    i18nKey: 'type.thesmarteditcomponenttype.media.name'
                }, {
                    type: "Boolean",
                    qualifier: "external",
                    i18nKey: 'thesmarteditComponentType_external'
                }, {
                    type: "ShortText",
                    qualifier: "urlLink",
                    i18nKey: 'thesmarteditComponentType_urlLink'
                }]
            };

            return [200, structure];
        });

        var component = {

            id: 'Component ID',
            pk: '123455667',
            uid: 'Comp_0006456345634',
            name: 'Custom Paragraph Component',
            headline: 'The Headline',
            active: true,
            content: 'the content to edit',
            activationDate: new Date().getTime(),
            creationtime: new Date().getTime(),
            modifiedtime: new Date().getTime(),
            media: '4',
            external: false,
            visible: true
        };

        $httpBackend.whenGET(/cmswebservices\/v1\/items\/thesmarteditComponentId/).respond(component);
        $httpBackend.whenPUT(/cmswebservices\/v1\/items\/thesmarteditComponentId/).respond(function(method, url, data) {
            component = JSON.parse(data);
            return [200, component];
        });

        var medias = [{
            id: '4',
            code: 'clone4',
            description: 'Clone background',
            altText: 'clone alttext',
            realFileName: 'clone_bckg.png',
            url: 'web/webroot/icons/clone_bckg.png'
        }];

        $httpBackend.whenGET(/cmswebservices\/cmsxdata\/contentcatalog\/staged\/Media\/(.+)/).respond(function(method, url) {
            var identifier = /Media\/(.+)/.exec(url)[1];
            console.info("get media", identifier);
            var filtered = medias.filter(function(media) {
                return media.id === identifier;
            });
            if (filtered.length === 1) {
                return [200, filtered[0]];
            } else {
                return [404];
            }
        });

        $httpBackend.whenGET(/cmswebservices\/cmsxdata\/contentcatalog\/staged\/Media/).respond(function(method, url) {
            console.info("query medias");
            var filtered = filterFilter(medias, parseQuery(url).search);
            return [200, {
                medias: filtered
            }];
        });

        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/view/).passThrough(); //calls to storefront render API
        $httpBackend.whenPUT(/contentslots/).passThrough();

    });
angular.module('tabsetApp').requires.push('backendMocks');
