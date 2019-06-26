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
describe('Test Routing', function() {

    var page;
    beforeEach(function() {
        page = require("../utils/components/Page.js");
        var perspectives = require("../utils/components/Perspectives.js");

        return page.actions.getAndWaitForWholeApp('test/e2e/routing').then(function() {
            return perspectives.actions.selectPerspective(perspectives.constants.DEFAULT_PERSPECTIVES.ALL).then(function() {
                return browser.waitForWholeAppToBeReady();
            });
        });

    });
    afterEach(function() {
        browser.ignoreSynchronization = false;
    });

    it('navigates to a custom view from the smartedit container', function() {
        expect(element(by.css('.ySmartEditExperienceToolbar')).isPresent()).toBe(true);
        browser.click(by.id('navigateButtonOuter')).then(function() {
            expect(browser.getCurrentUrl()).toContain('/customView');
            browser.waitForAbsence(element(by.css('ySmartEditToolbars')));
            expect(element(by.css('.content')).getText()).toBe('custom view 2');
        });
    });

    it('navigates to custom view from the inner smartedit iframe', function() {
        expect(element(by.css('.ySmartEditExperienceToolbar')).isPresent()).toBe(true);
        browser.switchToIFrame().then(function() {
            browser.click(by.id('navigateButtonInner')).then(function() {
                expect(browser.getCurrentUrl()).toContain('/customView');
                browser.waitForAbsence(element(by.css('.ySmartEditExperienceToolbar')));
                expect(element(by.css('.content')).getText()).toBe('custom view 2');
            });
        });
    });

    it('navigates to custom view from another view from the container', function() {
        browser.setLocation('/test').then(function() {
            browser.click(by.id('navigateButtonOuter')).then(function() {
                expect(browser.getCurrentUrl()).toContain('/customView');
                expect(element(by.css('.content')).getText()).toBe('custom view 2');
            });
        });
    });

    it('navigates to new view from the custom non smartedit iframe', function() {
        browser.setLocation('/test').then(function() {
            browser.ignoreSynchronization = true;
            browser.switchToIFrame(false).then(function() {
                browser.click(by.id('navigateButtonInner')).then(function() {
                    expect(browser.getCurrentUrl()).toContain('/customView');
                    expect(element(by.css('.content')).getText()).toBe('custom view 2');
                });
            });
        });
    });

});
