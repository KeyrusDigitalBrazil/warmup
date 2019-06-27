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
module.exports = (function() {

    var storefrontObject = {};

    storefrontObject.actions = {

        deepLink: function() {
            browser.switchToIFrame().then(function() {
                storefrontObject.actions.moveToComponent(storefrontObject.constants.DEEP_LINKS_SLOT_ID, 'ContentSlot');
                browser.click(storefrontObject.elements.secondPageLink());
                browser.switchToParent();
                browser.waitForWholeAppToBeReady();
            });
        },

        goToThirdPage: function() {
            browser.switchToIFrame().then(function() {
                storefrontObject.actions.moveToComponent(storefrontObject.constants.DEEP_LINKS_SLOT_ID, 'ContentSlot');
                browser.click(storefrontObject.elements.thirdPageLink());
                browser.switchToParent();
                browser.waitForWholeAppToBeReady();
            });
        },

        goToRequireJsPage: function() {
            browser.switchToIFrame().then(function() {
                storefrontObject.actions.moveToComponent(storefrontObject.constants.DEEP_LINKS_SLOT_ID, 'ContentSlot');
                browser.click(storefrontObject.elements.requireJsLink());
                browser.switchToParent();
                browser.waitForWholeAppToBeReady();
            });
        },

        clickRequireJsSuccessButton: function() {
            browser.switchToIFrame().then(function() {
                browser.click(storefrontObject.elements.requireJsSuccessButton());
            });
        },

        moveToComponent: function(componentId, componentType) {
            browser.switchToIFrame();
            browser.actions()
                .mouseMove(storefrontObject.elements.getOriginalComponentById(componentId, componentType), {
                    x: 0,
                    y: 0
                })
                .perform();
            return browser.waitForVisibility(storefrontObject.elements.getOriginalComponentById(componentId, componentType));
        },

        waitForNonPresenceOfSmartEditOverlay: function() {
            return browser.waitForAbsence("#smarteditoverlay");
        }

    };

    var _assertElementContains = function(element, content) {
        expect(element.getText()).toContain(content);
    };

    var _assertElementNotContains = function(element, content) {
        expect(element.getText()).not.toContain(content);
    };

    var _assertElementPresent = function(element, isPresent) {
        expect(element.isPresent()).toBe(isPresent);
    };

    var _assertElementDisplayed = function(element, isDisplayed) {
        expect(element.isDisplayed()).toBe(isDisplayed);
    };

    storefrontObject.assertions = {

        assertStoreFrontIsDisplayed: function() {
            expect(storefrontObject.elements.getBrowserUrl()).toContain('/storefront');
        },

        assertComponentContains: function(componentModel, content) {
            _assertElementContains(storefrontObject.elements.getComponentByModel(componentModel), content);
        },

        assertComponentHtmlContains: function(componentHtmlId, content) {
            expect(_getComponentInnerHtml(componentHtmlId)).toContain(content);
        },

        assertComponentInOverlayPresent: function(componentId, componentType, isPresent) {
            if (isPresent) {
                storefrontObject.actions.moveToComponent(componentId, componentType);
            }
            _assertElementPresent(storefrontObject.elements.getComponentInOverlayById(componentId, componentType), isPresent);
        },

        assertComponentInOverlayContains: function(componentId, componentType, content) {
            storefrontObject.actions.moveToComponent(componentId, componentType);
            _assertElementContains(storefrontObject.elements.getComponentInOverlayById(componentId, componentType), content);
        },

        assertComponentInOverlayNotContains: function(componentId, componentType, content) {
            storefrontObject.actions.moveToComponent(componentId, componentType);
            _assertElementNotContains(storefrontObject.elements.getComponentInOverlayById(componentId, componentType), content);
        },

        assertSmartEditOverlayDisplayed: function(isDisplayed) {
            _assertElementDisplayed(storefrontObject.elements.getSmartEditOverlay(), isDisplayed);
        },

        assertSmartEditOverlayPresent: function(isPresent) {
            _assertElementPresent(storefrontObject.elements.getSmartEditOverlay(), isPresent);
        },

        assertDecoratorShowsOnComponent: function(componentId, componentType, decoratorClass) {
            storefrontObject.actions.moveToComponent(componentId, componentType);
            browser.waitToBeDisplayed(by.css(_buildDecoratorSelector(componentId, componentType, decoratorClass)), "could not find decorator " + decoratorClass);
        },
        assertDecoratorDoesntShowOnComponent: function(componentId, componentType, decoratorClass) {
            browser.waitNotToBeDisplayed(by.css(_buildDecoratorSelector(componentId, componentType, decoratorClass)), "should not have found decorator " + decoratorClass);
        }


    };

    storefrontObject.constants = {

        COMPONENT_1_ID: 'component1',
        COMPONENT_2_ID: 'component2',
        COMPONENT_3_ID: 'component3',
        COMPONENT_4_ID: 'component4',

        COMPONENT_1_TYPE: 'componentType1',
        COMPONENT_2_TYPE: 'componentType2',
        COMPONENT_3_TYPE: 'SimpleResponsiveBannerComponent',
        COMPONENT_4_TYPE: 'componentType4',

        TOP_HEADER_SLOT_ID: 'topHeaderSlot',
        BOTTOM_HEADER_SLOT_ID: 'bottomHeaderSlot',
        SEARCH_BOX_SLOT: 'searchBoxSlot',
        FOOTER_SLOT_ID: 'footerSlot',
        OTHER_SLOT_ID: 'otherSlot',
        COMPONENT1_NAME: 'component1',
        COMPONENT4_NAME: 'component4',
        STATIC_SLOT_ID: 'staticDummySlot',
        STATIC_COMPONENT_NAME: 'staticDummyComponent',
        DEEP_LINKS_SLOT_ID: 'deepLinksSlot'

    };

    var _buildDecoratorSelector = function(componentId, componentType, decoratorClass) {
        return _buildComponentSelector(componentId, componentType, true) + " div." + decoratorClass;
    };

    var _buildComponentSelector = function(componentId, componentType, inOverlay) {
        var selector = '.smartEditComponent';

        if (inOverlay) {
            selector = '#smarteditoverlay ' + selector + 'X';
        }

        selector += '[data-smartedit-component-id="' + componentId + '"]';

        if (componentType) {
            selector += '[data-smartedit-component-type="' + componentType + '"]';
        }

        return selector;
    };

    var _getComponentInnerHtml = function(componentHtmlId) {
        browser.switchToIFrame();
        return element(by.css('#' + componentHtmlId + ' div')).getText();
    };

    storefrontObject.elements = {

        getBrowserUrl: function() {
            return browser.getCurrentUrl();
        },

        componentButton: function() {
            return element(by.css('#submitButton'));
        },

        secondComponentButton: function() {
            return element(by.id('secondaryButton'));
        },

        component1: function() {
            return storefrontObject.elements.getComponentById(
                storefrontObject.constants.COMPONENT_1_ID);
        },

        component2: function() {
            return storefrontObject.elements.getComponentById(
                storefrontObject.constants.COMPONENT_2_ID);
        },

        component3: function() {
            return storefrontObject.elements.getComponentById(
                storefrontObject.constants.COMPONENT_3_ID);
        },

        secondPageLink: function() {
            return element(by.id('deepLink'));
        },

        thirdPageLink: function() {
            return element(by.id('thirdPage'));
        },

        requireJsLink: function() {
            return element(by.id('deepLinkRequireJs'));
        },

        requireJsSuccessButton: function() {
            return element(by.id('requirejs-success-button'));
        },

        addToCartButton: function() {
            return element(by.id('addToCart'));
        },

        addToCartFeedback: function() {
            return element(by.id('feedback'));
        },

        secondPage: {
            component2: function() {
                return element(by.css('#component2 div'));
            }
        },

        getComponentById: function(componentId) {
            browser.switchToIFrame();
            return element(by.css(_buildComponentSelector(componentId)));
        },

        getComponentByModel: function(componentModel) {
            browser.switchToIFrame();
            return element(by.model(componentModel));
        },

        getComponentInOverlayById: function(componentId, componentType) {
            browser.switchToIFrame();
            return element(by.css(_buildComponentSelector(componentId, componentType, true)));
        },

        getOriginalComponentById: function(componentId, componentType) {
            browser.switchToIFrame();
            return element(by.css(_buildComponentSelector(componentId, componentType, false)));
        },

        getSmartEditOverlay: function() {
            browser.switchToIFrame();
            return element(by.id("smarteditoverlay"));
        }

    };

    return storefrontObject;

})();
