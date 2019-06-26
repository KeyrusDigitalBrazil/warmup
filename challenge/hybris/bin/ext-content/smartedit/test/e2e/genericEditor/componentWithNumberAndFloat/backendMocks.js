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
            "se.componentform.actions.cancel": "Cancel",
            "se.componentform.actions.submit": "Submit",
            "se.componentform.actions.replaceImage": "Replace Image",
            "type.thesmarteditComponentType.id.name": "id",
            "type.thesmarteditComponentType.quantity.name": "Quantity",
            "type.thesmarteditComponentType.price.name": "Price"
        });


        $httpBackend.whenGET(/cmswebservices\/v1\/types\/thesmarteditComponentType/).respond(function() {
            var structure = {
                attributes: [{
                    cmsStructureType: "ShortString",
                    qualifier: "id",
                    i18nKey: 'type.thesmarteditComponentType.id.name'
                }, {
                    cmsStructureType: "Number",
                    qualifier: "quantity",
                    i18nKey: 'type.thesmarteditComponentType.quantity.name'
                }, {
                    cmsStructureType: "Float",
                    qualifier: "price",
                    i18nKey: 'type.thesmarteditComponentType.price.name'
                }]
            };

            return [200, structure];
        });

        var component = {

            id: 'Component ID',
            quantity: 10,
            price: 100.15
        };


        $httpBackend.whenGET(URL_FOR_ITEM).respond(component);
        $httpBackend.whenPUT(URL_FOR_ITEM).respond(function(method, url, data) {
            component = JSON.parse(data);
            return [200, component];
        });

        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/view/).passThrough(); //calls to storefront render API
        $httpBackend.whenPUT(/contentslots/).passThrough();
        $httpBackend.whenGET(/\.html/).passThrough();


        $httpBackend.whenGET(/cmswebservices\/v1\/sites\/.*\/languages/).respond({
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
            }]
        });

    });
angular.module('genericEditorApp').requires.push('backendMocks');
