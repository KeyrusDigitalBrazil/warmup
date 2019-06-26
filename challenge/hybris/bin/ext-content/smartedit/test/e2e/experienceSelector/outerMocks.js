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
angular
    .module('OuterMocks', ['ngMockE2E', 'resourceLocationsModule', 'smarteditServicesModule', 'genericEditorModule', 'recompileDomModule'])
    .constant('SMARTEDIT_ROOT', 'web/webroot')
    .constant('SMARTEDIT_RESOURCE_URI_REGEXP', /^(.*)\/test\/e2e/)
    .constant('STOREFRONT_URI', 'http://127.0.0.1:9000/test/utils/storefront.html')
    .factory('previewTicketDataService', function() {

        var currentPreviewTicket = 'defaultTicket';

        var _getCurrentPreviewTicket = function() {
            return currentPreviewTicket;
        };

        var _setCurrentPreviewTicket = function(previewTicket) {
            currentPreviewTicket = previewTicket;
        };

        return {
            getCurrentPreviewTicket: _getCurrentPreviewTicket,
            setCurrentPreviewTicket: _setCurrentPreviewTicket
        };

    })
    .run(
        function($httpBackend, languageService, previewTicketDataService, I18N_RESOURCE_URI, STOREFRONT_URI) {

            var map = [{
                "value": "\"previewwebservices/v1/preview\"",
                "key": "previewTicketURI"
            }, {
                "value": "{\"smartEditLocation\":\"/test/e2e/experienceSelector/innerMocks.js\"}",
                "key": "applications.InnerMocks"
            }, {
                "value": "[\"*\"]",
                "key": "whiteListedStorefronts"
            }];

            $httpBackend.whenGET(/configuration/).respond(
                function() {
                    return [200, map];
                });

            $httpBackend.whenPUT(/configuration/).respond(404);

            $httpBackend
                .whenGET(I18N_RESOURCE_URI + "/" + languageService.getBrowserLocale())
                .respond({
                    'experience.selector.catalog': 'CATALOG',
                    'experience.selector.date.and.time': 'DATE/TIME',
                    'experience.selector.language': 'LANGUAGE',
                    'experience.selector.newfield': 'NEW FIELD',
                    'experience.selector.catalogversions': 'PRODUCT CATALOGS',
                    'se.componentform.actions.cancel': 'CANCEL',
                    'se.componentform.actions.apply': 'APPLY',
                    'se.componentform.select.date': 'Select a Date and Time',
                    'se.genericeditor.sedropdown.placeholder': 'Select an Option',
                    'se.cms.component.confirmation.modal.cancel': 'Cancel',
                    'se.cms.component.confirmation.modal.done': 'Done',
                    'se.modal.product.catalog.configuration': 'Product Catalog Configuration'
                });

            $httpBackend.whenGET(/fragments/).passThrough();


            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/electronics\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'Polish',
                    isocode: 'pl',
                    required: true
                }, {
                    nativeName: 'Italian',
                    isocode: 'it'
                }]
            });

            $httpBackend.whenGET(/storefront\.html/).respond("<somehtml/>");

            $httpBackend.whenGET(/cmswebservices\/v1\/sites\/apparel-uk\/languages/).respond({
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    required: true
                }, {
                    nativeName: 'French',
                    isocode: 'fr'
                }]
            });

            $httpBackend.whenGET(/\/dummystorefrontOtherPage.html/).respond(function() {
                // Test if we already loaded the homepage of the initial experience with a valid ticket
                if (previewTicketDataService.getCurrentPreviewTicket() === 'validTicketId') {
                    previewTicketDataService.setCurrentPreviewTicket('');
                    return [404, null, {
                        'Content-type': 'text/html'
                    }];
                } else {
                    return [200];
                }
            });

            $httpBackend.whenPOST(/previewwebservices\/v1\/preview/).respond(function(method, url, data) {
                var postedData = JSON.parse(data);

                var contentCatalogObject = postedData.catalogVersions.find(function(catalogVersion) {
                    return catalogVersion.catalog.indexOf('ContentCatalog') > -1;
                });

                if (contentCatalogObject.catalog === 'electronicsContentCatalog' &&
                    contentCatalogObject.catalogVersion === 'Online' &&
                    postedData.language === 'it') {
                    return [400, {
                        errors: [{
                            message: 'CatalogVersion with catalogId \'electronicsContentCatalog\' and version \'Online\' not found!',
                            "type": "UnknownIdentifierError"
                        }]
                    }];
                }

                if (contentCatalogObject.catalog === 'electronicsContentCatalog' &&
                    contentCatalogObject.catalogVersion === 'Online' &&
                    // postedData.resourcePath === STOREFRONT_URI &&
                    postedData.language === 'pl') {
                    return ['200', {
                        catalog: 'electronicsContentCatalog',
                        catalogVersion: 'Online',
                        resourcePath: STOREFRONT_URI,
                        language: 'pl',
                        ticketId: 'validTicketId1'
                    }];
                }


                if (contentCatalogObject.catalog === 'apparel-ukContentCatalog' &&
                    contentCatalogObject.catalogVersion === 'Staged' &&
                    // postedData.resourcePath === STOREFRONT_URI &&
                    postedData.language === 'en') {
                    return ['200', {
                        catalog: 'apparel-ukContentCatalog',
                        catalogVersion: 'Staged',
                        resourcePath: STOREFRONT_URI,
                        language: 'en',
                        ticketId: 'apparel-ukContentCatalogStagedValidTicket'
                    }];
                }

                // We can not check hours and minutes  here because of the difference between developer's time zone
                // and the timezone of the pipeline.
                if (contentCatalogObject.catalog === 'apparel-ukContentCatalog' &&
                    contentCatalogObject.catalogVersion === 'Online' &&
                    // postedData.resourcePath === STOREFRONT_URI &&
                    postedData.time && postedData.time.indexOf('2016-01-01T') >= 0 &&
                    postedData.language === 'fr') {
                    return ['200', {
                        catalog: 'apparel-ukContentCatalog',
                        catalogVersion: 'Online',
                        resourcePath: STOREFRONT_URI,
                        language: 'fr',
                        time: '1/1/16 1:00 PM',
                        ticketId: 'apparel-ukContentCatalogOnlineValidTicket'
                    }];
                }

                if (contentCatalogObject.catalog === 'apparel-ukContentCatalog' &&
                    contentCatalogObject.catalogVersion === 'Online' &&
                    // postedData.resourcePath === STOREFRONT_URI &&
                    postedData.language === 'fr') {
                    return ['200', {
                        catalog: 'apparel-ukContentCatalog',
                        catalogVersion: 'Online',
                        resourcePath: STOREFRONT_URI,
                        language: 'fr',
                        ticketId: 'apparel-ukContentCatalogOnlineValidTicket'
                    }];
                }

                if (contentCatalogObject.catalog === 'electronicsContentCatalog' &&
                    contentCatalogObject.catalogVersion === 'Online' &&
                    // postedData.resourcePath === STOREFRONT_URI &&
                    postedData.time && postedData.time.indexOf('2016-01-01T13:00') >= 0 &&
                    postedData.language === 'pl') {
                    return ['200', {
                        catalog: 'electronicsContentCatalog',
                        catalogVersion: 'Online',
                        resourcePath: STOREFRONT_URI,
                        language: 'pl',
                        time: '1/1/16 1:00 PM',
                        ticketId: 'validTicketId2'
                    }];
                }


                if (contentCatalogObject.catalog === 'electronicsContentCatalog' &&
                    contentCatalogObject.catalogVersion === 'Staged' &&
                    postedData.newField === 'New Data For Preview' &&
                    // postedData.resourcePath === STOREFRONT_URI &&
                    postedData.time && postedData.time.indexOf('2016-01-01T00:00:00') >= 0 &&
                    postedData.language === 'it') {
                    return ['200', {
                        catalog: 'electronicsContentCatalog',
                        catalogVersion: 'Staged',
                        resourcePath: STOREFRONT_URI,
                        language: 'it',
                        newField: 'New Data For Preview',
                        time: '1/1/16 12:00 AM',
                        ticketId: 'validTicketId2'
                    }];
                }

                if (contentCatalogObject.catalog === 'electronicsContentCatalog' &&
                    contentCatalogObject.catalogVersion === 'Staged' &&
                    // postedData.resourcePath === STOREFRONT_URI &&
                    postedData.time && postedData.time.indexOf('2016-01-01T00:00:00') >= 0 &&
                    postedData.language === 'it') {
                    return ['200', {
                        catalog: 'electronicsContentCatalog',
                        catalogVersion: 'Staged',
                        resourcePath: STOREFRONT_URI,
                        language: 'it',
                        time: '1/1/16 12:00 AM',
                        ticketId: 'validTicketId2'
                    }];
                }

                if (previewTicketDataService.getCurrentPreviewTicket() !== '') {
                    previewTicketDataService.setCurrentPreviewTicket('validTicketId');
                }

                var returnedData = angular.extend({}, postedData, {
                    resourcePath: STOREFRONT_URI,
                    ticketId: 'validTicketId'
                });
                return [200, returnedData];
            });

        });
angular.module('smarteditloader').requires.push('OuterMocks');
angular.module('smarteditcontainer').requires.push('OuterMocks');
