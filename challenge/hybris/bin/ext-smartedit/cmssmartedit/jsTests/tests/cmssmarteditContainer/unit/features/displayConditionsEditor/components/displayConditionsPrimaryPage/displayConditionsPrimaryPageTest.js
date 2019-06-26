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
describe('displayConditionsPrimaryPage', function() {

    var MOCK_ASSOCIATED_PRIMARY_PAGE = {
        uid: 'primaryPage1',
        label: 'primary-page-1',
        name: 'First Primary Page'
    };

    var MOCK_PRIMARY_PAGES = [{
        uid: 'primaryPage1',
        label: 'primary-page-1',
        name: 'First Primary Page'
    }, {
        uid: 'primaryPage2',
        label: 'primary-page-2',
        name: 'Second Primary Page'
    }];

    var element;
    var scope;
    var uiSelect;

    beforeEach(function() {
        var harness = AngularUnitTestHelper.prepareModule('displayConditionsPrimaryPageModule')
            .withTranslations({
                'se.cms.display.conditions.primary.page.label': 'Primary page associated to the variation'
            })
            .component('<display-conditions-primary-page ' +
                'data-read-only="readOnly" ' +
                'data-associated-primary-page="associatedPrimaryPage" ' +
                'data-all-primary-pages="allPrimaryPages" ' +
                'data-on-primary-page-select="onPrimaryPageSelect(primaryPage)">' +
                '</display-conditions-primary-page>');
        element = harness.element;
        scope = harness.scope;
        uiSelect = new UiSelectPageObject(element);
    });

    describe('when read only', function() {
        beforeEach(function() {
            scope.readOnly = true;
            scope.associatedPrimaryPage = MOCK_ASSOCIATED_PRIMARY_PAGE;
            scope.$digest();
        });

        it('should display a label for the associated primary page', function() {
            expect(element).toContainElementText('Primary page associated to the variation');
        });

        it('should display the associated primary page name', function() {
            expect(element).toContainElementText('First Primary Page');
        });
    });

    describe('when read and write', function() {
        beforeEach(function() {
            scope.readOnly = false;
            scope.associatedPrimaryPage = MOCK_ASSOCIATED_PRIMARY_PAGE;
            scope.allPrimaryPages = MOCK_PRIMARY_PAGES;
            scope.onPrimaryPageSelect = jasmine.createSpy('onPrimaryPageSelect');
            scope.$digest();
        });

        it('should have a UI select dropdown with the selected option as the associated primary page', function() {
            expect(uiSelect.getSelectedElement().text().trim()).toContain('First Primary Page');

        });

        it('should have a UI select dropdown with all primary pages as options', function() {
            uiSelect.getSelectToggle().click();
            expect(uiSelect.getSelectElement(0).text().trim()).toBe('First Primary Page');
            expect(uiSelect.getSelectElement(1).text().trim()).toBe('Second Primary Page');
        });

        it('should trigger the onPrimaryPageSelect callback on selecting another primary page', function() {
            uiSelect.clickSelectToggle();
            uiSelect.clickSelectElement(1);
            expect(scope.onPrimaryPageSelect).toHaveBeenCalled();
        });
    });


});
