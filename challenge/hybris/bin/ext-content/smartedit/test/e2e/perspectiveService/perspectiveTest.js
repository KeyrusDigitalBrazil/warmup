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
(function() {

    var perspectives = require("../utils/components/Perspectives.js");
    var storefront = require("../utils/components/Storefront.js");
    var hotKeys = require("../utils/components/HotKeys.js");
    var toolbar = require("../utils/components/WhiteToolbarComponentObject.js");
    var login = require('../utils/components/Login.js');
    var page = require('../utils/components/Page.js');

    var somePerspective = "somenameI18nKey";
    var permissionsPerspective = "permissionsI18nKey";
    var defaultPerspectiveName = "SE.PERSPECTIVE.NONE.NAME";
    var userRestrictedPerspective = "userRestrictedPerspectiveI18nKey";

    describe('Perspectives', function() {

        beforeEach(function() {
            browser.executeScript('window.sessionStorage.setItem("HAS_CONFIGURATIONS", arguments[0])', true);
            perspectives.actions.openAndBeReady();
        });

        afterEach(function(done) {
            browser.switchToParent().then(function() {
                page.actions.clearCookies();
                done();
            });
        });

        it('GIVEN application started in default perspective WHEN deep linked THEN default perspective is still selected and component is not present in the overlay', function() {
            perspectives.assertions.assertPerspectiveActive(defaultPerspectiveName);

            storefront.assertions.assertComponentInOverlayPresent(
                storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE, false);

            hotKeys.assertions.assertHotkeyTooltipIconPresent(false);

            browser.switchToIFrame();
            browser.pressKey("ESCAPE");
            perspectives.assertions.assertSmarteditOverlayIsPresent();
            browser.switchToParent();

            // Act
            storefront.actions.deepLink();

            // Assert
            perspectives.assertions.assertPerspectiveActive(defaultPerspectiveName);

            storefront.assertions.assertComponentInOverlayPresent(
                storefront.constants.COMPONENT_2_ID, storefront.constants.COMPONENT_2_TYPE, false);

            storefront.assertions.assertComponentHtmlContains(
                storefront.constants.COMPONENT_2_ID, storefront.constants.COMPONENT_2_ID);
        });

        it('WHEN new perspective is selected THEN features are activated and decorators are present in overlay', function() {
            // Act
            perspectives.actions.selectPerspective(somePerspective);

            // Assert
            perspectives.assertions.assertPerspectiveActive(somePerspective);

            storefront.assertions.assertComponentInOverlayPresent(
                storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE, true);

            storefront.assertions.assertComponentInOverlayContains(
                storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE,
                'Text_is_been_displayed_TextDisplayDecorator');
        });

        it('IF application is not in preview mode THEN hotkey tooltip icon is present', function() {
            // Act
            perspectives.actions.selectPerspective(somePerspective);

            // Assert
            perspectives.assertions.assertPerspectiveActive(somePerspective);
            hotKeys.assertions.assertHotkeyTooltipIconPresent(true);

            browser.switchToIFrame();
            browser.pressKey("ESCAPE");
            perspectives.assertions.assertSmarteditOverlayIsAbsent();
            browser.switchToParent();

        });

        it('GIVEN application started in default perspective, features are off; WHEN new perspective is selected AND permission service returns true for inner feature THEN feature whose permissions were registered from the inner app is present in overlay and feature is present in outer application', function() {
            // WHEN
            togglePerspectiveSessionStorage(true);
            perspectives.actions.selectPerspective(permissionsPerspective);

            //THEN
            storefront.assertions.assertComponentInOverlayContains(
                storefront.constants.COMPONENT_3_ID, storefront.constants.COMPONENT_3_TYPE,
                'Test permission component');

            storefront.assertions.assertComponentInOverlayContains(
                storefront.constants.COMPONENT_4_ID, storefront.constants.COMPONENT_4_TYPE,
                'Test permission decorator registered inner');

            toolbar.assertions.assertButtonPresent('Some Item');
        });

        it('GIVEN application started in default perspective, features are off; WHEN new perspective is selected AND permission service returns false for inner feature THEN feature whose permissions were registered from the inner app is not present in overlay and feature is not present in outer application', function() {
            // WHEN
            perspectives.actions.selectPerspective(somePerspective);
            perspectives.assertions.assertPerspectiveActive(somePerspective);

            togglePerspectiveSessionStorage(false);
            perspectives.actions.selectPerspective(permissionsPerspective);

            //THEN
            storefront.assertions.assertComponentInOverlayNotContains(
                storefront.constants.COMPONENT_4_ID, storefront.constants.COMPONENT_4_TYPE,
                'Test permission decorator registered inner');

            storefront.assertions.assertComponentInOverlayNotContains(
                storefront.constants.COMPONENT_3_ID, storefront.constants.COMPONENT_3_TYPE,
                'Test permission component');

            toolbar.assertions.assertButtonNotPresent('Some Item');
        });

        it('GIVEN application started in some perspective, features are on; WHEN new perspective is selected and permission service returns true THEN feature from some perspective is not present and feature new perspective is present', function() {
            // GIVEN
            perspectives.actions.selectPerspective(somePerspective);
            perspectives.assertions.assertPerspectiveActive(somePerspective);
            storefront.assertions.assertComponentInOverlayPresent(storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE, true);
            togglePerspectiveSessionStorage(true);

            // WHEN
            perspectives.actions.selectPerspective(permissionsPerspective);

            // THEN
            storefront.assertions.assertComponentInOverlayNotContains(storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE, true);
            toolbar.assertions.assertButtonPresent('Some Item');
        });

        function togglePerspectiveSessionStorage(enabled) {
            browser
                .switchToIFrame()
                .then(function() {
                    return setPerspectiveSessionStorage(enabled);
                })
                .then(function() {
                    browser.switchToParent().then(function() {
                        setPerspectiveSessionStorage(enabled);
                    });
                });
        }

        function setPerspectiveSessionStorage(enabled) {
            return browser.executeScript('window.sessionStorage.setItem("PERSPECTIVE_SERVICE_RESULT", arguments[0])', enabled);
        }
    });

    describe('Perspectives with user switch', function() {
        beforeEach(function() {
            browser.executeScript('window.sessionStorage.setItem("HAS_CONFIGURATIONS", arguments[0])', false);
            page.actions.getAndWaitForLogin('test/e2e/perspectiveService/perspectiveTest.html');
            login.loginAsAdmin().then(function() {
                setHasAccessRestrictedPerspective(true);
            });
        });

        afterEach(function(done) {
            browser.switchToParent().then(function() {
                page.actions.clearCookies();
                done();
            });
        });

        it('GIVEN a user has access to a perspective AND he access it and log out WHEN he log in again THEN the perspective is still selected', function() {
            perspectives.actions.selectPerspective(userRestrictedPerspective);
            perspectives.assertions.assertPerspectiveActive(userRestrictedPerspective);

            login.logoutUser();

            perspectives.actions.refreshAndWaitForAngularEnabled();

            page.actions.waitForLoginModal();

            login.loginAsAdmin();

            perspectives.assertions.assertPerspectiveActive(userRestrictedPerspective);
        });

        it('GIVEN a user has access to a perspective AND he access it and log out WHEN another user with no access to that perspective logs in THEN the default perspective should be selected', function() {
            perspectives.actions.selectPerspective(userRestrictedPerspective);
            perspectives.assertions.assertPerspectiveActive(userRestrictedPerspective);

            login.logoutUser();

            page.actions.getAndWaitForLogin('test/e2e/perspectiveService/perspectiveTest.html');

            login.loginAsCmsManager();

            setHasAccessRestrictedPerspective(false);

            perspectives.assertions.assertPerspectiveActive(defaultPerspectiveName);
        });

        function setHasAccessRestrictedPerspective(enabled) {
            browser.switchToIFrame().then(function() {
                browser.executeScript('window.sessionStorage.setItem("HAS_RESTRICTED_PERSPECTIVE", arguments[0])', enabled);
                perspectives.actions.openAndBeReady();
            });
        }
    });

})();
