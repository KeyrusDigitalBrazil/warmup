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
describe('catalogFilterDropdownModule', function() {
    var mockModules = {};
    setupMockModules(mockModules); // jshint ignore:line

    var $componentController;

    beforeEach(module('catalogFilterDropdownModule'));
    beforeEach(inject(function(_$componentController_) {
        $componentController = _$componentController_;
    }));

    describe('Component API', function() {

        it('should have proper api before initialized', function() {
            var ctrl = $componentController('catalogFilterDropdown', null);

            expect(ctrl.items).not.toBeDefined();
            expect(ctrl.selectedId).not.toBeDefined();
            expect(ctrl.onSelectCallback).not.toBeDefined();
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.onChange).toBeDefined();
            expect(ctrl.fetchStrategy).toBeDefined();
        });

        it('should have proper api after initialized', function() {
            var ctrl = $componentController('catalogFilterDropdown', null);
            ctrl.onSelectCallback = function() {};

            ctrl.$onInit();

            expect(ctrl.items.length).toBe(3);
            expect(ctrl.selectedId).toBe(ctrl.items[1].id);
            expect(ctrl.$onInit).toBeDefined();
            expect(ctrl.onChange).toBeDefined();
            expect(ctrl.fetchStrategy).toBeDefined();
        });

    });

});
