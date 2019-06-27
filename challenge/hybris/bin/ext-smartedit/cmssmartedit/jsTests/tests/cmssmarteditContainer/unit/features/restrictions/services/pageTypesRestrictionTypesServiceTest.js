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
describe('pageTypesRestrictionTypesService', function() {

    var pageTypesRestrictionTypesService,
        pageTypesRestrictionTypesRestService;
    var $q;
    var pageTypesRestrictionTypesMocks = new unit.mockData.pageTypesRestrictionTypes();

    beforeEach(angular.mock.module('pageTypesRestrictionTypesServiceModule', function($provide) {

        pageTypesRestrictionTypesRestService = jasmine.createSpyObj('pageTypesRestrictionTypesRestService', ['getRestrictionTypeCodesForPageType', 'getPageTypesRestrictionTypes']);
        pageTypesRestrictionTypesRestService.getRestrictionTypeCodesForPageType.and.callFake(function() {
            return $q.when(pageTypesRestrictionTypesMocks.getTypeCodesForContentPageMocks());
        });
        pageTypesRestrictionTypesRestService.getPageTypesRestrictionTypes.and.callFake(function() {
            return $q.when(pageTypesRestrictionTypesMocks.getMocks());
        });
        $provide.value('pageTypesRestrictionTypesRestService', pageTypesRestrictionTypesRestService);
    }));

    beforeEach(inject(function(_$q_, _pageTypesRestrictionTypesService_) {
        $q = _$q_;
        pageTypesRestrictionTypesService = _pageTypesRestrictionTypesService_;
    }));

    // ------------------------------------------------------------------------------------------

    it('should return all pageTypesRestrictionTypes', function() {
        expect(pageTypesRestrictionTypesService.getPageTypesRestrictionTypes())
            .toBeResolvedWithData(pageTypesRestrictionTypesMocks.getMocks().pageTypeRestrictionTypeList);
    });

    it('should cache the results and return cache if it exists', function() {
        var orig = pageTypesRestrictionTypesService.getPageTypesRestrictionTypes();
        var second = pageTypesRestrictionTypesService.getPageTypesRestrictionTypes();
        expect(pageTypesRestrictionTypesRestService.getPageTypesRestrictionTypes.calls.count()).toBe(1);
        expect(orig).toBe(second);
    });

    it('should return page types restriction types for specific page type', function() {
        expect(pageTypesRestrictionTypesService.getRestrictionTypeCodesForPageType("ContentPage"))
            .toBeResolvedWithData(pageTypesRestrictionTypesMocks.getTypeCodesForContentPageMocks());
    });

});
