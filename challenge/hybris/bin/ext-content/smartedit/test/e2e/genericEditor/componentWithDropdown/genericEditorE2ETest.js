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
describe("GenericEditor Dropdown - ", function() {

    var genericEditor = require("../genericEditorComponentObject.js");
    var dropdown = require("./dropdownObject.js");

    beforeEach(function() {

        genericEditor.actions.openAndBeReady("withDropdown");

    });

    it("GIVEN a structure API and a content API THEN all dropdowns will be populated with respective values (if found in their list) else will reset", function() {

        //has data in content API and match from list
        dropdown.elements.getDropdownValues(['dropdownA', 'dropdownB']).then(function(values) {
            expect(values).toEqual(['OptionA2', 'OptionB7-A1-A2']);
        });

        dropdown.elements.getMultiDropdownValue('dropdownC').then(function(values) {
            expect(values).toEqual(['OptionC3-A2', 'OptionC4-A2']);
        });

        dropdown.elements.getMultiDropdownValue('dropdownD').then(function(values) {
            expect(values).toEqual(['OptionD2-sample-element']);
        });

        //has data in content API but does not values match in the list
        dropdown.elements.getDropdownValues(['dropdownE']).then(function(values) {
            expect(values).toEqual(['Select an Option']);
        });

    });

    it("GIVEN a set of cascading dropdowns WHEN I change value of a dropdown THEN all the children dropdowns are reset while the independent ones are untouched", function() {

        dropdown.actions.clickDropdown('dropdownA').then(function() {
            dropdown.actions.selectOption('dropdownA', 'OptionA1').then(function() {
                dropdown.elements.getDropdownValues(['dropdownA', 'dropdownB', 'dropdownE']).then(function(values) {
                    expect(values).toEqual(['OptionA1', 'OptionB7-A1-A2', 'Select an Option']);
                });

                dropdown.elements.getMultiDropdownValue('dropdownC').then(function(values) {
                    expect(values).toEqual([]);
                });

                dropdown.elements.getMultiDropdownValue('dropdownD').then(function(values) {
                    expect(values).toEqual(['OptionD2-sample-element']);
                });
            });
        });

    });

    it("GIVEN a set of cascading dropdowns WHEN I update value of the parent dropdown THEN all the children dropdowns should update their options", function() {

        //change dropdown A
        dropdown.actions.clickDropdown('dropdownA');
        dropdown.actions.selectOption('dropdownA', 'OptionA1');
        dropdown.actions.clickDropdown('dropdownB');
        dropdown.assertions.assertListOfOptions('dropdownB', ['OptionB1-A1', 'OptionB2-A1', 'OptionB7-A1-A2']);

        dropdown.actions.clickDropdown('dropdownC');
        dropdown.assertions.assertListOfOptions('dropdownC', ['OptionC1-A1', 'OptionC2-A1']);

        dropdown.actions.clickDropdown('dropdownE');
        dropdown.assertions.assertListOfOptions('dropdownE', ['OptionE7-B7']);

        //change dropdown B
        dropdown.actions.clickDropdown('dropdownB');
        dropdown.actions.selectOption('dropdownB', 'OptionB1-A1');
        dropdown.actions.clickDropdown('dropdownE');
        dropdown.assertions.assertListOfOptions('dropdownE', ['OptionE1-B1']);

    });

    it("GIVEN a dropdown WHEN I start typing in the dropdown search THEN the options should be filtered to match the searched key", function() {

        dropdown.actions.clickMultiSelectDropdown('dropdownD');
        dropdown.assertions.assertListOfOptions('dropdownD', ['OptionD1-sample', 'OptionD3-element']);
        dropdown.assertions.searchAndAssertInDropdown('dropdownD', 'sample', ['OptionD1-sample']);
        dropdown.assertions.searchAndAssertInDropdown('dropdownD', '', ['OptionD1-sample', 'OptionD3-element']);
    });


});
