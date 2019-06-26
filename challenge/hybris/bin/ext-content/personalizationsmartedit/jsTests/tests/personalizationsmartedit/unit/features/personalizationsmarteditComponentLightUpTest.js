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
describe('personalizationsmarteditComponentLightUp', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $compile, $rootScope;

    beforeEach(module('personalizationsmarteditComponentLightUpDecorator'));
    beforeEach(inject(function(_$compile_, _$rootScope_, $templateCache) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        var directiveTemplate = $templateCache.get('web/features/personalizationsmartedit/componentLightUpDecorator/personalizationsmarteditComponentLightUpDecoratorTemplate.html');
        $templateCache.put('personalizationsmarteditComponentLightUpDecoratorTemplate.html', directiveTemplate);
    }));

    it('Replaces the element with the appropriate content', function() {
        // given
        var element = $compile("<div class=\"personalizationsmarteditComponentLightUp\"></div>")($rootScope);
        // when
        $rootScope.$digest();
        // then
        var subText = "<div ng-class=\"getPersonalizationComponentBorderClass()\">";
        expect(element.html().substring(0, subText.length)).toContain(subText);
    });

});
