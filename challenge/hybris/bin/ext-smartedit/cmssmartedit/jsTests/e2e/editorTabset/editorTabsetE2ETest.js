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
describe('Editor Tabset - ', function() {

    var editorTabset = e2e.componentObjects.EditorTabset;

    beforeEach(function() {
        browser.bootstrap(__dirname);
        browser.waitForVisibility(by.css('editor-tabset'));
    });

    it("WHEN I load the editorTabset THEN I expect to see tab1 in nav-tabs", function() {
        expect(editorTabset.tab1Tab().isPresent()).toBe(true, 'Expected tab1Tab to be present');
        expect(editorTabset.tab1TabContent().isPresent()).toBe(true, 'Expected tab1TabContent to be present');
        expect(editorTabset.tab2Tab().isPresent()).toBe(true, 'Expected tab2to be present');
        expect(editorTabset.tab2TabContent().isPresent()).toBe(true, 'Expected tab2Content to be present');
        expect(editorTabset.tab3Tab().isPresent()).toBe(true, 'Expected tab3 to be present');
        expect(editorTabset.tab3TabContent().isPresent()).toBe(true, 'Expected tab3TabContent to be present');
    });

    it('clicking on a tab will change the displayed content to the view of the selected tab', function() {
        // Arrange
        var targetTab = editorTabset.tab2Tab();
        var targetTabContent = editorTabset.tab2TabContent();
        var currentSelectedHeader = editorTabset.tab1Tab();
        var currentSelectedBody = editorTabset.tab1TabContent();

        expect(targetTab).not.toBe(currentSelectedHeader);
        expect(targetTab).not.toBeUndefined();
        expect(hasClass(targetTab, 'active')).toBeFalsy();
        expect(targetTabContent).not.toBeUndefined();
        expect(targetTabContent.isDisplayed()).toBeFalsy();
        expect(currentSelectedHeader).not.toBeUndefined();
        expect(currentSelectedBody).not.toBeUndefined();

        // Act
        browser.click(targetTab);

        // Assert
        expect(currentSelectedBody.isDisplayed()).toBeFalsy();
        expect(targetTabContent.isDisplayed()).toBeTruthy();
    });

    it('clicking on a tab in the dropdown menu will change the displayed content to the view of the selected tab', function() {
        // Arrange
        var dropDownHeader = getDropDownHeader();
        var targetTab = editorTabset.tab6DropdownMenu();
        var targetTabLink = editorTabset.tab6DropdownMenuLink();
        var targetTabContent = editorTabset.tab6TabContent();
        var currentSelectedHeader = editorTabset.tab1Link();
        var currentSelectedBody = editorTabset.tab1TabContent();

        expect(targetTab).not.toBe(currentSelectedHeader);
        expect(targetTab).not.toBeUndefined();
        expect(hasClass(targetTab, 'active')).toBeFalsy();
        expect(targetTabContent).not.toBeUndefined();
        expect(targetTabContent.isDisplayed()).toBeFalsy();
        expect(currentSelectedHeader).not.toBeUndefined();
        expect(currentSelectedBody).not.toBeUndefined();

        // Act
        browser.click(dropDownHeader);
        browser.click(targetTabLink);

        // Assert
        expect(currentSelectedBody.isDisplayed()).toBeFalsy();
        expect(targetTabContent.isDisplayed()).toBeTruthy();
    });

    it('clicking on a tab will change the displayed content to the view of the selected tab even when ' +
        'the tab has validation errors',
        function() {
            // Arrange
            var saveButton = element(by.css('#save-button'));
            var targetTab = editorTabset.tab3Tab();
            var targetTabLink = editorTabset.tab3Link();
            var targetTabContent = editorTabset.tab3TabContent();
            var currentSelectedHeader = editorTabset.tab1Link();
            var currentSelectedBody = editorTabset.tab1TabContent();

            expect(targetTab).not.toBe(currentSelectedHeader);
            expect(targetTab).not.toBeUndefined();
            expect(hasClass(targetTab, 'active')).toBeFalsy();
            expect(targetTabContent).not.toBeUndefined();
            expect(targetTabContent.isDisplayed()).toBeFalsy();
            expect(currentSelectedHeader).not.toBeUndefined();
            expect(currentSelectedBody).not.toBeUndefined();

            saveButton.click().then(function() {
                expect(hasClass(targetTabLink, 'sm-tab-error')).toBeTruthy();
            });

            // Act
            browser.click(targetTab);

            // Assert
            expect(targetTabContent.isDisplayed()).toBeTruthy();
        });

    it('clicking on save will execute save on all tabs', function() {
        // Arrange
        var saveButton = element(by.css('#save-button'));
        var targetTabHeader1 = editorTabset.tab1Link();
        var targetTabHeader2 = editorTabset.tab2Link();
        var targetTabHeader3 = editorTabset.tab3Link();

        expect(hasClass(targetTabHeader1, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(targetTabHeader2, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(targetTabHeader3, 'sm-tab-error')).toBeFalsy();

        // Act
        browser.click(saveButton);

        // Assert
        expect(hasClass(targetTabHeader1, 'sm-tab-error')).toBeTruthy();
        expect(hasClass(targetTabHeader2, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(targetTabHeader3, 'sm-tab-error')).toBeTruthy();
    });

    it('clicking on cancel will clear all tabs', function() {
        // Arrange
        var saveButton = element(by.css('#save-button'));
        var cancelButton = element(by.css('#cancel-button'));
        var targetTabHeader1 = editorTabset.tab1Link();
        var targetTabHeader2 = editorTabset.tab2Link();
        var targetTabHeader3 = editorTabset.tab3Link();

        expect(hasClass(targetTabHeader1, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(targetTabHeader2, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(targetTabHeader3, 'sm-tab-error')).toBeFalsy();

        browser.click(saveButton);

        expect(hasClass(targetTabHeader1, 'sm-tab-error')).toBeTruthy();
        expect(hasClass(targetTabHeader2, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(targetTabHeader3, 'sm-tab-error')).toBeTruthy();

        // Act
        browser.click(cancelButton);

        // Assert
        expect(hasClass(targetTabHeader1, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(targetTabHeader2, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(targetTabHeader3, 'sm-tab-error')).toBeFalsy();
    });

    function hasClass(element, className) {
        return element.getAttribute('class').then(function(classes) {
            return classes.split(' ').indexOf(className) !== -1;
        });
    }

    function getDropDownHeader() {
        return element.all(by.css('ul.nav.nav-tabs li a.dropdown-toggle')).get(0);
    }

});
