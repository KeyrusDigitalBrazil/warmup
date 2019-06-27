/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
describe('personalizationInfiniteScroll', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $compile, $rootScope;

    beforeEach(module('personalizationsmarteditCommons'));
    beforeEach(inject(function(_$compile_, _$rootScope_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    it('Add watcher functions to scope', function() {
        expect($rootScope.$$watchers).toBe(null);
        // given
        var element = $compile("<div personalization-infinite-scroll=\"addMoreItems()\" personalization-infinite-scroll-distance=\"2\"</div>")($rootScope); // jshint ignore:line
        // when
        $rootScope.$digest();
        // then
        expect($rootScope.$$watchers).not.toBe(null);
    });

});
