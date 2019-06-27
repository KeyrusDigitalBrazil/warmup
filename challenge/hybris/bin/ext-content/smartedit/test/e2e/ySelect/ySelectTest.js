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
describe('ySelect - ', function() {

    var ySelectAppTestComponentObjects = require('./YSelectTestPageObject');
    var ySelect = require('../utils/components/ySelectComponentObject');

    var example1Selector = new ySelect('example1');
    var multi1Selector = new ySelect('exampleMulti1');
    var multi2Selector = new ySelect('exampleMulti2');

    var example1SectorApp = new ySelectAppTestComponentObjects('example1');
    var multi1SelectorSelectorApp = new ySelectAppTestComponentObjects('exampleMulti1');
    var multi2AppSelector = new ySelectAppTestComponentObjects('exampleMulti2');

    beforeEach(function() {
        browser.get('test/e2e/ySelect/ySelect.html');
    });

    describe('Single Selector - ', function() {

        it('GIVEN a validation state THEN the selector should display ERROR, WARNING and NO STATE.', function() {
            example1SectorApp.actions.clickShowErrorButton();
            example1Selector.assertions.assertSelectorHasValidationType(ySelect.constants.VALIDATION_MESSAGE_TYPE.VALIDATION_ERROR);


            example1SectorApp.actions.clickShowWarningButton();
            example1Selector.assertions.assertSelectorHasValidationType(ySelect.constants.VALIDATION_MESSAGE_TYPE.WARNING);

            example1SectorApp.actions.clickResetValidationButton();
            example1Selector.assertions.assertSelectorHasNoValidationType();
        });

        it('GIVEN a user selects the German option THEN the displayed value of the option should be "de"', function() {
            example1Selector.actions.toggleSimpleSelector();
            example1Selector.actions.selectOptionByText('German');

            example1SectorApp.assertions.assertSelectorModelIsEqualTo('de');
        });


        it('GIVEN a selected option that does not exist in another set of options and "Force Reset" as checked WHEN switching to another set of options THEN the selected model should reset', function() {
            example1Selector.actions.toggleSimpleSelector();
            example1Selector.actions.selectOptionByText('Russian');

            example1SectorApp.actions.clickChangeSourceButton();

            example1SectorApp.assertions.assertSelectorModelIsEqualTo('');
        });

        it('GIVEN a the initial options and "Force Reset" as unchecked WHEN selecting a language that does not exist in another set THEN the selected model should reset', function() {
            example1SectorApp.actions.clickForceResetCheckBox();

            example1Selector.actions.toggleSimpleSelector();
            example1Selector.actions.selectOptionByText('Russian');

            example1SectorApp.actions.clickChangeSourceButton();

            example1SectorApp.assertions.assertSelectorModelIsEqualTo('ru');
        });

    });

    describe('Multi Selector -', function() {

        it('GIVEN a multi selector WHEN selecting a product in the dropdown list THEN the dropdown selection list should be updated', function() {
            multi1Selector.actions.toggleMultiSelector();
            multi1Selector.actions.selectOptionByText('Test Product 1');

            multi1Selector.assertions.assertMultiSelectorHasSelectedOptionsEqualTo(['Test Product 2', 'Test Product 3', 'Test Product 1']);
        });

        it('GIVEN a selected option that does not exist in another set of options and "Force Reset" as checked WHEN switching to another set of options THEN the multi selected model should reset', function() {
            /**
             *  The "Change Source" button is initially clicked twice in the test because the first click does not change the list items until the second time.
             *  We can't use the normal _fetchAll function in the test app, but use the _fetchAllClone function for ySelect to fetch the items with two fetch the items.
             *  If we dont _fetchAllClone the items, the directive ySelect will continue looping and freeze the browser. If we do clone, we need to double click change source initially.
             *  The problem is caused by the ySelect itself.
             */
            multi1SelectorSelectorApp.actions.clickChangeSourceButton();
            multi1SelectorSelectorApp.actions.clickChangeSourceButton();

            multi1SelectorSelectorApp.assertions.assertSelectorModelIsEqualTo('["product3"]');
        });

        it('GIVEN the initial options and "Force Reset" as unchecked WHEN selecting a language that does not exist in another set THEN the selected model should not reset', function() {
            multi1SelectorSelectorApp.actions.clickForceResetCheckBox();

            multi1SelectorSelectorApp.actions.clickChangeSourceButton();

            multi1SelectorSelectorApp.assertions.assertSelectorModelIsEqualTo('["product2","product3"]');
        });

        it('GIVEN an validation state THEN the selector should display ERROR, WARNING and NO STATE.', function() {
            multi2AppSelector.actions.clickShowErrorButton();
            multi2Selector.assertions.assertSelectorHasValidationType(ySelect.constants.VALIDATION_MESSAGE_TYPE.VALIDATION_ERROR);

            multi2AppSelector.actions.clickShowWarningButton();
            multi2Selector.assertions.assertSelectorHasValidationType(ySelect.constants.VALIDATION_MESSAGE_TYPE.WARNING);

            multi2AppSelector.actions.clickResetValidationButton();
            multi2Selector.assertions.assertSelectorHasNoValidationType();
        });

    });

});
