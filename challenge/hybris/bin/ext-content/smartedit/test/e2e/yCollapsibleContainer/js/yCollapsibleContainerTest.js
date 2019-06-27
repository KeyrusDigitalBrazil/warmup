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
describe("Collapsible Container", function() {

    var pageObjects = require("./yCollapsibleContainerTestPageObjects.js");

    beforeEach(function() {
        browser.get("test/e2e/yCollapsibleContainer/index.html");
    });

    describe("Icon Display:\n", function() {

        it("GIVEN the collapsible container is displayed in its default rendering\n" +
            "THEN the icon is rendered in the title's right-hand side",
            function() {
                pageObjects.assertions.iconIsRightAligned();
            }
        );

        it("GIVEN the collapsible container is displayed in its default rendering\n" +
            "THEN the collapsible container' content is not visible.",
            function() {
                pageObjects.assertions.contentIsNotVisible();
            }
        );

        it("GIVEN the collapsible container is displayed in its default rendering\n" +
            "WHEN the configuration of the collapsible container sets for a left-aligned icon\n" +
            "THEN the icon is rendered in the title's left-hand side",
            function() {
                pageObjects.actions.clickOnLeftAlignmentButton();
                pageObjects.assertions.iconIsLeftAligned();
            }
        );

        it("GIVEN the collapsible container is displayed in its default rendering\n" +
            "WHEN the configuration of the collapsible container disables the display of the icon\n" +
            "THEN the icon is not rendered in the title",
            function() {
                pageObjects.actions.clickOnWithoutIconButton();
                pageObjects.assertions.iconIsNotVisible();
            }
        );

    });

    describe("Title Display:\n", function() {

        it("GIVEN the collapsible container is displayed in its default rendering\n" +
            "WHEN the transcluded title is of HTML type\n" +
            "THEN the transcluded title gets properly rendered and remains active",
            function() {
                pageObjects.actions.clickOnHtmlTitleButton();
                pageObjects.assertions.htmlTitleIsVisible();
            }
        );

        it("GIVEN the collapsible container is displayed in its default rendering\n" +
            "WHEN the transcluded title is of HTML type\n" +
            "THEN the transcluded title gets properly rendered and remains active",
            function() {
                pageObjects.actions.clickOnTextTitleButton();
                pageObjects.assertions.textTitleIsVisible();
            }
        );

        it("GIVEN the collapsible container is displayed in its default rendering\n" +
            "WHEN no title has been indicated for transclusion\n" +
            "AND no action icon is being displayed\n" +
            "THEN the title is rendered empty but remains clickable",
            function() {
                pageObjects.actions.clickOnWithoutIconButton();
                pageObjects.actions.clickOnTitle();
                pageObjects.assertions.contentIsVisible();
            }
        );

    });

    describe("Content Display:\n", function() {

        it("GIVEN the collapsible container is collapsed\n" +
            "WHEN the collapsible container's title is clicked\n" +
            "THEN the collapsible container's content gets expanded",
            function() {
                pageObjects.actions.clickOnTitle();
                pageObjects.assertions.contentIsVisible();
            }
        );

        it("GIVEN the collapsible container is expanded\n" +
            "WHEN the collapsible container's title is clicked\n" +
            "THEN the collapsible container's content gets collapsed",
            function() {
                pageObjects.actions.clickOnExpandedContainer();
                pageObjects.assertions.contentIsNotVisible();
            }
        );

    });


});
