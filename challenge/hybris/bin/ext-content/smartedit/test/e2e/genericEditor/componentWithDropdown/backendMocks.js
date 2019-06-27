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
            "se.genericeditor.sedropdown.placeholder": "Select an Option",
            "type.thesmarteditcomponenttype.dropdowna.name": "Dropdown A",
            "type.thesmarteditcomponenttype.dropdownb.name": "Dropdown B (depends on A)",
            "type.thesmarteditcomponenttype.dropdownc.name": "Dropdown C (depends on A)",
            "type.thesmarteditcomponenttype.dropdownd.name": "Dropdown D",
            "type.thesmarteditcomponenttype.dropdowne.name": "Dropdown E (depends on B)",
            "type.thesmarteditcomponenttype.dropdownf.name": "Dropdown F (strategy)",
        });


        $httpBackend.whenGET(/cmswebservices\/v1\/types\/thesmarteditComponentType/).respond(function() {
            var structure = {
                attributes: [{
                    cmsStructureType: "EditableDropdown",
                    qualifier: "dropdownA",
                    i18nKey: 'type.thesmarteditComponentType.dropdownA.name',
                    uri: '/sampleOptionsAPI0',
                    paged: 'true'
                }, {
                    cmsStructureType: "EditableDropdown",
                    qualifier: "dropdownB",
                    i18nKey: 'type.thesmarteditComponentType.dropdownB.name',
                    uri: '/sampleOptionsAPI1',
                    dependsOn: 'dropdownA'
                }, {
                    cmsStructureType: "EditableDropdown",
                    qualifier: "dropdownC",
                    i18nKey: 'type.thesmarteditComponentType.dropdownC.name',
                    uri: '/sampleOptionsAPI2',
                    dependsOn: 'dropdownA',
                    collection: true
                }, {
                    cmsStructureType: "EditableDropdown",
                    qualifier: "dropdownD",
                    i18nKey: 'type.thesmarteditComponentType.dropdownD.name',
                    options: [{
                        id: '1',
                        label: 'OptionD1-sample',
                    }, {
                        id: '2',
                        label: 'OptionD2-sample-element',
                    }, {
                        id: '3',
                        label: 'OptionD3-element',
                    }],
                    collection: true
                }, {
                    cmsStructureType: "EditableDropdown",
                    qualifier: "dropdownE",
                    i18nKey: 'type.thesmarteditComponentType.dropdownE.name',
                    uri: '/sampleOptionsAPI3',
                    dependsOn: 'dropdownB'
                }]
            };

            return [200, structure];
        });

        var sampleOptionsAPI0_response = [{
            id: '1',
            label: 'OptionA1',
        }, {
            id: '2',
            label: 'OptionA2',
        }, {
            id: '3',
            label: 'OptionA3',
        }];

        $httpBackend.whenGET(/sampleOptionsAPI0\/(.+)/).respond(function(method, url) {

            var id = /sampleOptionsAPI0\/(.+)/.exec(url)[1];
            var item = sampleOptionsAPI0_response.find(function(option) {
                return option.id === id;
            });

            return [200, item];
        });


        $httpBackend.whenGET(/sampleOptionsAPI0/).respond(function(method, url) {

            var query = parseQuery(url);
            var mask = query.mask;
            //paging ignored in mock: just one page
            //query.pageSize;
            //query.currentPage;

            var options = sampleOptionsAPI0_response.filter(function(option) {
                return !mask || option.label.toLowerCase().indexOf(mask.toLowerCase()) >= 0;
            }) || [];
            var pagedResults = {
                "pagination": {
                    "count": options.length,
                    "page": query.currentPage,
                    "totalCount": options.length,
                    "totalPages": 1
                },
                "products": options
            };
            return [200, pagedResults];
        });

        var sampleOptionsAPI1_response = [{
            id: '1',
            parent: '1',
            label: 'OptionB1-A1',
        }, {
            id: '2',
            parent: '1',
            label: 'OptionB2-A1',
        }, {
            id: '7',
            parent: '1',
            label: 'OptionB7-A1-A2',
        }, {
            id: '7',
            parent: '2',
            label: 'OptionB7-A1-A2',
        }, {
            id: '3',
            parent: '2',
            label: 'OptionB3-A2',
        }, {
            id: '4',
            parent: '2',
            label: 'OptionB4-A2',
        }, {
            id: '5',
            parent: '3',
            label: 'OptionB5-A3',
        }, {
            id: '6',
            parent: '3',
            label: 'OptionB6-A3',
        }];

        $httpBackend.whenGET(/sampleOptionsAPI1/).respond(function(method, url) {

            var query = parseQuery(url);
            var filterId = query.dropdownA;

            var response = {};
            response.options = sampleOptionsAPI1_response.filter(function(option) {
                return option.parent === filterId;
            }) || [];

            return [200, response];
        });

        var sampleOptionsAPI2_response = [{
            id: '1',
            parent: '1',
            label: 'OptionC1-A1',
        }, {
            id: '2',
            parent: '1',
            label: 'OptionC2-A1',
        }, {
            id: '3',
            parent: '2',
            label: 'OptionC3-A2',
        }, {
            id: '4',
            parent: '2',
            label: 'OptionC4-A2',
        }, {
            id: '5',
            parent: '3',
            label: 'OptionC5-A3',
        }, {
            id: '6',
            parent: '3',
            label: 'OptionC6-A3',
        }];

        $httpBackend.whenGET(/sampleOptionsAPI2/).respond(function(method, url) {

            var query = parseQuery(url);
            var filterId = query.dropdownA;

            var response = {};
            response.options = sampleOptionsAPI2_response.filter(function(option) {
                return option.parent === filterId;
            }) || [];

            return [200, response];
        });

        var sampleOptionsAPI3_response = [{
            id: '1',
            parent: '1',
            label: 'OptionE1-B1',
        }, {
            id: '2',
            parent: '2',
            label: 'OptionE2-B2',
        }, {
            id: '3',
            parent: '3',
            label: 'OptionE3-B3',
        }, {
            id: '4',
            parent: '4',
            label: 'OptionE4-B4',
        }, {
            id: '5',
            parent: '5',
            label: 'OptionE5-B5',
        }, {
            id: '6',
            parent: '6',
            label: 'OptionE6-B6',
        }, {
            id: '7',
            parent: '7',
            label: 'OptionE7-B7',
        }];

        $httpBackend.whenGET(/sampleOptionsAPI3/).respond(function(method, url) {

            var query = parseQuery(url);
            var filterId = query.dropdownB;

            var response = {};
            response.options = sampleOptionsAPI3_response.filter(function(option) {
                return option.parent === filterId;
            }) || [];

            return [200, response];
        });


        var component = {

            id: 'Component ID',
            headline: 'The Headline',
            dropdownA: '2',
            dropdownB: '7',
            dropdownC: ['3', '4'],
            dropdownD: ['2'],
            dropdownE: '1',
            dropdownF: '',
            active: true,
            content: 'the content to edit',
            create: new Date().getTime(),
            media: 'contextualmenu_delete_off',
            external: false
        };


        $httpBackend.whenGET(URL_FOR_ITEM).respond(component);
        $httpBackend.whenPUT(URL_FOR_ITEM).respond(function(method, url, data) {
            component = JSON.parse(data);
            return [200, component];
        });

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

        $httpBackend.whenGET(/i18n/).passThrough();
        $httpBackend.whenGET(/view/).passThrough(); //calls to storefront render API
        $httpBackend.whenPUT(/contentslots/).passThrough();
        $httpBackend.whenGET(/\.html/).passThrough();

    });
angular.module('genericEditorApp').requires.push('backendMocks');
