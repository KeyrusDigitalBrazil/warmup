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
describe("Configuration Editor", function() {
    var defaultConfigData = require('./defaultConfigurations.json');
    var configurations = require("../utils/components/Configurations.js");
    var page = require("../utils/components/Page.js");
    var login = require('../utils/components/Login.js');
    var landingPage = require('../utils/pageObjects/LandingPagePageObject.js');

    beforeEach(function() {
        page.actions.getAndWaitForLogin('test/e2e/a_editConfigurations/editConfigurationsTest.html');
    });
    afterEach(function() {
        page.actions.clearCookies();
        browser.waitForAngularEnabled(true);
    });

    describe('Permissions', function() {
        afterEach(function() {
            login.logoutUser();
        });

        it("GIVEN I am logged in as a user without permission to view the configuration editor THEN configuration center will be hidden", function() {
            login.loginAsCmsManager();
            browser.waitUntilNoModal();
            browser.waitForAbsence(configurations.getConfigurationCenterButton());
        });

        it("GIVEN I am logged in as a user with permission to view the configuration editor THEN configuration center will be visible", function() {
            login.loginAsAdmin();
            browser.waitUntilNoModal();
            expect(configurations.getConfigurationCenterButton().isPresent()).toBe(true);
        });

        it("GIVEN I am logged in as a user with permission to view editor AND then switched to user without permission THEN configuration center must be hidden", function() {
            // GIVEN 
            login.loginAsAdmin();
            browser.waitUntilNoModal();

            logOutAndRedirectToLoginPage();

            // WHEN 
            login.loginAsCmsManager();
            browser.waitUntilNoModal();

            // // THEN
            browser.waitForAbsence(configurations.getConfigurationCenterButton());
        });

        it("GIVEN I am logged in as a user without permission to view editor AND then switched to user with permission THEN configuration center must be visible", function() {
            // GIVEN
            login.loginAsCmsManager();
            browser.waitUntilNoModal();

            logOutAndRedirectToLoginPage();

            // WHEN 
            login.loginAsAdmin();
            browser.waitUntilNoModal();

            // THEN
            expect(configurations.getConfigurationCenterButton().isPresent()).toBe(true);
        });

        function logOutAndRedirectToLoginPage() {
            login.logoutUser();

            // Clicking somewhere in the page to force it to load the login page; since the user no longer has a valid token, the 
            // login page will be displayed. 
            landingPage.actions.clickOnHomePageLink(landingPage.constants.ELECTRONICS_CATALOG, landingPage.constants.ACTIVE_CATALOG_VERSION);
        }
    });

    describe('Modified Configurations', function() {
        beforeEach(function(done) {
            login.loginAsAdmin().then(function() {
                return configurations.openConfigurationEditor();
            }).then(function() {
                configurations.waitForConfigurationModal(3);
                done();
            });
        });

        afterEach(function() {
            configurations.clickCancel();
        });

        it("GIVEN I'm in the Configuration Editor WHEN I attempt to add a duplicate key THEN I expect to see an error", function(done) {
            configurations.clickAdd();
            configurations.waitForConfigurationsToPopulate(4);
            configurations.setConfigurationKeyAndValue(0, 'previewTicketURI', 'previewTicketURI'); //add key and value
            configurations.clickSave();
            configurations.waitForErrorForKey("previewTicketURI");
            expect(configurations.getErrorForKey("previewTicketURI").getText()).toEqual("This is a duplicate key");
            done();
        });

        it("GIVEN I'm in the Configuration Editor WHEN user types an absolute URL THEN the editor shall display a checkbox", function() {
            configurations.setConfigurationValue(0, '"https://someuri"'); //add key and value
            expect(configurations.getAbsoluteUrlCheckbox().isDisplayed()).toBeTruthy();
        });


        it("GIVEN I'm in the Configuration Editor WHEN user types does not type an absolute URL THEN the editor shall not display a checkbox", function() {
            configurations.setConfigurationValue(0, '"/someuri/"'); //add key and value
            browser.waitForAbsence(configurations.getAbsoluteUrlCheckbox());
        });
    });

    describe('Configurations', function() {
        beforeEach(function(done) {
            login.loginAsAdmin().then(function() {
                return configurations.openConfigurationEditor();
            }).then(function() {
                configurations.waitForConfigurationModal(3);
                done();
            });
        });

        afterEach(function() {
            configurations.clickCancel();
            login.logoutUser();
        });

        it("GIVEN I am in the Configuration Editor THEN I expect to see a title, a save and cancel button, and configurations as defined in the backend", function() {
            expect(configurations.getConfigurationTitle().getText()).toBe('edit configuration ');
            expect(configurations.getCancelButton().isPresent()).toBe(true);
            expect(configurations.getSaveButton().isPresent()).toBe(true);
            expect(configurations.getConfigurations()).toEqual(defaultConfigData);
        });

        it("GIVEN I'm in the Configuration Editor WHEN I delete a configuration entry AND I reopen the configuration editor THEN I expect to see the remaining configurations", function() {
            configurations.deleteConfiguration(1); //delete the 2nd configuration
            configurations.clickSave();
            configurations.openConfigurationEditor();

            expect(configurations.getConfigurations()).toEqual([{
                "key": "previewTicketURI",
                "value": "\"thepreviewTicketURI\""
            }, {
                "value": "[\n  \"*\"\n]",
                "key": "whiteListedStorefronts"
            }]);
        });

        it("GIVEN I'm in the Configuration Editor WHEN I attempt to add a malformed configuration THEN an error is displayed", function() {
            configurations.clickAdd();
            configurations.waitForConfigurationsToPopulate(4);
            configurations.setConfigurationKeyAndValue(0, 'newkey', '{othermalformed}');
            configurations.clickSave();
            configurations.waitForErrorForKey("newkey");

            expect(configurations.getErrorForKey("newkey").getText()).toEqual("this value should be a valid JSON format");
        });

        it("GIVEN I'm in the Configuration Editor WHEN I attempt to add a malformed configuration AND re-open the configuration editor THEN I expect to see the original configurations", function() {
            configurations.clickAdd();
            configurations.waitForConfigurationsToPopulate(4);
            configurations.setConfigurationKeyAndValue(0, 'newkey', '{othermalformed}');
            configurations.clickSave();
            configurations.waitForErrorForKey("newkey");
            configurations.clickCancel();
            configurations.openConfigurationEditor();

            expect(configurations.getConfigurations()).toEqual([{
                "key": "previewTicketURI",
                "value": "\"thepreviewTicketURI\""
            }, {
                "key": 'i18nAPIRoot',
                "value": "{malformed}"
            }, {
                "value": "[\n  \"*\"\n]",
                "key": "whiteListedStorefronts"
            }]);
        });

        it("GIVEN I'm in the Configuration Editor WHEN I attempt to add a new well-formed configuration THEN the configuration will be added", function() {
            configurations.clickAdd();
            configurations.waitForConfigurationsToPopulate(4);
            configurations.setConfigurationKeyAndValue(0, 'newkey', '\"new value\"');
            configurations.clickSave();
            configurations.clickCancel();
            configurations.openConfigurationEditor();

            expect(configurations.getConfigurations()).toEqual([{
                "key": "previewTicketURI",
                "value": "\"thepreviewTicketURI\""
            }, {
                "key": 'i18nAPIRoot',
                "value": "{malformed}"
            }, {
                "value": "[\n  \"*\"\n]",
                "key": "whiteListedStorefronts"
            }, {
                "key": "newkey",
                "value": "\"new value\""
            }]);
        });

        it("GIVEN I'm in the Configuration Editor WHEN I attempt to modify an configuration with a well-formed configuration THEN I expect to see the configuration modified", function() {
            configurations.setConfigurationValue(1, '\"new\"');
            configurations.clickSave();
            configurations.openConfigurationEditor();

            expect(configurations.getConfigurations()).toEqual([{
                "key": "previewTicketURI",
                "value": "\"thepreviewTicketURI\""
            }, {
                "key": "i18nAPIRoot",
                "value": "\"new\""
            }, {
                "value": "[\n  \"*\"\n]",
                "key": "whiteListedStorefronts"
            }]);
        });


        it("GIVEN I'm in the Configuration Editor WHEN I attempt to add a duplicate key AND click cancel THEN I expect to see the original configuration in tact", function() {

            configurations.clickAdd().then(function() {
                return configurations.waitForConfigurationsToPopulate(4);
            }).then(function() {
                return configurations.setConfigurationKeyAndValue(0, 'previewTicketURI', 'previewTicketURI'); //add key and value
            }).then(function() {
                return configurations.clickSave();
            }).then(function() {
                configurations.waitForErrorForKey("previewTicketURI");
                configurations.clickCancel();
                return configurations.clickConfirmOk();
            }).then(function() {
                configurations.openConfigurationEditor();

                expect(configurations.getConfigurations()).toEqual([{
                    "key": "previewTicketURI",
                    "value": "\"thepreviewTicketURI\""
                }, {
                    "key": 'i18nAPIRoot',
                    "value": "{malformed}"
                }, {
                    "value": "[\n  \"*\"\n]",
                    "key": "whiteListedStorefronts"
                }]);
            });
        });

        it("GIVEN I'm in the Configuration Editor WHEN user types an absolute URL and does not tick the checkbox THEN the editor shall highlight the message and not save", function() {
            configurations.setConfigurationValue(0, '"https://someuri"'); //add key and value
            expect(configurations.getAbsoluteUrlCheckbox().isDisplayed()).toBeTruthy();

            expect(configurations.getAbsoluteUrlText().getAttribute('class')).not.toMatch(' not-checked');
            configurations.clickSave();
            expect(configurations.getAbsoluteUrlText().getAttribute('class')).toMatch(' not-checked');

            configurations.clickCancel();
            configurations.openConfigurationEditor();

            expect(configurations.getConfigurations()).toEqual([{
                "key": "previewTicketURI",
                "value": "\"thepreviewTicketURI\""
            }, {
                "key": 'i18nAPIRoot',
                "value": "{malformed}"
            }, {
                "value": "[\n  \"*\"\n]",
                "key": "whiteListedStorefronts"
            }]);
        });

        it("GIVEN I'm in the Configuration Editor WHEN user types an absolute URL and ticks the checkbox THEN the editor shall not highlight the message and save the content", function() {
            configurations.setConfigurationValue(0, '"https://someuri"'); //add key and value
            expect(configurations.getAbsoluteUrlCheckbox().isDisplayed()).toBeTruthy();
            browser.click(configurations.getAbsoluteUrlCheckbox());

            expect(configurations.getAbsoluteUrlText().getAttribute('class')).not.toMatch(' not-checked');
            configurations.clickSave();
            expect(configurations.getAbsoluteUrlText().getAttribute('class')).not.toMatch(' not-checked');

            configurations.clickCancel();
            configurations.openConfigurationEditor();

            expect(configurations.getConfigurations()).toEqual([{
                "key": "previewTicketURI",
                "value": "\"https://someuri\""
            }, {
                "key": 'i18nAPIRoot',
                "value": "{malformed}"
            }, {
                "value": "[\n  \"*\"\n]",
                "key": "whiteListedStorefronts"
            }]);
        });
    });

});
