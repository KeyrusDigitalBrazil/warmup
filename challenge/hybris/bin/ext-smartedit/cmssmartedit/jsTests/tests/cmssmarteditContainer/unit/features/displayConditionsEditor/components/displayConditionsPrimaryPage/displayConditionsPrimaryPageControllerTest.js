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
describe('displayConditionsPrimaryPageController', function() {

    var controller;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('displayConditionsPrimaryPageControllerModule')
            .controller('displayConditionsPrimaryPageController');
        controller = harness.controller;
    });

    describe('init', function() {
        it('should initialize the i18n keys on the scope', function() {
            expect(controller.associatedPrimaryPageLabelI18nKey).toBe('se.cms.display.conditions.primary.page.label');
        });
    });

});
