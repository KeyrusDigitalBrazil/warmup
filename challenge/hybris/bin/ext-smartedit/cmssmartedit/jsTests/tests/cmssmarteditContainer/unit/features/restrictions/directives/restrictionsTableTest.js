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
describe('restrictionsTable', function() {

    var scope, element, uiSelect;
    var template = '<restrictions-table data-editable="editable" data-restriction-criteria="restrictionCriteria" data-restrictions="restrictions" data-on-criteria-selected="onCriteriaSelected"></restrictions-table>';

    var MOCK_RESTRICITON_1 = {
        uid: 'SampleUid',
        name: 'Sample Name',
        typeCode: 'SampleRestriction',
        typeName: {
            en: 'Sample Restriction'
        },
        description: 'Sample Description'
    };

    var MOCK_RESTRICTION_2 = {
        uid: 'SampleUid2',
        name: 'Sample Name 2',
        typeCode: 'SampleRestriction2',
        typeName: {
            en: 'Sample Restriction 2'
        },
        description: 'Sample Description 2'
    };

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('restrictionsTableModule')
            .withTranslations({
                'se.cms.restrictions.criteria': 'Criteria:',
                'se.cms.restrictions.criteria.all': 'Match all',
                'se.cms.restrictions.criteria.any': 'Match any',
                'page.restrictions.list.empty': 'This page has no restrictions'
            })
            .mock('restrictionsCriteriaService', 'getRestrictionCriteriaOptions').and.returnValue([{
                id: 'all',
                label: 'se.cms.restrictions.criteria.all',
                editLabel: 'all',
                value: false
            }, {
                id: 'any',
                label: 'se.cms.restrictions.criteria.any',
                editLabel: 'any',
                value: true
            }])
            .component(template, {
                editable: false,
                onCriteriaSelected: jasmine.createSpy('onCriteriaSelected'),
                restrictions: [MOCK_RESTRICITON_1]
            });

        element = harness.element;
        uiSelect = new UiSelectPageObject(element);
        scope = harness.scope;
    });

    describe('restrictions list', function() {
        it('should render exactly one restrictions', function() {
            expect(element.find('.se-restrictions-list--item').length).toBe(1);
        });

        it('should render the name, type, and description of each restriction', function() {
            expect(element.find('#restriction-1 .ySERestrictionsNameHeader').text()).toBe('Sample Name');
            expect(element.find('#restriction-1 .ySERestrictionsTypeAndID').text()).toBe('SampleRestriction');
            expect(element.find('#restriction-1 .ySERestrictionsDescription').text()).toBe('Sample Description');
        });
    });

    describe('no restrictions', function() {
        beforeEach(function() {
            scope.restrictions = [];
            scope.$digest();
        });

        it('should not render the restrictions list', function() {
            expect(element.find('.ySERestrictionsListContainer').length).toBe(0);
        });
    });

    describe('read-only criteria', function() {
        beforeEach(function() {
            scope.restrictions = [MOCK_RESTRICITON_1, MOCK_RESTRICTION_2];
            scope.restrictionCriteria = {};
            scope.$digest();
        });

        it('should render any criteria', function() {
            scope.restrictionCriteria.label = 'se.cms.restrictions.criteria.any';
            scope.$digest();

            expect(element.find('.ySERestrictionsCriteria').text()).toContain('Criteria: Match any');
        });

        it('should render all criteria', function() {
            scope.restrictionCriteria.label = 'se.cms.restrictions.criteria.all';
            scope.$digest();

            expect(element.find('.ySERestrictionsCriteria').text()).toContain('Criteria: Match all');
        });
    });

    describe('editable criteria', function() {
        beforeEach(function() {
            scope.restrictions = [MOCK_RESTRICITON_1, MOCK_RESTRICTION_2];
            scope.editable = true;
            scope.$digest();
        });

        it('should render a selectable criteria list', function() {
            uiSelect.getSelectToggle().click();
            expect(uiSelect.getSelectedElement().text().trim()).toContain('all');
            expect(uiSelect.getSelectElement(0).text().trim()).toBe('all');
            expect(uiSelect.getSelectElement(1).text().trim()).toBe('any');
        });

        it('should update the selected criteria on select of "all"', function() {
            uiSelect.clickSelectToggle();
            uiSelect.clickSelectElement(0);
            expect(uiSelect.getSelectedElement().text().trim()).toContain('all');
            expect(scope.restrictionCriteria.label).toBe('se.cms.restrictions.criteria.all');
        });

        it('should update the selected criteria on select of "any"', function() {
            uiSelect.clickSelectToggle();
            uiSelect.clickSelectElement(1);
            expect(uiSelect.getSelectedElement().text().trim()).toContain('any');
            expect(scope.restrictionCriteria.label).toBe('se.cms.restrictions.criteria.any');
        });

        it('should reset from "any" to unselected when restrictions are removed', function() {
            // GIVEN
            uiSelect.clickSelectToggle();
            uiSelect.clickSelectElement(1);
            expect(uiSelect.getSelectedElement().text().trim()).toContain('any');
            scope.$digest();

            // WHEN
            scope.restrictions = [];
            scope.$digest();

            // THEN
            expect(uiSelect.getSelectedElement().length).toBe(0);
        });

        it('should reset to "all" when user removes restrictions, then adds at least two restrictions', function() {
            // GIVEN
            uiSelect.clickSelectToggle();
            uiSelect.clickSelectElement(1);
            expect(uiSelect.getSelectedElement().text().trim()).toContain('any');
            scope.$digest();

            // WHEN
            scope.restrictions = [];
            scope.$digest();

            scope.restrictions = [MOCK_RESTRICITON_1, MOCK_RESTRICTION_2];
            scope.$digest();

            // THEN
            uiSelect.clickSelectToggle();
            expect(uiSelect.getSelectedElement().text().trim()).toContain('all');
            expect(scope.restrictionCriteria.label).toBe('se.cms.restrictions.criteria.all');
        });
    });

    describe('no criteria', function() {
        it('should not render criteria', function() {
            expect(element.find('.ySERestrictionsCriteria').length).toBe(0);
        });
    });
});
