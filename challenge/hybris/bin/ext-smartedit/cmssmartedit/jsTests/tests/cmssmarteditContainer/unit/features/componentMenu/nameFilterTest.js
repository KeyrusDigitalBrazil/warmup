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
describe('filter test', function() {

    var $filter, cmsDragAndDropServiceModule;

    var object = [{
        name: 'Banner Component',
        code: 'BannerComponent'
    }, {
        name: 'Link Component',
        code: 'LinkComponent'
    }, {
        name: 'Test Component',
        code: 'TestComponent'
    }, {
        name: 'Simple Banner Component',
        code: 'SimpleBannerComponent'
    }, {
        name: 'Big Banner Component',
        code: 'BigBannerComponent'
    }];

    beforeEach(angular.mock.module('nameFilterModule'));

    beforeEach(inject(function(_$filter_) {
        $filter = _$filter_;
    }));

    it('name filter returns a couple results', function() {
        var criteria = 'Ban';
        var filterResult = [];

        filterResult = $filter('nameFilter')(object, criteria);
        expect(filterResult).not.toBeNull();
        expect(filterResult[0].name).toBe('Banner Component');
        expect(filterResult.length).toBe(3);
    });

    it('name filter returns no results', function() {
        var criteria = 'testing';
        var filterResult = [];

        filterResult = $filter('nameFilter')(object, criteria);
        expect(filterResult).not.toBeNull();
        expect(filterResult).toEqual([]);
        expect(filterResult.length).toBe(0);
    });

    it('name filter returns one result testing case sensitivity', function() {
        var criteria = 'simple';
        var filterResult = [];

        filterResult = $filter('nameFilter')(object, criteria);
        expect(filterResult[0].name).toBe('Simple Banner Component');
        expect(filterResult.length).toBe(1);
    });

    it('name filter returns multiple results based on OR principle', function() {
        var criteria = 'simple ban';
        var filterResult = [];

        filterResult = $filter('nameFilter')(object, criteria);
        expect(filterResult[0].name).toBe('Simple Banner Component');
        expect(filterResult.length).toBe(1);
    });

    it('criteria is less than length of 3', function() {
        var criteria = 'ba';
        var filterResult = [];

        filterResult = $filter('nameFilter')(object, criteria);
        expect(filterResult.length).toBe(5);
    });

    it('criteria is empty, filter returns original object', function() {
        var criteria = ' ';
        var filterResult = [];

        filterResult = $filter('nameFilter')(object, criteria);
        expect(filterResult.length).toBe(5);
    });

    it('criteria is null, filter returns original object', function() {
        var filterResult = [];
        var criteria = null;

        filterResult = $filter('nameFilter')(object, criteria);
        expect(filterResult.length).toBe(5);
    });

});
