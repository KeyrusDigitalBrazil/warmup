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
describe('directive:sanitizeHtmlInput', function() {

    var $compile, $rootScope;
    var element, elementScope;

    beforeEach(angular.mock.module('sanitizeHtmlInputModule'));

    beforeEach(inject(function(_$compile_, _$rootScope_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    beforeEach(function() {
        elementScope = $rootScope.$new();
        var originalElement = angular.element('<input sanitize-html-input></input>');
        element = $compile(originalElement)(elementScope);
        elementScope.$digest();
    });

    it('WILL sanitize inputs containing curly brackets', function() {
        element.val('{{555-444}}');
        var originalValue = element.val();
        element.trigger('change');
        var sanitizedValue = element.val();

        expect(originalValue).toBe('{{555-444}}');
        expect(sanitizedValue).toBe('%7B%7B555-444%7D%7D');
    });

});
