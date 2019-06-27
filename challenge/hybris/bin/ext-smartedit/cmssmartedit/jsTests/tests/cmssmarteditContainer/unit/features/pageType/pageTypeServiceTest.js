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
/* jshint unused:false, undef:false */
describe('pageTypeService - ', function() {
    'use strict';

    var $q, restServiceFactory, pageTypeService, pageTypeRestService, PAGE_TYPES_URI;

    beforeEach(angular.mock.module('resourceLocationsModule', function($provide) {
        PAGE_TYPES_URI = 'PAGE_TYPES_URI';
        $provide.value('PAGE_TYPES_URI', PAGE_TYPES_URI);
    }));

    beforeEach(angular.mock.module('smarteditServicesModule', function($provide) {
        pageTypeRestService = jasmine.createSpyObj('pageTypeRestService', ['get']);
        restServiceFactory = jasmine.createSpyObj('restServiceFactory', ['get']);
        restServiceFactory.get.and.returnValue(pageTypeRestService);
        $provide.value('restServiceFactory', restServiceFactory);
    }));

    beforeEach(angular.mock.module('pageTypeServiceModule'));

    beforeEach(inject(function(_$q_, _pageTypeService_) {
        $q = _$q_;
        pageTypeService = _pageTypeService_;
    }));

    beforeEach(function() {
        pageTypeRestService.get.and.returnValue($q.when({
            pageTypes: [{
                code: 'contentPageType',
                name: {
                    "en": 'Content Page - en',
                    "fr": 'Content Page - fr'
                },
                description: {
                    "en": 'Description for content page - en',
                    "fr": 'Description for content page - fr'
                }
            }, {
                code: 'productPageType',
                name: {
                    "en": 'Product Page - en',
                    "fr": 'Product Page - fr'
                },
                description: {
                    "en": 'Description for product page - en',
                    "fr": 'Description for product page - fr'
                }
            }]
        }));
    });

    it('GIVEN that the page types are request multiple times THEN all subsequent request should read from the cache', function() {
        pageTypeService.getPageTypes();
        pageTypeService.getPageTypes();
        expect(pageTypeRestService.get.calls.count()).toBe(1);
    });

    it('GIVEN that the page types are requested, a promise is returned containing an array of Page Type IDs', function() {
        var result = pageTypeService.getPageTypes();
        expect(result).toBeResolvedWithData(
            [{
                code: 'contentPageType',
                name: {
                    "en": 'Content Page - en',
                    "fr": 'Content Page - fr'
                },
                description: {
                    "en": 'Description for content page - en',
                    "fr": 'Description for content page - fr'
                }
            }, {
                code: 'productPageType',
                name: {
                    "en": 'Product Page - en',
                    "fr": 'Product Page - fr'
                },
                description: {
                    "en": 'Description for product page - en',
                    "fr": 'Description for product page - fr'
                }
            }]
        );
    });
});
