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
describe('displayConditionsPageInfo', function() {

    var element;
    var scope;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('displayConditionsPageInfoModule')
            .withTranslations({
                'se.cms.display.conditions.label': 'Display Condition',
                'se.cms.display.conditions.primary.id': 'Primary',
                'se.cms.display.conditions.variation.id': 'Variation',
                'se.cms.display.conditions.primary.description': 'This is a primary page and it will be displayed if no other variation page exists',
                'se.cms.display.conditions.variation.description': 'This is a variation page that supports restriction rules to control the page visibility'
            })
            .component('<display-conditions-page-info data-page-name="pageName" data-page-type="pageType" data-is-primary="isPrimary"></display-conditions-page-info>', {
                pageName: 'somePageName',
                pageType: 'somePageType',
                isPrimary: true
            });
        element = harness.element;
        scope = harness.scope;
    });

    it('should render the page name', function() {
        expect(element).toContainElementText('somePageName');
    });

    it('should render the page type', function() {
        expect(element).toContainElementText('somePageType');
    });

    it('should render the translated Condition label', function() {
        expect(element).toContainElementText('Condition');
    });

    describe('for a primary page', function() {
        beforeEach(function() {
            scope.isPrimary = true;
            scope.$digest();
        });

        it('should render the primary page translated value', function() {
            expect(element).toContainElementText('Primary');
        });
    });

    describe('for a page variation', function() {
        beforeEach(function() {
            scope.isPrimary = false;
            scope.$digest();
        });

        it('should render the variation page translated value', function() {
            expect(element).toContainElementText('Variation');
        });
    });
});
