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
describe('personalizationsmarteditSegmentExpressionAsHtml', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $componentController;

    beforeEach(module('personalizationsmarteditManageCustomizationViewModule'));
    beforeEach(module('personalizationsmarteditSegmentExpressionAsHtml'));
    beforeEach(inject(function(_$componentController_) {
        $componentController = _$componentController_;
    }));

    describe('Component API', function() {

        it('should have proper api when initialized', function() {
            var ctrl = $componentController('personalizationsmarteditSegmentExpressionAsHtml', null);

            expect(ctrl.segmentExpression).toEqual({});
            expect(ctrl.operators).toEqual(['AND', 'OR', 'NOT']);
            expect(ctrl.emptyGroup).toEqual('[]');
            expect(ctrl.emptyGroupAndOperators).toEqual(['AND', 'OR', 'NOT', '[]']);
            expect(ctrl.getExpressionAsArray).toBeDefined();
        });

    });

});
