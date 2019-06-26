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
module.exports = {
    STORE_FRONT_HOME_PAGE: "storefront.html",

    // Elements
    mainLoginUsernameInput: function() {
        return element(by.id('username_L2F1dGhvcml6YXRpb25zZXJ2ZXIvb2F1dGgvdG9rZW4'));
    },
    mainLoginPasswordInput: function() {
        return element(by.id('password_L2F1dGhvcml6YXRpb25zZXJ2ZXIvb2F1dGgvdG9rZW4'));
    },
    mainLoginSubmitButton: function() {
        return element(by.id('submit_L2F1dGhvcml6YXRpb25zZXJ2ZXIvb2F1dGgvdG9rZW4'));
    },
    fake1LoginUsernameInput: function() {
        return element(by.id('username_L2F1dGhFbnRyeVBvaW50MQ'));
    },
    fake1LoginPasswordInput: function() {
        return element(by.id('password_L2F1dGhFbnRyeVBvaW50MQ'));
    },
    fake1LoginSubmitButton: function() {
        return element(by.id('submit_L2F1dGhFbnRyeVBvaW50MQ'));
    },
    fake2LoginUsernameInput: function() {
        return element(by.id('username_L2F1dGhFbnRyeVBvaW50Mg'));
    },
    fake2LoginPasswordInput: function() {
        return element(by.id('password_L2F1dGhFbnRyeVBvaW50Mg'));
    },
    fake2LoginSubmitButton: function() {
        return element(by.id('submit_L2F1dGhFbnRyeVBvaW50Mg'));
    },
    requiredError: function() {
        return element(by.id('requiredError'));
    },
    authenticationError: function() {
        return element(by.id('invalidError'));
    },
    userAccountButton: function() {
        return element(by.id('userAccountDropdown'));
    },
    logoutButton: function() {
        return element(by.css('a.se-sign-out__link'));
    },
    languageSelectorDropdown: function() {
        return element(by.css('.se-login-language #uiSelectToolingLanguage'));
    },
    languageSelectorOptionByLanguage: function(language) {
        return this.languageSelectorDropdown().element(by.cssContainingText('.ui-select-choices-row', language));
    },
    languageSelectorFirstInList: function() {
        return element(by.css('.ui-select-choices-row:first-child'));
    },

    // Actions
    logoutUser: function() {
        browser.switchToParent().then(function() {
            return browser.click(this.userAccountButton());
        }.bind(this)).then(function() {
            browser.wait(protractor.ExpectedConditions.elementToBeClickable(this.logoutButton()), 5000, "Timed out waiting for logout button");
            return browser.click(this.logoutButton());
        }.bind(this));
    },

    loginAsUser: function(username, password) {
        return browser.wait(protractor.ExpectedConditions.elementToBeClickable(this.mainLoginUsernameInput()), 5000, "Timed out waiting for username input").then(function() {
            return this.mainLoginUsernameInput().sendKeys(username).then(function() {
                return this.mainLoginPasswordInput().sendKeys(password).then(function() {
                    return browser.click(this.mainLoginSubmitButton(), "could no click on main login submit button");
                }.bind(this));
            }.bind(this));
        }.bind(this));
    },

    loginAsInvalidUser: function() {
        this.loginAsUser('invalid', 'invalid');
    },

    loginAsCmsManager: function() {
        return this.loginAsUser('cmsmanager', '1234').then(function() {
            return browser.waitForWholeAppToBeReady();
        });
    },

    loginAsAdmin: function() {
        return this.loginAsUser('admin', '1234').then(function() {
            return browser.waitForWholeAppToBeReady();
        });
    },

    loginAsCmsManagerToLandingPage: function() {
        this.loginAsUser('cmsmanager', '1234');
        browser.waitForContainerToBeReady();
    },

    loginToAuthForFake1: function() {
        browser.wait(protractor.ExpectedConditions.elementToBeClickable(this.fake1LoginUsernameInput()), 5000,
            "Timed out waiting for fake 1 username input");
        this.fake1LoginUsernameInput().sendKeys('fake1');
        this.fake1LoginPasswordInput().sendKeys('1234');
        browser.click(this.fake1LoginSubmitButton());
    },

    loginToAuthForFake2: function() {
        browser.wait(protractor.ExpectedConditions.elementToBeClickable(this.fake2LoginUsernameInput()), 5000,
            "Timed out waiting for fake 2 username input");
        this.fake2LoginUsernameInput().sendKeys('fake2');
        this.fake2LoginPasswordInput().sendKeys('1234');
        browser.click(this.fake2LoginSubmitButton());
    },

    toggleLanguageSelectorDropdown: function() {
        browser.click(this.languageSelectorDropdown());
    },

    waitForLanguageSelectorToBePopulated: function() {
        this.toggleLanguageSelectorDropdown();
        browser.waitToBeDisplayed(this.languageSelectorOptionByLanguage('English'));
        this.toggleLanguageSelectorDropdown();
    },


    // Assertions
    assertLanguageSelectorLanguage: function(language) {
        expect(this.languageSelectorDropdown().getText()).toBe(language);
    },

    assertLanguageSelectorFirstInList: function(language) {
        expect(this.languageSelectorFirstInList().getText()).toBe(language);
    }
};
