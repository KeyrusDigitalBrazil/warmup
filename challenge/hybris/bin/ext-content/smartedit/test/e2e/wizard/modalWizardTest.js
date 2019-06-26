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
var WizardPageObject = require('./wizardPageObject');

describe("Modal Wizard", function() {

    var wizardPage = new WizardPageObject();

    beforeEach(function() {
        wizardPage.open();
    });

    it("WHEN the wizard is opened, " +
        "THEN the wizard (underlying)template should be displayed",
        function() {
            wizardPage.openWizard();
            wizardPage.assertWizardDisplayed();
        });

    it("WHEN it is first opened, " +
        "THEN I should be on the first step of the wizard",
        function() {
            wizardPage.openWizard();
            wizardPage.assertStepDisplayed(wizardPage.elements.nameStep);
        });

    it("WHEN I have a form validation, " +
        "AND the validation is failing, " +
        "THEN I should see the next button disabled",
        function() {
            wizardPage.openWizard();
            wizardPage.assertNextIsDisabled();
        });

    it("WHEN I have a form validation, " +
        "AND the validation is passing, " +
        "THEN I should see the next button enabled",
        function() {
            wizardPage.openWizard();
            wizardPage.makeNameStepValid();
            wizardPage.assertNextIsEnabled();
        });

    it("WHEN I click Next, " +
        "THEN I should navigate to the next step of the wizard",
        function() {
            wizardPage.openWizard();
            wizardPage.makeNameStepValid();
            wizardPage.clickNext();
            wizardPage.assertStepDisplayed(wizardPage.elements.genderStep);
        });

    it("WHEN the last step is displayed, " +
        "THEN I should see a done button instead of a next button",
        function() {
            wizardPage.openToSummaryStep();
            wizardPage.assertNextIsNotPresent();
            wizardPage.assertDoneIsPresent();
        });

    it("WHEN I click Back, " +
        "THEN I should navigate to the previous step of the wizard",
        function() {
            wizardPage.openToAgeStep();
            wizardPage.assertStepDisplayed(wizardPage.elements.ageStep);
            wizardPage.clickBack(); //to gender
            wizardPage.assertStepDisplayed(wizardPage.elements.genderStep);
        });

    it("WHEN I click cancel, " +
        "AND I dismiss the cancel confirmation, " +
        "THEN the wizard should still be displayed",
        function() {
            wizardPage.openWizard();
            wizardPage.clickCancel();
            wizardPage.dismissAlert(); // cancel the cancel...
            wizardPage.assertWizardDisplayed();
        });

    it("WHEN I click cancel, " +
        "AND I accept the cancel confirmation, " +
        "THEN the wizard should be closed",
        function() {
            wizardPage.openWizard();
            wizardPage.clickCancel();
            wizardPage.acceptAlert();
            wizardPage.assertWizardNotDisplayed();
        });

    it("WHEN I click Done, " +
        "AND I dismiss the close confirmation, " +
        "THEN the wizard should still be displayed",
        function() {
            wizardPage.openToSummaryStep();
            wizardPage.clickDone();
            wizardPage.dismissAlert();
            wizardPage.assertWizardDisplayed();
        });

    it("WHEN I click Done, " +
        "AND I accept the close confirmation, " +
        "THEN the wizard should be closed",
        function() {
            wizardPage.openToSummaryStep();
            wizardPage.clickDone();
            wizardPage.acceptAlert();
            wizardPage.assertWizardNotDisplayed();
        });

    it("WHEN I add a step at runtime, " +
        "THEN I should see the step in the nav bar and be able to navigate to it",
        function() {
            wizardPage.openToGenderStep();
            wizardPage.clickOffendedToggle();
            wizardPage.clickNext();
            wizardPage.assertStepDisplayed(wizardPage.elements.offendedStep);
        });

    it("WHEN I remove a step at runtime, " +
        "THEN I should see the step removed from the navbar and navigating should skip that step",
        function() {
            wizardPage.openToGenderStep();
            wizardPage.clickOffendedToggle();
            wizardPage.clickNext();
            wizardPage.assertStepDisplayed(wizardPage.elements.offendedStep);
            wizardPage.clickBack();
            wizardPage.clickOffendedToggle();
            wizardPage.clickNext();
            wizardPage.assertStepDisplayed(wizardPage.elements.ageStep);
        });

    it("WHEN I select a wizard navbar step, " +
        "AND that step is before the current step, " +
        "THEN I should be navigated directly to that step",
        function() {
            wizardPage.openToGenderStep();
            wizardPage.clickNavbar(wizardPage.elements.nameNavbar);
            wizardPage.assertStepDisplayed(wizardPage.elements.nameStep);
        });

    it("WHEN I select a wizard navbar step, " +
        "AND that step is ahead of the current step, " +
        "THEN I should stay on the current step",
        function() {
            wizardPage.openWizard().then(function() {
                expect(wizardPage.elements.ageNavbar.isEnabled()).toBe(false);
            });
        });

});
