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

    var componentObject = {};

    componentObject.constants = {
        TOP_HEADER_SLOT_ID: 'topHeaderSlot',
        BOTTOM_HEADER_SLOT_ID: 'bottomHeaderSlot',
        SEARCH_BOX_SLOT: 'searchBoxSlot',
        FOOTER_SLOT_ID: 'footerSlot',
        OTHER_SLOT_ID: 'otherSlot',
        COMPONENT1_NAME: 'component1',
        COMPONENT2_NAME: 'component2',
        COMPONENT4_NAME: 'component4',
        STATIC_SLOT_ID: 'staticDummySlot',
        STATIC_COMPONENT_NAME: 'staticDummyComponent',
        EMPTY_DUMMY_SLOT_ID: 'emptyDummySlot'
    };

    componentObject.elements = {
        getComponentById: function(componentId) {
            return browser.switchToIFrame().then(function() {
                return element(by.css('.smartEditComponent[data-smartedit-component-id=' + componentId + ']'));
            });
        },
        getComponentLocationById: function(componentId) {
            return componentObject.elements.getComponentById(componentId).then(function(component) {
                return component.getLocation();
            });
        },
        getComponentInOverlayById: function(slotId) {
            return element(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + slotId + "']"));
        },
        getComponentsBySlotId: function(slotId, expectedElementsInSlotCount) {
            return browser.switchToIFrame().then(function() {
                return browser.waitUntil(function() {
                    var deferred = protractor.promise.defer();
                    element.all(by.css(".smartEditComponent[data-smartedit-component-id='" + slotId + "'] .smartEditComponent")).count().then(function(count) {
                        deferred.fulfill(count === expectedElementsInSlotCount);
                    });
                    return deferred.promise;
                }, "Expected " + expectedElementsInSlotCount + " components in slot").then(function() {
                    return element.all(by.css("#smarteditoverlay .smartEditComponentX[data-smartedit-component-id='" + slotId + "'] .smartEditComponentX"));
                });
            });
        },
        getComponentByAttributeAndValue: function(attribute, value) {
            return browser.switchToIFrame().then(function() {
                return element(by.css('.smartEditComponent[' + attribute + '=' + value + ']'));
            });
        }
    };

    componentObject.actions = {
        scrollComponentIntoView: function(componentId) {
            browser.waitUntilNoModal();
            return componentObject.elements.getComponentById(componentId).then(function(component) {
                return component.getLocation().then(function(location) {
                    return browser.executeScript('window.scrollTo(' + location.x + ", " + location.y + ");");
                });
            });
        },
        moveToComponent: function(componentId) {
            browser.waitUntilNoModal();
            return browser.switchToIFrame().then(function() {
                return componentObject.elements.getComponentById(componentId).then(function(element) {
                    return browser.actions()
                        .mouseMove(element)
                        .perform();
                });
            });
        },
        moveToComponentByAttributeAndValue: function(attribute, value) {
            return browser.switchToIFrame().then(function() {
                return componentObject.elements.getComponentByAttributeAndValue(attribute, value).then(function(element) {
                    return browser.actions()
                        .mouseMove(element)
                        .perform();
                });
            });
        },
        goToSecondPage: function() {
            browser.waitForWholeAppToBeReady();
            return browser.switchToIFrame().then(function() {
                browser.click(by.id('deepLink'));
                browser.waitForFrameToBeReady();
                browser.waitUntilNoModal();
                return;
            });
        },
        goToThirdPage: function() {
            browser.waitForWholeAppToBeReady();
            return browser.switchToIFrame().then(function() {
                browser.click(by.id('thirdPage'));
                browser.waitForFrameToBeReady();
                browser.waitUntilNoModal();
                return;
            });
        }
    };

    return componentObject;
})();
