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

    var pageObjects = {

        actions: {

            /* [ icon ] */
            clickOnLeftAlignmentButton: function() {
                return browser.click(pageObjects.elements.getButton.leftAlignment());
            },
            clickOnWithoutIconButton: function() {
                return browser.click(pageObjects.elements.getButton.withoutIcon());
            },

            /* [ title ] */
            clickOnTextTitleButton: function() {
                return browser.click(pageObjects.elements.getButton.textTitle());
            },
            clickOnHtmlTitleButton: function() {
                return browser.click(pageObjects.elements.getButton.htmlTitle());
            },
            clickOnTitle: function() {
                return browser.click(pageObjects.elements.getTitle());
            },
            clickOnExpandedContainer: function() {
                return pageObjects.actions.clickOnTitle().then(function() {
                    return pageObjects.actions.clickOnTitle();
                });
            }
        },

        assertions: {

            /* [ icon ] */
            iconIsLeftAligned: function() {
                expect(pageObjects.elements.getIcon().isDisplayed()).toBe(true);
                expect(pageObjects.elements.getIcon().getCssValue("order")).toBe("1");
                expect(pageObjects.elements.getTitle().getCssValue("order")).toBe("2");
            },
            iconIsRightAligned: function() {
                expect(pageObjects.elements.getIcon().isDisplayed()).toBe(true);
                expect(pageObjects.elements.getIcon().getCssValue("order")).toBe("2");
                expect(pageObjects.elements.getTitle().getCssValue("order")).toBe("1");
            },
            iconIsNotVisible: function() {
                expect(browser.findElement("a.yCollapsibleContainer__icon", false).isDisplayed()).toBe(false);
            },

            /* [ title ] */
            htmlTitleIsVisible: function() {
                expect(pageObjects.elements.getHtmlTitle().isDisplayed()).toBe(true);
            },
            textTitleIsVisible: function() {
                expect(pageObjects.elements.getTitle().getText()).toContain("plain text as a title");
            },

            /* [ content ] */
            contentIsVisible: function() {
                expect(pageObjects.elements.getContent().getAttribute("aria-expanded")).toBe("true");
            },
            contentIsNotVisible: function() {
                expect(pageObjects.elements.getContent().getAttribute("aria-expanded")).toBe("false");

            }

        },

        constants: {},

        elements: {

            /* [ title ] */
            getHtmlTitle: function() {
                return browser.findElement("#content-html", true);
            },
            getTitle: function() {
                return browser.findElement("a.yCollapsibleContainer__title", true);
            },
            getIcon: function() {
                return browser.findElement("a.yCollapsibleContainer__icon", true);
            },

            /* [ content ] */
            getContent: function() {
                return browser.findElement(".panel-collapse", false);
            },

            /* [ button ] */
            getButton: {
                // icon
                leftAlignment: function() {
                    return browser.findElement("#button-left", true);
                },
                withoutIcon: function() {
                    return browser.findElement("#button-without-icon", true);
                },
                // title
                textTitle: function() {
                    return browser.findElement("#button-text", true);
                },
                htmlTitle: function() {
                    return browser.findElement("#button-html", true);
                },
                // display
                expanded: function() {
                    return browser.findElement("#button-expanded", true);
                },
                collapsed: function() {
                    return browser.findElement("#button-collapsed", true);
                },
                // reset
                reset: function() {
                    return browser.findElement("#button-reset", true);
                }
            }

        },

        utils: {}

    };

    return pageObjects;

})();
