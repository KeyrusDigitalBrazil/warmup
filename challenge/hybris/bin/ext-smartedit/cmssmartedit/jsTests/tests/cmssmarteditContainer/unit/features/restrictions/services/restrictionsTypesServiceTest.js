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
describe('restrictionTypesService', function() {

    var restrictionTypesService;
    var restrictionTypesRestService, pageTypesRestrictionTypesService, languageService;
    var $q;
    var restrictionTypeMocks = new unit.mockData.restrictionTypes();

    var restrictionTypesCodesForContentPageType = ["CMSTimeRestriction", "CMSUserRestriction", "CMSUserGroupRestriction", "CMSUiExperienceRestriction"];

    var restrictionTypesForContentPageType = [{
        code: 'CMSTimeRestriction',
        name: {
            de: 'DAS blabla',
            en: 'Time Restriction'
        }
    }, {
        code: 'CMSUserRestriction',
        name: {
            en: 'User Restriction'
        }
    }];

    var catalogRestrictionType = {
        code: 'CMSCatalogRestriction',
        name: {
            en: 'Catalog Restriction'
        }
    };

    beforeEach(angular.mock.module('restrictionTypesServiceModule', function($provide) {
        languageService = jasmine.createSpyObj('languageService', ['getResolveLocaleIsoCode']);
        languageService.getResolveLocaleIsoCode.and.callFake(function() {
            return $q.when('en');
        });
        $provide.value('languageService', languageService);

        restrictionTypesRestService = jasmine.createSpyObj('restrictionTypesRestService', ['getRestrictionTypes']);
        restrictionTypesRestService.getRestrictionTypes.and.callFake(function() {
            return $q.when(restrictionTypeMocks.getMocks());
        });
        $provide.value('restrictionTypesRestService', restrictionTypesRestService);

        pageTypesRestrictionTypesService = jasmine.createSpyObj('pageTypesRestrictionTypesService', ['getRestrictionTypeCodesForPageType']);
        pageTypesRestrictionTypesService.getRestrictionTypeCodesForPageType.and.callFake(function() {
            return $q.when(restrictionTypesCodesForContentPageType);
        });
        $provide.value('pageTypesRestrictionTypesService', pageTypesRestrictionTypesService);
    }));

    beforeEach(inject(function(_$q_, _restrictionTypesService_) {
        $q = _$q_;
        restrictionTypesService = _restrictionTypesService_;
    }));

    // ------------------------------------------------------------------------------------------

    it('should return all restriction types', function() {
        expect(restrictionTypesService.getRestrictionTypes()).toBeResolvedWithData(restrictionTypeMocks.getMocks().restrictionTypes);
    });

    it('should cache the results and return cache if it exists', function() {
        var orig = restrictionTypesService.getRestrictionTypes();
        var second = restrictionTypesService.getRestrictionTypes();
        expect(restrictionTypesRestService.getRestrictionTypes.calls.count()).toBe(1);
        expect(orig).toBe(second);
    });

    it('should return restriction types for specific page type', function() {
        expect(restrictionTypesService.getRestrictionTypesByPageType("ContentPage")).toBeResolvedWithData(restrictionTypesForContentPageType);
    });

    it('should get a restriction type object for to given type code', function() {
        expect(restrictionTypesService.getRestrictionTypeForTypeCode('CMSCatalogRestriction')).toBeResolvedWithData(catalogRestrictionType);
    });


});
