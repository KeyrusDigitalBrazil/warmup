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
describe('displayConditionsEditorModel', function() {

    var MOCK_PRIMARY_PAGE_INFO = {
        pageName: 'Some Page Name',
        pageType: 'somePageType',
        isPrimary: true
    };

    var MOCK_VARIATION_PAGE_INFO = {
        pageName: 'Some Page Name',
        pageType: 'somePageType',
        isPrimary: false
    };

    var MOCK_VARIATIONS = [{
        pageName: 'Some Variation Page Name',
        creationDate: new Date(),
        restrictions: 1
    }, {
        pageName: 'Some Other Variation Page Name',
        creationDate: new Date(),
        restrictions: 2
    }];

    var MOCK_PRIMARY_PAGE = {
        uid: 'somePrimaryPage',
        name: 'Some Primary Page',
        label: 'some-primary-page'
    };

    var ANOTHER_MOCK_PRIMARY_PAGE = {
        uid: 'someOtherPrimaryPage',
        name: 'Some Other Primary Page',
        label: 'some-other-primary-page'
    };

    var MOCK_PRIMARY_PAGES = [{
        uid: 'somePrimaryPage',
        name: 'Some Primary Page',
        label: 'some-primary-page'
    }, {
        uid: 'someOtherPrimaryPage',
        name: 'Some Other Primary Page',
        label: 'some-other-primary-page'
    }];

    var service;
    var mocks;
    var $q, $rootScope;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('displayConditionsEditorModelModule')
            .mock('displayConditionsFacade', 'getVariationsForPageUid')
            .mock('displayConditionsFacade', 'getPrimaryPageForVariationPage')
            .mock('displayConditionsFacade', 'getPrimaryPagesForVariationPageType')
            .mock('displayConditionsFacade', 'getPageInfoForPageUid')
            .mock('displayConditionsFacade', 'updatePage')
            .mock('systemEventService', 'publishAsync')
            .service('displayConditionsEditorModel');
        service = harness.service;
        mocks = harness.mocks;
        $q = harness.injected.$q;
        $rootScope = harness.injected.$rootScope;

    });

    describe('initModel', function() {
        beforeEach(function() {
            service._initModelForPrimary = jasmine.createSpy('_initModelForPrimary');
            service._initModelForVariation = jasmine.createSpy('_initModelForVariation');
        });

        it('should put the page name, page, type, and is primary values on the model scope', function() {
            mocks.displayConditionsFacade.getPageInfoForPageUid.and.returnValue($q.when(MOCK_PRIMARY_PAGE_INFO));
            service.initModel('somePageUid');
            $rootScope.$digest();

            expect(service.pageName).toBe('Some Page Name');
            expect(service.pageType).toBe('somePageType');
            expect(service.isPrimary).toBe(true);
        });

        it('should delegate to _initModelForPrimary if the page is primary', function() {
            mocks.displayConditionsFacade.getPageInfoForPageUid.and.returnValue($q.when(MOCK_PRIMARY_PAGE_INFO));
            service.initModel('somePageUid');
            $rootScope.$digest();

            expect(service._initModelForPrimary).toHaveBeenCalledWith('somePageUid');
        });

        it('should delegate to _initModelForVariation if the page is variation', function() {
            mocks.displayConditionsFacade.getPageInfoForPageUid.and.returnValue($q.when(MOCK_VARIATION_PAGE_INFO));
            service.initModel('somePageUid');
            $rootScope.$digest();

            expect(service._initModelForVariation).toHaveBeenCalledWith('somePageUid');
        });
    });

    describe('_initModelForPrimary', function() {
        beforeEach(function() {
            mocks.displayConditionsFacade.getVariationsForPageUid.and.returnValue($q.when(MOCK_VARIATIONS));
        });

        it('should put the variations on the model scope', function() {
            service._initModelForPrimary('somePageUid');
            $rootScope.$digest();

            expect(service.variations).toEqual(MOCK_VARIATIONS);
        });
    });

    describe('_initModelForVariation', function() {
        beforeEach(function() {
            mocks.displayConditionsFacade.getPrimaryPageForVariationPage.and.returnValue($q.when(MOCK_PRIMARY_PAGE));
            mocks.displayConditionsFacade.getPrimaryPagesForVariationPageType.and.returnValue($q.when(MOCK_PRIMARY_PAGES));
        });

        it('should put the associated primary page on the model scope', function() {
            service._initModelForVariation('somePageUid');
            $rootScope.$digest();

            expect(service.associatedPrimaryPage).toEqual(MOCK_PRIMARY_PAGE);
            expect(service.originalPrimaryPage).toEqual(MOCK_PRIMARY_PAGE);
        });

        it('should put the primary pages on the model scope if the page is a content page', function() {
            service.pageType = 'ContentPage';
            service._initModelForVariation('somePageUid');
            $rootScope.$digest();

            expect(service.primaryPages).toEqual(MOCK_PRIMARY_PAGES);
        });

        it('should not put the primary pages on the model scope if the page is a content page', function() {
            service.pageType = 'CategoryPage';
            service._initModelForVariation('somePageUid');
            $rootScope.$digest();

            expect(service.primaryPages).not.toBeDefined();
        });
    });

    describe('setAssociatedPrimaryPage', function() {
        it('should put the associated primary page on the scope', function() {
            service.setAssociatedPrimaryPage(MOCK_PRIMARY_PAGE);
            expect(service.associatedPrimaryPage).toEqual(MOCK_PRIMARY_PAGE);
        });
    });

    describe('save', function() {
        beforeEach(function() {
            service.isDirty = jasmine.createSpy('isDirty');
            service.pageUid = 'somePageUid';
            service.associatedPrimaryPage = {
                label: 'some-label-to-save'
            };
            mocks.displayConditionsFacade.updatePage.and.returnValue($q.when(true));
        });

        it('should delegate to the facade to update page if dirty', function() {
            service.isDirty.and.returnValue(true);
            expect(service.save()).toBeResolvedWithData(true);
            expect(mocks.displayConditionsFacade.updatePage).toHaveBeenCalledWith('somePageUid', {
                label: 'some-label-to-save'
            });
        });

        it('should not delegate to the facade if not dirty', function() {
            service.isDirty.and.returnValue(false);
            expect(service.save()).toBeResolvedWithData(true);
            expect(mocks.displayConditionsFacade.updatePage).not.toHaveBeenCalled();
        });
    });

    describe('isDirty', function() {
        it('should return false if there is no original primary page on the model', function() {
            service.originalPrimaryPage = undefined;
            service.associatedPrimaryPage = ANOTHER_MOCK_PRIMARY_PAGE;
            expect(service.isDirty()).toBe(false);
        });

        it('should return false if there is no associated primary page on the model', function() {
            service.originalPrimaryPage = MOCK_PRIMARY_PAGE;
            service.associatedPrimaryPage = undefined;
            expect(service.isDirty()).toBe(false);
        });

        it('should return false if the associated primary page and original primary page are the same', function() {
            service.originalPrimaryPage = MOCK_PRIMARY_PAGE;
            service.associatedPrimaryPage = MOCK_PRIMARY_PAGE;
            expect(service.isDirty()).toBe(false);
        });

        it('should return false if the associated primary page and original primary page are different', function() {
            service.originalPrimaryPage = MOCK_PRIMARY_PAGE;
            service.associatedPrimaryPage = ANOTHER_MOCK_PRIMARY_PAGE;
            expect(service.isDirty()).toBe(true);
        });
    });

});
