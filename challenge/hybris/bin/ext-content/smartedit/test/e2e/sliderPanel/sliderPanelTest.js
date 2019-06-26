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
describe("Slider Panel", function() {

    var componentObjects = require("../utils/components/sliderPanelComponentObjects"),
        pageObjects = require("./sliderPanelTestPageObjects.js");

    beforeEach(function() {
        browser.get("test/e2e/sliderPanel/sliderPanelTest.html");
    });

    describe("Default Rendering:", function() {

        it("GIVEN the page contains a 'modal' slider panel " +
            "WHEN the page gets loaded or when the modal is displayed" +
            "THEN the slider panel is hidden by default",
            function() {

                componentObjects.assertions.assertForNonPresenceOfModalSliderPanel();

                pageObjects.actions.showModal().then(function() {
                    var sliderPanel = componentObjects.elements.getModalSliderPanel();
                    expect(sliderPanel.isPresent()).toBe(true);
                    expect(sliderPanel.isDisplayed()).toBe(false);
                });

            }
        );

    });

    describe("Slider panel get properly displayed", function() {

        beforeEach(function() {
            pageObjects.actions.showSliderPanel();
        });

        it("GIVEN the page contains a 'modal' slider panel " +
            "WHEN the modal and slider panel get both displayed" +
            "THEN the slider panel gets properly displayed",
            function() {
                expect(componentObjects.elements.getModalSliderPanel().isDisplayed()).toBe(true);
            }
        );

        it("GIVEN the page contains a 'modal' slider panel " +
            "WHEN the modal and slider panel get displayed" +
            "THEN the save button is rendered as disabled by default",
            function() {
                expect(componentObjects.elements.getModalSliderPanelTitle().getText()).toContain("Slider Panel Title");
                expect(componentObjects.elements.getModalSliderPanelCancelButton().isDisplayed()).toBe(true);
                expect(componentObjects.elements.getModalSliderPanelCancelButton().isEnabled()).toBe(true);
                expect(componentObjects.elements.getModalSliderPanelSaveButton().isDisplayed()).toBe(true);
                expect(componentObjects.elements.getModalSliderPanelSaveButton().isEnabled()).toBe(false);
            }
        );

    });

    describe("Overlaying content is not visible but can be scrolled", function() {

        beforeEach(function() {
            pageObjects.actions.showSliderPanel();
        });

        it("GIVEN the page contains a 'modal' slider panel " +
            "WHEN the slider panel is displayed" +
            "THEN any overlaying content is not visible",
            function() {
                browser.testThatOverflowingContentIsHidden(componentObjects.elements.getModalSliderPanelBody());
            }
        );

    });

    describe("Save is enabled when content is defined as 'isDirty'", function() {

        beforeEach(function() {
            pageObjects.actions.showSliderPanel();
            pageObjects.actions.clickOnIsDirtySwitch();
        });

        it("GIVEN the page contains a 'modal' slider panel " +
            "WHEN the slider panel is displayed in dirty mode" +
            "THEN the save button gets enabled and slider panel is hidden on click",
            function() {

                expect(componentObjects.elements.getModalSliderPanel().isDisplayed()).toBe(true);
                expect(componentObjects.elements.getModalSliderPanelSaveButton().isDisplayed()).toBe(true);
                expect(componentObjects.elements.getModalSliderPanelSaveButton().isEnabled()).toBe(true);

                componentObjects.actions.clickOnModalSliderPanelSaveButton();
                expect(componentObjects.elements.getModalSliderPanel().isDisplayed()).toBe(false);

            }
        );

    });

    describe("Slider panel is hidden on cancel", function() {

        beforeEach(function() {
            pageObjects.actions.showSliderPanel();
        });

        it("GIVEN the page contains a 'modal' slider panel " +
            "WHEN the slider panel is displayed in dirty mode" +
            "THEN the save button gets enabled",
            function() {
                expect(componentObjects.elements.getModalSliderPanel().isDisplayed()).toBe(true);
                componentObjects.actions.clickOnModalSliderPanelCancelButton();
                expect(componentObjects.elements.getModalSliderPanel().isDisplayed()).toBe(false);
            }
        );

    });

    describe("Slider panel shows confirmation on dismiss (callback is called)", function() {

        beforeEach(function() {
            pageObjects.actions.showSliderPanel();
        });

        it("GIVEN the page contains a 'modal' slider panel " +
            "WHEN the dismiss icon is clicked on slider panel " +
            "THEN a confirmation window opens",
            function() {
                expect(componentObjects.elements.getModalSliderPanel().isDisplayed()).toBe(true);
                componentObjects.actions.clickOnModalSliderPanelDismissButton();
                componentObjects.assertions.checkIfConfirmationModalIsPresent();
            }
        );

    });

});
