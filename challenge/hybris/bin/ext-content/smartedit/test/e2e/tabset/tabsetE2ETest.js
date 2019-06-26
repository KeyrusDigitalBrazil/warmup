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
describe('y Tabset', function() {

    beforeEach(function() {
        browser.get('test/e2e/tabset/tabset.html');
    });

    it('will display all the visible tabs', function() {
        // Arrange
        var tabset = element(by.css('y-tabset'));
        var numVisibleTabsExpected = 2;

        // Act/Assert
        expect(tabset).not.toBeUndefined();

        for (var i = 1; i <= numVisibleTabsExpected; i++) {
            var tab = getVisibleTabHeader(i);
            validateTabHeader(tab, i);
        }
    });

    it('will display not-visible tabs in a drop-down', function() {
        // Arrange
        var numVisibleTabsExpected = 2;
        var maxNumTabs = 4;

        // Act/Assert
        var header = getDropDownHeader();
        expect(header).not.toBeNull();
        for (var i = numVisibleTabsExpected + 1; i <= maxNumTabs; i++) {
            var tab = getDropDownTabHeader(numVisibleTabsExpected, i);
            validateTabHeader(tab, i);
        }
    });

    it('will change content when clicking other tab', function() {
        // Arrange
        var targetTab = getVisibleTabHeader(2);
        var targetTabContent = element(by.css("div y-tab:nth-child(2)"));
        var currentTab = element(by.css('ul.nav-tabs li.active'));
        var currentSelectedBody = element(by.css('div y-tab:nth-child(1)'));

        expect(targetTab).not.toBe(currentTab);
        expect(targetTab).not.toBeFalsy();
        expect(targetTabContent).not.toBeUndefined();
        expect(targetTabContent.isDisplayed()).toBe(false);
        expect(currentTab).not.toBeUndefined();
        expect(currentSelectedBody).not.toBeUndefined();
        expect(currentSelectedBody.isDisplayed()).toBe(true);

        // Act
        browser.click(targetTab);

        // Assert
        expect(targetTabContent.isDisplayed()).toBe(true);
        expect(currentSelectedBody.isDisplayed()).toBe(false);
    });

    it('will change content when clicked in tab in the drop-down', function() {
        // Arrange
        var numVisibleTabsExpected = 2;
        var dropDownHeader = getDropDownHeader();
        var targetTab = getDropDownTabHeader(numVisibleTabsExpected, 3);
        var targetTabContent = element(by.css("div y-tab:nth-child(3)"));
        var currentTab = element(by.css('ul.nav-tabs li.active'));
        var currentSelectedBody = element(by.css('div y-tab:nth-child(1)'));

        expect(targetTab).not.toBe(currentTab);
        expect(targetTab).not.toBeFalsy();
        expect(targetTabContent).not.toBeUndefined();
        expect(targetTabContent.isDisplayed()).toBe(false);
        expect(currentTab).not.toBeUndefined();
        expect(currentSelectedBody).not.toBeUndefined();
        expect(currentSelectedBody.isDisplayed()).toBe(true);

        // Act
        browser.click(dropDownHeader);
        browser.click(targetTab);

        // Assert
        expect(targetTabContent.isDisplayed()).toBe(true);
        expect(currentSelectedBody.isDisplayed()).toBe(false);
    });

    it('will show errors in tab', function() {
        // Arrange
        var tab = getVisibleTabHeader(1);
        var addErrorButton = element(by.css("button#add-error1-button"));

        expect(addErrorButton).not.toBeNull();
        expect(hasClass(tab, 'sm-tab-error')).toBeFalsy();

        // Act
        browser.click(addErrorButton);

        // Assert
        expect(hasClass(tab, 'sm-tab-error')).toBe(true);
    });

    it('will show error on drop-down and MORE header', function() {
        // Arrange
        var numVisibleTabsExpected = 2;
        var dropDownHeader = getDropDownHeader();
        var tab = getDropDownTabHeader(numVisibleTabsExpected, 3);

        var addErrorButton = element(by.css("button#add-error2-button"));

        expect(addErrorButton).not.toBeNull();
        expect(hasClass(dropDownHeader, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(tab, 'sm-tab-error')).toBeFalsy();

        // Act
        browser.click(addErrorButton);

        // Assert
        expect(hasClass(dropDownHeader, 'sm-tab-error')).toBe(true);
        expect(hasClass(tab, 'sm-tab-error')).toBe(true);
    });

    it('can reset errors in tab and in MORE header', function() {
        // Arrange
        var numVisibleTabsExpected = 2;
        var tab = getVisibleTabHeader(1);
        var dropDownHeader = getDropDownHeader();
        var dropDownTab = getDropDownTabHeader(numVisibleTabsExpected, 3);

        var addErrorButton1 = element(by.css("button#add-error1-button"));
        var addErrorButton2 = element(by.css("button#add-error2-button"));
        var resetButton = element(by.css("button#reset-button"));

        expect(addErrorButton1).not.toBeNull();
        expect(addErrorButton2).not.toBeNull();
        expect(resetButton).not.toBeNull();

        expect(hasClass(dropDownHeader, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(dropDownTab, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(tab, 'sm-tab-error')).toBeFalsy();

        browser.click(addErrorButton1);
        browser.click(addErrorButton2);

        expect(hasClass(dropDownHeader, 'sm-tab-error')).toBe(true);
        expect(hasClass(dropDownTab, 'sm-tab-error')).toBe(true);
        expect(hasClass(tab, 'sm-tab-error')).toBe(true);

        // Act
        browser.click(resetButton);

        // Assert
        expect(hasClass(tab, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(dropDownHeader, 'sm-tab-error')).toBeFalsy();
        expect(hasClass(dropDownTab, 'sm-tab-error')).toBeFalsy();
    });

    function validateTabHeader(tab, id) {
        var expectedContent = "Tab " + id;
        browser.getInnerHTML(tab).then(function(actualContent) {
            actualContent = actualContent.trim();
            expect(actualContent).toBe(expectedContent);
        });
    }

    function getVisibleTabHeader(tabNum) {
        return element.all(by.css('ul.nav.nav-tabs li:nth-child(' + tabNum + ') a')).get(0);
    }

    function hasClass(element, className) {
        return element.getAttribute('class').then(function(classes) {
            return classes.split(' ').indexOf(className) !== -1;
        });
    }

    function getDropDownHeader() {
        return element.all(by.css('ul.nav.nav-tabs li a.dropdown-toggle')).get(0);
    }

    function getDropDownTabHeader(numVisibleTabs, tabNum) {
        var indexInDropDown = tabNum - numVisibleTabs;
        return element.all(by.css('ul.dropdown-menu li:nth-child(' + indexInDropDown + ") a")).get(0);
    }
});
