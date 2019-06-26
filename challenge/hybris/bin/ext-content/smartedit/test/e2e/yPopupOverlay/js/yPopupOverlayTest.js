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
describe("yPopupOverlay", function() {

    var pageObjects = require("./yPopupOverlayTestPageObjects");

    beforeEach(function() {
        browser.get("test/e2e/yPopupOverlay/index.html");
        browser.waitForPresence(by.css('#test-div'));
    });


    describe('default click to show/hide', function() {
        it('opens on click of the anchor', function() {
            pageObjects.actions.clickAnchor();
            pageObjects.assertions.assertPopupIsVisible();
        });

        it('closes when clicking outside of the popup', function() {
            pageObjects.actions.clickAnchor();
            pageObjects.assertions.assertPopupIsVisible();
            pageObjects.actions.clickAnchor();
            pageObjects.assertions.assertPopupIsNotVisible();
        });
    });

    describe('popup trigger', function() {

        it('Does not open by clicking anchor when trigger is not "click"', function() {
            pageObjects.actions.setTriggerValue('xyz');
            pageObjects.actions.clickAnchor();
            pageObjects.assertions.assertPopupIsNotVisible();
        });

        it('opens on trigger set to true', function() {
            pageObjects.actions.setTriggerValue('true');
            pageObjects.assertions.assertPopupIsVisible();
        });

        it('closes when trigger set to neither click, nor true', function() {
            pageObjects.actions.setTriggerValue('true');
            pageObjects.assertions.assertPopupIsVisible();
            pageObjects.actions.setTriggerValue('BALBLABLA');
            pageObjects.assertions.assertPopupIsNotVisible();
        });
    });

    describe('popup show/hide callbacks', function() {

        it('Executes the show and hide popup callback expressions', function() {

            var oldShow = pageObjects.utils.getShowCount();
            var oldHide = pageObjects.utils.getHideCount();

            pageObjects.actions.clickAnchor();
            expect(pageObjects.utils.getShowCount()).toEqual(oldShow + 1);

            pageObjects.actions.clickAnchor();
            expect(pageObjects.utils.getHideCount()).toEqual(oldHide + 1);
        });
    });

    describe('positioning', function() {

        it('Default - Bottom+Right', function() {
            pageObjects.actions.clickAnchor();

            pageObjects.utils.getAnchorSize().then(function(anchorSize) {
                pageObjects.utils.getAnchorLocation().then(function(anchorLoc) {
                    pageObjects.utils.getPopupLocation().then(function(popupLoc) {
                        expect(Math.round(popupLoc.x)).toBe(anchorLoc.x);
                        expect(Math.round(popupLoc.y)).toBe(anchorLoc.y + anchorSize.height);
                    });
                });
            });
        });

        it('Top+Right', function() {
            pageObjects.actions.setPopupTopAlign();
            pageObjects.actions.setPopupRightAlign();

            pageObjects.actions.clickAnchor();

            pageObjects.utils.getAnchorLocation().then(function(anchorLoc) {
                pageObjects.utils.getPopupSize().then(function(popupSize) {
                    pageObjects.utils.getPopupLocation().then(function(popupLoc) {
                        expect(Math.round(popupLoc.x)).toBe(anchorLoc.x);
                        expect(Math.round(popupLoc.y)).toBe(anchorLoc.y - popupSize.height);
                    });
                });
            });
        });

        it('Top+Left', function() {
            pageObjects.actions.setPopupTopAlign();
            pageObjects.actions.setPopupLeftAlign();

            pageObjects.actions.clickAnchor();

            pageObjects.utils.getAnchorLocation().then(function(anchorLoc) {
                pageObjects.utils.getAnchorSize().then(function(anchorSize) {
                    pageObjects.utils.getPopupSize().then(function(popupSize) {
                        pageObjects.utils.getPopupLocation().then(function(popupLoc) {
                            expect(Math.round(popupLoc.x)).toBe(anchorLoc.x + anchorSize.width - popupSize.width);
                            expect(Math.round(popupLoc.y)).toBe(anchorLoc.y - popupSize.height);
                        });
                    });
                });
            });
        });

        it('Bottom+Left', function() {
            pageObjects.actions.setPopupBottomAlign();
            pageObjects.actions.setPopupLeftAlign();

            pageObjects.actions.clickAnchor();

            pageObjects.utils.getAnchorLocation().then(function(anchorLoc) {
                pageObjects.utils.getAnchorSize().then(function(anchorSize) {
                    pageObjects.utils.getPopupSize().then(function(popupSize) {
                        pageObjects.utils.getPopupLocation().then(function(popupLoc) {
                            expect(Math.round(popupLoc.x)).toBe(anchorLoc.x + anchorSize.width - popupSize.width);
                            expect(Math.round(popupLoc.y)).toBe(anchorLoc.y + anchorSize.height);
                        });
                    });
                });
            });
        });

    });

});
