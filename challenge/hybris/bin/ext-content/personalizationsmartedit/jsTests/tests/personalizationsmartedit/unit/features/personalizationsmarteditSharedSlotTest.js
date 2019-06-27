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
describe('personalizationsmarteditSharedSlot', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $compile, $rootScope;

    beforeEach(module('personalizationsmarteditTemplates'));
    beforeEach(module('personalizationsmarteditSharedSlotDecorator'));
    beforeEach(inject(function(_$compile_, _$rootScope_, _$q_, $templateCache) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
        var directiveTemplate = $templateCache.get('web/features/personalizationsmartedit/sharedSlotDecorator/personalizationsmarteditSharedSlotDecoratorTemplate.html');
        $templateCache.put('personalizationsmarteditSharedSlotDecoratorTemplate.html', directiveTemplate);

        mockModules.slotSharedService.isSlotShared.and.callFake(function() {
            var deferred = _$q_.defer();
            deferred.resolve({});
            return deferred.promise;
        });


    }));

    it('Replaces the element with the appropriate content', function() {
        // given
        var element = $compile("<div class=\"personalizationsmarteditSharedSlot\" data-active=\"true\" data-smartedit-component-id=\"Test\"></div>")($rootScope);
        // when
        $rootScope.$apply();
        // then
        var subText = "<div>\n" + "    <div class=\"cmsx-ctx-wrapper1 se-slot-contextual-menu-level1\">\n";
        expect(element.html().substring(0, subText.length)).toContain(subText);
    });

});
