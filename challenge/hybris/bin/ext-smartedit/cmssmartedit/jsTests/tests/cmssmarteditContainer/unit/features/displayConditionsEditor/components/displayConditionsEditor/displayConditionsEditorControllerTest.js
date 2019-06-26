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
describe('displayConditionsEditorController', function() {

    var controller;
    var mocks;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('displayConditionsEditorModule')
            .mock('displayConditionsEditorModel', 'initModel')
            .mock('displayConditionsEditorModel', 'setAssociatedPrimaryPage')
            .mock('pageService', 'isPagePrimaryWithContext').and.returnResolvedPromise(true)
            .mock('pageService', 'buildUriContextForCurrentPage').and.returnResolvedPromise({
                CONTEXT_SITE_ID: 'CONTEXT_SITE_ID',
                CONTEXT_CATALOG: 'CONTEXT_CATALOG',
                CONTEXT_CATALOG_VERSION: 'CONTEXT_CATALOG_VERSION'
            })
            .mock('homepageService', 'hasFallbackHomePage').and.returnResolvedPromise(true)
            .mock('homepageService', 'getHomepageDetailsForContext').and.returnResolvedPromise(true)
            .mock('homepageService', 'sendEventHideReplaceParentHomePageInfo')
            .mock('homepageService', 'sendEventShowReplaceParentHomePageInfo')
            .controller('displayConditionsEditorController');
        controller = harness.controller;
        mocks = harness.mocks;

        controller.page = {
            uid: 'somePageUid'
        };

        mocks.displayConditionsEditorModel.pageName = 'somePageName';
        mocks.displayConditionsEditorModel.pageType = 'somePageType';
        mocks.displayConditionsEditorModel.isPrimary = true;
        mocks.displayConditionsEditorModel.variations = ['someVariation', 'someOtherVariation'];
        mocks.displayConditionsEditorModel.associatedPrimaryPage = 'someAssociatedPrimaryPage';
        mocks.displayConditionsEditorModel.isAssociatedPrimaryReadOnly = true;
        mocks.displayConditionsEditorModel.primaryPages = ['somePrimaryPage', 'someOtherPrimaryPage'];
    });

    it('$onInit will delegate to the model to initialize state', function() {
        controller.$onInit();
        expect(mocks.displayConditionsEditorModel.initModel).toHaveBeenCalledWith('somePageUid');
    });

    it('getPageName will return the pageName property from the model', function() {
        expect(controller.getPageName()).toBe('somePageName');
    });

    it('getPageType will return the pageType property from the model', function() {
        expect(controller.getPageType()).toBe('somePageType');
    });

    it('isPagePrimary will return the isPrimary property from the model', function() {
        expect(controller.isPagePrimary()).toBe(true);
    });

    it('getVariations will return the variations property from the model', function() {
        expect(controller.getVariations()).toEqual(['someVariation', 'someOtherVariation']);
    });

    it('getAssociatedPrimaryPage will return the associatedPrimaryPage property from the model', function() {
        expect(controller.getAssociatedPrimaryPage()).toBe('someAssociatedPrimaryPage');
    });

    it('getIsAssociatedPrimaryReadOnly will return the isAssociatedPrimaryReadOnly property from the model', function() {
        expect(controller.getIsAssociatedPrimaryReadOnly()).toBe(true);
    });

    it('getPrimaryPages will return the primaryPages property from the model', function() {
        expect(controller.getPrimaryPages()).toEqual(['somePrimaryPage', 'someOtherPrimaryPage']);
    });

    it('onPrimaryPageSelect will delegate to the model to set the new primary page', function() {
        controller.onPrimaryPageSelect({
            label: 'some-primary-page-label',
            name: 'Some Primary Page',
            uid: 'somePrimaryPage'
        });

        expect(controller.page.label).toEqual('some-primary-page-label');
    });

});
