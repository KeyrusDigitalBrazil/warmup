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
describe('pageListService', function() {

    var pageListService;
    var pagesRestService, $q;

    beforeEach(angular.mock.module(function($provide) {
        $provide.value("PAGE_CONTEXT_SITE_ID", "PAGE_CONTEXT_SITE_ID");
        $provide.value("PAGE_CONTEXT_CATALOG", "PAGE_CONTEXT_CATALOG");
        $provide.value("PAGE_CONTEXT_CATALOG_VERSION", "PAGE_CONTEXT_CATALOG_VERSION");
    }));

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('pageListServiceModule')
            .mock('pagesRestService', 'get')
            .mock('pagesRestService', 'getById')
            .service('pageListService');
        pageListService = harness.service;
        pagesRestService = harness.mocks.pagesRestService;
        $q = harness.injected.$q;
    });

    describe('getPageListForCatalog', function() {
        it('should return a rejected promise when the rest service fails', function() {
            pagesRestService.get.and.returnValue($q.reject());
            expect(pageListService.getPageListForCatalog('apparel-uk', 'apparel-ukContentCatalog', 'Online')).toBeRejected();
        });

        it('should return a set of pages for the catalog when the rest service succeeds', function() {
            pagesRestService.get.and.returnValue($q.when([{
                creationtime: '2016-04-08T21:16:41+0000',
                modifiedtime: '2016-04-08T21:16:41+0000',
                pk: '8796387968048',
                template: 'PageTemplate',
                title: 'page1TitleSuffix',
                typeCode: 'ContentPage',
                uid: 'somePageUid'
            }]));

            expect(pageListService.getPageListForCatalog('apparel-uk', 'apparel-ukContentCatalog', 'Online')).toBeResolvedWithData([{
                creationtime: '2016-04-08T21:16:41+0000',
                modifiedtime: '2016-04-08T21:16:41+0000',
                pk: '8796387968048',
                template: 'PageTemplate',
                title: 'page1TitleSuffix',
                typeCode: 'ContentPage',
                uid: 'somePageUid'
            }]);
        });
    });



    describe('getPageById', function() {
        it('should return a rejected promise when the rest services fails', function() {
            pagesRestService.getById.and.returnValue($q.reject());
            expect(pageListService.getPageById('somePageUid')).toBeRejected();
        });

        it('should resolve to a page when the rest service succeeds', function() {
            pagesRestService.getById.and.returnValue($q.when({
                creationtime: '2016-04-08T21:16:41+0000',
                modifiedtime: '2016-04-08T21:16:41+0000',
                pk: '8796387968048',
                template: 'PageTemplate',
                title: 'page1TitleSuffix',
                typeCode: 'ContentPage',
                uid: 'somePageUid'
            }));

            expect(pageListService.getPageById('somePageUid')).toBeResolvedWithData({
                creationtime: '2016-04-08T21:16:41+0000',
                modifiedtime: '2016-04-08T21:16:41+0000',
                pk: '8796387968048',
                template: 'PageTemplate',
                title: 'page1TitleSuffix',
                typeCode: 'ContentPage',
                uid: 'somePageUid'
            });
        });
    });
});
