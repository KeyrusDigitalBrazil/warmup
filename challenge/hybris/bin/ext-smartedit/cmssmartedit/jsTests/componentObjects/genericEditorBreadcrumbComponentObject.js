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
var genericEditorCommons;
if (typeof require !== 'undefined') {
    genericEditorCommons = require('./commonGenericEditorComponentObject');
}

module.exports = (function() {

    var componentObject = {};

    componentObject.constants = {};

    componentObject.elements = {
        getBreadcrumb: function() {
            var modalElement = genericEditorCommons.elements.getTopEditorModal();
            browser.waitForPresence(modalElement);
            var breadcrumb = modalElement.element(by.css('generic-editor-breadcrumb'));
            browser.waitForPresence(breadcrumb);
            return breadcrumb;
        },
        getBreadcrumbElementInLevel: function(expectedNestingLevel) {
            var breadcrumb = this.getBreadcrumb();
            browser.waitForPresence(breadcrumb);
            browser.waitForPresence(breadcrumb.element(by.css('.se-ge-breadcrumb__item')));
            return breadcrumb.all(by.css('.se-ge-breadcrumb__item')).get(expectedNestingLevel - 1);
        },
        getElementTitleByNestingLevel: function(expectedNestingLevel) {
            var breadcrumb = componentObject.elements.getBreadcrumbElementInLevel(expectedNestingLevel);
            browser.waitForPresence(breadcrumb);
            var title = breadcrumb.element(by.css('.se-ge-breadcrumb__title'));
            browser.waitForPresence(title);
            return title;
        },
        getElementInfoByNestingLevel: function(expectedNestingLevel) {
            var breadcrumb = componentObject.elements.getBreadcrumbElementInLevel(expectedNestingLevel);
            browser.waitForPresence(breadcrumb);
            var info = breadcrumb.element(by.css('.se-ge-breadcrumb__info'));
            browser.waitForPresence(info);
            return info;
        }
    };

    componentObject.actions = {};

    componentObject.assertions = {
        componentIsDisplayedInCorrectNestingLevel: function(componentName, componentType, expectedNestingLevel) {
            expect(componentObject.elements.getElementTitleByNestingLevel(expectedNestingLevel).getText()).toBe(componentName,
                'Expected breadcrumb to show ' + componentName + ' at the ' + expectedNestingLevel + ' level.');
            expect(componentObject.elements.getElementInfoByNestingLevel(expectedNestingLevel).getText()).toBe(componentType,
                'Expected breadcrumb to show ' + componentType + ' at the ' + expectedNestingLevel + ' level.');
        }
    };

    componentObject.utils = {};

    return componentObject;

}());
