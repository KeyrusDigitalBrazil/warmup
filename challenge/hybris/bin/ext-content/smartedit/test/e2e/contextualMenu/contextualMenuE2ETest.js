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
describe('Contextual menu', function() {

    var storefront, perspectives;

    var buildDecoratorName = function(prefix, id, type, index) {
        return prefix + '-' + id + '-' + type + '-' + index;
    };
    beforeEach(function(done) {
        browser.get('test/e2e/contextualMenu/contextualMenuTest.html');
        browser.waitForWholeAppToBeReady();
        storefront = require("../utils/components/Storefront.js");
        perspectives = require("../utils/components/Perspectives.js");
        perspectives.actions.selectPerspective(perspectives.constants.DEFAULT_PERSPECTIVES.ALL).then(function() {
            browser.waitForWholeAppToBeReady();
            done();
        });
    });

    it("Upon loading SmartEdit, contextualMenu named 'INFO' will be added to ComponentType1 and contextualMenu named 'DELETE' will be added to ComponentType2",
        function() {
            browser.switchToIFrame();

            //Assert on ComponentType1
            browser.actions().mouseMove(element(by.id(storefront.constants.COMPONENT_1_ID))).perform();
            expect(element(by.id(buildDecoratorName('INFO', storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE, 0))).isPresent()).toBe(true);
            expect(by.id(buildDecoratorName('DELETE', storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE, 0))).toBeAbsent();

            //Assert on ComponentType2
            browser.actions().mouseMove(element(by.id(storefront.constants.COMPONENT_2_ID))).perform();
            expect(by.id(buildDecoratorName('INFO', storefront.constants.COMPONENT_2_ID, storefront.constants.COMPONENT_2_TYPE, 0))).toBeAbsent();
            expect(element(by.id(buildDecoratorName('DELETE', storefront.constants.COMPONENT_2_ID, storefront.constants.COMPONENT_2_TYPE, 0))).isPresent()).toBe(true);

        }
    );

    it('Display a string template popup', function() {
        browser.switchToIFrame();
        browser.actions().mouseMove(element(by.id(storefront.constants.COMPONENT_1_ID))).perform();
        var e = perspectives.elements.deprecated_getElementInOverlay(storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE);
        browser.click(e.element(by.css(".cmsx-ctx-more-btn")));
        browser.click(by.id(buildDecoratorName('TEMPLATE', storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE, 2)));
        expect(element(by.css("#ctx-template")).isDisplayed()).toBe(true);
    });

    it('Display a templateUrl popup', function() {
        browser.switchToIFrame();
        browser.actions().mouseMove(element(by.id(storefront.constants.COMPONENT_2_ID))).perform();
        browser.click(by.id(buildDecoratorName('TEMPLATEURL', storefront.constants.COMPONENT_2_ID, storefront.constants.COMPONENT_2_TYPE, 1)));
        expect(element(by.css("#ctx-template-url")).isDisplayed()).toBe(true);
    });


    it("contextualMenu item WILL change the DOM element of ComponentType1 WHEN condition callback is called",
        function() {
            browser.switchToIFrame();
            //Assert on ComponentType1
            browser.click(perspectives.elements.deprecated_getElementInOverlay(storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE));
            expect(element(by.className('conditionClass1')).isPresent()).toBe(true);
        });

    it("Can add and remove contextual menu items on the fly", function() {
        // Arrange
        browser.switchToIFrame();

        var component4DecoratorName = buildDecoratorName('INFO', storefront.constants.COMPONENT_4_ID, storefront.constants.COMPONENT_4_TYPE, 0);
        var component3DecoratorName = buildDecoratorName('enable', storefront.constants.COMPONENT_3_ID, storefront.constants.COMPONENT_3_TYPE, 0);

        expect(by.id(component4DecoratorName)).toBeAbsent();

        // Act / Assert
        storefront.assertions.assertComponentInOverlayPresent(storefront.constants.COMPONENT_3_ID, storefront.constants.COMPONENT_3_TYPE, true);
        browser.click(by.id(component3DecoratorName));

        storefront.assertions.assertComponentInOverlayPresent(storefront.constants.COMPONENT_4_ID, storefront.constants.COMPONENT_4_TYPE, true);
        expect(element(by.id(component4DecoratorName)).isPresent()).toBe(true, 'Expected new contextual menu item to be present');

        storefront.assertions.assertComponentInOverlayPresent(storefront.constants.COMPONENT_3_ID, storefront.constants.COMPONENT_3_TYPE, true);
        browser.actions().mouseMove(element(by.id(storefront.constants.COMPONENT_3_ID))).perform();
        browser.click(by.id(component3DecoratorName));

        storefront.assertions.assertComponentInOverlayPresent(storefront.constants.COMPONENT_4_ID, storefront.constants.COMPONENT_4_TYPE, true);
        browser.actions().mouseMove(element(by.id(storefront.constants.COMPONENT_4_ID))).perform();
        browser.waitForAbsence(element(by.id(component4DecoratorName)), 'Expected contextual menu item to be removed');
    });
});
