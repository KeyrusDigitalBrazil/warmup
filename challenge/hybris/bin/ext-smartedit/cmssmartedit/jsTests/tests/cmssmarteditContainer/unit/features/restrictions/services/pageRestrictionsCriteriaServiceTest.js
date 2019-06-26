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
describe('pageRestrictionsCriteriaService', function() {

    var pageRestrictionsCriteriaService;

    beforeEach(angular.mock.module('pageRestrictionsCriteriaModule'));

    beforeEach(inject(function(_pageRestrictionsCriteriaService_) {
        pageRestrictionsCriteriaService = _pageRestrictionsCriteriaService_;
    }));

    // ------------------------------------------------------------------------------------------

    it('should return all criteria options', function() {
        expect(pageRestrictionsCriteriaService.getRestrictionCriteriaOptions().length).toBe(2);
        expect(pageRestrictionsCriteriaService.getRestrictionCriteriaOptions()[0].value).toBe(false);
        expect(pageRestrictionsCriteriaService.getRestrictionCriteriaOptions()[1].value).toBe(true);
    });

    it('should get the "All" restriction criteria from a given page object', function() {
        var fakePage = {
            onlyOneRestrictionMustApply: false
        };
        expect(pageRestrictionsCriteriaService.getRestrictionCriteriaOptionFromPage(fakePage))
            .toBe(pageRestrictionsCriteriaService.getRestrictionCriteriaOptions()[0]);
    });

    it('should get the "Any" restriction criteria from a given page object', function() {
        var fakePage = {
            onlyOneRestrictionMustApply: true
        };
        expect(pageRestrictionsCriteriaService.getRestrictionCriteriaOptionFromPage(fakePage))
            .toBe(pageRestrictionsCriteriaService.getRestrictionCriteriaOptions()[1]);
    });

});
