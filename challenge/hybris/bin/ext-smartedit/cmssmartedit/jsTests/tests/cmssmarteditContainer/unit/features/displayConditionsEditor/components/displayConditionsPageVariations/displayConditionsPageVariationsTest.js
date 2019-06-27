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
describe('displayConditionsPageVariations', function() {

    var NO_VARIATIONS = [];
    var MOCK_VARIATIONS = [{
        pageName: 'Variation Page 1',
        creationDate: '2016-07-07T14:33:37+0000',
        restrictions: 123
    }, {
        pageName: 'Variation Page 2',
        creationDate: '2016-08-07T14:33:37+0000',
        restrictions: 456
    }];

    var element;
    var scope;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('displayConditionsPageVariationsModule')
            .withTranslations({
                'se.cms.display.conditions.header.page.name': 'Page Name',
                'se.cms.display.conditions.header.creation.date': 'Creation Date',
                'se.cms.display.conditions.header.restrictions': 'Restrictions',
                'se.cms.display.conditions.variation.pages.title': 'Variation Pages Associated to this Primary',
                'se.cms.display.conditions.variations.description': 'The variation page with the most applicable restrictions is displayed. If more than one variation page applies, the oldest variation page is displayed',
                'se.cms.display.conditions.no.variations': 'There are no page variations for this primary.'
            })
            .component('<display-conditions-page-variations data-variations="variations"></display-conditions-page-variations>', {
                variations: NO_VARIATIONS
            });
        element = harness.element;
        scope = harness.scope;
    });

    describe('on init', function() {
        it('should have variations defined on the scope', function() {
            expect(scope.variations).toBe(NO_VARIATIONS);
        });

        it('should display a title', function() {
            expect(element).toContainElementText('Variation Pages Associated to this Primary');
        });
    });

    describe('with no variations', function() {
        beforeEach(function() {
            scope.variations = NO_VARIATIONS;
            scope.$digest();
        });

        it('should display a message indicating to variations are present', function() {
            expect(element).toContainElementText('There are no page variations for this primary');
        });
    });

    describe('with some variations', function() {
        beforeEach(function() {
            scope.variations = MOCK_VARIATIONS;
            scope.$digest();
        });

        it('should display a client-paged-list for the variations', function() {
            expect(element).toContainChildElement('client-paged-list');
        });
    });
});
