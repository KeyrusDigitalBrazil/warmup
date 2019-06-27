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
describe('filterByFieldFilterModule', function() {

    var $filter;
    var keys = ["name", "template", "typeCode", "uid"];
    var object = [{
        name: "Add Edit Address Page",
        numberOfRestrictions: 0,
        onlyOneRestrictionMustApply: true,
        template: "AccountPageTemplate",
        typeCode: "CategoryPage",
        mockParam: "Mock",
        uid: "add-edit-address"
    }, {
        name: "Mobile Update Email Page",
        numberOfRestrictions: 1,
        onlyOneRestrictionMustApply: false,
        template: "MobileAccountPageTemplate",
        typeCode: "ContentPage",
        mockParam: "Mock",
        uid: "mobile-update-email"
    }, {
        name: "Frequently Asked Questions FAQ Page",
        numberOfRestrictions: 0,
        onlyOneRestrictionMustApply: true,
        template: "ContentPage1Template",
        typeCode: "CategoryPage",
        mockParam: "Mock",
        uid: "faq"
    }, {
        name: "Mobile Add Edit Address Page",
        numberOfRestrictions: 1,
        onlyOneRestrictionMustApply: false,
        template: "MobileAccountPageTemplate",
        typeCode: "ContentPage",
        mockParam: "Mock",
        uid: "mobile-add-edit-address"
    }, {
        name: "Mobile Address Book Page",
        numberOfRestrictions: 1,
        onlyOneRestrictionMustApply: false,
        template: "MobileAccountPageTemplate",
        typeCode: "ContentPage",
        mockParam: "Mock",
        uid: "mobile-address-book"
    }];


    beforeEach(angular.mock.module('filterByFieldFilterModule'));

    beforeEach(inject(function(_$filter_) {
        $filter = _$filter_;
    }));

    it('should return no search results when searching for a key that was not specified', function() {
        var criteria = "Mock";

        var filterResult = $filter('filterByField')(object, criteria, keys);
        expect(filterResult.length).toBe(0);
    });

    it('should return search results based on fields that only correspond to strings', function() {
        var criteria = "false";

        var filterResult = $filter('filterByField')(object, criteria, keys);
        expect(filterResult.length).toBe(0);
    });

    it('should return results based on keys (name)', function() {
        var criteria = "Add Edit";

        var filterResult = $filter('filterByField')(object, criteria, keys);
        expect(filterResult[0].name).toBe('Add Edit Address Page');
        expect(filterResult.length).toBe(2);
    });

    it('should return results based on keys (uid)', function() {
        var criteria = "faq";

        var filterResult = $filter('filterByField')(object, criteria, keys);
        expect(filterResult[0].uid).toBe('faq');
        expect(filterResult.length).toBe(1);
    });

    it('should not be case sensitive', function() {
        var criteria = "FREQUENTLY";

        var filterResult = $filter('filterByField')(object, criteria, keys);
        expect(filterResult[0].name).toBe('Frequently Asked Questions FAQ Page');
        expect(filterResult.length).toBe(1);
    });

    it('should return results regardless of search string order', function() {
        var criteria = "Mobile Add";

        var filterResult = $filter('filterByField')(object, criteria, keys);
        expect(filterResult.length).toBe(2);
    });

    it('should return results based on all the fields when no keys are provided', function() {
        var criteria = "Mock";

        var filterResult = $filter('filterByField')(object, criteria);
        expect(filterResult.length).toBe(5);
    });


    it('should return original object when search string is null', function() {
        var criteria = null;

        var filterResult = $filter('filterByField')(object, criteria, keys);
        expect(filterResult.length).toBe(5);
    });

    it('should call the callbackfcn when provided', function() {
        var criteria = "Mobile Add";
        var mockCallback = jasmine.createSpy("callback spy");

        $filter('filterByField')(object, criteria, keys, mockCallback);
        expect(mockCallback).toHaveBeenCalled();
    });

    it('should call the callbackfcn when the criteria is null', function() {
        var criteria = null;
        var mockCallback = jasmine.createSpy("callback spy");

        $filter('filterByField')(object, criteria, keys, mockCallback);
        expect(mockCallback).toHaveBeenCalled();
    });

});
