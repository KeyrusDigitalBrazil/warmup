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
describe('componentCloneInfoForm', function() {

    var MOCK_PAGES = [{
        uid: 'somePageUid',
        name: 'Some Page Name',
        typeCode: 'somePageTypeCode',
        label: 'somePageLabel'
    }];

    var mocks,
        resource,
        controller,
        $q,
        $rootScope,
        systemEventService;

    beforeEach(angular.mock.module('smarteditServicesModule', function($provide) {
        systemEventService = jasmine.createSpyObj('systemEventService', ['publishAsync']);
        $provide.value('systemEventService', systemEventService);
        $provide.constant('PAGE_CONTEXT_SITE_ID', 'PAGE_CONTEXT_SITE_ID');
        $provide.constant('PAGE_CONTEXT_CATALOG', 'PAGE_CONTEXT_CATALOG');
        $provide.constant('PAGE_CONTEXT_CATALOG_VERSION', 'PAGE_CONTEXT_CATALOG_VERSION');
        $provide.constant('GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT', 'GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT');
    }));

    beforeEach(function() {
        resource = jasmine.createSpyObj('resource', ['get']);

        var harness = AngularUnitTestHelper.prepareModule('componentCloneInfoFormModule')
            .mock('catalogService', 'isUriContextEqualToCatalogVersion').and.returnValue(resource)
            .mock('languageService', 'getLanguagesForSite').and.returnValue(resource)
            .mock('pageFacade', 'contentPageWithLabelExists').and.returnValue(resource)
            .mock('pageService', 'getPrimaryPagesForPageType').and.returnValue(resource)
            .controller('componentCloneInfoFormController');
        controller = harness.controller;

        controller.uriContext = {
            siteId: 'testSite',
            catalogId: 'testCatalog',
            version: 'testVersion'
        };

        controller.targetCatalogVersion = {
            siteId: 'otherTestSite',
            catalogId: 'otherTestCatalog',
            version: 'otherTestVersion'
        };

        controller.pageInfoEditorApi = {
            getContent: function() {
                return MOCK_PAGES[0];
            },
            clearMessages: function() {}
        };

        mocks = harness.mocks;
        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;
    });

    it('GIVEN a category page WHEN onInit is called and there exists a primary category page in the selected catalog version THEN it should expose a "catalogVersionContainsPageWithSameTypeCode" object', function() {
        mocks.pageService.getPrimaryPagesForPageType.and.returnValue($q.when(MOCK_PAGES));

        //GIVEN
        controller.pageTypeCode = 'CategoryPage';

        //WHEN
        controller.$onInit();
        $rootScope.$digest();

        //THEN
        expect(controller.catalogVersionContainsPageWithSameTypeCode).toBe(true);
    });

    it('GIVEN a content page WHEN onInit is called THEN it should not expose a "catalogVersionContainsPageWithSameTypeCode" object', function() {
        mocks.pageService.getPrimaryPagesForPageType.and.returnValue($q.when(MOCK_PAGES));

        //GIVEN
        controller.pageTypeCode = 'ContentPage';

        //WHEN
        controller.$onInit();
        $rootScope.$digest();

        //THEN
        expect(controller.catalogVersionContainsPageWithSameTypeCode).toBe(false);
    });

    it('GIVEN a content page WHEN the label field changes and a page exists in the selected catalog version with that label THEN it should trigger an editor validation event', function() {
        mocks.pageFacade.contentPageWithLabelExists.and.returnValue($q.when(true));

        //GIVEN
        controller.pageTypeCode = 'ContentPage';

        //WHEN
        controller.pageLabel = 'alreadyExists';
        controller.$doCheck();
        $rootScope.$digest();

        //THEN
        expect(systemEventService.publishAsync).toHaveBeenCalled();
    });

    it('GIVEN a content page WHEN the label field changes and a page does not exist in the selected catalog version with that label THEN it should clear all validation messages from the editor', function() {
        mocks.pageFacade.contentPageWithLabelExists.and.returnValue($q.when(false));
        spyOn(controller.pageInfoEditorApi, 'clearMessages').and.callThrough();

        //GIVEN
        controller.pageTypeCode = 'ContentPage';

        //WHEN
        controller.pageLabel = 'somethingElse';
        controller.$doCheck();
        $rootScope.$digest();

        //THEN
        expect(controller.pageInfoEditorApi.clearMessages).toHaveBeenCalled();
    });
});
