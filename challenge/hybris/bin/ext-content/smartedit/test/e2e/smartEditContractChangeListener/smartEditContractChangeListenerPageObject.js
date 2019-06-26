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

    var clickThrough = require('../../../smartedit-build/test/e2e/componentObjects/clickThroughOverlayComponentObject');

    function forceClickThroughOverlay(element) {
        return clickThrough.utils.clickThroughOverlay(element);
    }

    var page = {};

    page.elements = {
        enlargeComponentButton: function() {
            return element(by.id('enlargeComponentButton'));
        },
        shrinkComponentButton: function() {
            return element(by.id('shrinkComponentButton'));
        },
        moveComponentButton: function() {
            return element(by.id('moveComponentButton'));
        },
        removeComponentButton: function() {
            return element(by.id('removeComponentButton'));
        },
        toggleComponentTypeButton: function() {
            return element(by.id('toggleComponentTypeButton'));
        },
        changePage: function() {
            return element(by.id('changePage'));
        },
        removeSlotButton: function() {
            return element(by.id('removeSlotButton'));
        },
        addSlotButton: function() {
            return element(by.id('addSlotButton'));
        },
        animateComponentButton: function() {
            return element(by.id('animateComponentButton'));
        },
        stopAnimateComponentButton: function() {
            return element(by.id('stopAnimateComponentButton'));
        },
        pageChangeTest: function() {
            return element(by.tagName('page-change-test'));
        },
        getBoundingClientRect: function(element) {
            return browser.executeScript("return arguments[0].getBoundingClientRect();", element);
        },
        getTotalStoreFrontElements: function() {
            return element(by.id('total-store-front-components')).getText();
        },
        getTotalVisibleStoreFrontElements: function() {
            return element(by.id('total-visible-store-front-components')).getText();
        },
        getTotalSakExecutorElements: function() {
            return element(by.id('total-sak-executor-elements')).getText();
        },
        getTotalResizeListeners: function() {
            return element(by.id('total-resize-listeners')).getText();
        },
        getTotalRepositionListeners: function() {
            return element(by.id('total-reposition-listeners')).getText();
        }
    };

    page.actions = {
        enlargeComponent: function() {
            return forceClickThroughOverlay(page.elements.enlargeComponentButton());
        },
        shrinkComponent: function() {
            return forceClickThroughOverlay(page.elements.shrinkComponentButton());
        },
        moveComponent: function() {
            return forceClickThroughOverlay(page.elements.moveComponentButton());
        },
        removeComponent: function() {
            return forceClickThroughOverlay(page.elements.removeComponentButton());
        },
        toggleComponentType: function() {
            return forceClickThroughOverlay(page.elements.toggleComponentTypeButton());
        },
        changePage: function() {
            return forceClickThroughOverlay(page.elements.changePage());
        },
        removeSlot: function() {
            return forceClickThroughOverlay(page.elements.removeSlotButton());
        },
        addSlot: function() {
            return forceClickThroughOverlay(page.elements.addSlotButton());
        },
        animateComponent: function() {
            return forceClickThroughOverlay(page.elements.animateComponentButton());
        },
        stopAnimateComponent: function() {
            return forceClickThroughOverlay(page.elements.stopAnimateComponentButton());
        }
    };
    page.assertions = {
        pageHasChanged: function(newvalue) {
            return expect(page.elements.pageChangeTest().getText()).toBe(newvalue);
        },

        elementsHaveSameDimensions: function(component1, component2, marginOfError) {

            component1.getSize().then(function(component1Size) {
                var expectedWidth = component1Size.width;
                var expectedHeight = component1Size.height;
                component2.getSize().then(function(component2Size) {
                    if (marginOfError) {
                        expect(Math.abs(component2Size.width - expectedWidth)).toBeLessThan(marginOfError);
                        expect(Math.abs(component2Size.height - expectedWidth)).toBeLessThan(marginOfError);
                    } else {
                        expect(component2Size.width).toBe(expectedWidth);
                        expect(component2Size.height).toBe(expectedHeight);
                    }
                });
            });
        },
        elementsHaveSamePosition: function(component1, component2, marginOfError) {
            page.elements.getBoundingClientRect(component1).then(function(component1Position) {
                var expectedLeft = component1Position.left;
                var expectedTop = component1Position.top;
                page.elements.getBoundingClientRect(component2).then(function(component2Position) {
                    if (marginOfError) {
                        expect(Math.abs(component2Position.left - expectedLeft)).toBeLessThan(marginOfError);
                        expect(Math.abs(component2Position.top - expectedTop)).toBeLessThan(marginOfError);
                    } else {
                        expect(component2Position.left).toBe(expectedLeft);
                        expect(component2Position.top).toBe(expectedTop);
                    }
                });
            });
        },
        overlayAndStoreFrontAreSynced: function() {
            browser.switchToIFrame();
            browser.waitUntil(function() {
                return protractor.promise.all([page.elements.getTotalVisibleStoreFrontElements(), page.elements.getTotalSakExecutorElements()]).then(function(array) {
                    return Number(array[0]) === Number(array[1]);
                });
            }, "Number of SAK executor instances different from the number of visible storefront elements");

            browser.waitUntil(function() {
                return protractor.promise.all([page.elements.getTotalStoreFrontElements(), page.elements.getTotalRepositionListeners()]).then(function(array) {
                    return Number(array[0]) >= Number(array[1]);
                });
            }, "Number of reposition listeners different from the number of visible storefront elements");

            browser.waitUntil(function() {
                return protractor.promise.all([page.elements.getTotalStoreFrontElements(), page.elements.getTotalResizeListeners()]).then(function(array) {
                    return Number(array[0]) >= Number(array[1]);
                });
            }, "Number of resize listeners different from the number of visible storefront elements");


            browser.waitForSelectorToContainText(by.id("healthStatus"), "OK");
        }
    };

    return page;
})();
