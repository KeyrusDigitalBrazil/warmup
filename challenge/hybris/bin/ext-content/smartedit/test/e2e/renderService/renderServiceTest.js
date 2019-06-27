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
describe('Component and Slot Rendering', function() {

    var EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER = 'Some dirtied content';
    var EXPECTED_CONTENT_1_AFTER_RENDER = 'test component 1';
    var EXPECTED_CONTENT_2_AFTER_RENDER = 'test component 2';

    var decorators = require('../utils/components/Decorators.js');
    var page = require('../utils/components/Page.js');
    var perspectives = require('../utils/components/Perspectives.js');
    var storefront = require('../utils/components/Storefront.js');
    var toolbar = require('../utils/components/WhiteToolbarComponentObject.js');

    // displaying the mocked 'render service' storefront with the 'ALL' perspective
    beforeEach(function() {
        page.actions.getAndWaitForWholeApp('test/e2e/renderService/index.html');
        perspectives.actions.selectPerspective(perspectives.constants.DEFAULT_PERSPECTIVES.ALL);
    });

    it('WHEN the user triggers a re-render from SmartEdit THEN the component is re-rendered with new content', function() {
        browser.click(decorators.elements.dirtyContentDecorator(storefront.constants.COMPONENT_1_ID));
        expect(storefront.elements.component1().getText()).toContain(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER,
            'Expected component 1 content to contain default content');
        expect(storefront.elements.component1().getText()).not.toContain(EXPECTED_CONTENT_1_AFTER_RENDER,
            'Expected component 1 not to contain re-rendered content');
        browser.click(decorators.elements.renderDecorator(storefront.constants.COMPONENT_1_ID));
        browser.wait(EC.presenceOf(storefront.elements.component1()), 5000, 'Timed out waiting for presence of component 1');
        expect(storefront.elements.component1().getText()).not.toContain(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER,
            'Expected component 1 content not to contain default content');
        expect(storefront.elements.component1().getText()).toContain(EXPECTED_CONTENT_1_AFTER_RENDER,
            'Expected component 1 to contain re-rendered content');
    });

    it('WHEN the user triggers a re-render from SmartEdit container THEN the component is re-rendered with new content', function() {
        browser.click(decorators.elements.dirtyContentDecorator(storefront.constants.COMPONENT_1_ID));
        expect(storefront.elements.component1().getText()).toContain(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER,
            'Expected component 1 content to contain default content');
        expect(storefront.elements.component1().getText()).not.toContain(EXPECTED_CONTENT_1_AFTER_RENDER,
            'Expected component 1 not to contain re-rendered content');
        browser.click(toolbar.elements.renderButton());
        expect(storefront.elements.component1().getText()).not.toContain(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER,
            'Expected component 1 content not to contain default content');
        expect(storefront.elements.component1().getText()).toContain(EXPECTED_CONTENT_1_AFTER_RENDER,
            'Expected component 1 to contain re-rendered content');
    });

    it('WHEN the user triggers a slot re-render from SmartEdit THEN the slot is re-rendered with content from the storefront', function() {
        browser.click(decorators.elements.dirtyContentDecorator(storefront.constants.COMPONENT_1_ID));
        browser.click(decorators.elements.dirtyContentDecorator(storefront.constants.COMPONENT_2_ID));
        browser.click(decorators.elements.renderSlotDecorator(storefront.constants.TOP_HEADER_SLOT_ID));
        assertComponent1IsReRendered();
        assertComponent2IsReRendered();
    });

    it('WHEN the user triggers a slot re-render from SmartEdit Container THEN the slot is re-rendered with content from the storefront', function() {
        browser.click(decorators.elements.dirtyContentDecorator(storefront.constants.COMPONENT_1_ID));
        browser.click(decorators.elements.dirtyContentDecorator(storefront.constants.COMPONENT_2_ID));
        browser.click(toolbar.elements.renderSlotButton());
        assertComponent1IsReRendered();
        assertComponent2IsReRendered();
    });

    function assertComponent1IsReRendered() {
        browser.wait(function() {
            return storefront.elements.component1().getText().then(function(text) {
                return text;
            }, function() {
                return '';
            }).then(function(componentText) {
                return componentText.indexOf(EXPECTED_CONTENT_1_AFTER_RENDER) >= 0 &&
                    componentText.indexOf(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER) < 0;
            });
        }, 5000, 'Expected component to re-render');
    }

    function assertComponent2IsReRendered() {
        browser.wait(function() {
            return storefront.elements.component2().getText().then(function(text) {
                return text;
            }, function() {
                return '';
            }).then(function(componentText) {
                return componentText.indexOf(EXPECTED_CONTENT_2_AFTER_RENDER) >= 0 &&
                    componentText.indexOf(EXPECTED_COMPONENT_CONTENT_BEFORE_RENDER) < 0;
            });
        }, 5000, 'Expected component to re-render');
    }

});

/* ---------
 [ hotkeys ]
 -------- */

// toggle of SE overlay

describe("Effects of 'mode switch' key press:", function() {

    var experienceSelector = require("../utils/components/ExperienceSelector.js"),
        HotKeys = require("../utils/components/HotKeys.js"),
        page = require('../utils/components/Page.js'),
        perspectives = require('../utils/components/Perspectives.js'),
        storefront = require('../utils/components/Storefront.js'),
        inflectionPoint = require('../utils/components/InflectionPoint.js');

    // displaying the mocked 'render service' storefront with the 'ALL' perspective
    beforeEach(function(done) {
        page.actions.getAndWaitForWholeApp('test/e2e/renderService/index.html').then(function() {
            perspectives.actions.selectPerspective(perspectives.constants.DEFAULT_PERSPECTIVES.ALL).then(function() {
                browser.waitForWholeAppToBeReady().then(function() {
                    done();
                });
            });
        });
    });

    // overlay
    it("GIVEN the user is not on the Preview perspective" +
        " WHEN the 'mode switch' hotkey gets pressed" +
        " THEN the SE overlay is hidden and the hotkey notification is shown",
        function() {
            storefront.assertions.assertSmartEditOverlayDisplayed(true);
            HotKeys.actions.pressHotKeyModeSwitch().then(function() {
                storefront.assertions.assertSmartEditOverlayDisplayed(false);
            }).then(function() {
                HotKeys.assertions.assertHotkeyNotificationPresence();

                HotKeys.actions.pressHotKeyModeSwitch().then(function() {
                    storefront.assertions.assertSmartEditOverlayDisplayed(true);
                }).then(function() {
                    HotKeys.assertions.assertHotkeyNotificationAbsence();
                });
            });
        }
    );

    // overlay after navigation
    it("GIVEN the user is not on the Preview perspective" +
        " WHEN the 'mode switch' hotkey gets pressed and the user navigates to another page" +
        " THEN the SE overlay remains hidden after navigation",
        function() {
            storefront.assertions.assertSmartEditOverlayDisplayed(true);
            HotKeys.actions.pressHotKeyModeSwitch().then(function() {
                return storefront.assertions.assertSmartEditOverlayDisplayed(false);
            }).then(function() {
                return storefront.actions.deepLink();
            }).then(function() {
                storefront.assertions.assertSmartEditOverlayDisplayed(false);
            });
        }
    );

    // experience panel
    it("GIVEN the user is not on the Preview perspective and the experience panel is opened" +
        " WHEN the 'mode switch' hotkey gets pressed" +
        " THEN the experience panel gets closed",
        function() {
            experienceSelector.actions.widget.openExperienceSelector().then(function() {
                expect(experienceSelector.elements.widget.getExperienceMenu().isDisplayed()).toBe(true);
                HotKeys.actions.pressHotKeyModeSwitch().then(function() {
                    expect(experienceSelector.elements.widget.getExperienceMenu().isDisplayed()).toBe(false);
                });
            });
        }
    );

    // inflection point selector
    it("GIVEN the user is not on the Preview perspective" +
        " WHEN the inflection point menu is opened and the user press the 'mode switch' hotkey" +
        " THEN the inflection point menu gets closed",
        function() {
            inflectionPoint.actions.openInflectionPointMenu().then(function() {
                expect(inflectionPoint.elements.getInflectionPointMenu().isDisplayed()).toBe(true);
                HotKeys.actions.pressHotKeyModeSwitch().then(function() {
                    expect(inflectionPoint.elements.getInflectionPointMenu().isDisplayed()).toBe(false);
                });
            });
        }
    );

    // perspective selector
    it("GIVEN the user is not on the Preview perspective and the perspective selector is opened" +
        " WHEN the 'mode switch' hotkey gets pressed" +
        " THEN the perspective selector gets closed",
        function() {
            perspectives.actions.openPerspectiveSelectorDropdown().then(function() {
                perspectives.assertions.assertPerspectiveSelectorDropdownDisplayed(true);
                HotKeys.actions.pressHotKeyModeSwitch().then(function() {
                    perspectives.assertions.assertPerspectiveSelectorDropdownDisplayed(false);
                });
            });
        }
    );

});
