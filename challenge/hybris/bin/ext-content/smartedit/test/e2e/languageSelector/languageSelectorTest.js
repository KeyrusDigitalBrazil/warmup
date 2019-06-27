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
/* jshint undef:false */
require('./../utils/components/LoginLogoutFunctions.js');

afterEach(function() {

    afterEachAuthentificationTest();

});

describe("languageSelector", function() {

    var experienceSelector = require('../utils/components/ExperienceSelector.js');
    var headerToolbar = require('../utils/components/HeaderToolbarComponentObject.js');
    var loginForm = require('../utils/components/Login.js');
    var storefront = require("../utils/components/Storefront.js");
    var perspectives = require("../utils/components/Perspectives.js");

    var buildDecoratorName = function(prefix, id, type, index) {
        return prefix + '-' + id + '-' + type + '-' + index;
    };

    beforeAll(function() {
        browser.isDelayed = true;
    });
    afterAll(function() {
        browser.isDelayed = false;
    });

    beforeEach(function() {
        browser.driver.manage().deleteAllCookies();
        browser.get('test/e2e/languageSelector/languageSelectorTest.html');
        browser.waitForAngularEnabled(false);
    });

    afterEach(function() {
        browser.driver.manage().deleteAllCookies();
        browser.waitForAngularEnabled(true);
    });

    it("GIVEN I am on the login page WHEN I select French language, THEN the first select box in the list is French",
        function() {
            // WHEN
            selectLanguageFromLogin('French');
            loginForm.toggleLanguageSelectorDropdown();

            //ASSERT
            loginForm.assertLanguageSelectorFirstInList("FRENCH");
        });

    it("GIVEN my browser has unsupported language, THEN the translation map is still fetched for that language", function() {
        browser.manage().addCookie({
            name: "SELECTED_LANGUAGE",
            value: JSON.stringify({
                isoCode: "kl",
                name: "Klingon"
            })
        });
        browser.refresh();
        expect(element(by.id('username_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('klName');
        expect(element(by.id('password_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('klPassword');
    });

    it("GIVEN I am on the login page WHEN I select English language THEN it should translate the i18n keys", function() {
        selectLanguage('English', 'uiSelectToolingLanguage');
        expect(element(by.id('username_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('Name');
        expect(element(by.id('password_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('Password');
    });

    it("GIVEN I am on the login page WHEN I select French language THEN it should translate the i18n keys",
        function() {
            selectLanguage('French', 'uiSelectToolingLanguage');
            expect(element(by.id('username_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('Nom');
            expect(element(by.id('password_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('Mot de passe');
        });

    it("GIVEN I am on the login page, AND I select French language, WHEN coming back to the page, THEN it should load the french localization",
        function() {
            selectLanguage('French', 'uiSelectToolingLanguage');
            expect(element(by.id('username_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('Nom');
            browser.refresh();
            expect(element(by.id('username_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('Nom');
            expect(element(by.id('password_' + MAIN_AUTH_SUFFIX)).getAttribute('placeholder')).toBe('Mot de passe');
        });

    it("GIVEN I select French Language, AND submitting right credentials, THEN the container should be localized with French",
        function() {

            //GIVEN
            selectLanguageAndLogin('French');

            //THEN
            browser.click(experienceSelector.elements.widget.button());

            //ASSERT
            expect(experienceSelector.elements.catalog.label().getText()).toBe('CATALOGUE');
            expect(experienceSelector.elements.dateAndTime.label().getText()).toBe('DATE ET HEURE');
            expect(experienceSelector.elements.language.label().getText()).toBe('LANGUE');
        });

    it("GIVEN I select French Language, AND submitting right credentials, THEN the store front should be localized with French",
        function() {
            //GIVEN
            selectLanguageAndLogin('French');

            //THEN
            browser.switchToIFrame();
            headerToolbar.assertions.localizedFieldIsTranslated(by.id('localizationField'), 'Je suis localisée');
            browser.switchToParent();
        });


    it("GIVEN I logged in smartedit, AND change the language on the header toolbar, THEN the store front should be localized with French",
        function() {

            //GIVEN
            selectLanguageAndLogin('English');

            //THEN
            selectLanguage('French', 'languageSelectorDropdown');
            browser.switchToIFrame();

            //ASSERT
            headerToolbar.assertions.localizedFieldIsTranslated(by.id('localizationField'), 'Je suis localisée');
            browser.switchToParent();
        });

    it("GIVEN I selected French on the login page, AND I changed it to English on the header toolbar, WHEN I logout THEN the login page should be localized in English",
        function() {

            //GIVEN
            selectLanguageAndLogin('French').then(function() {
                //THEN
                selectLanguage('English', 'languageSelectorDropdown').then(function() {
                    headerToolbar.actions.clickOnLogout().then(function() {
                        loginForm.waitForLanguageSelectorToBePopulated();
                        loginForm.assertLanguageSelectorLanguage('ENGLISH');
                    });
                });
            });
        });

    it("GIVEN I logged in smartedit, AND change the language on the header toolbar, THEN the decorators should be localized with French",
        function() {

            //GIVEN
            selectLanguageAndLogin('English');

            perspectives.actions.selectPerspective(perspectives.constants.DEFAULT_PERSPECTIVES.ALL).then(function() {
                browser.waitForWholeAppToBeReady();

                selectComponent1ContextualMenu();
                expect(element(by.css("dummy")).getText()).toBe('dymmyText in english');
                selectComponent1ContextualMenu();

                //THEN
                browser.switchToParent();

                selectLanguage('French', 'languageSelectorDropdown');

                browser.switchToIFrame();

                //ASSERT
                selectComponent1ContextualMenu();
                expect(element(by.css("dummy")).getText()).toBe('dymmyText in french');
            });

        });

    function selectLanguageAndLogin(language) {
        element(by.id('username_' + MAIN_AUTH_SUFFIX)).sendKeys('customermanager');
        element(by.id('password_' + MAIN_AUTH_SUFFIX)).sendKeys('123');
        return selectLanguageFromLogin(language).then(function() {
            return browser.click(by.id('submit_' + MAIN_AUTH_SUFFIX));
        }).then(function() {
            return browser.waitForWholeAppToBeReady();
        });
    }

    function selectLanguage(value, id) {
        var languageSelector = headerToolbar.elements.getLanguageSelector(id);
        if (id === 'uiSelectToolingLanguage') {
            return browser.waitForPresence(languageSelector).then(function() {
                return browser.click(languageSelector.element(by.css('.ui-select-toggle')), "UI select toggle not clickable").then(function() {
                    return browser.click(languageSelector.element(by.cssContainingText('.ui-select-choices-row', value)), "UI select choice with value " + value + " not clickable");
                });
            });
        } else {
            return browser.waitForPresence(languageSelector).then(function() {
                return browser.click(languageSelector.element(by.css('.yToolbarActions__icon')), "icon dropdown not clickable").then(function() {
                    return browser.click(languageSelector.element(by.cssContainingText('.yToolbarActions__dropdown-element', value)), "language select choice with value " + value + " not clickable");
                });
            });
        }
    }

    function selectLanguageFromLogin(value) {
        var languageSelector = element(by.css('.se-login-language #uiSelectToolingLanguage'));
        return browser.waitForPresence(languageSelector).then(function() {
            return browser.click(languageSelector.element(by.css('.ui-select-toggle')), "UI select toggle not clickable").then(function() {
                return browser.click(languageSelector.element(by.cssContainingText('.ui-select-choices-row', value)), "UI select choice with value " + value + " not clickable");
            });
        });
    }

    function selectComponent1ContextualMenu() {
        browser.switchToIFrame();
        browser.actions().mouseMove(element(by.id(storefront.constants.COMPONENT_1_ID))).perform();
        return browser.click(by.id(buildDecoratorName('TEMPLATEURL', storefront.constants.COMPONENT_1_ID, storefront.constants.COMPONENT_1_TYPE, 0)));
    }


});
